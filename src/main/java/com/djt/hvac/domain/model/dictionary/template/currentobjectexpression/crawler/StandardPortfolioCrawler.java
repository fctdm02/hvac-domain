package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.djt.hvac.domain.model.common.dsl.currentobject.NodeType;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.utils.TagUtils;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public abstract class StandardPortfolioCrawler implements PortfolioCrawler {
  
  protected final Optional<NodeType> nodeType;
  protected final List<String> tags;
  protected final boolean wildcardTag;

  protected StandardPortfolioCrawler(
      Optional<NodeType> nodeType,
      List<String> tags,
      boolean wildcardTag) {
    this.nodeType = requireNonNull(nodeType, "nodeType cannot be null");
    requireNonNull(tags, "tags cannot be null");
    List<String> list = Lists.newArrayList();
    list.addAll(tags);
    Collections.sort(list);
    List<String> theTags = ImmutableList.copyOf(list);
    TagUtils.validateTags(theTags);
    this.tags = theTags;
    this.wildcardTag = wildcardTag;
  }
  
  protected boolean matches(AbstractNodeEntity targetNode) {
    
    boolean matches = true;
    
    if (wildcardTag) {

      if ((this.nodeType.isPresent() && this.nodeType.get().getId() != targetNode.getNodeType().getId())
          || (!this.tags.isEmpty() && !targetNode.getNodeTagNames().containsAll(this.tags))) {
        
        matches = false;
      }
      
    } else {

      if ((this.nodeType.isPresent() && this.nodeType.get().getId() != targetNode.getNodeType().getId())
          || (!this.tags.isEmpty() && !this.tags.equals(targetNode.getNodeTagNamesAsSortedList()))) {
        
        matches = false;
      }
      
    }
    
    return matches;
  }

  // Test Methods

  public Optional<NodeType> getNodeType() {
    return nodeType;
  }

  public List<String> getTags() {
    return tags;
  }

  public boolean getWildcardTag() {
    return wildcardTag;
  }  
}
