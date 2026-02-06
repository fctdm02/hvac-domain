package com.djt.hvac.domain.model.dictionary.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * 
 * @author tmyers
 * 
 * <pre>
   id   name                tag_types          node_types
   ====================================================================================================================================
    1   Tag                 Marker, Key-Value  Portfolio, Site, Building, Sub-building, Floor, Zone, Meter, Equipment, Point, Area of Interest
    2   Group               Marker, Key-Value  Site, Building
    3   Area of Interest    Marker             Area of Interest
    5   Meter Type          Marker             Meter, Equipment
    6   Point Tag           Marker, Key-Value  Point
    7   Point Haystack Tag  Marker             Point
    
    8   Equipment Metadata  Marker             Equipment
    
    4   Equipment Type      Marker             Equipment
    9   Plant Type          Marker             Plant
    10  Loop Type           Marker             Loop 
 * </pre>
 */
public enum TagGroupType {
  TAG                (1, "Tag",                new ArrayList<>(Arrays.asList(TagType.MARKER, TagType.KEY_VALUE)), new ArrayList<>(Arrays.asList(NodeType.PORTFOLIO, NodeType.SITE, NodeType.BUILDING, NodeType.SUB_BUILDING, NodeType.FLOOR, NodeType.EQUIPMENT, NodeType.POINT, NodeType.AREA_OF_INTEREST, NodeType.PLANT, NodeType.LOOP))),  
  GROUP              (2, "Group",              new ArrayList<>(Arrays.asList(TagType.MARKER)),                    new ArrayList<>(Arrays.asList(NodeType.SITE, NodeType.BUILDING))),
  AREA_OF_INTEREST   (3, "Area of Interest",   new ArrayList<>(Arrays.asList(TagType.MARKER, TagType.KEY_VALUE)), new ArrayList<>(Arrays.asList(NodeType.SITE, NodeType.AREA_OF_INTEREST))),
  EQUIPMENT_TYPE     (4, "Equipment Type",     new ArrayList<>(Arrays.asList(TagType.MARKER)),                    new ArrayList<>(Arrays.asList(NodeType.EQUIPMENT))),
  METER_TYPE         (5, "Meter Type",         new ArrayList<>(Arrays.asList(TagType.MARKER)),                    new ArrayList<>(Arrays.asList(NodeType.METER, NodeType.EQUIPMENT))),
  POINT_TAG          (6, "Point Tag",          new ArrayList<>(Arrays.asList(TagType.MARKER, TagType.KEY_VALUE)), new ArrayList<>(Arrays.asList(NodeType.POINT))),
  POINT_HAYSTACK_TAG (7, "Point Haystack Tag", new ArrayList<>(Arrays.asList(TagType.MARKER)),                    new ArrayList<>(Arrays.asList(NodeType.POINT))),
  EQUIPMENT_METADATA (8, "Equipment Metadata", new ArrayList<>(Arrays.asList(TagType.MARKER)),                    new ArrayList<>(Arrays.asList(NodeType.EQUIPMENT))),
  PLANT_TYPE         (9, "Plant Type",         new ArrayList<>(Arrays.asList(TagType.MARKER)),                    new ArrayList<>(Arrays.asList(NodeType.PLANT))),
  LOOP_TYPE          (10,"Loop Type",          new ArrayList<>(Arrays.asList(TagType.MARKER)),                    new ArrayList<>(Arrays.asList(NodeType.LOOP)));
  
  private static final Map<Integer, TagGroupType> TAG_GROUP_TYPES;
  
  private final int id;
  private final String name;
  private final Set<TagType> tagTypes = new HashSet<>();
  private final Set<NodeType> nodeTypes = new HashSet<>();
  
  static {
    Map<Integer, TagGroupType> types = Maps.newHashMap();
    for (TagGroupType type : TagGroupType.values()) {
      types.put(type.id, type);
    }
    TAG_GROUP_TYPES = ImmutableMap.copyOf(types);
  }
  
  public static TagGroupType get(int id) {
    return TAG_GROUP_TYPES.get(id);
  }

  private TagGroupType(
      int id, 
      String name, 
      List<TagType> tagTypes,
      List<NodeType> nodeTypes) {
    
    this.id = id;
    this.name = name;
    this.tagTypes.addAll(tagTypes);
    this.nodeTypes.addAll(nodeTypes);
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }

  public Set<TagType> getTagTypes() {
    return tagTypes;
  }  
  
  public Set<NodeType> getNodeTypes() {
    return nodeTypes;
  }  
}
