//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;

/**
 * 
 * @author tmyers
 * 
 * <pre>
class {MIGRATION_NAME} < ActiveRecord::Migration[5.0]
  def up
    execute <<-SQL
      SET ROLE '#{ENV['DATABASE_USERNAME']}';
      -- MIGRATE CONTENT
      
    SQL
  end

  def down
    execute <<-SQL
      SET ROLE '#{ENV['DATABASE_USERNAME']}';
      -- ROLLBACK CONTENT
      
    SQL
  end
end 
 * </pre>
 * @param <T>
 */
public abstract class AbstractDictionaryDataMigrationGenerator<T extends AbstractEntity> implements DictionaryDataMigrationGenerator<T> {
  
  protected StringBuilder sb = new StringBuilder();
  
  
  @Override
  public String generateInsertMigration(List<T> entities) {
    
    generateMigratePrefix();
    for (T entity: entities) {
      
      generateInsert(entity);  
    }
    generateMigrateSuffix();
    
    
    generateRollbackPrefix();
    for (T entity: entities) {
      
      generateDelete(entity);  
    }
    generateRollbackSuffix();
    
    return sb.toString();
  }
  
  @Override
  public String generateUpdateMigration(Map<T, T> entities) {

    generateMigratePrefix();
    for (Map.Entry<T, T> entry: entities.entrySet()) {
      
      T entityBefore = entry.getKey();
      T entityAfter = entry.getValue();
      generateUpdate(entityBefore, entityAfter);
    }
    generateMigrateSuffix();

    
    generateRollbackPrefix();
    for (Map.Entry<T, T> entry: entities.entrySet()) {
      
      T entityBefore = entry.getValue();
      T entityAfter = entry.getKey();
      generateUpdate(entityBefore, entityAfter);
    }
    generateRollbackSuffix();
    
    return sb.toString();
  }
  
  @Override
  public String generateDeleteMigration(List<T> entities) {
    
    generateMigratePrefix();
    for (T entity: entities) {
      
      generateDelete(entity);  
    }
    generateMigrateSuffix();

    
    generateRollbackPrefix();
    for (T entity: entities) {
      
      generateInsert(entity);  
    }
    generateRollbackSuffix();
    
    return sb.toString();
  }  
  
  
  protected abstract void generateInsert(T entity);

  protected abstract void generateUpdate(T entityBefore, T entityAfter);
  
  protected abstract void generateDelete(T entity);
  
  
  protected void generateMigratePrefix() {
    
    sb.setLength(0);
    sb.append("class MIGRATION_NAME < ActiveRecord::Migration[5.0]\n");
    sb.append("  def up\n");
    sb.append("    execute <<-SQL\n");
    sb.append("      SET ROLE '#{ENV['DATABASE_USERNAME']}';\n\n");
  }

  protected void generateMigrateSuffix() {

    sb.append("\n    SQL\n");
    sb.append("  end\n");
  }
  
  protected void generateRollbackPrefix() {

    sb.append("\n  def down\n");
    sb.append("    execute <<-SQL\n");
    sb.append("      SET ROLE '#{ENV['DATABASE_USERNAME']}';\n\n");
  }

  protected void generateRollbackSuffix() {

    sb.append("\n    SQL\n");
    sb.append("  end\n");
    sb.append("end\n");
  }
  
  protected void validatePersistentIdentity(AbstractPersistentEntity entity) {
    
    if (entity.getPersistentIdentity() == null) {
      throw new IllegalStateException("persistentIdentity must be set prior for entity: ["
          + entity.getNaturalIdentity()
          + "].");
    }
  }
}
//@formatter:on