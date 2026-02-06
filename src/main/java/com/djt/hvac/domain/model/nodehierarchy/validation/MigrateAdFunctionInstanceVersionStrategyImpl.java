package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceInputConstantEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

public final class MigrateAdFunctionInstanceVersionStrategyImpl extends AbstractPortfolioRemediationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(MigrateAdFunctionInstanceVersionStrategyImpl.class);
  
  private static final RemediationStrategy INSTANCE = new MigrateAdFunctionInstanceVersionStrategyImpl();
  
  public static RemediationStrategy get() {
    return INSTANCE;
  }

  private MigrateAdFunctionInstanceVersionStrategyImpl() {
  }

  @Override
  public void remediate(Map<String, Object> entities) {
    
    AbstractAdFunctionInstanceEntity adFunctionInstance = (AbstractAdFunctionInstanceEntity)entities.get("function_instance");
    AbstractAdFunctionTemplateEntity adFunctionTemplate = adFunctionInstance.getAdFunctionTemplate();
    
    // Do a "copy on write" rather than modify the instance in place solely. 
    NodeTagTemplatesContainer nodeTagTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
    PortfolioEntity portfolio = adFunctionInstance.getEquipment().getRootPortfolioNode();
    
    // Migrate the AD function template version.
    adFunctionInstance.setTemplateVersion();
    
    // Make sure that all input constants are accounted for.
    Set<AdFunctionTemplateInputConstantEntity> templateInputConstants = adFunctionTemplate.getInputConstants();
    Set<AdFunctionInstanceInputConstantEntity> instanceInputConstants = adFunctionInstance.getInputConstants();
    
    Set<String> templateInputConstantNames = new HashSet<>();
    for (AdFunctionTemplateInputConstantEntity templateInputConstant: templateInputConstants) {
      templateInputConstantNames.add(templateInputConstant.getName());
    }
    
    Set<String> instanceInputConstantNames = new HashSet<>();
    for (AdFunctionInstanceInputConstantEntity instanceInputConstant: instanceInputConstants) {
      instanceInputConstantNames.add(instanceInputConstant.getAdFunctionTemplateInputConstant().getName());
    }
    
    if (templateInputConstants.size() >= instanceInputConstants.size()) {
      for (AdFunctionTemplateInputConstantEntity templateInputConstant: templateInputConstants) {
        
        String templateInputConstantName = templateInputConstant.getName();
        if (!instanceInputConstantNames.contains(templateInputConstantName)) {
          
          AdFunctionInstanceInputConstantEntity adFunctionInstanceInputConstant = new AdFunctionInstanceInputConstantEntity(
              adFunctionInstance,
              templateInputConstant,
              templateInputConstant.getDefaultValue());
          
          try {
            adFunctionInstance.addInputConstant(adFunctionInstanceInputConstant);
          } catch (EntityAlreadyExistsException e) {
            throw new RuntimeException(e.getMessage(), e);
          }
          
          LOGGER.info("MIGRATE: Adding input constant {}, performing copy on write", templateInputConstantName);
          adFunctionInstance.setIsModified("inputConstant:added");
          adFunctionInstance.getEquipment().setIsModified("childAdFunctionInstance:inputConstant:added");
        }
      }
    } else {
      for (AdFunctionInstanceInputConstantEntity instanceInputConstant: instanceInputConstants) {
        
        String instanceInputConstantName = instanceInputConstant.getAdFunctionTemplateInputConstant().getName();
        if (!templateInputConstantNames.contains(instanceInputConstantName)) {
          
          LOGGER.info("MIGRATE: Removing input constant {}, performing copy on write", instanceInputConstantName);
          instanceInputConstant.setIsDeleted();
          adFunctionInstance.setIsModified("inputConstant:removed");
          adFunctionInstance.getEquipment().setIsModified("childAdFunctionInstance:inputConstant:removed");
        }
      }
    }
    
    // Copy on write.
    LOGGER.info("MIGRATE: performing copy on write: {}", adFunctionInstance);
    AbstractAdFunctionInstanceEntity.createAdFunctionInstance(
        nodeTagTemplatesContainer,
        portfolio,
        adFunctionInstance);
  }
  
  @Override
  public String getRemediationDescription() {
    return "Migrate AD function instance (version mismatch)";
  }
}
