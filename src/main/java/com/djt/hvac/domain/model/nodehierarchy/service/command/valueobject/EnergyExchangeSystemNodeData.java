//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.common.query.model.QueryResponseItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = EnergyExchangeSystemNodeData.Builder.class)
@JsonPropertyOrder({
  "id",
  "buildingId",
  "nodePath",
  "displayName",
  "typeId",
  "nodeType",
  "systemTypeId",
  "parentIds",
  "childIds",
  "metadataTags",
  "metadataTagIds",
  "convertToNodeType"
})
public class EnergyExchangeSystemNodeData extends QueryResponseItem implements Serializable {
  
  private static final long serialVersionUID = 6410851857010839706L;
  
  // When equipment type id is set to -1, then we null it out
  public static final Integer NULL = Integer.valueOf(-1);
  
  // When equipment type id is set to -2, then we leave it alone
  public static final Integer IGNORE = Integer.valueOf(-2);
  
  public static final String NODE_TYPE_EQUIPMENT = "EQUIPMENT";
  public static final String NODE_TYPE_PLANT = "PLANT";
  public static final String NODE_TYPE_LOOP = "LOOP";
  
  public static final Integer CHILLED_WATER_SYSTEM_TYPE_ID = Integer.valueOf(1);
  public static final Integer HOT_WATER_SYSTEM_TYPE_ID = Integer.valueOf(2);
  public static final Integer STEAM_SYSTEM_TYPE_ID = Integer.valueOf(3);
  public static final Integer AIR_SUPPLY_SYSTEM_TYPE_ID = Integer.valueOf(4);
  
  private final Integer id;
  private final Integer buildingId;
  private final String nodePath;
  private final String displayName;
  private final Integer typeId;
  private final String nodeType;
  private final Integer systemTypeId;
  private final List<Integer> parentIds;
  private final List<Integer> childIds;
  private final List<String> metadataTags;
  private final List<Integer> metadataTagIds;
  private final String convertToNodeType;
  private final List<EnergyExchangeParentChildData> parents;
  private final List<EnergyExchangeParentChildData> children;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(EnergyExchangeSystemNodeData data) {
    return new Builder(data);
  }

  private EnergyExchangeSystemNodeData(Builder builder) {
    this.id = builder.id;
    this.buildingId = builder.buildingId;
    this.nodePath = builder.nodePath;
    this.displayName = builder.displayName;
    this.typeId = builder.typeId;
    this.nodeType = builder.nodeType;
    this.systemTypeId = builder.systemTypeId;
    this.parentIds = builder.parentIds;
    this.childIds = builder.childIds;
    this.metadataTags = builder.metadataTags;
    this.metadataTagIds = builder.metadataTagIds;
    this.convertToNodeType = builder.convertToNodeType;
    this.parents = builder.parents;
    this.children = builder.children;
  }
  
  public Integer getId() {
    return id;
  }
  
  public Integer getBuildingId() {
    return buildingId;
  }
  
  public String getNodePath() {
    return nodePath;
  }
  
  public String getDisplayName() {
    return displayName;
  }
  
  public Integer getTypeId() {
    return typeId;
  }

  public String getNodeType() {
    return nodeType;
  }
  
  public Integer getSystemTypeId() {
    return systemTypeId;
  }
  
  public List<Integer> getParentIds() {
    return parentIds;
  }

  public List<Integer> getChildIds() {
    return childIds;
  }
  
  public List<String> getMetadataTags() {
    return metadataTags;
  }
  
  public List<Integer> getMetadataTagIds() {
    return metadataTagIds;
  }
  
  public String getConvertToNodeType() {
    return convertToNodeType;
  }
  
  public List<EnergyExchangeParentChildData> getParents() {
    return parents;
  }
  
  public List<EnergyExchangeParentChildData> getChildren() {
    return children;
  }
  
  public static class Builder {
    private Integer id;
    private Integer buildingId;
    private String nodePath;
    private String displayName;
    private Integer typeId = IGNORE; // We will not update the type if set to this, as null represents removing the type.
    private String nodeType = NODE_TYPE_EQUIPMENT;
    private Integer systemTypeId;
    private List<Integer> parentIds;
    private List<Integer> childIds;
    private List<String> metadataTags;
    private List<Integer> metadataTagIds;
    private String convertToNodeType;
    private List<EnergyExchangeParentChildData> parents;
    private List<EnergyExchangeParentChildData> children;

    private Builder() {}

    private Builder(EnergyExchangeSystemNodeData request) {
      requireNonNull(request, "request cannot be null");
      this.id = request.id;
      this.buildingId = request.buildingId;
      this.nodePath = request.nodePath;
      this.displayName = request.displayName;
      this.typeId = request.typeId;
      this.nodeType = request.nodeType;
      this.systemTypeId = request.systemTypeId;
      this.parentIds = request.parentIds;
      this.childIds = request.childIds;
      this.metadataTags = request.metadataTags;
      this.metadataTagIds = request.metadataTagIds;
      this.convertToNodeType = request.convertToNodeType;
      this.parents = request.parents;
      this.children = request.children;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(Integer id) {
      requireNonNull(id, "id cannot be null");
      this.id = id;
      return this;
    }

    public Builder withBuildingId(Integer buildingId) {
      this.buildingId = buildingId;
      return this;
    }

    public Builder withNodePath(String nodePath) {
      requireNonNull(nodePath, "nodePath cannot be null");
      this.nodePath = nodePath;
      return this;
    }
    
    public Builder withNodeType(String nodeType) {
      requireNonNull(nodeType, "nodeType cannot be null");
      if (!nodeType.equals(NODE_TYPE_EQUIPMENT) 
          && !nodeType.equals(NODE_TYPE_PLANT) 
          && !nodeType.equals(NODE_TYPE_LOOP)) {
        throw new IllegalArgumentException("'nodeType' must be one of EQUIPMENT, PLANT or LOOP");
      }
      this.nodeType = nodeType;
      return this;
    }
    
    public Builder withTypeId(Integer typeId) {
      this.typeId = typeId;
      return this;
    }
    
    public Builder withDisplayName(String displayName) {
      requireNonNull(displayName, "displayName cannot be null");
      this.displayName = displayName;
      return this;
    }
    
    public Builder withSystemTypeId(Integer systemTypeId) {
      this.systemTypeId = systemTypeId;
      return this;
    }

    public Builder withParentIds(List<Integer> parentIds) {
      this.parentIds = parentIds;
      return this;
    }

    public Builder withChildIds(List<Integer> childIds) {
      this.childIds = childIds;
      return this;
    }
    
    public Builder withMetadataTags(List<String> metadataTags) {
      this.metadataTags = metadataTags;
      return this;
    }

    public Builder withMetadataTagIds(List<Integer> metadataTagIds) {
      this.metadataTagIds = metadataTagIds;
      return this;
    }

    public Builder withConvertToNodeType(String convertToNodeType) {
      this.convertToNodeType = convertToNodeType;
      return this;
    }

    public Builder withParents(List<EnergyExchangeParentChildData> parents) {
      this.parents = parents;
      return this;
    }
    
    public Builder withChildren(List<EnergyExchangeParentChildData> children) {
      this.children = children;
      return this;
    }
    
    public EnergyExchangeSystemNodeData build() {
      requireNonNull(id, "id cannot be null");
      if (this.parentIds != null || this.childIds != null) {
        requireNonNull(systemTypeId, "systemTypeId cannot be null when either parentIds or childIds are specified");  
      }
      requireNonNull(nodeType, "nodeType cannot be null");
      
      if (convertToNodeType != null) {
        
        if (nodeType.equals(NODE_TYPE_EQUIPMENT) 
            && !convertToNodeType.equals(NODE_TYPE_PLANT) 
            && !convertToNodeType.equals(NODE_TYPE_LOOP)) {
          
          throw new IllegalArgumentException("For EQUIPMENT node type, 'convertToNodeType' must be one of PLANT or LOOP");
          
        } else if (nodeType.equals(NODE_TYPE_PLANT) 
            && !convertToNodeType.equals(NODE_TYPE_EQUIPMENT) 
            && !convertToNodeType.equals(NODE_TYPE_LOOP)) {
          
          throw new IllegalArgumentException("For PLANT node type, 'convertToNodeType' must be one of EQUIPMENT or LOOP");
          
        } else if (nodeType.equals(NODE_TYPE_LOOP) 
            && !convertToNodeType.equals(NODE_TYPE_PLANT) 
            && !convertToNodeType.equals(NODE_TYPE_EQUIPMENT)) {
          
          throw new IllegalArgumentException("For LOOP node type, 'convertToNodeType' must be one of PLANT or EQUIPMENT");
          
        }
      }
      
      return new EnergyExchangeSystemNodeData(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((metadataTags == null) ? 0 : metadataTags.hashCode());
    result = prime * result + ((metadataTagIds == null) ? 0 : metadataTagIds.hashCode());
    result = prime * result + ((nodePath == null) ? 0 : nodePath.hashCode());
    result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
    result = prime * result + ((parentIds == null) ? 0 : parentIds.hashCode());
    result = prime * result + ((childIds == null) ? 0 : childIds.hashCode());
    result = prime * result + ((systemTypeId == null) ? 0 : systemTypeId.hashCode());
    result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
    result = prime * result + ((convertToNodeType == null) ? 0 : convertToNodeType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EnergyExchangeSystemNodeData other = (EnergyExchangeSystemNodeData) obj;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (metadataTags == null) {
      if (other.metadataTags != null)
        return false;
    } else if (!metadataTags.equals(other.metadataTags))
      return false;
    if (metadataTagIds == null) {
      if (other.metadataTagIds != null)
        return false;
    } else if (!metadataTagIds.equals(other.metadataTagIds))
      return false;
    if (nodePath == null) {
      if (other.nodePath != null)
        return false;
    } else if (!nodePath.equals(other.nodePath))
      return false;
    if (nodeType == null) {
      if (other.nodeType != null)
        return false;
    } else if (!nodeType.equals(other.nodeType))
      return false;
    if (parentIds == null) {
      if (other.parentIds != null)
        return false;
    } else if (!parentIds.equals(other.parentIds))
      return false;
    if (childIds == null) {
      if (other.childIds != null)
        return false;
    } else if (!childIds.equals(other.childIds))
      return false;
    if (systemTypeId == null) {
      if (other.systemTypeId != null)
        return false;
    } else if (!systemTypeId.equals(other.systemTypeId))
      return false;
    if (typeId == null) {
      if (other.typeId != null)
        return false;
    } else if (!typeId.equals(other.typeId))
      return false;
    if (convertToNodeType == null) {
      if (other.convertToNodeType != null)
        return false;
    } else if (!convertToNodeType.equals(other.convertToNodeType))
      return false;
    return true;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("EnergyExchangeSystemNodeData [id=")
        .append(id)
        .append(", buildingId=")
        .append(buildingId)
        .append(", nodePath=")
        .append(nodePath)
        .append(", displayName=")
        .append(displayName)
        .append(", typeId=")
        .append(typeId)
        .append(", nodeType=")
        .append(nodeType)
        .append(", systemTypeId=")
        .append(systemTypeId)
        .append(", parentIds=")
        .append(parentIds)
        .append(", childIds=")
        .append(childIds)
        .append(", metadataTags=")
        .append(metadataTags)
        .append(", metadataTagIds=")
        .append(metadataTagIds)
        .append(", convertToNodeType=")
        .append(convertToNodeType)
        .append("]")
        .toString();
  }
}
//@formatter:on