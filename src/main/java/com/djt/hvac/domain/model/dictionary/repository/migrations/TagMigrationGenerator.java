//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;

public final class TagMigrationGenerator extends AbstractDictionaryDataMigrationGenerator<TagEntity> {

  protected void generateInsert(TagEntity entity) {
    
    validatePersistentIdentity(entity);
    
    String scopedToConstraintId = "null";  
    NodeType scopedToConstraintNodeType = entity.getScopedToConstraint();
    if (scopedToConstraintNodeType != null) {
      scopedToConstraintId = Integer.toString(scopedToConstraintNodeType.getId());
    }
    
    sb.append("INSERT INTO tag_tbl (id, tag_group_id, tag_type_id, name, ui_inferred, scoped_to_constraint) VALUES (")
        .append(entity.getPersistentIdentity())
        .append(", ")
        .append(entity.getTagGroupType().getId())
        .append(", ")
        .append(entity.getTagType().getId())
        .append(", '")
        .append(entity.getName())
        .append("', ")
        .append(entity.getUiInferred().toString())
        .append(", ")
        .append(scopedToConstraintId)
        .append("); \n");
  }

  protected void generateUpdate(TagEntity entityBefore, TagEntity entityAfter) {
    
    validatePersistentIdentity(entityBefore);
    
    String scopedToConstraintId = "null";  
    NodeType scopedToConstraintNodeType = entityAfter.getScopedToConstraint();
    if (scopedToConstraintNodeType != null) {
      scopedToConstraintId = Integer.toString(scopedToConstraintNodeType.getId());
    }
    
    sb.append("UPDATE tag_tbl SET tag_group_id = ")
        .append(entityAfter.getTagGroupType().getId())
        .append(", tag_type_id = ")
        .append(entityAfter.getTagType().getId())
        .append((", name = '"))
        .append(entityAfter.getName())
        .append("', ui_inferred = ")
        .append(entityAfter.getUiInferred().toString())
        .append(", scoped_to_constraint = ")
        .append(scopedToConstraintId)
        .append(" WHERE id = ")
        .append(entityBefore.getPersistentIdentity())
        .append("; \n");
  }
  
  protected void generateDelete(TagEntity entity) {
    
    validatePersistentIdentity(entity);
    
    sb.append("DELETE FROM tag_tbl WHERE id = ")
        .append(entity.getPersistentIdentity())
        .append("; \n");
  }
}
//@formatter:on