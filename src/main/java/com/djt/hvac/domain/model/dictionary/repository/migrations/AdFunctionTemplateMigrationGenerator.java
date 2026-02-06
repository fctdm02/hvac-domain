//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateOutputPointEntity;

public final class AdFunctionTemplateMigrationGenerator extends AbstractDictionaryDataMigrationGenerator<AbstractAdFunctionTemplateEntity> {

  protected void generateInsert(AbstractAdFunctionTemplateEntity entity) {
    
    generateBaseModuleInsert(entity.getAdFunction());
    generateBaseTemplateInsert(entity);
    
    generateEnergyExchangeInsert(entity);
    
    for (AdFunctionTemplateInputPointEntity inputPoint: entity.getInputPoints()) {
      generateInputPointInsert(inputPoint);
      generateInputPointTagInsert(inputPoint);
    }
    
    for (AdFunctionTemplateInputConstantEntity inputConstant: entity.getInputConstants()) {
      generateInputConstantInsert(inputConstant);
    }

    for (AdFunctionTemplateOutputPointEntity outputPoint: entity.getOutputPoints()) {
      generateOutputPointInsert(outputPoint);
    }
  }
  
  protected void generateUpdate(AbstractAdFunctionTemplateEntity entityBefore, AbstractAdFunctionTemplateEntity entityAfter) {
    
    String entityBeforeSignature = entityBefore.getSignature();
    String entityAfterSignature = entityAfter.getSignature();
    if (!entityBeforeSignature.equals(entityAfterSignature)) {

      boolean modifiedInputConstants = false;
      boolean modifiedInputPoints = false;
      boolean modifiedFunction = false;
      boolean modifiedTemplate = false;
      boolean modifiedEnergyExchangeType = false;
      
      // INPUT CONSTANTS
      Set<AdFunctionTemplateInputConstantEntity> beforeEntityInputConstants = new TreeSet<>();
      Set<String> beforeEntityInputConstantNames = new TreeSet<>();
      for (AdFunctionTemplateInputConstantEntity inputConstant: entityBefore.getInputConstants()) {
        beforeEntityInputConstants.add(inputConstant);
        beforeEntityInputConstantNames.add(inputConstant.getName());
      }
      Set<AdFunctionTemplateInputConstantEntity> afterEntityInputConstants = new TreeSet<>();
      Set<String> afterEntityInputConstantNames = new TreeSet<>();
      for (AdFunctionTemplateInputConstantEntity inputConstant: entityAfter.getInputConstants()) {
        afterEntityInputConstants.add(inputConstant);
        afterEntityInputConstantNames.add(inputConstant.getName());
      }

      // INPUT CONSTANT INSERT
      for (AdFunctionTemplateInputConstantEntity inputConstant: afterEntityInputConstants) {
        
        if (!beforeEntityInputConstantNames.contains(inputConstant.getName())) {
          
          generateInputConstantInsert(inputConstant);
          modifiedInputConstants = true;
        }
      }

      // INPUT CONSTANT DELETE
      for (AdFunctionTemplateInputConstantEntity inputConstant: beforeEntityInputConstants) {
        
        if (!afterEntityInputConstantNames.contains(inputConstant.getName())) {
          
          generateInputConstantDelete(inputConstant);
          modifiedInputConstants = true;
        }
      }
      
      // INPUT CONSTANT UPDATE
      for (AdFunctionTemplateInputConstantEntity inputConstantBefore: entityBefore.getInputConstants()) {
        
        AdFunctionTemplateInputConstantEntity inputConstantAfter = entityAfter.getInputConstant(inputConstantBefore.getName());
        if (inputConstantAfter != null) {

          String inputConstantBeforeSignature = inputConstantBefore.getSignature();
          String inputConstantAfterSignature = inputConstantAfter.getSignature();
          if (!inputConstantBeforeSignature.equals(inputConstantAfterSignature)) {
            
            // BASE ROW
            StringBuilder suffix = new StringBuilder();
            boolean modified = false;
            if (!inputConstantBefore.getSequenceNumber().equals(inputConstantAfter.getSequenceNumber())) {
              suffix.append("seq_no = ").append(inputConstantAfter.getSequenceNumber());
              modified = true;
            }

            if (!inputConstantBefore.getUnit().getPersistentIdentity().equals(inputConstantAfter.getUnit().getPersistentIdentity())) {
              if (modified) {
                suffix.append(", ");
              }
              suffix.append("unit_id = ").append(inputConstantAfter.getUnit().getPersistentIdentity());
              modified = true;
            }
            
            if (!inputConstantBefore.getName().equals(inputConstantAfter.getName())) {
              if (modified) {
                suffix.append(", ");
              }
              suffix.append("name = '").append(inputConstantAfter.getName().replaceAll("'", "''")).append("'");
              modified = true;
            }

            if (!inputConstantBefore.getDescription().equals(inputConstantAfter.getDescription())) {
              if (modified) {
                suffix.append(", ");
              }
              suffix.append("description = '").append(inputConstantAfter.getDescription().replaceAll("'", "''")).append("'");
              modified = true;
            }

            if (inputConstantBefore.getDataType().getId() != inputConstantAfter.getDataType().getId()) {
              if (modified) {
                suffix.append(", ");
              }
              suffix.append("data_type_id = ").append(inputConstantAfter.getDataType().getId());
              modified = true;
            }

            if (!inputConstantBefore.getIsRequired().equals(inputConstantAfter.getIsRequired())) {
              if (modified) {
                suffix.append(", ");
              }
              suffix.append("is_required = ").append(inputConstantAfter.getIsRequired());
              modified = true;
            }
            
            if (!inputConstantBefore.getDefaultValue().equals(inputConstantAfter.getDefaultValue())) {
              if (modified) {
                suffix.append(", ");
              }
              suffix.append("default_value = '").append(inputConstantAfter.getDefaultValue()).append("'");
              modified = true;
            }

            if (modified) {
              
              modifiedInputConstants = true;
              sb.append("UPDATE ad_rule_template_input_consts SET ")
                  .append(suffix.toString())
                  .append(" WHERE id = ")
                  .append(inputConstantBefore.getPersistentIdentity())
                  .append("; \n");
            }
          }
        }
      }
      
      // INPUT POINTS
      Set<AdFunctionTemplateInputPointEntity> beforeEntityInputPoints = new TreeSet<>();
      Set<String> beforeEntityInputPointNames = new TreeSet<>();
      for (AdFunctionTemplateInputPointEntity inputPoint: entityBefore.getInputPoints()) {
        beforeEntityInputPoints.add(inputPoint);
        beforeEntityInputPointNames.add(inputPoint.getName());
      }
      Set<AdFunctionTemplateInputPointEntity> afterEntityInputPoints = new TreeSet<>();
      Set<String> afterEntityInputPointNames = new TreeSet<>();
      for (AdFunctionTemplateInputPointEntity inputPoint: entityAfter.getInputPoints()) {
        afterEntityInputPoints.add(inputPoint);
        afterEntityInputPointNames.add(inputPoint.getName());
      }

      // INPUT POINT INSERT
      for (AdFunctionTemplateInputPointEntity inputPoint: afterEntityInputPoints) {
        
        if (!beforeEntityInputPointNames.contains(inputPoint.getName())) {
          
          generateInputPointInsert(inputPoint);
          generateInputPointTagInsert(inputPoint);
          modifiedInputPoints = true;
        }
      }

      // INPUT POINT DELETE
      for (AdFunctionTemplateInputPointEntity inputPoint: beforeEntityInputPoints) {
        
        if (!afterEntityInputPointNames.contains(inputPoint.getName())) {
          
          generateInputPointTagDelete(inputPoint);
          generateInputPointDelete(inputPoint);
          modifiedInputPoints= true;
        }
      }
      
      // INPUT POINT UPDATE
      for (AdFunctionTemplateInputPointEntity inputPointBefore: entityBefore.getInputPoints()) {
        
        AdFunctionTemplateInputPointEntity inputPointAfter = entityAfter.getInputPointByNameNullIfNotExists(inputPointBefore.getName());
        if (inputPointAfter != null) {

          String inputPointBeforeSignature = inputPointBefore.getSignature();
          String inputPointAfterSignature = inputPointAfter.getSignature();
          if (!inputPointBeforeSignature.equals(inputPointAfterSignature)) {
            
            // BASE ROW
            boolean modified = false;
            StringBuilder suffix = new StringBuilder();
            if (!inputPointBefore.getSequenceNumber().equals(inputPointAfter.getSequenceNumber())) {
              suffix.append("seq_no = ").append(inputPointAfter.getSequenceNumber());
              modified = true;
            }
            
            if (!inputPointBefore.getName().equals(inputPointAfter.getName())) {
              if (modified) {
                suffix.append(", ");
              }
              suffix.append("name = '").append(inputPointAfter.getName().replaceAll("'", "''")).append("'");
              modified = true;
            }

            if (!inputPointBefore.getDescription().equals(inputPointAfter.getDescription())) {
              if (modified) {
                suffix.append(", ");
              }
              suffix.append("description = '").append(inputPointAfter.getDescription().replaceAll("'", "''")).append("'");
              modified = true;
            }

            if (!inputPointBefore.getCurrentObjectExpressionNullIfNotExists().equals(inputPointAfter.getCurrentObjectExpressionNullIfNotExists())) {
              if (modified) {
                suffix.append(", ");
              }
              if (!inputPointAfter.getCurrentObjectExpressionNullIfNotExists().equals("NULL")) {
                suffix.append("current_object_expression = '").append(inputPointAfter.getCurrentObjectExpressionNullIfNotExists()).append("'");
              } else {
                suffix.append("current_object_expression = ").append(inputPointAfter.getCurrentObjectExpressionNullIfNotExists());
              }
              
              modified = true;
            }

            if (!inputPointBefore.getIsRequired().equals(inputPointAfter.getIsRequired())) {
              if (modified) {
                suffix.append(", ");
              }
              suffix.append("is_required = ").append(inputPointAfter.getIsRequired());
              modified = true;
            }

            if (!inputPointBefore.getIsArray().equals(inputPointAfter.getIsArray())) {
              if (modified) {
                suffix.append(", ");
              }
              suffix.append("is_array = ").append(inputPointAfter.getIsArray());
              modified = true;
            }
            
            if (modified) {
              
              modifiedInputPoints = true;
              sb.append("UPDATE ad_rule_template_input_points SET ")
                .append(suffix.toString())
                .append(" WHERE id = ")
                .append(inputPointBefore.getPersistentIdentity())
                .append("; \n\n\n");          
            }
          }
        }
      }      
      
      // OUTPUT POINTS (NOTHING TO DO)
      
      // AD FUNCTION
      AdFunctionEntity entityBeforeFunction = entityAfter.getAdFunction();
      AdFunctionEntity entityAfterFunction = entityAfter.getAdFunction();
      if (!entityBeforeFunction.getSignature().equals(entityAfterFunction.getSignature())) {
        
        sb.append("UPDATE ad_rules SET jar = '")
          .append(entityAfterFunction.getName().replaceAll("'", "''"))
          .append("', description = '")
          .append(entityAfterFunction.getDescription().replaceAll("'", "''"))
          .append("' WHERE id = ")
          .append(entityBefore.getPersistentIdentity())
          .append("; \n");
        modifiedFunction = true;
      }
      
      // ENERGY EXCHANGE TYPE
      if (!entityBefore.getEnergyExchangeType().equals(entityAfter.getEnergyExchangeType())) {
        
        sb.append("DELETE FROM ad_rule_template_tags WHERE ad_rule_template_id = ")
          .append(entityBefore.getPersistentIdentity())
          .append("; \n");
        
        sb.append("INSERT INTO ad_rule_template_tags (ad_rule_template_id, tag_id) VALUES (")
          .append(entityBefore.getPersistentIdentity())
          .append(", ")
          .append(entityAfter.getEnergyExchangeType().getPersistentIdentity())
          .append("); \n");
        modifiedEnergyExchangeType = true;
      }       
      
      // BASE TEMPLATE
      StringBuilder suffix = new StringBuilder();
      if (!entityBefore.getAdFunction().getPersistentIdentity().equals(entityAfter.getAdFunction().getPersistentIdentity())) {
        suffix.append("ad_rule_id = ").append(entityAfter.getAdFunction().getPersistentIdentity());
        modifiedTemplate = true;
      }
      
      if (!entityBefore.getFaultOrReferenceNumber().equals(entityAfter.getFaultOrReferenceNumber())) {
        if (modifiedTemplate) {
          suffix.append(", ");
        }
        suffix.append("fault_number = '").append(entityAfter.getFaultOrReferenceNumber()).append("'");
        modifiedTemplate = true;
      }

      if (!entityBefore.getName().equals(entityAfter.getName())) {
        if (modifiedTemplate) {
          suffix.append(", ");
        }
        AbstractAdFunctionTemplateEntity adFunctionTemplate = DictionaryContext.getAdFunctionTemplatesContainer().getDifferentAdFunctionTemplateByNameNullIfNotExists(entityBefore, entityAfter.getName());
        if (adFunctionTemplate == null) {
          suffix.append("name = '").append(entityAfter.getName().replaceAll("'", "''")).append("'");  
        } else {
          suffix.append("name = '").append(entityAfter.getName().replaceAll("'", "''")).append("_").append(entityAfter.getEnergyExchangeType().getName()).append("'");
        }
        modifiedTemplate = true;
      }

      if (!entityBefore.getDisplayName().equals(entityAfter.getDisplayName())) {
        if (modifiedTemplate) {
          suffix.append(", ");
        }
        suffix.append("display_name = '").append(entityAfter.getDisplayName().replaceAll("'", "''")).append("'");
        modifiedTemplate = true;
      }

      if (!entityBefore.getDescription().equals(entityAfter.getDescription())) {
        if (modifiedTemplate) {
          suffix.append(", ");
        }
        suffix.append("description = '").append(entityAfter.getName().replaceAll("'", "''")).append("'");
        modifiedTemplate = true;
      }

      if (!entityBefore.getNodeFilterExpressionNullIfNotExists().equals(entityAfter.getNodeFilterExpressionNullIfNotExists())) {
        if (modifiedTemplate) {
          suffix.append(", ");
        }
        if (!entityAfter.getNodeFilterExpressionNullIfNotExists().equals("NULL")) {
          suffix.append("node_filter_expression = '").append(entityAfter.getNodeFilterExpressionNullIfNotExists()).append("'");  
        } else {
          suffix.append("node_filter_expression = ").append(entityAfter.getNodeFilterExpressionNullIfNotExists());
        }
        modifiedTemplate = true;
      }

      if (!entityBefore.getTupleConstraintExpressionNullIfNotExists().equals(entityAfter.getTupleConstraintExpressionNullIfNotExists())) {
        if (modifiedTemplate) {
          suffix.append(", ");
        }
        if (!entityAfter.getTupleConstraintExpressionNullIfNotExists().equals("NULL")) {
          suffix.append("tuple_constraint = '").append(entityAfter.getTupleConstraintExpressionNullIfNotExists()).append("'");  
        } else {
          suffix.append("tuple_constraint = ").append(entityAfter.getTupleConstraintExpressionNullIfNotExists());
        }
        modifiedTemplate = true;
      }

      if (!entityBefore.getIsBeta().equals(entityAfter.getIsBeta())) {
        if (modifiedTemplate) {
          suffix.append(", ");
        }
        suffix.append("beta = ").append(entityAfter.getIsBeta());
        modifiedTemplate = true;
      }

      
      if (modifiedInputPoints || modifiedInputConstants || modifiedEnergyExchangeType || modifiedFunction || modifiedTemplate) {

        sb.append("UPDATE ad_rule_templates SET version = ")
          .append(Integer.toString(entityBefore.getVersion() + 1));
        if (modifiedTemplate) {
          sb.append(", ")
            .append(suffix.toString());
        }
        sb.append(" WHERE id = ")
          .append(entityBefore.getPersistentIdentity())
          .append("; \n");
      }
      
      // SANITY CHECK
      if (!modifiedFunction 
          && !modifiedTemplate 
          && !modifiedEnergyExchangeType 
          && !modifiedInputPoints 
          && !modifiedInputConstants) {
        throw new RuntimeException("Signatures different, yet no changes made for entityBefore: " + entityBefore);
      }
      
      // CANDIDATES AND PROSPECTS
      sb.append("DELETE FROM ad_rule_instance_candidates WHERE rule_template_id = ")
          .append(entityBefore.getPersistentIdentity())
          .append(";\n");

      sb.append("DELETE FROM ad_function_error_messages WHERE ad_function_template_id = ")
          .append(entityBefore.getPersistentIdentity())
          .append(";\n");      
    }
  }
  
  protected void generateDelete(AbstractAdFunctionTemplateEntity entity) {

    for (AdFunctionTemplateOutputPointEntity outputPoint: entity.getOutputPoints()) {
      generateOutputPointDelete(outputPoint);
    }

    for (AdFunctionTemplateInputConstantEntity inputConstant: entity.getInputConstants()) {
      generateInputConstantDelete(inputConstant);
    }

    for (AdFunctionTemplateInputPointEntity inputPoint: entity.getInputPoints()) {
      generateInputPointTagDelete(inputPoint);
      generateInputPointDelete(inputPoint);
    }
    
    generateEnergyExchangeDelete(entity);
    
    generateBaseTemplateDelete(entity);
    
    generateBaseModuleDelete(entity.getAdFunction());
  }
  
  private void generateBaseModuleInsert(AdFunctionEntity entity) {
    
    sb.append("INSERT INTO ad_rules (id, jar, description) VALUES (")
        .append(entity.getPersistentIdentity())
        .append(", '")
        .append(entity.getName())
        .append("', '")
        .append(entity.getDescription())
        .append("'); \n");  
  }
  
  private void generateBaseTemplateInsert(AbstractAdFunctionTemplateEntity entity) {
   
    String tupleConstraintExpression = "NULL";
    if (entity.getTupleConstraintExpression() != null && !entity.getTupleConstraintExpression().trim().isEmpty()) {
      tupleConstraintExpression = "'" + entity.getTupleConstraintExpression() + "'";
    }

    String nodeFilterExpression = "NULL";
    if (entity.getNodeFilterExpression() != null && !entity.getNodeFilterExpression().trim().isEmpty()) {
      nodeFilterExpression = "'" + entity.getNodeFilterExpression() + "'";
    }
    
    sb.append("INSERT INTO ad_rule_templates (id, ad_rule_id, fault_number, name, display_name, description, tuple_constraint, node_filter_expression, version) VALUES (")
        .append(entity.getPersistentIdentity())
        .append(", ")
        .append(entity.getAdFunction().getPersistentIdentity())
        .append(", '")
        .append(entity.getFaultOrReferenceNumber())
        .append("', '")
        .append(entity.getName())
        .append("', '")
        .append(entity.getDisplayName())
        .append("', '")
        .append(entity.getDescription())
        .append("', ")
        .append(tupleConstraintExpression)
        .append(", ")
        .append(nodeFilterExpression)
        .append(", ")
        .append(entity.getVersion())
        .append("); \n");
    
    sb.append("UPDATE ad_rule_templates SET beta=true WHERE id=")
        .append(entity.getPersistentIdentity())
        .append(";\n");
  }
  
  private void generateEnergyExchangeInsert(AbstractAdFunctionTemplateEntity entity) {

    sb.append("INSERT INTO ad_rule_template_tags (ad_rule_template_id, tag_id) VALUES (")
        .append(entity.getPersistentIdentity())
        .append(", ")
        .append(entity.getEnergyExchangeType().getPersistentIdentity())
        .append("); \n");  
  }

  private void generateInputPointInsert(AdFunctionTemplateInputPointEntity inputPoint) {
    
    String currentObjectExpression = "NULL";
    if (inputPoint.getCurrentObjectExpression() != null && !inputPoint.getCurrentObjectExpression().trim().isEmpty()) {
      currentObjectExpression = "'" + inputPoint.getCurrentObjectExpression() + "'";
    }

    sb.append("INSERT INTO ad_rule_template_input_points (id, ad_rule_template_id, seq_no, name, description, current_object_expression, is_required, is_array) VALUES (")
        .append(inputPoint.getPersistentIdentity())
        .append(", ")
        .append(inputPoint.getParentRuleTemplate().getPersistentIdentity())
        .append(", ")
        .append(inputPoint.getSequenceNumber())
        .append(", '")
        .append(inputPoint.getName())
        .append("', '")
        .append(inputPoint.getDescription())
        .append("', ")
        .append(currentObjectExpression)
        .append(", ")
        .append(inputPoint.getIsRequired())
        .append(", ")
        .append(inputPoint.getIsArray())
        .append("); \n");  
  }
  
  private void generateInputPointTagInsert(AdFunctionTemplateInputPointEntity inputPoint) {
    
    sb.append("INSERT INTO ad_rule_template_input_point_tag_tbl (ad_rule_template_input_point_id, tag_id) VALUES \n");
    List<TagEntity> tags = new ArrayList<>();
    tags.addAll(inputPoint.getTags());
    if (tags.size() > 0) {
      for (int i=0; i < tags.size(); i++) {
        
        TagEntity tag = tags.get(i);
        sb.append("    (")
            .append(inputPoint.getPersistentIdentity())
            .append(", ")
            .append(tag.getPersistentIdentity());
        
        if (i < tags.size()-1) {
          sb.append("), \n");
        } else {
          sb.append("); \n");
        }
      }
    } else {
      throw new IllegalArgumentException("AD function template input point: ["
          + inputPoint
          + "] needs to have at least one tag associated with it.");
    }
  }
  
  private void generateInputConstantInsert(AdFunctionTemplateInputConstantEntity inputConstant) {
   
    sb.append("INSERT INTO ad_rule_template_input_consts (id, ad_rule_template_id, data_type_id, unit_id, seq_no, name, description, default_value) VALUES (")
        .append(inputConstant.getPersistentIdentity())
        .append(", ")
        .append(inputConstant.getParentRuleTemplate().getPersistentIdentity())
        .append(", ")
        .append(inputConstant.getDataType().getId())
        .append(", ")
        .append(inputConstant.getUnit().getPersistentIdentity())
        .append(", ")
        .append(inputConstant.getSequenceNumber())
        .append(", '")
        .append(inputConstant.getName())
        .append("', '")
        .append(inputConstant.getDescription())
        .append("', '")
        .append(inputConstant.getDefaultValue())
        .append("'); \n");      
  }
  
  private void generateOutputPointInsert(AdFunctionTemplateOutputPointEntity outputPoint) {

    String range = "NULL";
    if (outputPoint.getRange() != null && !outputPoint.getRange().trim().isEmpty()) {
      range = "'" + outputPoint.getRange() + "'";
    }
    
    sb.append("INSERT INTO ad_rule_template_output_points (id, ad_rule_template_id, data_type_id, unit_id, seq_no, description, range) VALUES (")
        .append(outputPoint.getPersistentIdentity())
        .append(", ")
        .append(outputPoint.getParentRuleTemplate().getPersistentIdentity())
        .append(", ")
        .append(outputPoint.getDataType().getId())
        .append(", ")
        .append(outputPoint.getUnit().getPersistentIdentity())
        .append(", ")
        .append(outputPoint.getSequenceNumber())
        .append(", '")
        .append(outputPoint.getDescription())
        .append("', ")
        .append(range)
        .append("); \n");      
  }
  
  private void generateBaseModuleDelete(AdFunctionEntity entity) {
    
    sb.append("DELETE FROM ad_rules WHERE id = ")
        .append(entity.getPersistentIdentity())
        .append("; \n");    
  }
  
  private void generateBaseTemplateDelete(AbstractAdFunctionTemplateEntity entity) {
    
    sb.append("DELETE FROM ad_rule_templates WHERE id = ")
        .append(entity.getPersistentIdentity())
        .append("; \n");    
  }
  
  private void generateEnergyExchangeDelete(AbstractAdFunctionTemplateEntity entity) {

    sb.append("DELETE FROM ad_rule_template_tags WHERE ad_rule_template_id = ")
        .append(entity.getPersistentIdentity())
        .append("; \n");  
  }

  private void generateInputPointDelete(AdFunctionTemplateInputPointEntity inputPoint) {

    sb.append("DELETE FROM ad_rule_template_input_points WHERE id = ")
        .append(inputPoint.getPersistentIdentity())
        .append("; \n");  
  }
  
  private void generateInputPointTagDelete(AdFunctionTemplateInputPointEntity inputPoint) {

    sb.append("DELETE FROM ad_rule_template_input_point_tag_tbl WHERE ad_rule_template_input_point_id = ")
        .append(inputPoint.getPersistentIdentity())
        .append("; \n");  
  }
  
  private void generateInputConstantDelete(AdFunctionTemplateInputConstantEntity inputConstant) {

    sb.append("DELETE FROM ad_rule_template_input_consts WHERE id = ")
        .append(inputConstant.getPersistentIdentity())
        .append("; \n");  
  }
  
  private void generateOutputPointDelete(AdFunctionTemplateOutputPointEntity outputPoint) {

    sb.append("DELETE FROM ad_rule_template_output_points WHERE id = ")
        .append(outputPoint.getPersistentIdentity())
        .append("; \n");  
  }
  
  protected void generateOutputPointUpdate(AdFunctionTemplateOutputPointEntity outputPoint) {
    
    String range = "NULL";
    if (outputPoint.getRange() != null && !outputPoint.getRange().trim().isEmpty()) {
      range = "'" + outputPoint.getRange() + "'";
    }
    
    sb.append("UPDATE ad_rule_template_output_points SET data_type_id = ")
        .append(outputPoint.getDataType().getId())
        .append(", unit_id = ")
        .append(outputPoint.getUnit().getPersistentIdentity())
        .append(", seq_no = ")
        .append(outputPoint.getSequenceNumber())
        .append(", description = '")
        .append(outputPoint.getDescription())
        .append("', range = ")
        .append(range)
        .append(" WHERE id = ")
        .append(outputPoint.getPersistentIdentity())
        .append("; \n");      
  }  
}
//@formatter:on