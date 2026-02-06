package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

public final class DeleteAndReAddTagsFromTemplateStrategyImpl extends AbstractPortfolioRemediationStrategy {
  
  private static final RemediationStrategy INSTANCE = new DeleteAndReAddTagsFromTemplateStrategyImpl();
  
  public static RemediationStrategy get() {
    return INSTANCE;
  }
  
  private DeleteAndReAddTagsFromTemplateStrategyImpl() {
  }
  
  @Override
  public void remediate(Map<String, Object> entities) {
    
    AbstractPointEntity point = (AbstractPointEntity)entities.get("point"); 
    
    point.removeHaystackTags();
    AbstractNodeTagTemplateEntity pointTemplate = point.getPointTemplateNullIfEmpty();
    if (pointTemplate != null) {
      
      Set<TagEntity> tagsToAdd = pointTemplate.getTags();
      point.addNodeTags(tagsToAdd);
    }
  }

  @Override
  public String getRemediationDescription() {
    return "Delete and re-add haystack tags for point from point template";
  }
}
