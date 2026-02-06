package com.djt.hvac.domain.model.dictionary.template.nodetag;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage.MessageType;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;

public abstract class AbstractNodeTagTemplateEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private String name;
  private String description;
  private final Set<NodeType> parentNodeTypes;
  private final NodeType nodeType;
  private final TagGroupType tagGroupType;
  private Boolean isPublic;
  private Set<TagEntity> tags = new TreeSet<>();
  private Boolean isDeprecated;
  private Integer replacementPointTemplateId;
  
  private transient String _normalizedTags;
  private transient Set<String> _normalizedTagsAsSet;
  
  public AbstractNodeTagTemplateEntity(
      Integer persistentIdentity,
      String name,
      String description,
      Set<NodeType> parentNodeTypes,
      NodeType nodeType,
      TagGroupType tagGroupType,
      Boolean isPublic,
      Set<TagEntity> tags,
      Boolean isDeprecated,
      Integer replacementPointTemplateId) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(parentNodeTypes, "parentNodeTypes cannot be null");
    requireNonNull(nodeType, "nodeType cannot be null");
    requireNonNull(tagGroupType, "tagGroupType cannot be null");
    requireNonNull(isPublic, "isPublic cannot be null");
    requireNonNull(tags, "tags cannot be null");
    requireNonNull(isDeprecated, "isDeprecated cannot be null");
    this.name = name;
    this.description = description;
    this.parentNodeTypes = parentNodeTypes;
    this.nodeType = nodeType;
    this.tagGroupType = tagGroupType;
    this.isPublic = isPublic;
    
    if (!tags.isEmpty()) {
      this.tags.addAll(tags);
    }
    
    this.isDeprecated = isDeprecated;
    this.replacementPointTemplateId = replacementPointTemplateId;
    
    if (parentNodeTypes.isEmpty()) {
      throw new IllegalStateException("parentNodeTypes cannot be empty for name: " + name);
    }
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    
    if (this.name == null && name == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.name == null && name != null) 
        || (this.name != null && name == null)) {
      
      this.name = name;
      setIsModified("name");
      
    } else if (this.name != null && name != null) {
      
      if (!this.name.equals(name)) {

        this.name = name;
        setIsModified("name");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }     
  }   

  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    
    if (this.description == null && description == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.description == null && description != null) 
        || (this.description != null && description == null)) {
      
      this.description = description;
      setIsModified("description");
      
    } else if (this.description != null && description != null) {
      
      if (!this.description.equals(description)) {

        this.description = description;
        setIsModified("description");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }     
  }
  
  public Set<NodeType> getParentNodeTypes() {
    return parentNodeTypes;
  }
  
  public NodeType getParentNodeType() {
    
    List<NodeType> list = new ArrayList<>();
    list.addAll(parentNodeTypes);
    return list.get(0);
  }  
  
  public NodeType getNodeType() {
    return nodeType;
  }
  
  public TagGroupType getTagGroupType() {
    return this.tagGroupType;
  }
  
  public Boolean getIsPublic() {
    return isPublic;
  }
  
  public void setIsPublic(Boolean isPublic) {
    
    if (this.isPublic == null && isPublic == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.isPublic == null && isPublic != null) 
        || (this.isPublic != null && isPublic == null)) {
      
      this.isPublic = isPublic;
      setIsModified("isPublic");
      
    } else if (this.isPublic != null && isPublic != null) {
      
      if (!this.isPublic.equals(isPublic)) {

        this.isPublic = isPublic;
        setIsModified("isPublic");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }     
  }
  
  public Set<TagEntity> getTags() {
    return tags;
  }
  
  public void setTags(Set<TagEntity> tags) {
    
    if (tags == null) {
      throw new IllegalArgumentException("tags cannot be null.");
    }
    
    if (!this.tags.equals(tags)) {

      this.tags = tags;
      setIsModified("tags");
      
    } else {
      
      // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
      
    }
  }
  
  public List<String> getTagsAsStringList() {
    
    List<String> list = new ArrayList<>();
    for (TagEntity tag: tags) {
      list.add(tag.getName());
    }
    return list;
  }    
  
  public Boolean getIsDeprecated() {
    return isDeprecated;
  }
  
  public void setIsDeprecated(Boolean isDeprecated) {
    
    if (this.isDeprecated == null && isDeprecated == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.isDeprecated == null && isDeprecated != null) 
        || (this.isDeprecated != null && isDeprecated == null)) {
      
      this.isDeprecated = isDeprecated;
      setIsModified("isDeprecated");
      
    } else if (this.isDeprecated != null && isDeprecated != null) {
      
      if (!this.isDeprecated.equals(isDeprecated)) {

        this.isDeprecated = isDeprecated;
        setIsModified("isDeprecated");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }     
  }  

  public Integer getReplacementPointTemplateId() {
    return replacementPointTemplateId;
  }
  
  public void setReplacementPointTemplateId(Integer replacementPointTemplateId) {
    
    if (this.replacementPointTemplateId == null && replacementPointTemplateId == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.replacementPointTemplateId == null && replacementPointTemplateId != null) 
        || (this.replacementPointTemplateId != null && replacementPointTemplateId == null)) {
      
      this.replacementPointTemplateId = replacementPointTemplateId;
      setIsModified("replacementPointTemplateId");
      
    } else if (this.replacementPointTemplateId != null && replacementPointTemplateId != null) {
      
      if (!this.replacementPointTemplateId.equals(replacementPointTemplateId)) {

        this.replacementPointTemplateId = replacementPointTemplateId;
        setIsModified("replacementPointTemplateId");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }     
  }   
  
  @Override
  public String getNaturalIdentity() {
    return getName();
  }  

  public Set<String> getNormalizedTagsAsSet() {
    
    if (_normalizedTagsAsSet == null) {
      _normalizedTagsAsSet = TagEntity.getTagNamesAsSet(tags);
    }
    return _normalizedTagsAsSet;
  }
  
  public String getNormalizedTags() {
    
    if (_normalizedTags == null) {
      _normalizedTags = TagEntity.getTagNamesAsString(tags);
    }
    return _normalizedTags;
  }
  
  @Override
  public void validateSimple(List<SimpleValidationMessage> messages) {
    
    Iterator<TagEntity> tagIterator = tags.iterator();
    while (tagIterator.hasNext()) {
      
      TagEntity tag = tagIterator.next();
      
      TagGroupType tagTagGroupType = tag.getTagGroupType(); 
      if (tagTagGroupType.equals(tagGroupType)) {
      
        messages.add(new SimpleValidationMessage(
            MessageType.ERROR,
            name,
            "tag group type",
            "Point template: ["
                + name
                + "] has expected tag group type: ["
                + tagGroupType
                + "] but encountered tag: ["
                + tag.getName()
                + "] that has a tag group type of: ["
                + tagTagGroupType
                + "]"
            )); 
      }
    }
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    // DO NOTHING
  }
}
