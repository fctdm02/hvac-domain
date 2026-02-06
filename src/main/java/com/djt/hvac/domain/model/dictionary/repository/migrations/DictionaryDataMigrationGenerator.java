//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.common.AbstractEntity;

/**
 * 
 * @author tmyers
 *
 * @param <T> Where <code>T</code> is the dictionary entity type to create insert/update/delete 
 *        migrations for.  That is, ruby migrations, that are to be applied against the migrations
 *        app in the DEV environment.  Dictionary data "source of truth" is the DEV environment, 
 *        as that is where change originates (unlike customer data, whose "source of truth" is the
 *        PROD environment.
 * </p>        
 * NOTE: The persistent identity of the dictionary data entities MUST BE SET when instantiated.        
 */
public interface DictionaryDataMigrationGenerator<T extends AbstractEntity> {
  
  String MIGRATION_NAME_TOKEN = "MIGRATION_NAME";

  /**
   * 
   * @param entities the entities to generate insert migrations for
   * 
   * @return the generated migration
   */
  String generateInsertMigration(List<T> entities);
  
  /**
   * 
   * @param entities the entities to generate insert migrations for
   * key is "before" entity and value is "after" entity
   * 
   * @return the generated migration
   */
  String generateUpdateMigration(Map<T,T> entities);
  
  /**
   * 
   * @param entities the entities to generate delete migrations for
   * 
   * @return the generated migration
   */
  String generateDeleteMigration(List<T> entities);
}
//@formatter:on