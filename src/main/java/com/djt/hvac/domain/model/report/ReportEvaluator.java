//@formatter:off
// This class does the evaluation
package com.djt.hvac.domain.model.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.dsl.tagquery.TagQueryExpression;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.CurrentObjectExpression;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.AbstractReportTemplatePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEquipmentSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateRulePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateStandardPointSpecEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.rule.AdRuleFunctionInstanceEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

/**
 * <pre>
 * Basically, there will exist a report instance for every building/report template combination.
 * What will vary, however, will be its state/status with regards to being correctly configured
 * for all of it equipment/point specs.
 *
 * So, when we evaluate a given building/report template combination, we are seeing if we can add
 * additional equipment/point specs, or remove them, depending upon what has changed (e.g. equipment
 * can be added/removed, equipment types can change, points can be added/removed, points can be
 * re-tagged and rule instances can be activated/de-activated against equipment/rule templates)
 *
 * The report evaluation process iterates through all of the equipment for a given building as follows:
 *
 * 1. See if the equipment matches one of the report template equipment spec for:
 *    a. equipment type
 *
 *    b. node filter expression (i.e. equipment metadata)
 *
 *    c. zero or more bound point specs.  A bound point spec is defined as having all required points matched.
 *
 *    d. tuple constraint expression (i.e. to match against the existence/non-existence of bound point specs)
 *
 * 2. Has zero or more bound pieces of equipment:
 *    a. If zero bound equipment --OR-- no equipment has GREEN status, then the report will be
 *       instantiated with: isValid=false and isEnabled=false (i.e. "invalid" state)
 *
 *    b. If non-zero bound equipment --AND-- at least one equipment has GREEN status, then the report
 *       will be instantiated with: isValid=true and isEnabled=false (i.e. "disabled" state)
 *
 *       NOTES
 *       - It is only from the valid/disabled (disabled) state can the report be transitioned to
 *       the valid/enabled (enabled) state (by the user)
 *
 *       - The system will periodically re-evaluate every report instance to either add/remove
 *       bound equipment/points, meaning that it can move back and forth between being valid or
 *       invalid.  When invalid, it will always be disabled.  Then valid/disabled, then it is eligible
 *       to be transitioned to valid/enabled.
 *
 * REPORT STATE:
 * =============
 *              VALID/ENABLED ===== (state #2)   At least 1 GREEN Equipment - "enabled" state
 *                /\    ||       ||
 *                ||    \/       ||
 *   BEGIN-1:  VALID/DISABLED    || (state #1)   At least 1 GREEN Equipment - "disabled" state
 *                ||    /\       ||
 *                \/    ||       ||
 *   BEGIN-2: INVALID/DISABLED <=== (state #3)   Zero Equipment OR no GREEN Equipment - "invalid" state
 *
 *
 * REPORT STATUS: (derived/evaluated on-demand)
 * ============================================
 *   GREEN: 1 or more pieces of equipment where GREEN status is between 70-100% (inclusive)
 *  YELLOW: 1 or more pieces of equipment where GREEN status is between 0-70% (exclusive)
 *     RED: 0 or more pieces of equipment where either 0 pieces of equipment or NONE with GREEN status
 *
 *
 * EQUIPMENT STATUS: (derived/evaluated on-demand)
 * ===============================================
 *  GREEN: All required point specs have been matched
 *    RED: At least 1 required point spec has not been matched
 * </pre>
 * 
 * @author tommyers
 *
 */
public class ReportEvaluator {

  // Used for debugging.
  public static Integer DEBUG_REPORT_TEMPLATE_ID;
  public static Integer DEBUG_POINT_SPEC_ID;
  public static Integer DEBUG_EQUIPMENT_ID;
  public static Integer DEBUG_POINT_ID;

  private static final Logger LOGGER = LoggerFactory.getLogger(ReportEvaluator.class);

  // What we need to do here is evaluate the report template against the building to see:
  // 1. If it moved between valid and invalid state
  // (NOTE: If going from valid to invalid, the report is disabled if enabled)
  // 2. If any equipment are to be added/removed
  // 3. If any points are to be added/removed
  //
  // NOTE: equipmentMap is only used for reformatting the point tuple constraint with available 
  // bound point spec names.  Normally, it will be null.
  public static void evaluate(
      Map<AbstractEnergyExchangeTypeEntity, Set<EnergyExchangeEntity>> allDescendantEquipmentByType,
      ReportInstanceEntity reportInstance,
      Map<Integer, String> equipmentMap) {

    try {
      
      Set<ReportInstanceEquipmentEntity> greenEquipmentSet = new TreeSet<>();
      Set<ReportInstanceEquipmentErrorMessagesEntity> redEquipmentSet = new TreeSet<>();

      ReportTemplateEntity reportTemplate = reportInstance.getReportTemplate();
      //Integer reportTemplateId = reportTemplate.getPersistentIdentity();
      //if (DEBUG_REPORT_TEMPLATE_ID != null && DEBUG_REPORT_TEMPLATE_ID.equals(reportTemplateId)) {
      //  if (LOGGER.isDebugEnabled()) {
      //    LOGGER.debug("DEBUG_REPORT_TEMPLATE_ID");
      //  }
      //}
      
      for (ReportTemplateEquipmentSpecEntity equipmentSpec: reportTemplate.getEquipmentSpecs()) {

        AbstractEnergyExchangeTypeEntity equipmentSpecEquipmentType = equipmentSpec.getEnergyExchangeType();

        Set<EnergyExchangeEntity> validByTypeEquipment = allDescendantEquipmentByType.get(equipmentSpecEquipmentType);
        if (validByTypeEquipment != null && !validByTypeEquipment.isEmpty()) {

          for (EnergyExchangeEntity equipment: validByTypeEquipment) {

            Integer equipmentId = equipment.getPersistentIdentity();
            
            //if (DEBUG_EQUIPMENT_ID != null && DEBUG_EQUIPMENT_ID.equals(equipmentId)) {
            //  if (LOGGER.isDebugEnabled()) {
            //    LOGGER.debug("DEBUG_EQUIPMENT_ID");
            //  }
            //}

            ReportInstanceEquipmentEntity greenEquipment = null;
            List<Integer> errorMessages = new ArrayList<>();

            AbstractEnergyExchangeTypeEntity equipmentEquipmentType = equipment.getEnergyExchangeTypeNullIfNotExists();

            // STEP 1: EQUIPMENT/PLANT/LOOP TYPE MUST EXIST AND MATCH THE TYPE SPECIFIED BY THE REPORT TEMPLATE
            if (equipmentEquipmentType.equals(equipmentSpecEquipmentType)) {

              // STEP 2: EQUIPMENT ELIGIBILITY VIA NODE FILTER EXPRESSION (METADATA TAG MATCHING)
              boolean isValidForNodeFilterExpression = true;
              String nodeFilterExpression = equipmentSpec.getNodeFilterExpression();
              if (nodeFilterExpression != null && !nodeFilterExpression.trim().isEmpty()) {

                Set<String> metadataTags = equipment.getMetadataTags();
                TagQueryExpression exp = TagQueryExpression.parse(nodeFilterExpression);
                if (!exp.match(metadataTags)) {
                  isValidForNodeFilterExpression = false;
                }
              }

              // Get any matching/qualifying points.  At the end, we then evaluate the tuple constraint expression.
              if (isValidForNodeFilterExpression) {

                greenEquipment = new ReportInstanceEquipmentEntity(
                    reportInstance,
                    equipmentSpec,
                    equipment);

                Set<String> boundPointSpecNames = null;
                Set<String> missingPointSpecNames = null;
                if (equipmentMap != null && equipmentMap.keySet().contains(equipmentId)) {
                  boundPointSpecNames = new TreeSet<>();
                  missingPointSpecNames = new TreeSet<>();
                } else {
                  boundPointSpecNames = new HashSet<>();
                }
                for (AbstractReportTemplatePointSpecEntity pointSpec: equipmentSpec.getPointSpecs()) {

                  Integer pointSpecId = pointSpec.getPersistentIdentity();
                  //if (DEBUG_POINT_SPEC_ID != null && DEBUG_POINT_SPEC_ID.equals(pointSpecId)) {
                  //  if (LOGGER.isDebugEnabled()) {
                  //    LOGGER.debug("DEBUG_POINT_SPEC_ID");
                  //  }
                  //}
                  
                  String pointSpecName = pointSpec.getName();
                  
                  if (missingPointSpecNames != null) {
                    missingPointSpecNames.add(pointSpecName);  
                  }
                  
                  boolean isRequired = pointSpec.isRequired();
                  boolean isArray = pointSpec.isArray();

                  if (pointSpec instanceof ReportTemplateStandardPointSpecEntity) {

                    String currentObjectExpression = pointSpec.getCurrentObjectExpression();

                    Set<AbstractPointEntity> points = CurrentObjectExpression.getPoints(
                        equipment,
                        currentObjectExpression);

                    Set<String> standardPointSpecHaystackTags = ((ReportTemplateStandardPointSpecEntity) pointSpec).getHaystackTags();

                    Set<AbstractPointEntity> evaluatedBoundPoints = new TreeSet<>();
                    Iterator<AbstractPointEntity> pointsIterator = points.iterator();
                    while (pointsIterator.hasNext()) {

                      AbstractPointEntity point = pointsIterator.next();
                      //Integer pointId = point.getPersistentIdentity();
                      //if (DEBUG_POINT_ID != null && DEBUG_POINT_ID.equals(pointId)) {
                      //  if (LOGGER.isDebugEnabled()) {
                      //    LOGGER.debug("DEBUG_POINT_ID");
                      //  }
                      //}

                      if (!point.getMetricId().startsWith("/Async/Rule")) {

                        // RP-10479 and RP-11212: We ignore points that have been marked for deletion 
                        // or are associated with inactive AD computed point function instances.
                        if (!point.getIsDeleted() 
                            && !point.getDisplayName().endsWith("_inactive")
                            && standardPointSpecHaystackTags.equals(point.getHaystackTags())) {
                          
                          evaluatedBoundPoints.add(point);
                        }
                      }
                    }

                    if (!isArray && evaluatedBoundPoints.size() > 0) {

                      boundPointSpecNames.add(pointSpecName);
                      if (missingPointSpecNames != null) {
                        missingPointSpecNames.remove(pointSpecName);
                      }
                      Iterator<AbstractPointEntity> pointIterator = evaluatedBoundPoints.iterator();
                      while (pointIterator.hasNext()) {

                        AbstractPointEntity point = pointIterator.next();

                        if (pointSpec.getRequiredDataTypeId() != null && point.getDataType() != DataType.get(pointSpec.getRequiredDataTypeId())) {
                          if (isRequired) {
                            errorMessages.add(pointSpecId);
                          }
                        } else {
                          ReportInstancePointEntity reportInstancePoint = null;
                          try {
                            reportInstancePoint = new ReportInstancePointEntity(
                                greenEquipment,
                                equipmentSpec.getPointSpec(pointSpecId),
                                point);

                            greenEquipment.addReportInstancePoint(reportInstancePoint);
                          } catch (Exception e) {
                            throw new IllegalStateException("Unable to add report instance point: " + reportInstancePoint, e);
                          }
                        }
                      }
                    } else if (evaluatedBoundPoints.isEmpty() && isRequired) {

                      errorMessages.add(pointSpecId);
                    }

                  } else if (pointSpec instanceof ReportTemplateRulePointSpecEntity) {

                    // Look for a rule instance that is a match for the given rule template.
                    boolean foundRulePoint = false;
                    AdRuleFunctionTemplateEntity ruleTemplate = ((ReportTemplateRulePointSpecEntity) pointSpec).getRuleTemplate();
                    
                    String currentObjectExpression = pointSpec.getCurrentObjectExpression();

                    Set<AbstractAdFunctionInstanceEntity> adFunctionInstances = CurrentObjectExpression.getAdFunctionInstances(
                        equipment,
                        currentObjectExpression);
                    
                    Iterator<AbstractAdFunctionInstanceEntity> adFunctionInstanceIterator = adFunctionInstances.iterator();
                    while (adFunctionInstanceIterator.hasNext()) {

                      AbstractAdFunctionInstanceEntity adFunctionInstance = adFunctionInstanceIterator.next();

                      if (adFunctionInstance instanceof AdRuleFunctionInstanceEntity) {

                        AdRuleFunctionInstanceEntity ruleInstance = (AdRuleFunctionInstanceEntity)adFunctionInstance;

                        // RP-10479: Duplicate Report Instance Point SQL Exception when performing auto-evaluate.
                        // We ignore points that have been marked for deletion.
                        if (adFunctionInstance.getAdFunctionTemplate().getPersistentIdentity().equals(ruleTemplate.getPersistentIdentity())
                            && !adFunctionInstance.getIsDeleted()) {

                          foundRulePoint = true;
                          boundPointSpecNames.add(pointSpecName);
                          if (missingPointSpecNames != null) {
                            missingPointSpecNames.remove(pointSpecName);
                          }

                          AbstractPointEntity point = ruleInstance.getOutputPoint().getPoint();
                          //Integer pointId = point.getPersistentIdentity();
                          //if (DEBUG_POINT_ID != null && DEBUG_POINT_ID.equals(pointId)) {
                          //  if (LOGGER.isDebugEnabled()) {
                          //    LOGGER.debug("DEBUG_POINT_ID");
                          //  }
                          //}

                          ReportInstancePointEntity reportInstancePoint = null;
                          try {
                            reportInstancePoint = new ReportInstancePointEntity(
                                greenEquipment,
                                equipmentSpec.getPointSpec(pointSpecId),
                                point);

                            greenEquipment.addReportInstancePoint(reportInstancePoint);
                          } catch (Exception e) {
                            throw new IllegalStateException("Unable to add report instance point: " + reportInstancePoint, e);
                          }
                          break;
                        }
                      }
                    }

                    if (!foundRulePoint && isRequired) {

                      errorMessages.add(pointSpecId);
                    }

                  } else {
                    throw new IllegalStateException("Unsupported point spec: " + pointSpec.getClassAndNaturalIdentity());
                  }
                }

                // CHECK THE TUPLE CONSTRAINT EXPRESSION
                String tupleConstraintExpression = equipmentSpec.getTupleConstraintExpression();
                if (tupleConstraintExpression != null && !tupleConstraintExpression.trim().isEmpty()) {

                  TagQueryExpression exp = TagQueryExpression.parse(tupleConstraintExpression);
                  if (!exp.match(boundPointSpecNames)) {

                    errorMessages.add(Integer.valueOf(equipmentSpec.getPersistentIdentity().intValue() * -1));
                    
                    if (equipmentMap != null && equipmentMap.keySet().contains(equipmentId)) {
                      
                      Comparator<String> comparator = new Comparator<String>() {

                        @Override
                        public int compare(String o1, String o2) {
                          if (o1.length() < o2.length()) {
                            return 1;
                          } else if (o1.length() == o2.length()) {
                            return 0;
                          }
                          return -1;
                        }
                      };
                      
                      List<String> pointSpecNames = new ArrayList<>();
                      pointSpecNames.addAll(missingPointSpecNames);
                      pointSpecNames.addAll(boundPointSpecNames);
                      Collections.sort(pointSpecNames, comparator);
                      
                      String tupleConstraintErrorMessage = equipmentSpec.getTupleConstraintExpression();
                      
                      if (tupleConstraintErrorMessage.equals("(((OaHumidity && OaTemp) || (OffPremOaTemp && OffPremOaHumidity)) && ((OaFlow && DaFlow && OaFlowMinSp) || (OaDmprMinPosSp && (OaDmprCmd || (MaTemp && RaTemp)))) && (ColdDeckTempSp || ColdDeckTemp || DaTempSp || DaTemp))")) {
                        
                        tupleConstraintErrorMessage = "(<br>" +
                            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;((OaHumidity && OaTemp) || (OffPremOaTemp && OffPremOaHumidity))<br>" + 
                            "&nbsp; && ((OaFlow && DaFlow && OaFlowMinSp) || (OaDmprMinPosSp && (OaDmprCmd || (MaTemp && RaTemp))))<br>" +
                            "&nbsp; && (ColdDeckTempSp || ColdDeckTemp || DaTempSp || DaTemp)<br>" +
                            ")<br>"; 
                      }
                      
                      tupleConstraintErrorMessage = tupleConstraintErrorMessage
                          .replace("&&", "AND")
                          .replace("||", "OR");
                      
                      for (String pointSpecName: pointSpecNames) {
                        if (boundPointSpecNames.contains(pointSpecName)) {

                          tupleConstraintErrorMessage = tupleConstraintErrorMessage
                              .replace(
                                  pointSpecName,
                                  "<font color='#00AA00'>" 
                                  + pointSpecName.toUpperCase()
                                  + "</font>");
                          
                        } else {

                          tupleConstraintErrorMessage = tupleConstraintErrorMessage
                              .replace(
                                  pointSpecName,
                                  "<font color='#FF0000'>" 
                                  + pointSpecName.toUpperCase()
                                  + "</font>");
                          
                        }
                      }
                      
                      for (String pointSpecName: pointSpecNames) {
                        if (boundPointSpecNames.contains(pointSpecName)) {

                          tupleConstraintErrorMessage = tupleConstraintErrorMessage
                              .replace(
                                  pointSpecName.toUpperCase(),
                                  "<font color='#00AA00'>" 
                                  + pointSpecName
                                  + "</font>");
                          
                        } else {

                          tupleConstraintErrorMessage = tupleConstraintErrorMessage
                              .replace(
                                  pointSpecName.toUpperCase(),
                                  "<font color='#FF0000'>" 
                                  + pointSpecName
                                  + "</font>");
                          
                        }
                      }
                      
                      equipmentMap.put(equipmentId, tupleConstraintErrorMessage);
                    }
                  }
                }

                // There are two scenarios for point evaluation:
                // 1: The equipment matched for required points
                // 2: The equipment did not match for any required points,
                // so thus, one or more error messages where added.
                //
                // If error messages are empty, then we have a fully matched equipment,
                // with regards to point spec matching, otherwise, there were errors,
                // so we log them and put them in a map, keyed by equipment id.
                //
                // Lastly, like rules, there needs to be at least one bound point, so if we
                // don't have any points, then add an error message for the equipment. It must
                // be noted that it may be an issue with the report template definition if we
                // get to this scenario.
                if (boundPointSpecNames.isEmpty() && errorMessages.isEmpty()) {
                  throw new IllegalStateException("Expected at least one matching point for node: ["
                      + equipment.getNodePath()
                      + "] and report template: ["
                      + reportTemplate.getName()
                      + "]");
                }

                // At this point, if no error messages, then we have a GREEN equipment
                if (errorMessages.isEmpty()) {

                  // GREEN EQUIPMENT
                  greenEquipmentSet.add(greenEquipment);

                } else {

                  // RED EQUIPMENT
                  ReportInstanceEquipmentErrorMessagesEntity redEquipment = new ReportInstanceEquipmentErrorMessagesEntity(
                      reportInstance,
                      equipment,
                      errorMessages);
                  redEquipmentSet.add(redEquipment);
                }
              }
            }
          }
        }
      }

      reportInstance.update(greenEquipmentSet, redEquipmentSet);

      if (LOGGER.isDebugEnabled()) {

        LOGGER.debug("\n\n{}\nGREEN: {}\nRED: {}\nTOTAL: {}\nisValid: {}\nisEnabled: {}\nneedsDisabling: {}\nisModified: {}\n",
            reportInstance,
            reportInstance.getNumEquipmentInGreenStatus(),
            reportInstance.getNumEquipmentInRedStatus(),
            reportInstance.getNumEquipmentTotal(),
            reportInstance.isValid(),
            reportInstance.isEnabled(),
            reportInstance.getNeedsDisabling(),
            reportInstance.getIsModified());
      }

    } catch (Exception e) {

      reportInstance.setIsModified("unable_to_evaluate");

      LOGGER.error("Unable to evaluate report instance: [{}], invalidating.  Error: {}",
          reportInstance,
          e.getMessage(), 
          e);
    }
  }
}
//@formatter:on