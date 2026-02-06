//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.ArrayList;
import java.util.List;

import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;

public final class PointTemplateMigrationGenerator extends AbstractDictionaryDataMigrationGenerator<PointTemplateEntity> {

  protected void generateInsert(PointTemplateEntity entity) {
    
    int parentNodeTypeId = NodeType.EQUIPMENT.getId();
    if (entity.getParentNodeTypes().contains(NodeType.BUILDING)) {
      parentNodeTypeId = NodeType.BUILDING.getId();
    }
    
    String replacementPointTemplateId = "null";
    if (entity.getReplacementPointTemplateId() != null) {
      replacementPointTemplateId = entity.getReplacementPointTemplateId().toString();
    }
    
    sb.append("INSERT INTO point_templates (id, tag_group_id, name, description, unit_id, is_public, parent_node_type_id, is_deprecated, replacement_point_template_id) VALUES (")
        .append(entity.getPersistentIdentity())
        .append((", "))
        .append(entity.getTagGroupType().getId())
        .append((", '"))
        .append(entity.getName())
        .append(("', '"))
        .append(entity.getDescription())
        .append(("', "))
        .append(entity.getUnit().getPersistentIdentity())
        .append((", "))
        .append(entity.getIsPublic().toString())
        .append((", "))
        .append(parentNodeTypeId)
        .append((", "))
        .append(entity.getIsDeprecated().toString())
        .append((", "))
        .append(replacementPointTemplateId)
        .append("); \n");
    
    generateTagInsert(entity);
    
    generateEnergyExchangeInsert(entity);
  }
  
  protected void generateUpdate(PointTemplateEntity entityBefore, PointTemplateEntity entityAfter) {
    
    int parentNodeTypeId = NodeType.EQUIPMENT.getId();
    if (entityAfter.getParentNodeTypes().contains(NodeType.BUILDING)) {
      parentNodeTypeId = NodeType.BUILDING.getId();
    }
    
    String replacementPointTemplateId = "null";
    if (entityAfter.getReplacementPointTemplateId() != null) {
      replacementPointTemplateId = entityAfter.getReplacementPointTemplateId().toString();
    }
    
    sb.append("UPDATE point_templates SET tag_group_id = ")
        .append(entityAfter.getTagGroupType().getId())
        .append(", name = '")
        .append(entityAfter.getName())
        .append("', description = '")
        .append(entityAfter.getDescription())
        .append("', unit_id = ")
        .append(entityAfter.getUnit().getPersistentIdentity())
        .append(", is_public = ")
        .append(entityAfter.getIsPublic().toString())
        .append(", parent_node_type_id = ")
        .append(parentNodeTypeId)
        .append(", is_deprecated = ")
        .append(entityAfter.getIsDeprecated().toString())
        .append(", replacement_point_template_id = ")
        .append(replacementPointTemplateId)
        .append(" WHERE id = ")
        .append(entityBefore.getPersistentIdentity())
        .append("; \n");

    sb.append("DELETE FROM point_template_tags WHERE node_template_id = ")
        .append(entityBefore.getPersistentIdentity())
        .append("; \n");
    generateTagInsert(entityAfter);

    sb.append("DELETE FROM point_template_equipment_types WHERE node_template_id = ")
        .append(entityBefore.getPersistentIdentity())
        .append("; \n");
    generateEnergyExchangeInsert(entityAfter);
  }
  
  protected void generateDelete(PointTemplateEntity entity) {

    sb.append("DELETE FROM point_templates WHERE id = ")
        .append(entity.getPersistentIdentity())
        .append("; \n");
  }
  
  private void generateTagInsert(PointTemplateEntity entity) {
    
    sb.append("INSERT INTO point_template_tags (node_template_id, tag_id) VALUES \n");
    List<TagEntity> tags = new ArrayList<>();
    tags.addAll(entity.getTags());
    if (tags.size() > 0) {
      for (int i=0; i < tags.size(); i++) {
        
        TagEntity tag = tags.get(i);
        sb.append("    (")
            .append(entity.getPersistentIdentity())
            .append(", ")
            .append(tag.getPersistentIdentity());
        
        if (i < tags.size()-1) {
          sb.append("), \n");
        } else {
          sb.append("); \n");
        }
      }
    } else {
      throw new IllegalArgumentException("Point template: ["
          + entity
          + "] needs to have at least one tag associated with it.");
    }    
  }
  
  private void generateEnergyExchangeInsert(PointTemplateEntity entity) {
    
    List<AbstractEnergyExchangeTypeEntity> energyExchangeTypes = new ArrayList<>();
    energyExchangeTypes.addAll(entity.getParentEnergyExchangeTypes());
    if (!energyExchangeTypes.isEmpty()) {
      
      sb.append("INSERT INTO point_template_equipment_types (node_template_id, tag_id) VALUES \n");
      for (int i=0; i < energyExchangeTypes.size(); i++) {
        
        AbstractEnergyExchangeTypeEntity energyExchangeType = energyExchangeTypes.get(i);
        sb.append("    (")
            .append(entity.getPersistentIdentity())
            .append(", ")
            .append(energyExchangeType.getPersistentIdentity());
        
        if (i < energyExchangeTypes.size()-1) {
          sb.append("), \n");
        } else {
          sb.append("); \n");
        }
      }      
    }    
  }  
}
//@formatter:on