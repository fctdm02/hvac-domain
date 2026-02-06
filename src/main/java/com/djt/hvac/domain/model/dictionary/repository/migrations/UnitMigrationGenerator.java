//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import com.djt.hvac.domain.model.dictionary.UnitEntity;

public final class UnitMigrationGenerator extends AbstractDictionaryDataMigrationGenerator<UnitEntity> {

  protected void generateInsert(UnitEntity entity) {
    
    validatePersistentIdentity(entity);
    
    sb.append("INSERT INTO unit_tbl (id, aggregator_id, display_name, discovered) VALUES (")
        .append(entity.getPersistentIdentity())
        .append(", ")
        .append(entity.getAggregatorType().getId())
        .append(", '")
        .append(entity.getName())
        .append(", FALSE); \n");
  }

  protected void generateUpdate(UnitEntity entityBefore, UnitEntity entityAfter) {
    
    validatePersistentIdentity(entityBefore);
    
    sb.append("UPDATE unit_tbl SET aggregator_id = ")
        .append(entityAfter.getAggregatorType().getId())
        .append((", name = '"))
        .append(entityAfter.getName())
        .append("' WHERE id = ")
        .append(entityBefore.getPersistentIdentity())
        .append("; \n");
  }
  
  protected void generateDelete(UnitEntity entity) {
    
    validatePersistentIdentity(entity);
    
    sb.append("DELETE FROM unit_tbl WHERE id = ")
        .append(entity.getPersistentIdentity())
        .append("; \n");
  }
}
//@formatter:on