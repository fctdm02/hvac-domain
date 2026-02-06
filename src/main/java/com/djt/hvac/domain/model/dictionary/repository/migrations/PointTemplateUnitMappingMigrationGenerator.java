//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateUnitMappingEntity;

public final class PointTemplateUnitMappingMigrationGenerator extends AbstractDictionaryDataMigrationGenerator<PointTemplateUnitMappingEntity> {

  protected void generateInsert(PointTemplateUnitMappingEntity entity) {
    
    sb.append("INSERT INTO point_template_unit_mappings (point_template_id, unit_mapping_id, priority) VALUES (")
        .append(entity.getPointTemplate().getPersistentIdentity())
        .append((", "))
        .append(entity.getUnitMapping().getPersistentIdentity())
        .append((", "))
        .append(entity.getPriority())
        .append("); \n");
  }

  protected void generateUpdate(PointTemplateUnitMappingEntity entityBefore, PointTemplateUnitMappingEntity entityAfter) {
    
    sb.append("UPDATE point_template_unit_mappings SET priority = ")
        .append(entityAfter.getPriority())
        .append(" WHERE point_template_id = ")
        .append(entityBefore.getPointTemplate().getPersistentIdentity())
        .append((" AND unit_mapping_id = "))
        .append(entityBefore.getUnitMapping().getPersistentIdentity())
        .append("; \n");
  }
  
  protected void generateDelete(PointTemplateUnitMappingEntity entity) {

    sb.append("DELETE FROM point_template_unit_mappings WHERE point_template_id = ")
        .append(entity.getPointTemplate().getPersistentIdentity())
        .append((" AND unit_mapping_id = "))
        .append(entity.getUnitMapping().getPersistentIdentity())
        .append("; \n");
  }
}
//@formatter:on