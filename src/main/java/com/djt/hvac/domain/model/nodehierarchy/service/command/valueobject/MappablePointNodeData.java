//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = MappablePointNodeData.Builder.class)
@JsonPropertyOrder({
  "id",
  "nodePath",
  "parentEquipmentTypeId",
  "name",
  "oldPointTemplateId",
  "pointTemplateId",  
  "oldDisplayName",
  "displayName",
  "oldUnitId",
  "unitId",
  "pointDataTypeId",
  "range",
  "metadataTags",
  "quantity"  
})
public class MappablePointNodeData implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private final Integer id;
  private final String nodePath;
  private final String oldDisplayName;
  private final String displayName;
  private final String name;
  private final Integer oldPointTemplateId;
  private final Integer pointTemplateId;
  private final Integer oldUnitId;
  private final Integer unitId;
  private final Integer pointDataTypeId;
  private final Integer parentEquipmentTypeId;
  private final String range; // Only has meaning when useGrouping=false
  private final List<String> metadataTags; // Only has meaning when useGrouping=false
  private final Integer quantity; // Only has meaning when useGrouping=true

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(MappablePointNodeData data) {
    return new Builder(data);
  }

  private MappablePointNodeData(Builder builder) {
    this.id = builder.id;
    this.nodePath = builder.nodePath;
    this.oldDisplayName = builder.oldDisplayName;
    this.displayName = builder.displayName;
    this.name = builder.name;
    this.oldPointTemplateId = builder.oldPointTemplateId;
    this.pointTemplateId = builder.pointTemplateId;
    this.oldUnitId = builder.oldUnitId;
    this.unitId = builder.unitId;
    this.pointDataTypeId = builder.pointDataTypeId;
    this.parentEquipmentTypeId = builder.parentEquipmentTypeId;
    this.range = builder.range;
    this.metadataTags = builder.metadataTags;
    this.quantity = builder.quantity;
  }
  
  public Integer getId() {
    return id;
  }
  
  public String getNodePath() {
    return nodePath;
  }
  
  public String getOldDisplayName() {
    return oldDisplayName;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public String getName() {
    return name;
  }
  
  public Integer getOldPointTemplateId() {
    return oldPointTemplateId;
  }
  
  public Integer getPointTemplateId() {
    return pointTemplateId;
  }

  public Integer getOldUnitId() {
    return oldUnitId;
  }
  
  public Integer getUnitId() {
    return unitId;
  }
    
  public Integer getPointDataTypeId() {
    return pointDataTypeId;
  }

  public Integer getParentEquipmentTypeId() {
    return parentEquipmentTypeId;
  }

  public String getRange() {
    return range;
  }
  
  public List<String> getMetadataTags() {
    return metadataTags;
  }

  public Integer getQuantity() {
    return quantity;
  }
  
  @JsonIgnore
  private void checkRequiredField(Object object, String validationMessage) {
    if (object == null) {
      throw new IllegalArgumentException(validationMessage);
    }
  }
  
  public static Integer calculatePointGroupHashValue(
      Integer parentEquipmentTypeId,
      String name,
      Integer pointTemplateId,
      String displayName,
      Integer unitId,
      Integer pointDataTypeId,
      Integer quantity) {
    
    String identity = new StringBuilder()
        .append(parentEquipmentTypeId.toString())
        .append("_")
        .append(name)
        .append("_")
        .append(displayName)
        .append("_")
        .append(pointTemplateId.toString())
        .append("_")
        .append(unitId.toString())
        .append("_")
        .append(pointDataTypeId.toString())
        .append("_")
        .append(quantity.toString())
        .append("_")
        .toString();
    
     return Integer.valueOf(identity.hashCode());
  }
 
  public static class Builder {
    private Integer id;
    private String nodePath;
    private String oldDisplayName;
    private String displayName;
    private String name;
    private Integer oldPointTemplateId;
    private Integer pointTemplateId;
    private Integer oldUnitId;
    private Integer unitId;
    private Integer pointDataTypeId;
    private Integer parentEquipmentTypeId;
    private String range; // Only has meaning when useGrouping=false
    private List<String> metadataTags; // Only has meaning when useGrouping=false
    private Integer quantity; // Only has meaning when useGrouping=true

    private Builder() {}

    private Builder(MappablePointNodeData mappablePointNodeData) {
      requireNonNull(mappablePointNodeData, "response cannot be null");
      this.id = mappablePointNodeData.id;
      this.nodePath = mappablePointNodeData.nodePath;
      this.oldDisplayName = mappablePointNodeData.oldDisplayName;
      this.displayName = mappablePointNodeData.displayName;
      this.name = mappablePointNodeData.name;
      this.oldPointTemplateId = mappablePointNodeData.oldPointTemplateId;
      this.pointTemplateId = mappablePointNodeData.pointTemplateId;
      this.oldUnitId = mappablePointNodeData.oldUnitId;
      this.unitId = mappablePointNodeData.unitId;
      this.pointDataTypeId = mappablePointNodeData.pointDataTypeId;
      this.parentEquipmentTypeId = mappablePointNodeData.parentEquipmentTypeId;
      this.range = mappablePointNodeData.range;
      this.metadataTags = mappablePointNodeData.metadataTags;
      this.quantity = mappablePointNodeData.quantity;
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
    
    public Builder withNodePath(String nodePath) {
      this.nodePath = nodePath;
      return this;
    }
    
    public Builder withOldDisplayName(String oldDisplayName) {
      this.oldDisplayName = oldDisplayName;
      return this;
    }
    
    public Builder withDisplayName(String displayName) {
      requireNonNull(displayName, "displayName cannot be null");
      this.displayName = displayName;
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }
    
    public Builder withOldPointTemplateId(Integer oldPointTemplateId) {
      this.oldPointTemplateId = oldPointTemplateId;
      return this;
    }
    
    public Builder withPointTemplateId(Integer pointTemplateId) {
      this.pointTemplateId = pointTemplateId;
      return this;
    }

    public Builder withOldUnitId(Integer oldUnitId) {
      this.oldUnitId = oldUnitId;
      return this;
    }

    public Builder withUnitId(Integer unitId) {
      this.unitId = unitId;
      return this;
    }

    public Builder withPointDataTypeId(Integer pointDataTypeId) {
      requireNonNull(pointDataTypeId, "pointDataTypeId cannot be null");
      this.pointDataTypeId = pointDataTypeId;
      return this;
    }
    
    public Builder withParentEquipmentTypeId(Integer parentEquipmentTypeId) {
      this.parentEquipmentTypeId = parentEquipmentTypeId;
      return this;
    }

    public Builder withRange(String range) {
      this.range = range;
      return this;
    }

    public Builder withMetadataTags(List<String> metadataTags) {
      this.metadataTags = metadataTags;
      return this;
    }
    
    public Builder withQuantity(Integer quantity) {
      this.quantity = quantity;
      return this;
    }
    
    @JsonIgnore
    private void checkRequiredField(Object object, String validationMessage) {
      if (object == null) {
        throw new IllegalArgumentException(validationMessage);
      }
    }
    
    public MappablePointNodeData build() {
      checkRequiredField(id, "id cannot be null.");
      if (this.metadataTags == null) {
        checkRequiredField(displayName, "displayName cannot be null.");
        checkRequiredField(pointTemplateId, "pointTemplateId cannot be null.");
        checkRequiredField(unitId, "unitId cannot be null.");
        checkRequiredField(pointDataTypeId, "pointDataTypeId cannot be null.");
      }
      return new MappablePointNodeData(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((nodePath == null) ? 0 : nodePath.hashCode());
    result = prime * result + ((oldDisplayName == null) ? 0 : oldDisplayName.hashCode());
    result = prime * result + ((oldPointTemplateId == null) ? 0 : oldPointTemplateId.hashCode());
    result = prime * result + ((oldUnitId == null) ? 0 : oldUnitId.hashCode());
    result =
        prime * result + ((parentEquipmentTypeId == null) ? 0 : parentEquipmentTypeId.hashCode());
    result = prime * result + ((pointDataTypeId == null) ? 0 : pointDataTypeId.hashCode());
    result = prime * result + ((pointTemplateId == null) ? 0 : pointTemplateId.hashCode());
    result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
    result = prime * result + ((range == null) ? 0 : range.hashCode());
    result = prime * result + ((metadataTags == null) ? 0 : metadataTags.hashCode());
    result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
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
    MappablePointNodeData other = (MappablePointNodeData) obj;
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
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (nodePath == null) {
      if (other.nodePath != null)
        return false;
    } else if (!nodePath.equals(other.nodePath))
      return false;
    if (oldDisplayName == null) {
      if (other.oldDisplayName != null)
        return false;
    } else if (!oldDisplayName.equals(other.oldDisplayName))
      return false;
    if (oldPointTemplateId == null) {
      if (other.oldPointTemplateId != null)
        return false;
    } else if (!oldPointTemplateId.equals(other.oldPointTemplateId))
      return false;
    if (oldUnitId == null) {
      if (other.oldUnitId != null)
        return false;
    } else if (!oldUnitId.equals(other.oldUnitId))
      return false;
    if (parentEquipmentTypeId == null) {
      if (other.parentEquipmentTypeId != null)
        return false;
    } else if (!parentEquipmentTypeId.equals(other.parentEquipmentTypeId))
      return false;
    if (pointDataTypeId == null) {
      if (other.pointDataTypeId != null)
        return false;
    } else if (!pointDataTypeId.equals(other.pointDataTypeId))
      return false;
    if (pointTemplateId == null) {
      if (other.pointTemplateId != null)
        return false;
    } else if (!pointTemplateId.equals(other.pointTemplateId))
      return false;
    if (quantity == null) {
      if (other.quantity != null)
        return false;
    } else if (!quantity.equals(other.quantity))
      return false;
    if (range == null) {
      if (other.range != null)
        return false;
    } else if (!range.equals(other.range))
      return false;
    if (metadataTags == null) {
      if (other.metadataTags != null)
        return false;
    } else if (!metadataTags.equals(other.metadataTags))
      return false;
    if (unitId == null) {
      if (other.unitId != null)
        return false;
    } else if (!unitId.equals(other.unitId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("MappablePointNodeData [id=")
        .append(id)
        .append(", nodePath=")
        .append(nodePath)
        .append(", oldDisplayName=")
        .append(oldDisplayName)
        .append(", displayName=")
        .append(displayName)
        .append(", name=")
        .append(name)
        .append(", oldPointTemplateId=")
        .append(oldPointTemplateId)
        .append(", pointTemplateId=")
        .append(pointTemplateId)
        .append(", oldUnitId=")
        .append(oldUnitId)
        .append(", unitId=")
        .append(unitId)
        .append(", pointDataTypeId=")
        .append(pointDataTypeId)
        .append(", parentEquipmentTypeId=")
        .append(parentEquipmentTypeId)
        .append(", range=")
        .append(range)
        .append(", metadataTags=")
        .append(metadataTags)
        .append(", quantity=")
        .append(quantity)
        .append("]").
        toString();
  }
}
//@formatter:on