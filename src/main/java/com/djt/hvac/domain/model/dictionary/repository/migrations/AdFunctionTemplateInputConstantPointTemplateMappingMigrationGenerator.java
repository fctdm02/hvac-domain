//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantPointTemplateMappingEntity;

public final class AdFunctionTemplateInputConstantPointTemplateMappingMigrationGenerator extends AbstractDictionaryDataMigrationGenerator<AdFunctionTemplateInputConstantPointTemplateMappingEntity> {

  protected void generateInsert(AdFunctionTemplateInputConstantPointTemplateMappingEntity entity) {
    
    sb.append("INSERT INTO ad_function_template_input_const_point_template_mappings (ad_function_template_input_const_id, point_template_id) VALUES (")
        .append(entity.getAdFunctionTemplateInputConstant().getPersistentIdentity())
        .append((", "))
        .append(entity.getPointTemplate().getPersistentIdentity())
        .append("); \n");
  }

  protected void generateUpdate(AdFunctionTemplateInputConstantPointTemplateMappingEntity entityBefore, AdFunctionTemplateInputConstantPointTemplateMappingEntity entityAfter) {
    
    throw new IllegalStateException("Updates are not supported for AdFunctionTemplateInputConstantPointTemplateMappingEntity");
  }
  
  protected void generateDelete(AdFunctionTemplateInputConstantPointTemplateMappingEntity entity) {

    sb.append("DELETE FROM ad_function_template_input_const_point_template_mappings WHERE ad_function_template_input_const_id = ")
        .append(entity.getAdFunctionTemplateInputConstant().getPersistentIdentity())
        .append((" AND point_template_id = "))
        .append(entity.getPointTemplate().getPersistentIdentity())
        .append("; \n");
  }
}
//@formatter:on