//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.computedpoint.AdComputedPointFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionErrorMessagesEntity;
import com.djt.hvac.domain.model.function.AdFunctionEvaluator;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.function.AdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.function.computedpoint.AdComputedPointFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceCandidateBoundPointsDto;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.function.dto.RedGreenDto;
import com.djt.hvac.domain.model.function.rule.AdRuleFunctionInstanceEntity;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

public class FindAdFunctionInstanceCandidatesVisitor {

  List<AdFunctionInstanceDto> findAdFunctionInstanceCandidates(
      PortfolioEntity portfolio,
      FunctionType functionType,
      Set<Integer> buildingIdsToProcess) {

    List<AdFunctionInstanceDto> adFunctionInstanceCandidates = new ArrayList<>();
    Iterator<AbstractNodeEntity> nodeIterator = portfolio.getAllNodes().iterator();
    while (nodeIterator.hasNext()) {

      AbstractNodeEntity node = nodeIterator.next();
      
      BuildingEntity building = node.getAncestorBuilding();
      Integer buildingId = building.getPersistentIdentity();
      
      if (node instanceof EnergyExchangeEntity && (buildingIdsToProcess == null || buildingIdsToProcess.contains(buildingId))) {

        EnergyExchangeEntity energyExchangeEntity = (EnergyExchangeEntity)node;
        Integer energyExchangeEntityId = energyExchangeEntity.getPersistentIdentity();
        
        Set<Integer> boundAdFunctionTemplateIds = energyExchangeEntity.getBoundAdFunctionTemplateIds();
        
        List<AbstractAdFunctionTemplateEntity> adFunctionTemplates = DictionaryContext
            .getAdFunctionTemplatesContainer()
            .getAdFunctionTemplates(functionType);

        for (AbstractAdFunctionTemplateEntity adFunctionTemplate: adFunctionTemplates) {

          if (AdFunctionEvaluator.INCLUDE_BETA_FUNCTION_TEMPLATES 
              || !(adFunctionTemplate.getIsBeta() != null && adFunctionTemplate.getIsBeta())) {

            Integer adFunctionTemplateId = adFunctionTemplate.getPersistentIdentity();
            if (!boundAdFunctionTemplateIds.contains(adFunctionTemplateId)) {

              RedGreenDto redGreenDto = AdFunctionEvaluator.evaluateForBoundPoints(energyExchangeEntity, adFunctionTemplate);
              AdFunctionInstanceCandidateBoundPointsDto boundCandidatePoints = redGreenDto.getGreen();
              if (boundCandidatePoints != null) {
                
                energyExchangeEntity.removeAdFunctionErrorMessages(adFunctionTemplate);
                    
                String candidateJson = null;
                try {
                  candidateJson = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(boundCandidatePoints);
                } catch (JsonProcessingException e) {
                  throw new IllegalStateException(
                      "Unable to marshall bound candidate points as JSON: " + boundCandidatePoints, e);
                }

                AdFunctionInstanceDto dto = new AdFunctionInstanceDto();
                dto.setEquipmentId(energyExchangeEntityId);
                dto.setNodePath(energyExchangeEntity.getNodePath());
                dto.setTemplateId(adFunctionTemplate.getPersistentIdentity());
                dto.setAdFunctionTemplateDescription(adFunctionTemplate.getNaturalIdentity());
                dto.setAdFunctionType(adFunctionTemplate.getAdFunction().getFunctionType().getName());
                dto.setCandidateJson(candidateJson);
                adFunctionInstanceCandidates.add(dto);
                
                try {
                  
                  // Instantiate the candidate entity, as the new method will involve saving the entity graph
                  // directly to the repository, as opposed to returning a DTO list of things to save as is done now.
                  AbstractAdFunctionInstanceEntity candidate = null;
                  if (functionType != null && functionType.equals(FunctionType.RULE)
                      || adFunctionTemplate.getAdFunction().getFunctionType().equals(FunctionType.RULE)) {
                    
                    candidate = new AdRuleFunctionInstanceEntity(
                        null,
                        energyExchangeEntity,
                        (AdRuleFunctionTemplateEntity)adFunctionTemplate,
                        true, // isCandidate
                        false, // isIgnored
                        adFunctionTemplate.getVersion(),
                        Integer.valueOf(1)); // InstanceVersion
                                
                  } else if (functionType != null && functionType.equals(FunctionType.COMPUTED_POINT)
                        || adFunctionTemplate.getAdFunction().getFunctionType().equals(FunctionType.COMPUTED_POINT)) {

                    candidate = new AdComputedPointFunctionInstanceEntity(
                        null,
                        energyExchangeEntity,
                        (AdComputedPointFunctionTemplateEntity)adFunctionTemplate,
                        true, // isCandidate
                        false, // isIgnored
                        adFunctionTemplate.getVersion(),
                        Integer.valueOf(1)); // InstanceVersion
                                
                  } else {
                    throw new IllegalStateException("Unsupported function type: ["
                        + functionType.getName() 
                        + "]");
                  }
                  
                  // INPUT POINTS
                  Iterator<Entry<Integer, List<Integer>>> boundCandidatePointsIterator = boundCandidatePoints.getBoundCandidatePoints().entrySet().iterator();
                  while (boundCandidatePointsIterator.hasNext()) {

                    Entry<Integer, List<Integer>> entry = boundCandidatePointsIterator.next();
                    Integer templateInputPointId = entry.getKey();
                    List<Integer> boundPointList = entry.getValue();

                    int subscript = 0;
                    Iterator<Integer> boundPointListIterator = boundPointList.iterator();
                    while (boundPointListIterator.hasNext()) {

                      Integer pointId = boundPointListIterator.next();
                      
                      AdFunctionTemplateInputPointEntity templateInputPoint = adFunctionTemplate.getInputPoint(templateInputPointId); 
                          
                      AdFunctionInstanceEligiblePoint point = (AdFunctionInstanceEligiblePoint) portfolio.getChildNodeNullIfNotExists(pointId);
                      
                      // If the point could not be found, then it was deleted. In this case, we do not instantiate the candidate input point. 
                      // This will lead to the candidate being either modified/deleted.
                      if (point != null) {
                        
                        candidate.addInputPoint(new AdFunctionInstanceInputPointEntity(
                            candidate, 
                            templateInputPoint,
                            point,
                            Integer.valueOf(subscript++)));
                        
                      } else {
                        candidate.setIsDeleted();
                      }
                    }
                  }

                  energyExchangeEntity.addAdFunctionInstanceCandidate(candidate);

                } catch (Exception e) {
                  throw new IllegalStateException("Unable to instantiate AD function instance candidate: ["
                      + dto
                      + "], error: "
                      + e.getMessage(), e);
                }
                
              } else if (redGreenDto.getRed() != null) {
                
                // The energy exchange node is eligible (type and node filter expression), but lacking required points.
                energyExchangeEntity.addAdFunctionErrorMessages(AdFunctionErrorMessagesEntity
                    .Mapper
                    .getInstance()
                    .mapDtoToEntity(
                        energyExchangeEntity,
                        redGreenDto.getRed()));
                
              } else {
                
                // The energy exchange node:
                // 1. is the wrong type or doesn't match the node filter expression (ineligible)
                // 2. for AD computed point function templates, the equipment already has a mappable point for the assoc. point template
                energyExchangeEntity.removeAdFunctionErrorMessages(adFunctionTemplate);
              }
              
            } else {
              
              // The energy exchange node is already associated with either an AD function instance or AD function instance candidate.
              energyExchangeEntity.removeAdFunctionErrorMessages(adFunctionTemplate);
              
            }
          }
        }
      }
    }
    return adFunctionInstanceCandidates;
  }
}
//@formatter:on