package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.utils;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;

public class TagUtils {

  public static void validateTags(List<String> tagNames) {
    requireNonNull(tagNames, "tagNames cannot be null");
    if (!tagNames.isEmpty()) {
      
      TagsContainer tagsContainer = DictionaryContext.getTagsContainer();
      
      for (String tagName: tagNames) {

        try {
          tagsContainer.getTagByName(tagName);
        } catch (EntityDoesNotExistException ednee) {
          throw new IllegalStateException("There does not exist any tag with the name: ["
              + tagName 
              + "]", ednee);
        }
      }
    }
  }

  private TagUtils() {}
}
