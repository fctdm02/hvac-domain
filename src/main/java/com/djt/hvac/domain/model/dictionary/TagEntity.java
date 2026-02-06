//@formatter:off
package com.djt.hvac.domain.model.dictionary;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.dto.TagDto;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.dictionary.enums.TagType;

public class TagEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final String name;
  private final TagGroupType tagGroupType;
  private final TagType tagType;
  private final NodeType scopedToConstraint;
  private final Boolean uiInferred;
  
  public TagEntity(
      Integer persistentIdentity,
      String name,
      TagGroupType tagGroupType,
      TagType tagType,
      NodeType scopedToConstraint,
      Boolean uiInferred) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    requireNonNull(tagGroupType, "tagGroupType cannot be null");
    requireNonNull(tagType, "tagType cannot be null");
    requireNonNull(uiInferred, "uiInferred cannot be null");
    this.name = name;
    this.tagGroupType = tagGroupType;
    this.tagType = tagType;
    this.scopedToConstraint = scopedToConstraint;
    this.uiInferred = uiInferred;
  }
  
  public String getName() {
    return name;
  }

  public TagGroupType getTagGroupType() {
    return tagGroupType;
  }

  public TagType getTagType() {
    return tagType;
  }

  public NodeType getScopedToConstraint() {
    return scopedToConstraint;
  }

  public Boolean getUiInferred() {
    return uiInferred;
  }

  public String getNaturalIdentity() {
    return new StringBuilder()
	.append(tagGroupType.getName())
	.append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
	.append(name)
	.toString();
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public static Set<String> getTagNamesAsSet(Set<TagEntity> tags) {
    
    Set<String> tagNamesAsSet = new TreeSet<>();
    if (tags != null && !tags.isEmpty()) {
      
      Iterator<TagEntity> iterator = tags.iterator();
      while (iterator.hasNext()) {
        
        TagEntity tag = iterator.next();
        tagNamesAsSet.add(tag.name);
      }
    }
    return tagNamesAsSet;
  }
  
  public static String getTagNamesAsString(Set<TagEntity> tags) {
    
    String tagNamesAsString = null;
    if (tags != null && !tags.isEmpty()) {
      
      Set<String> set = getTagNamesAsSet(tags);
      tagNamesAsString = set
          .toString()
          .replaceAll(", ",  ",")
          .replace("[", "")
          .replace("]", "")
          .replace("\"", "");
      
    } else {
      tagNamesAsString = "";
    }
    return tagNamesAsString;
  }
  
  public static List<String> getTagNamesAsList(Set<TagEntity> tags) {
    
    List<String> list = new ArrayList<>();
    list.addAll(getTagNamesAsSet(tags));
    return list;
  }
  
  public static class Mapper implements DtoMapper<TagsContainer, TagEntity, TagDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<TagDto> mapEntitiesToDtos(List<TagEntity> entities) {

      List<TagDto> list = new ArrayList<>();
      for (TagEntity entity: entities) {
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }

    @Override
    public TagDto mapEntityToDto(TagEntity e) {
      
      TagDto d = new TagDto();
      
      d.setId(e.getPersistentIdentity());
      d.setTagGroupId(e.getTagGroupType().getId());
      d.setTagGroup(e.getTagGroupType().getName());
      d.setTagTypeId(e.getTagType().getId());
      d.setTagType(e.getTagType().getName());
      d.setName(e.getName());
      d.setUiInferred(e.getUiInferred());
      
      Integer scopedToConstraint = null;
      if (e.getScopedToConstraint() != null) {
        scopedToConstraint = e.getScopedToConstraint().getId();
      }
      d.setScopedToConstraint(scopedToConstraint);
      
      return d;
    }

    public List<TagEntity> mapDtosToEntities(
        TagsContainer tagsContainer,
        List<TagDto> dtos) {

      List<TagEntity> list = new ArrayList<>();
      for (TagDto dto: dtos) {
        list.add(mapDtoToEntity(tagsContainer, dto));
      }
      return list;
    }
    
    @Override
    public TagEntity mapDtoToEntity(
        TagsContainer tagsContainer,
        TagDto d) {
      
      try {
        
        NodeType scopedToConstraint = null;
        if (d.getScopedToConstraint() != null) {
          scopedToConstraint = NodeType.get(d.getScopedToConstraint());
        }
        
        return new TagEntity(
            d.getId(),
            d.getName(),
            TagGroupType.get(d.getTagGroupId()),
            TagType.get(d.getTagTypeId()),
            scopedToConstraint,
            d.getUiInferred());
        
      } catch (Exception e) {
        throw new RuntimeException("Unable to map DTO: ["
            + d, e);
      }      
    }
  }  
}
//@formatter:on