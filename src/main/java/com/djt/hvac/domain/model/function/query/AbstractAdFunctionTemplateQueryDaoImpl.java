//@formatter:off
package com.djt.hvac.domain.model.function.query;

import java.util.SortedMap;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;

/**
 * 
 * @author tmyers
 *
 */
public abstract class AbstractAdFunctionTemplateQueryDaoImpl implements AdFunctionTemplateQueryDao {
  
  public AbstractAdFunctionTemplateQueryDaoImpl() {
  }
  
  @Override
  public SortedMap<String, String> getAffectedRulesForPointTemplateId(int pointTemplateId) {
    
    NodeTagTemplatesContainer pointTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
    SortedMap<String, String> map = new TreeMap<>();
    for (AbstractAdFunctionTemplateEntity adFunctionTemplate: DictionaryContext.getAdFunctionTemplatesContainer().getAdFunctionTemplates()) {
      for (AdFunctionTemplateInputPointEntity inputPoint: adFunctionTemplate.getInputPoints()) {
        
        PointTemplateEntity pointTemplate = pointTemplatesContainer.getPointTemplateByTags(inputPoint.getTags());
        if (pointTemplate != null) {
          
          map.put("RULE ID: " + adFunctionTemplate.getPersistentIdentity(), adFunctionTemplate.getDescription());
        }
      }
    }
    return map;
  }
  
  @Override
  public SortedMap<String, String> getAffectedRulesForTagId(int tagId) {
    
    TagsContainer tagsContainer = DictionaryContext.getTagsContainer();
    TagEntity tag = null;;
    try {
      
      tag = tagsContainer.getTag(tagId);

      SortedMap<String, String> map = new TreeMap<>();
      for (AbstractAdFunctionTemplateEntity adFunctionTemplate: DictionaryContext.getAdFunctionTemplatesContainer().getAdFunctionTemplates()) {
        for (AdFunctionTemplateInputPointEntity inputPoint: adFunctionTemplate.getInputPoints()) {
          for (TagEntity t: inputPoint.getTags()) {
            if (t.equals(tag)) {
            
              map.put("RULE ID: " + adFunctionTemplate.getPersistentIdentity(), adFunctionTemplate.getDescription());
            }
          }
        }
      }
      return map;      
      
    } catch (EntityDoesNotExistException e) {
      throw new IllegalStateException("Tag with id: ["
          + tagId
          + "] does not exist.");
    }
  }  
}
//@formatter:on