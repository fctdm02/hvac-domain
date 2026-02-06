package com.djt.hvac.domain.model.nodehierarchy.dto;

public enum TagType {
    MARKER (1, "Marker"),
    KEY_VALUE(2, "Key-Value");
  
  private final int id;
  private final String tagType;
   
  private TagType ( int id, String tagType ) {
      this.id = id;
      this.tagType = tagType;
    }
  
  public String getTagType () {
    return tagType;
  }
  
  public int getId () {
    return id;
  }
  
  public static TagType fromId ( int id ) {
    for ( TagType tagType : values() ) {
      if ( tagType.id == id ) 
        return tagType;
    }
    
    throw new IllegalArgumentException("Invalid TagType Id (" + id + ")");
  }
}
