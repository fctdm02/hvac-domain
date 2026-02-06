//@formatter:off
package com.djt.hvac.domain.model.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.dsl.tagquery.TagQueryExpression;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.CurrentObjectExpression;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateOutputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.computedpoint.AdComputedPointFunctionTemplateEntity;
import com.djt.hvac.domain.model.function.dto.AdFunctionErrorMessagesDto;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceCandidateBoundPointsDto;
import com.djt.hvac.domain.model.function.dto.RedGreenDto;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

/**
 * <P>
 * We have a valid AD function instance/instance candidate when all required template input points are
 * "bound" to points associated with the given equipment, either directly as children of
 * the equipment, or as a result of processing the child points of the equipment list that
 * was the result of processing the 'current object expression' of a template input point.
 * </P><P>
 * If a template input point is *not* an array, then there can only be *one* point that matches/binds.
 * Conversely, if a template input point *is* an array, then we can have *more than one* bound point.
 * </P>
 * 
 * @author tommyers
 */
public class AdFunctionEvaluator {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(AdFunctionEvaluator.class);
  
  // Used for debugging/development
  public static boolean INCLUDE_BETA_FUNCTION_TEMPLATES;
  public static int DEBUG_FUNCTION_TEMPLATE_ID;
  public static int DEBUG_EQUIPMENT_ID;  
  public static int CURRENT_FUNCTION_TEMPLATE_ID;
  public static int CURRENT_EQUIPMENT_ID;  
  
  // This method signature is invoked by equipment validate().
  public static RedGreenDto validate(
      AbstractAdFunctionInstanceEntity adFunctionInstance,
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages,
      boolean remediate) {
    
    return evaluateForBoundPoints(
        adFunctionInstance.getEquipment(), 
        adFunctionInstance.getAdFunctionTemplate(),
        adFunctionInstance,
        issueTypes,
        adFunctionInstance.getAllBoundPoints(),
        validationMessages,
        remediate);
  }

  // This method signature is invoked by FindAdFunctionInstanceCandidatesVisitor.
  public static RedGreenDto evaluateForBoundPoints(
      EnergyExchangeEntity equipment,
      AbstractAdFunctionTemplateEntity adFunctionTemplate) {
    
    return evaluateForBoundPoints(
        equipment, 
        adFunctionTemplate,
        null,
        null,
        null,
        null,
        false);
  }
  
  // This method has logic that is common to both, which is, given a piece of equipment and a AD function template,
  // is the combination valid?  If a AD function instance candidate/instance, set of previously bound points and a list of 
  // validation messages are given, then do the extra work to identify what is wrong with the existing
  // candidate/instance (which includes comparing the newly evaluated bound points to the previously bound points)
  private static RedGreenDto evaluateForBoundPoints(
      EnergyExchangeEntity equipment,
      AbstractAdFunctionTemplateEntity adFunctionTemplate,
      AbstractAdFunctionInstanceEntity adFunctionInstance,
      Set<IssueType> issueTypes,
      Map<AdFunctionTemplateInputPointEntity, Set<AdFunctionInstanceEligiblePoint>> allPersistedBoundPoints,
      List<ValidationMessage> validationMessages,
      boolean remediate) {
    
    // If the given equipment is a match for the given AD function template, then the output
    // of this method are the set of "bound" points.  If abstractAdFunctionInstance, allPersistedBoundPoints
    // and validationMessages are non-null, then the appropriate validation messages are
    // created, which are then used for validation/remediation purposes.
    int totalNumberOfBoundPoints = 0;
    Map<Integer, List<Integer>> allBoundPointIds = null;
    Map<Integer, Set<AdFunctionInstanceEligiblePoint>> allBoundPoints = null;
    boolean isInvalidForEquipment = false;
    Set<String> boundPointNames = new HashSet<>();
    List<Integer> errorMessages = new ArrayList<>();
    
    AbstractEnergyExchangeTypeEntity adFunctionTemplateEquipmentType = adFunctionTemplate.getEnergyExchangeType();

    Map<String, Object> entities = null;
    
    Integer equipmentId = null;
    if (equipment != null) {
      equipmentId = equipment.getPersistentIdentity();
    }
    
    Integer adFunctionTemplateId = adFunctionTemplate.getPersistentIdentity();
    if (adFunctionInstance != null) {
      
      if (remediate) {
        entities = new HashMap<>();
        if (adFunctionInstance.getIsCandidate()) {
          entities.put("function_candidate", adFunctionInstance);  
        } else {
          entities.put("function_instance", adFunctionInstance);  
        }
      }
    }
    CURRENT_FUNCTION_TEMPLATE_ID = adFunctionTemplateId;
    CURRENT_EQUIPMENT_ID = equipmentId;
    if (adFunctionTemplateId == DEBUG_FUNCTION_TEMPLATE_ID && equipmentId != null && equipmentId == DEBUG_EQUIPMENT_ID) {
      LOGGER.debug("BREAKPOINT");
    }
    
    // STEP 0: IF THE EQUIPMENT NO LONGER EXISTS OR IF ANY POINTS DO NOT EXIST FOR INSTANCE CANDIDATES, 
    // THEN WE WOULD HAVE SET THE DELETED FLAG.  IF TRUE, WE DELETE AND RETURN NULL RIGHT AWAY
    if (adFunctionInstance != null && (equipmentId == null || adFunctionInstance.getIsDeleted())) {
      
      IssueType issueType = IssueType.AD_FUNCTION_EQUIPMENT_DOES_NOT_HAVE_TYPE;
      if (issueTypes.contains(issueType)) {
        RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
            issueType, 
            adFunctionInstance);
        if (remediationStrategy != null) {
          
          String details = null;
          if (equipmentId == null) {
            details = "Equipment no longer exists";
          } else {
            details = "Instance candidate has point that no longer exists";
          }
          
          validationMessages.add(ValidationMessage.builder()
              .withIssueType(issueType)
              .withDetails(details)
              .withEntityType(adFunctionInstance.getClass().getSimpleName())
              .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
              .withRemediationDescription(remediationStrategy.getRemediationDescription())
              .withRemediationStrategy(remediationStrategy)
              .build());
          
          if (remediate) {
            remediationStrategy.remediate(entities);
          }
        }
      }
      return RedGreenDto
          .builder()
          .build();
    }
    
    // STEP 1: EQUIPMENT TYPE MUST EXIST AND MATCH THE TYPE SPECIFIED BY THE AD FUNCTION TEMPLATE,
    // INCLUDING THE NODE FILTER EXPRESSION, IF SPECIFIED (WHICH ARE THE TAGS)
    Optional<AbstractEnergyExchangeTypeEntity> equipmentTypeOptional = equipment.getEnergyExchangeType();
    if (!equipmentTypeOptional.isPresent()) {
      
      isInvalidForEquipment = true;
      if (adFunctionInstance != null) {
        
        IssueType issueType = IssueType.AD_FUNCTION_EQUIPMENT_DOES_NOT_HAVE_TYPE;
        if (issueTypes.contains(issueType)) {
          RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
              issueType, 
              adFunctionInstance);
          if (remediationStrategy != null) {

            validationMessages.add(ValidationMessage.builder()
                .withIssueType(issueType)
                .withDetails("Expected equipment type: ["
                    + adFunctionTemplateEquipmentType
                    + "]")
                .withEntityType(adFunctionInstance.getClass().getSimpleName())
                .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                .withRemediationDescription(remediationStrategy.getRemediationDescription())
                .withRemediationStrategy(remediationStrategy)
                .build());
            
            if (remediate) {
              remediationStrategy.remediate(entities);
            }
          }
        }
      }
      
    } else {

      AbstractEnergyExchangeTypeEntity equipmentType = equipmentTypeOptional.get();
      if (!equipmentType.equals(adFunctionTemplateEquipmentType)) {
        
        isInvalidForEquipment = true;
        if (adFunctionInstance != null) {
          
          IssueType issueType = IssueType.AD_FUNCTION_EQUIPMENT_HAS_INVALID_EQUIPMENT_TYPE;
          if (issueTypes.contains(issueType)) {
            RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                issueType, 
                adFunctionInstance);
            if (remediationStrategy != null) {

              validationMessages.add(ValidationMessage.builder()
                  .withIssueType(issueType)
                  .withDetails("Encountered equipment type: ["
                      + equipmentType
                      + "], yet expected: ["
                      + adFunctionTemplateEquipmentType
                      + "]")
                  .withEntityType(adFunctionInstance.getClass().getSimpleName())
                  .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                  .withRemediationDescription(remediationStrategy.getRemediationDescription())
                  .withRemediationStrategy(remediationStrategy)
                  .build());
              
              if (remediate) {
                remediationStrategy.remediate(entities);
              }
            }
          }
        }
        
      } else {
        
        // STEP 2: EQUIPMENT/PLANT/LOOP ELIGIBILITY VIA NODE FILTER EXPRESSION IF EXISTS 
        // THAT IS, METADATA TAG MATCHING
        String nodeFilterExpression = adFunctionTemplate.getNodeFilterExpression();
        if (nodeFilterExpression != null && !nodeFilterExpression.trim().isEmpty()) {
          
          Set<String> metadataTags = equipment.getMetadataTags();
          TagQueryExpression exp = TagQueryExpression.parse(nodeFilterExpression);
          if (!exp.match(metadataTags)) {
            
            isInvalidForEquipment = true;
            if (adFunctionInstance != null) {
              
              IssueType issueType = IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_MATCH_NODE_FILTER_EXPRESSION;
              if (issueTypes.contains(issueType)) {
                RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                    issueType, 
                    adFunctionInstance);
                if (remediationStrategy != null) {

                  validationMessages.add(ValidationMessage.builder()
                      .withIssueType(issueType)
                      .withDetails("Metadata tags: "
                          + metadataTags
                          + " do not match node filter expression: ["
                          + adFunctionTemplateEquipmentType
                          + "]")
                      .withEntityType(adFunctionInstance.getClass().getSimpleName())
                      .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                      .withRemediationDescription(remediationStrategy.getRemediationDescription())
                      .withRemediationStrategy(remediationStrategy)
                      .build());
                  
                  if (remediate) {
                    remediationStrategy.remediate(entities);
                  }
                }
              }
            }
          }
        }

        if (!isInvalidForEquipment) {
          
          // STEP 3: POINT ELIGIBILITY 
          allBoundPointIds = new TreeMap<>();
          allBoundPoints = new TreeMap<>();
          
          // PASS ONE FOR POINT ELIGIBILITY: GET POINTS WHOSE HAYSTACK TAGS MATCH THAT OF THE TEMPLATE INPUT POINT
          // AND EVALUATE THE AD FUNCTION INSTANCE AGAINST THE EQUIPMENT/HIERARCHY, AS IT EXISTS NOW (I.E. FROM SCRATCH)
          Set<AdFunctionTemplateInputPointEntity> templateInputPoints = adFunctionTemplate.getInputPoints();
          Iterator<AdFunctionTemplateInputPointEntity> templateInputPointsIterator = templateInputPoints.iterator();
          while (templateInputPointsIterator.hasNext()) {
            
            AdFunctionTemplateInputPointEntity templateInputPoint = templateInputPointsIterator.next();

            Integer templateInputPointId = templateInputPoint.getPersistentIdentity();
            if (adFunctionInstance != null) {
              
              if (remediate) {
                entities.put("function_template_input_point", templateInputPoint);
              }
            }
            
            Boolean isRequired = templateInputPoint.getIsRequired();
            Boolean isArray = templateInputPoint.getIsArray();
            Set<String> templateInputPointTags = templateInputPoint.getNormalizedTagsAsSet();
            String currentObjectExpression = templateInputPoint.getCurrentObjectExpression();

            
            Set<AdFunctionInstanceEligiblePoint> adFunctionInstanceEligiblePoints = CurrentObjectExpression.getAdFunctionInstanceEligiblePoints(
                equipment, 
                currentObjectExpression);
            
            
            // CURR OBJ EXPR, WHEN NULL, REFERS TO THE EQUIPMENT BEING EVALUATED.  THAT IS, THE LIST OF
            // POINTS TO EVALUATE ARE THE DIRECT CHILDREN OF THE EQUIPMENT.  HERE ARE THE PERMUTATIONS:
            //
            // parentEquipment(): THE LIST OF POINTS TO EVALUATE ARE THE DIRECT CHILDREN OF THE *PARENT* 
            // EQUIPMENT OF THE EQUIPMENT BEING EVALUATED.  PARENT HERE IS VIA THE EQUIPMENT HIERARCHY, NOT 
            // THE NODE HIERARCHY.
            //
            // childEquipment(tags=vav): THE LIST OF POINTS TO EVALUATE ARE THE DIRECT CHILDREN OF ONE OF THE
            // *CHILDREN* EQUIPMENT OF THE EQUIPMENT BEING EVALUATED.  PARENT HERE EQUIPMENT HIERARCHY. NOT
            // THE NODE HIERARCHY.
            //
            // ancestor(type=building): THE LIST OF POINTS TO EVALUATION ARE THE DIRECT CHILDREN OF THE
            // BUILDING ANCESTOR NODE, IF IT EXISTS.
            //
            // A POINT IS "BOUND" IF ITS HAYSTACK TAGS MATCH THOSE OF THE AD FUNCTION TEMPLATE INPUT POINT
            //
            // EACH AD FUNCTION TEMPLATE INPUT POINT MUST BE "BOUND" WITH EQUIPMENT POINTS ACCORDING 
            // TO THE FOLLOWING CHARACTERISTICS (WHERE LIST OF POINTS IS DETERMINED BY CURR OBJ EXPR):
            // ---------------------------------------------------------------------------------------
            // 00: NON-ARRAY AND  NON-REQUIRED: ZERO OR ONE BOUND POINT CAN EXIST (TOO MANY ARE DIS-QUALIFYING)
            // 01: NON-ARRAY AND      REQUIRED: ONE AND ONLY ONE BOUND POINT MUST EXIST (TOO MANY ARE DIS-QUALIFYING)
            // 10:     ARRAY AND  NON-REQUIRED: ZERO OR ONE OR MORE BOUND POINTS CAN EXIST 
            // 11:     ARRAY AND      REQUIRED: ONE OR MORE BOUND POINTS MUST EXIST (NONE ARE DIS-QUALIFYING)
            //
            // KEEP TRACK OF THE NEWLY EVALUATED BOUND POINTS, SO THAT WE CAN COMPARE THEM AGAINST THE CURRENTLY 
            // PERSISTED BOUND POINTS, AS WE MAY NEED TO POSSIBLY:
            // 1. DELETE THE AD FUNCTION INSTANCE CANDIDATE/INSTANCE
            // 2. DELETE AN INPUT POINT ASSOCIATED WITH AN AD FUNCTION INSTANCE
            // 3. ADD AN INPUT POINT TO AN AD FUNCTION INSTANCE
            //
            // NOTE: FOR AD FUNCTION INSTANCE CANDIDATES, WHEN IT COMES TO INPUT POINTS, IT IS EASIER TO JUST DELETE THE CANDIDATE
            // AND ALLOW IT TO BE AUTOMATICALLY RE-GENERATED
            
            Set<AdFunctionInstanceEligiblePoint> evaluatedBoundPoints = new TreeSet<>();
            Iterator<AdFunctionInstanceEligiblePoint> adFunctionInstanceEligiblePointsIterator = adFunctionInstanceEligiblePoints.iterator();
            while (adFunctionInstanceEligiblePointsIterator.hasNext()) {
              
              AdFunctionInstanceEligiblePoint adFunctionInstanceEligiblePoint = adFunctionInstanceEligiblePointsIterator.next();

              if (adFunctionInstance != null) {
                
                if (remediate) {
                  entities.put("point", adFunctionInstanceEligiblePoint);
                }
              }
              
              // RP-10488: If a point has been marked as deleted, then ignore it when evaluating for haystack tags.
              Set<String> inputPointHaystackTags = null;
              if (!adFunctionInstanceEligiblePoint.getIsDeleted()) {
                inputPointHaystackTags = adFunctionInstanceEligiblePoint.getHaystackTags();  
              } else {
                inputPointHaystackTags = new HashSet<>();
              }
              
              if (templateInputPointTags.equals(inputPointHaystackTags)) {
                evaluatedBoundPoints.add(adFunctionInstanceEligiblePoint);
              }
            }
            
            if (!isArray && evaluatedBoundPoints.size() > 1) {
              
              // RP-7798: AdFunctionEvaluator: When multiple bound points exist for a scalar template input point, use the first bound point instead of rejecting the equipment.
              AdFunctionInstanceEligiblePoint firstPoint = evaluatedBoundPoints.iterator().next();
              evaluatedBoundPoints.clear();
              evaluatedBoundPoints.add(firstPoint);
              if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("More than one bound point was found for scalar template input point: [{}] on equipment: [{}], using first point only: [{}]",
                    templateInputPoint,
                    equipment,
                    firstPoint);
              }
              /*
              isInvalidForEquipment = true;
              if (adFunctionInstance != null) {
                
                IssueType issueType = IssueType.AD_FUNCTION_EQUIPMENT_HAS_TOO_MANY_BOUND_POINTS_FOR_SCALAR_INPUT;
                if (issueTypes.contains(issueType)) {
                  RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                      issueType, 
                      adFunctionInstance);
                  if (remediationStrategy != null) {
                   
                    validationMessages.add(ValidationMessage.builder()
                        .withIssueType(issueType)
                        .withDetails("Too many bound points: ["
                            + evaluatedBoundPoints.size()
                            + "] exist for scalar AD function template input point: ["
                            + templateInputPoint.getName()
                            + "] with tags: "
                            + templateInputPointTags
                            + " (there can only be 1 bound point)")    
                        .withEntityType(adFunctionInstance.getClass().getSimpleName())
                        .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                        .withRemediationDescription(remediationStrategy.getRemediationDescription())
                        .withRemediationStrategy(remediationStrategy)
                        .build());
                    
                    if (remediate) {
                      remediationStrategy.remediate(entities);
                    }                
                  }
                }
                break;            
              }
              */
            }
            
            if (isRequired && evaluatedBoundPoints.isEmpty()) {
              
              isInvalidForEquipment = true;
              errorMessages.add(templateInputPointId);
              if (adFunctionInstance != null) {
                
                IssueType issueType = IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_HAVE_ANY_BOUND_POINTS_FOR_REQUIRED_INPUT;
                if (issueTypes.contains(issueType)) {
                  RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                      issueType, 
                      adFunctionInstance);
                  if (remediationStrategy != null) {

                    validationMessages.add(ValidationMessage.builder()
                        .withIssueType(issueType)
                        .withDetails("No bound points found for required AD function template input point: ["
                            + templateInputPoint.getName()
                            + "] with tags: " 
                            + templateInputPointTags)    
                        .withEntityType(adFunctionInstance.getClass().getSimpleName())
                        .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                        .withRemediationDescription(remediationStrategy.getRemediationDescription())
                        .withRemediationStrategy(remediationStrategy)
                        .build());
                    
                    if (remediate) {
                      remediationStrategy.remediate(entities);
                    }                
                  }
                }
                break;
              }
            }           

            if (!evaluatedBoundPoints.isEmpty()) {
              
              totalNumberOfBoundPoints = totalNumberOfBoundPoints + evaluatedBoundPoints.size();
              allBoundPointIds.put(templateInputPointId, extractPointIds(evaluatedBoundPoints));
              allBoundPoints.put(templateInputPointId, evaluatedBoundPoints);
              boundPointNames.add(templateInputPoint.getName());
            }
          }
          
          // PASS TWO FOR POINT ELIGIBILITY: ONLY NEEDED FOR PERSISTED AD FUNCTION INSTANCE CANDIDATES/INSTANCES: 
          // REMOVE POINTS THAT NO LONGER EVALUATE TO TRUE AND 
          // ADD NEW POINTS THAT HAVE BEEN EVALUATED TO TRUE (FOR EQUIPMENT/AD FUNCTION TEMPLATE PAIR)
          // ONLY DO THIS IF WE HAVEN'T ALREADY DECIDED TO DELETE THE AD FUNCTION INSTANCE
          if (!isInvalidForEquipment && adFunctionInstance != null) {

            templateInputPointsIterator = templateInputPoints.iterator();
            while (templateInputPointsIterator.hasNext()) {
              
              AdFunctionTemplateInputPointEntity templateInputPoint = templateInputPointsIterator.next();
              
              Integer templateInputPointId = templateInputPoint.getPersistentIdentity();
              if (adFunctionInstance != null) {
                
                if (remediate) {
                  entities.put("function_template_input_point", templateInputPoint);
                }
              }
              
              Boolean isRequired = templateInputPoint.getIsRequired();
              Boolean isArray = templateInputPoint.getIsArray();
              Set<String> templateInputPointTags = templateInputPoint.getNormalizedTagsAsSet();
              String currentObjectExpression = templateInputPoint.getCurrentObjectExpression();

              // Any point that is in the currently persisted set that is NOT in the newly evaluated set is to be removed.
              Set<AdFunctionInstanceEligiblePoint> persistedBoundPoints = allPersistedBoundPoints.get(templateInputPoint);
              
              // Not all AD function template input points have to be bound to points (i.e. optional).
              if (persistedBoundPoints == null) {
                persistedBoundPoints = new HashSet<>();
              }
              
              // Any point that has been evaluated to true, but is not currently persisted will be added.
              Set<AdFunctionInstanceEligiblePoint> evaluatedPoints = allBoundPoints.get(templateInputPointId);
              if (evaluatedPoints == null) {
                evaluatedPoints = new HashSet<>();
              }
              
              Set<AdFunctionInstanceEligiblePoint> persistedBoundPointsVictimList = new HashSet<AdFunctionInstanceEligiblePoint>();
              Iterator<AdFunctionInstanceEligiblePoint> currentlyPersistedPointIterator = persistedBoundPoints.iterator();
              while (currentlyPersistedPointIterator.hasNext()) {
                
                AdFunctionInstanceEligiblePoint currentlyPersistedPoint = currentlyPersistedPointIterator.next();
                
                if (remediate) {
                  entities.put("point", currentlyPersistedPoint);
                }
                
                // DETERMINE WHY THE POINT IS NO LONGER VALID FOR THE AD FUNCTION INSTANCE.
                if (!evaluatedPoints.contains(currentlyPersistedPoint)) {
                  
                  // FIRST, REMOVE THE INVALID POINT FROM THE SET OF PERSISTED POINTS, AS THE INVALID POINT IS TO BE
                  // DELETED, EITHER OUTRIGHT, OR BY VIRTUE OF THE INSTANCE BEING DELETED.
                  // NOTE: WE HAVE TO REMOVE THE POINT AFTER WE FINISH ITERATING IN ORDER TO AVOID CONCURRENT EXCEPTION
                  persistedBoundPointsVictimList.add(currentlyPersistedPoint);
                  
                  Set<String> pointHaystackTags = currentlyPersistedPoint.getHaystackTags();
                  if (!templateInputPointTags.equals(pointHaystackTags)) {
                    
                    // 00: NON-ARRAY AND NON-REQUIRED: ZERO OR ONE BOUND POINT CAN EXIST (TOO MANY ARE DIS-QUALIFYING).
                    if (!isArray && !isRequired) {
                      
                      IssueType issueType = IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_SCALAR_INPUT;
                      if (issueTypes.contains(issueType)) {
                        RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                            issueType, 
                            adFunctionInstance);
                        if (remediationStrategy != null) {

                          validationMessages.add(ValidationMessage.builder()
                              .withIssueType(issueType)
                              .withDetails("Point: [" 
                                  + currentlyPersistedPoint.getPersistentIdentity() 
                                  + "] has haystack tags: " 
                                  + currentlyPersistedPoint.getHaystackTags() 
                                  + ", but optional scalar template input point: [" 
                                  + templateInputPoint.getName()
                                  + "] has tags: " 
                                  + templateInputPointTags)    
                              .withEntityType(adFunctionInstance.getClass().getSimpleName() + "InputPoint")
                              .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                              .withRemediationDescription(remediationStrategy.getRemediationDescription())
                              .withRemediationStrategy(remediationStrategy)
                              .build());
                          
                          if (remediate) {
                            remediationStrategy.remediate(entities);
                          }
                        }
                      }
                      
                     // 01: NON-ARRAY AND IS-REQUIRED 
                    } else if (!isArray && isRequired) {
                      
                      IssueType issueType = IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_SCALAR_INPUT;
                      if (issueTypes.contains(issueType)) {
                        RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                            issueType, 
                            adFunctionInstance);
                        if (remediationStrategy != null) {

                          validationMessages.add(ValidationMessage.builder()
                              .withIssueType(issueType)
                              .withDetails("Point: [" 
                                  + currentlyPersistedPoint.getPersistentIdentity() 
                                  + "] has haystack tags: " 
                                  + currentlyPersistedPoint.getHaystackTags() 
                                  + ", but required scalar template input point: [" 
                                  + templateInputPoint.getName()
                                  + "] has tags: " 
                                  + templateInputPointTags)    
                              .withEntityType(adFunctionInstance.getClass().getSimpleName() + "InputPoint")
                              .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                              .withRemediationDescription(remediationStrategy.getRemediationDescription())
                              .withRemediationStrategy(remediationStrategy)
                              .build());
                          
                          if (remediate) {
                            remediationStrategy.remediate(entities);
                          }
                        }
                      }                      
                     
                    // 10: IS-ARRAY AND NON-REQUIRED  
                    } else if (isArray && !isRequired) {
                      
                      IssueType issueType = IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_ARRAY_INPUT;
                      if (issueTypes.contains(issueType)) {
                        RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                            issueType, 
                            adFunctionInstance);
                        if (remediationStrategy != null) {

                          validationMessages.add(ValidationMessage.builder()
                              .withIssueType(issueType)
                              .withDetails("Point: [" 
                                  + currentlyPersistedPoint.getPersistentIdentity() 
                                  + "] has haystack tags: " 
                                  + currentlyPersistedPoint.getHaystackTags() 
                                  + ", but optional array template input point: ["
                                  + templateInputPoint.getName()
                                  + "] has tags: " 
                                  + templateInputPointTags)    
                              .withEntityType(adFunctionInstance.getClass().getSimpleName() + "InputPoint")
                              .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                              .withRemediationDescription(remediationStrategy.getRemediationDescription())
                              .withRemediationStrategy(remediationStrategy)
                              .build());
                          
                          if (remediate) {
                            remediationStrategy.remediate(entities);
                          }
                        }
                      }
                      
                    // 11: IS-ARRAY AND IS-REQUIRED
                    } else if (isArray && isRequired) {
                      
                      IssueType issueType = IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_GT_1;
                      if (issueTypes.contains(issueType)) {
                        RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                            issueType, 
                            adFunctionInstance);
                        if (remediationStrategy != null) {

                          validationMessages.add(ValidationMessage.builder()
                              .withIssueType(issueType)
                              .withDetails("Point: [" 
                                  + currentlyPersistedPoint.getPersistentIdentity() 
                                  + "] has haystack tags: " 
                                  + currentlyPersistedPoint.getHaystackTags() 
                                  + ", but required array template input point: [" 
                                  + templateInputPoint.getName()
                                  + "] has tags: " 
                                  + templateInputPointTags)    
                              .withEntityType(adFunctionInstance.getClass().getSimpleName() + "InputPoint")
                              .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                              .withRemediationDescription(remediationStrategy.getRemediationDescription())
                              .withRemediationStrategy(remediationStrategy)
                              .build());
                          
                          if (remediate) {
                            remediationStrategy.remediate(entities);
                          }
                        }
                      }
                    }
                  // TAGS MATCH, BUT POINT IS NO LONGER REACHABLE VIA CURRENT OBJECT EXPRESSION.  
                  } else {

                    if (remediate) {
                      entities.put("point", currentlyPersistedPoint);
                    }
                    
                    IssueType issueType = IssueType.AD_FUNCTION_INPUT_POINT_IS_NO_LONGER_ELIGIBLE;
                    if (issueTypes.contains(issueType)) {
                      RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                          issueType, 
                          adFunctionInstance);
                      if (remediationStrategy != null) {
 
                        validationMessages.add(ValidationMessage.builder()
                            .withIssueType(issueType)
                            .withDetails("Point: [" 
                                + currentlyPersistedPoint.getPersistentIdentity() 
                                + "] with haystack tags: " 
                                + currentlyPersistedPoint.getHaystackTags() 
                                + " has been evaluated to no longer match (unreachable) with template input point: ["
                                + templateInputPoint.getName()
                                + " with haystack tags: "
                                + templateInputPointTags
                                + " isRequired: ["
                                + isRequired
                                + "], isArray: ["
                                + isArray
                                + "], currentObjectExpression: ["
                                + currentObjectExpression
                                + "]")    
                            .withEntityType(adFunctionInstance.getClass().getSimpleName() + "InputPoint")
                            .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                            .withRemediationDescription(remediationStrategy.getRemediationDescription())
                            .withRemediationStrategy(remediationStrategy)
                            .build());
                        
                        if (remediate) {
                          remediationStrategy.remediate(entities);
                        }                        
                      }
                    }
                  }
                }
              }
              // REMOVE ANY INVALID POINTS THAT WE HAVE REMOVED FROM THE SET THAT WE USE FOR EVALUATION
              // IN ORDER TO BE LESS CONFUSING WHEN DEBUGGING.
              persistedBoundPoints.removeAll(persistedBoundPointsVictimList);
              
              // We only care about adding new points to an AD function instance if the instance itself has not been deleted and there is a
              // mis-match between the evaluated points and the persisted points for a given AD function template input point. 
              Iterator<AdFunctionInstanceEligiblePoint> newlyEvaluatedPointIterator = evaluatedPoints.iterator();
              while (newlyEvaluatedPointIterator.hasNext()) {
                
                AdFunctionInstanceEligiblePoint newlyEvaluatedPoint = newlyEvaluatedPointIterator.next();

                if (remediate) {
                  entities.put("point", newlyEvaluatedPoint);
                }
                
                if (!persistedBoundPoints.contains(newlyEvaluatedPoint)) {

                  // In addition, we can only have a new qualifying input point IF:
                  // A: The template input point is scalar and there is NOT ALREADY A BOUND POINT 
                  // (which would mean that this is an optional point)
                  // or
                  // B: The template input point is array 
                  //if (isArray || persistedBoundPoints.isEmpty()) {

                    IssueType issueType = IssueType.NEW_QUALIFYING_INPUT_POINT_FOR_AD_FUNCTION_AVAILABLE;
                    if (issueTypes.contains(issueType)) {
                      RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                          issueType, 
                          adFunctionInstance);
                      if (remediationStrategy != null) {

                        validationMessages.add(ValidationMessage.builder()
                            .withIssueType(issueType)
                            .withDetails("Point: [" 
                                + newlyEvaluatedPoint.getPersistentIdentity() 
                                + "] with haystack tags: " 
                                + newlyEvaluatedPoint.getHaystackTags() 
                                + " has been evaluated to match with template input point: ["
                                + templateInputPoint.getName()
                                + " with haystack tags: "
                                + templateInputPointTags
                                + " isRequired: ["
                                + isRequired
                                + "], isArray: ["
                                + isArray
                                + "], currentObjectExpression: ["
                                + currentObjectExpression
                                + "]")    
                            .withEntityType(adFunctionInstance.getClass().getSimpleName() + "InputPoint")
                            .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                            .withRemediationDescription(remediationStrategy.getRemediationDescription())
                            .withRemediationStrategy(remediationStrategy)
                            .build());
                        
                        if (remediate) {
                          remediationStrategy.remediate(entities);
                        }                  
                      }
                    }
                  //}
                }
              }
            }
          }
        }
      }
    }

    // IF THE EQUPMENT TYPE MATCHES, SEE IF THE TUPLE CONSTRAINT EXPRESSION IS SATISFIED.
    String tupleConstraintExpression = adFunctionTemplate.getTupleConstraintExpression();
    if (!isInvalidForEquipment && tupleConstraintExpression != null && !tupleConstraintExpression.trim().isEmpty()) {

      TagQueryExpression exp = TagQueryExpression.parse(tupleConstraintExpression);
      if (!exp.match(boundPointNames)) {

        isInvalidForEquipment = true;
        errorMessages.add(adFunctionTemplateId * -1);
        if (adFunctionInstance != null) {
          
          IssueType issueType = IssueType.AD_FUNCTION_POINT_TUPLE_CONSTRAINT_EXPRESSION_FALSE;
          if (issueTypes.contains(issueType)) {
            RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                issueType, 
                adFunctionInstance);
            if (remediationStrategy != null) {

              validationMessages.add(ValidationMessage.builder()
                  .withIssueType(IssueType.AD_FUNCTION_POINT_TUPLE_CONSTRAINT_EXPRESSION_FALSE)
                  .withDetails("Equipment: ["
                      + equipment.getPersistentIdentity()
                      + "] did not satisfy point tuple constraint expression: ["
                      + tupleConstraintExpression
                      + "] for AD function template: ["
                      + adFunctionTemplate.getPersistentIdentity()
                      + "], bound point names instead were: "
                      + boundPointNames)
                  .withEntityType(adFunctionInstance.getClass().getSimpleName())
                  .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                  .withRemediationDescription(remediationStrategy.getRemediationDescription())
                  .withRemediationStrategy(remediationStrategy)
                  .build());
              
              if (remediate) {
                remediationStrategy.remediate(entities);
              }
            }
          }
        }
      }
    }
    
    if (adFunctionTemplateId == DEBUG_FUNCTION_TEMPLATE_ID && equipmentId == DEBUG_EQUIPMENT_ID) {
      LOGGER.debug("isInvalidForEquipment: " + isInvalidForEquipment);
      LOGGER.debug("allPersistedBoundPoints: " + allPersistedBoundPoints);
      LOGGER.debug("allBoundPointIds: " + allBoundPointIds);
    }
    
    // Verify that the instance has the correct number of constants.  If it is missing any, then add a remediation to add the missing constant(s).  SO, we perform
    // remediation, but there is nothing wrong with the instance itself (other than the missing constant)
    // 
    // In addition, verify that the version of the instance, which originally came from the AD function template at the time of its creation, matches that of the 
    // AD function template *currently*.  If there is a mis-match, and we are here, the equipment is still valid for the updated AD function template, so all we
    // need to do is make sure that the constants are all accounted for (either need to be added/removed), so just do a copy on write using existing functionality.
    if (!isInvalidForEquipment
        && adFunctionInstance != null
        && !adFunctionInstance.getIsCandidate()) {
      
      // Verify constants only.
      if (adFunctionInstance.getInputConstants().size() != adFunctionTemplate.getInputConstants().size()) {
        
        Iterator<AdFunctionTemplateInputConstantEntity> iterator = adFunctionTemplate.getInputConstants().iterator();
        while (iterator.hasNext()) {
          
          AdFunctionTemplateInputConstantEntity templateConstant = iterator.next();
          AdFunctionInstanceInputConstantEntity instanceConstant = adFunctionInstance.getInputConstantByTemplateConstantId(templateConstant.getPersistentIdentity());
          if (instanceConstant == null) {
            
            IssueType issueType = IssueType.AD_FUNCTION_MISSING_INPUT_CONSTANT;
            if (issueTypes.contains(issueType)) {
              RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
                  issueType, 
                  adFunctionInstance);
              if (remediationStrategy != null) {
                validationMessages.add(ValidationMessage.builder()
                    .withIssueType(IssueType.AD_FUNCTION_MISSING_INPUT_CONSTANT)
                    .withDetails("AD Function Instance: ["
                        + adFunctionInstance 
                        + "] with id: [" 
                        + adFunctionInstance.getPersistentIdentity() 
                        + "] is missing input constant: [" 
                        + templateConstant.getName() 
                        + "]")
                    .withEntityType(adFunctionInstance.getClass().getSimpleName())
                    .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                    .withRemediationDescription(remediationStrategy.getRemediationDescription())
                    .withRemediationStrategy(remediationStrategy)
                    .build());
                if (remediate) {
                  entities.put("function_template_input_constant", templateConstant);
                  remediationStrategy.remediate(entities);
                }
              }
            }
          }
        }
      }
      
      // Verify AD template version match. If not, then do a copy on write and also remediate constants if needed.
      boolean isTemplateVersionOutOfDate = adFunctionInstance.isTemplateVersionOutOfDate(); 
      if (isTemplateVersionOutOfDate) {

        IssueType issueType = IssueType.VERSION_MISMATCH_MIGRATE_AD_FUNCTION_INSTANCE;
        if (issueTypes.contains(issueType)) {
          RemediationStrategy remediationStrategy = AdFunctionRemediationStrategyFinder.find(
              issueType, 
              adFunctionInstance);
          if (remediationStrategy != null) {
            validationMessages.add(ValidationMessage.builder()
                .withIssueType(IssueType.VERSION_MISMATCH_MIGRATE_AD_FUNCTION_INSTANCE)
                .withDetails("AD Function Instance: ["
                    + adFunctionInstance 
                    + "] with id: [" 
                    + adFunctionInstance.getPersistentIdentity() 
                    + "] is out of date with AD function template version: [" 
                    + adFunctionInstance.getAdFunctionTemplate().getVersion() 
                    + "], instance AD function template version: ["
                    + adFunctionInstance.getTemplateVersion()
                    + "]")
                .withEntityType(adFunctionInstance.getClass().getSimpleName())
                .withNaturalIdentity(adFunctionInstance.getNaturalIdentity())
                .withRemediationDescription(remediationStrategy.getRemediationDescription())
                .withRemediationStrategy(remediationStrategy)
                .build());
            if (remediate) {
              remediationStrategy.remediate(entities);
            }
          }
        }
      }
    }
    
    if (isInvalidForEquipment) {
      
      AdFunctionErrorMessagesDto red = null;
      if (!errorMessages.isEmpty()) {
        
        red = AdFunctionErrorMessagesDto
            .builder()
            .withAdFunctionTemplateId(adFunctionTemplateId)
            .withEnergyExchangeId(equipmentId)
            .withErrorMessages(errorMessages)
            .build();
      }
      
      return RedGreenDto
          .builder()
          .withRed(red)
          .build();
    }
    
    // RP-8297: When evaluating AD computed point function templates against equipment, do not 
    // create a "candidate" when there already exists a mappable point or custom point with a
    // point template assigned that matches that of the AD computed point function template.
    if (adFunctionTemplate instanceof AdComputedPointFunctionTemplateEntity) {
      
      Map<String, AbstractPointEntity> assignedPointTemplateHaystackTags = equipment.getAssignedPointTemplateHaystackTags();
      if (!assignedPointTemplateHaystackTags.isEmpty()) {
        
        for (AdFunctionTemplateOutputPointEntity templateOutputPoint: adFunctionTemplate.getOutputPoints()) {
          
          String normalizedTags = templateOutputPoint.getNormalizedTags();
          AbstractPointEntity point = assignedPointTemplateHaystackTags.get(normalizedTags);
          if (point != null) {

            LOGGER.debug("{}-{} already has point {} with tags {}",
                equipment,
                adFunctionTemplate,
                point.getClassAndNaturalIdentity(),
                normalizedTags);
            
            return RedGreenDto
                .builder()
                .build();
          }
        }
      }
    }
    
    return RedGreenDto
        .builder()
        .withGreen(AdFunctionInstanceCandidateBoundPointsDto
            .builder()
            .withBoundCandidatePoints(allBoundPointIds)
            .build())
        .build();
  }
  
  private static List<Integer> extractPointIds(Set<AdFunctionInstanceEligiblePoint> points) {
    
    List<Integer> pointIds = new ArrayList<>();
    Iterator<AdFunctionInstanceEligiblePoint> pointIterator = points.iterator();
    while (pointIterator.hasNext()) {
      pointIds.add(pointIterator.next().getPersistentIdentity());
    }
    return pointIds;
  }
}
//@formatter:on