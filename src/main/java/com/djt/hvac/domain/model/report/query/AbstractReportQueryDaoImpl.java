//@formatter:off
package com.djt.hvac.domain.model.report.query;

import java.util.SortedMap;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.AbstractReportTemplatePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEquipmentSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateRulePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateStandardPointSpecEntity;

/**
 * 
 * @author tmyers
 *
 */
public abstract class AbstractReportQueryDaoImpl implements ReportQueryDao {
  
  public AbstractReportQueryDaoImpl() {
  }

  @Override
  public SortedMap<String, String> getAffectedReportsForPointTemplateId(int pointTemplateId) {
    
    NodeTagTemplatesContainer pointTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
    SortedMap<String, String> map = new TreeMap<>();
    for (ReportTemplateEntity reportTemplate: DictionaryContext.getReportTemplatesContainer().getReportTemplates()) {
      for (ReportTemplateEquipmentSpecEntity equipmentSpec: reportTemplate.getEquipmentSpecs()) {
        for (AbstractReportTemplatePointSpecEntity pointSpec: equipmentSpec.getPointSpecs()) {
          
          if (pointSpec instanceof ReportTemplateStandardPointSpecEntity) {
            
            ReportTemplateStandardPointSpecEntity standardPointSpec = (ReportTemplateStandardPointSpecEntity)pointSpec;
            PointTemplateEntity pointTemplate = pointTemplatesContainer.getPointTemplateByTags(standardPointSpec.getTags());
            if (pointTemplate != null) {
              
              map.put("REPORT ID: " + reportTemplate.getPersistentIdentity(), reportTemplate.getDescription());
            }
          }
        }
      }
    }
    return map;
  }
  
  @Override
  public SortedMap<String, String> getAffectedReportsForTagId(int tagId) {
    
    TagsContainer tagsContainer = DictionaryContext.getTagsContainer();
    TagEntity tag = null;;
    try {
      
      tag = tagsContainer.getTag(tagId);

      SortedMap<String, String> map = new TreeMap<>();
      for (ReportTemplateEntity reportTemplate: DictionaryContext.getReportTemplatesContainer().getReportTemplates()) {
        for (ReportTemplateEquipmentSpecEntity es: reportTemplate.getEquipmentSpecs()) {
          for (AbstractReportTemplatePointSpecEntity aps: es.getPointSpecs()) {
            
            if (aps instanceof ReportTemplateStandardPointSpecEntity) {
              
              ReportTemplateStandardPointSpecEntity sps = (ReportTemplateStandardPointSpecEntity)aps;
              for (TagEntity t: sps.getTags()) {
                if (t.equals(tag)) {
                  
                  map.put("REPORT ID: " + reportTemplate.getPersistentIdentity(), reportTemplate.getDescription());
                }
              }
              
            } else {
              
              ReportTemplateRulePointSpecEntity rps = (ReportTemplateRulePointSpecEntity)aps;
              AdRuleFunctionTemplateEntity adFunctionTemplate = rps.getRuleTemplate();
              for (AdFunctionTemplateInputPointEntity inputPoint: adFunctionTemplate.getInputPoints()) {
                for (TagEntity t: inputPoint.getTags()) {
                  if (t.equals(tag)) {
                  
                    map.put("REPORT ID: " + reportTemplate.getPersistentIdentity(), reportTemplate.getDescription());
                  }
                }                
              }
              
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


/*


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


*/