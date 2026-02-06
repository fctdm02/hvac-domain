package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.function.AdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

public final class AddAdFunctionInstanceInputPointStrategyImpl extends AbstractPortfolioRemediationStrategy {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(AddAdFunctionInstanceInputPointStrategyImpl.class);
  
  private static final RemediationStrategy INSTANCE = new AddAdFunctionInstanceInputPointStrategyImpl();
  
  public static RemediationStrategy get() {
    return INSTANCE;
  }

  private AddAdFunctionInstanceInputPointStrategyImpl() {
  }

  @Override
  public void remediate(Map<String, Object> entities) {
    
    AbstractAdFunctionInstanceEntity adFunctionInstance = (AbstractAdFunctionInstanceEntity)entities.get("function_instance");
    AdFunctionTemplateInputPointEntity adFunctionTemplateInputPoint = (AdFunctionTemplateInputPointEntity)entities.get("function_template_input_point");
    AdFunctionInstanceEligiblePoint point = (AdFunctionInstanceEligiblePoint)entities.get("point");     
    Integer subscript = adFunctionInstance.getInputPoints().size();
    
    try {
      
      // Do a "copy on write" rather than modify the instance in place solely. 
      NodeTagTemplatesContainer nodeTagTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
      PortfolioEntity portfolio = adFunctionInstance.getEquipment().getRootPortfolioNode();
      
      // Add a new instance input point.
      AdFunctionInstanceInputPointEntity adFunctionInstanceInputPoint = new AdFunctionInstanceInputPointEntity(
          adFunctionInstance,
          adFunctionTemplateInputPoint,
          point,
          subscript);
      adFunctionInstance.addInputPoint(adFunctionInstanceInputPoint);
      adFunctionInstance.setIsModified("inputPoint:added");
      adFunctionInstance.getEquipment().setIsModified("childAdFunctionInstance:inputPoint:added");
      
      // Copy on write.
      LOGGER.info("Adding input point {}, performing copy on write", adFunctionInstanceInputPoint);
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
    return "Add AD function instance input point";
  }
}
