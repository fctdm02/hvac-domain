package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceInputConstantEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

public final class AddMissingAdFunctionInstanceInputConstantStrategyImpl extends AbstractPortfolioRemediationStrategy {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(AddAdFunctionInstanceInputPointStrategyImpl.class);
  
  private static final RemediationStrategy INSTANCE = new AddMissingAdFunctionInstanceInputConstantStrategyImpl();
  
  public static RemediationStrategy get() {
    return INSTANCE;
  }

  private AddMissingAdFunctionInstanceInputConstantStrategyImpl() {
  }

  @Override
  public void remediate(Map<String, Object> entities) {
    
    AbstractAdFunctionInstanceEntity adFunctionInstance = (AbstractAdFunctionInstanceEntity)entities.get("function_instance"); 
    AdFunctionTemplateInputConstantEntity adFunctionTemplateInputConstant = (AdFunctionTemplateInputConstantEntity)entities.get("function_template_input_constant"); 
    
    try {
      
      // Do a "copy on write" rather than modify the instance in place solely. 
      NodeTagTemplatesContainer nodeTagTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
      PortfolioEntity portfolio = adFunctionInstance.getEquipment().getRootPortfolioNode();
      
      // Add a new instance input constant.
      AdFunctionInstanceInputConstantEntity adFunctionInstanceInputConstant = new AdFunctionInstanceInputConstantEntity(
          adFunctionInstance,
          adFunctionTemplateInputConstant,
          adFunctionTemplateInputConstant.getDefaultValue());
      adFunctionInstance.addInputConstant(adFunctionInstanceInputConstant);
      adFunctionInstance.setIsModified("inputConstant:added");
      adFunctionInstance.getEquipment().setIsModified("childAdFunctionInstance:inputConstant:added");
      
      // Copy on write.
      LOGGER.info("Adding input constant {}, performing copy on write", adFunctionInstanceInputConstant);
      AbstractAdFunctionInstanceEntity.createAdFunctionInstance(
          nodeTagTemplatesContainer,
          portfolio,
          adFunctionInstance);
      
    } catch (EntityAlreadyExistsException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  @Override
  public String getRemediationDescription() {
    return "Add missing input constant";
  }
}
