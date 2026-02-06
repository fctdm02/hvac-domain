package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.function.AdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

public final class DeleteAdFunctionInstanceInputPointStrategyImpl extends AbstractPortfolioRemediationStrategy {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAdFunctionInstanceInputPointStrategyImpl.class);
  
  private static final RemediationStrategy INSTANCE = new DeleteAdFunctionInstanceInputPointStrategyImpl();
  
  public static RemediationStrategy get() {
    return INSTANCE;
  }

  private DeleteAdFunctionInstanceInputPointStrategyImpl() {
  }
  
  @Override
  public void remediate(Map<String, Object> entities) {
    
    AbstractAdFunctionInstanceEntity adFunctionInstance = (AbstractAdFunctionInstanceEntity)entities.get("function_instance"); 
    AdFunctionTemplateInputPointEntity adFunctionTemplateInputPoint = (AdFunctionTemplateInputPointEntity)entities.get("function_template_input_point"); 
    AdFunctionInstanceEligiblePoint point = (AdFunctionInstanceEligiblePoint)entities.get("point");     
    
    // Do a "copy on write" rather than modify the instance in place.  If the modified instance doesn't 
    // have any input points anymore, then don't do a copy on write, just mark it deleted.
    NodeTagTemplatesContainer nodeTagTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
    PortfolioEntity portfolio = adFunctionInstance.getEquipment().getRootPortfolioNode();
    
    // Find the input point that we need to "delete" and remove it from the parent AD function instance 
    // collection by marking it as "deleted".
    for (AdFunctionInstanceInputPointEntity ip: adFunctionInstance.getInputPoints()) {
      if (ip.getAdFunctionTemplateInputPoint().equals(adFunctionTemplateInputPoint) && ip.getPoint().equals(point)) {
        
        ip.setIsDeleted();
        
        // If, after marking the above input point as "deleted", there are still input points, then clone the instance.
        // NOTE: This will always be the case as the AdFunctionEvaluator will already know this and will only call this
        // method if the state of the instance is still valid after removal of this point.
        if (adFunctionInstance.getNumNonDeletedInputPoints() > 0) {

          // Copy on write.
          LOGGER.info("Removing input point {}, performing copy on write", ip);
          AbstractAdFunctionInstanceEntity.createAdFunctionInstance(
              nodeTagTemplatesContainer,
              portfolio,
              adFunctionInstance);
          
        } else {
          
          LOGGER.info("Removing instance as input point to remove, {}, leaves the instance with zero points", ip);
          adFunctionInstance.getEquipment().addDeletedAdFunctionInstance(adFunctionInstance);
        }
      }
    }
  }
  
  @Override
  public String getRemediationDescription() {
    return "Delete input point";
  }
}
