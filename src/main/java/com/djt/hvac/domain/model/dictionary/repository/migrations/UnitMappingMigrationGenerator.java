//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;

public final class UnitMappingMigrationGenerator extends AbstractDictionaryDataMigrationGenerator<UnitMappingEntity> {

  protected void generateInsert(UnitMappingEntity entity) {
    
    validatePersistentIdentity(entity);
    
    sb.append("INSERT INTO unit_mappings (id, ip_unit_id, si_unit_id, ip_to_si_conversion_factor, si_to_ip_conversion_factor) VALUES (")
        .append(entity.getPersistentIdentity())
        .append((", "))
        .append(entity.getIpUnit().getPersistentIdentity())
        .append((", "))
        .append(entity.getSiUnit().getPersistentIdentity())
        .append((", '"))
        .append(entity.getIpToSiConversionFactor())
        .append(("', '"))
        .append(entity.getSiToIpConversionFactor())
        .append("'); \n");
  }

  protected void generateUpdate(UnitMappingEntity entityBefore, UnitMappingEntity entityAfter) {
    
    validatePersistentIdentity(entityBefore);
    
    sb.append("UPDATE unit_mappings SET ip_unit_id = ")
        .append(entityAfter.getIpUnit().getPersistentIdentity())
        .append((", si_unit_id = "))
        .append(entityAfter.getSiUnit().getPersistentIdentity())
        .append((", ip_to_si_conversion_factor = '"))
        .append(entityAfter.getIpToSiConversionFactor())
        .append("', si_to_ip_conversion_factor = '")
        .append(entityAfter.getSiToIpConversionFactor())
        .append("' WHERE id = ")
        .append(entityBefore.getPersistentIdentity())
        .append("; \n");
  }
  
  protected void generateDelete(UnitMappingEntity entity) {
    
    validatePersistentIdentity(entity);
    
    sb.append("DELETE FROM unit_mappings WHERE id = ")
        .append(entity.getPersistentIdentity())
        .append("; \n");
  }
}
//@formatter:on