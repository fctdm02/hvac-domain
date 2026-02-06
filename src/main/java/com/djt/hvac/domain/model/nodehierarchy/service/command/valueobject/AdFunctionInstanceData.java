//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.common.query.model.QueryResponseItem;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableMap;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdFunctionInstanceData.Builder.class)
public class AdFunctionInstanceData extends QueryResponseItem {

  private final Integer adFunctionInstanceId;
  private final Integer energyExchangeId;
  private final Integer adFunctionTemplateId;
  private final String nodePath;
  private final Integer energyExchangeTypeId;
  private final String adFunctionTemplateName;
  private final String adFunctionTemplateFaultNumber;
  private final String adFunctionTemplateDescription;
  private final boolean ignore;
  private final Map<Integer, String> dataByConstantIds;
  
  // Used by the query/view
  private List<AdFunctionInstanceInputConstant> inputConstants;
  private List<AdFunctionInstanceInputPoint> inputPoints;
  private Integer outputPointId;
  private String outputPointDescription;
  private String outputPointNodePath;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdFunctionInstanceData adFunctionInstanceData) {
    return new Builder(adFunctionInstanceData);
  }

  private AdFunctionInstanceData (Builder builder) {

    this.adFunctionInstanceId = builder.adFunctionInstanceId;
    this.energyExchangeId = builder.energyExchangeId;
    this.adFunctionTemplateId = builder.adFunctionTemplateId;
    this.nodePath = builder.nodePath;
    this.energyExchangeTypeId = builder.energyExchangeTypeId;
    this.adFunctionTemplateName = builder.adFunctionTemplateName;
    this.adFunctionTemplateFaultNumber = builder.adFunctionTemplateFaultNumber;
    this.adFunctionTemplateDescription = builder.adFunctionTemplateDescription;
    this.ignore = builder.ignore;
    this.dataByConstantIds = builder.dataByConstantIds;
    this.inputConstants = builder.inputConstants;
    this.inputPoints = builder.inputPoints;
    this.outputPointId = builder.outputPointId;
    this.outputPointDescription = builder.outputPointDescription;
    this.outputPointNodePath = builder.outputPointNodePath;
  }
  
  public Integer getAdFunctionInstanceId() {
    return adFunctionInstanceId;
  }

  public Integer getEnergyExchangeId() {
    return energyExchangeId;
  }

  public Integer getAdFunctionTemplateId() {
    return adFunctionTemplateId;
  }
  
  public String getNodePath() {
    return nodePath;
  }
  
  public Integer getEnergyExchangeTypeId() {
    return energyExchangeTypeId;
  }
  
  public String getAdFunctionTemplateName() {
    return adFunctionTemplateName;
  }
  
  public String getAdFunctionTemplateFaultNumber() {
    return adFunctionTemplateFaultNumber;
  }
  
  public String getAdFunctionTemplateDescription() {
    return adFunctionTemplateDescription;
  }
  
  public boolean getIgnore() {
    return ignore;
  }
  
  public Map<Integer, String> getDataByConstantIds() {
    return dataByConstantIds;
  }
  
  public void addInputConstant(AdFunctionInstanceInputConstant inputConstant) {
    if (this.inputConstants == null) {
      this.inputConstants = new ArrayList<>();
    }
    this.inputConstants.add(inputConstant);
  }  
  
  public List<AdFunctionInstanceInputConstant> getInputConstants() {
    return this.inputConstants;
  }
  
  public void addInputPoint(AdFunctionInstanceInputPoint inputPoint) {
    if (this.inputPoints == null) {
      this.inputPoints = new ArrayList<>();
    }
    this.inputPoints.add(inputPoint);
  }  
  
  public List<AdFunctionInstanceInputPoint> getInputPoints() {
    return this.inputPoints;
  }
  
  public void setOutputPointId(Integer outputPointId) {
    this.outputPointId = outputPointId;
  }
  
  public Integer getOutputPointId() {
    return outputPointId;
  }

  public void setOutputPointDescription(String outputPointDescription) {
    this.outputPointDescription = outputPointDescription;
  }
  
  public String getOutputPointDescription() {
    return outputPointDescription;
  }

  public void setOutputPointNodePath(String outputPointNodePath) {
    this.outputPointNodePath = outputPointNodePath;
  }
  
  public String getOutputPointNodePath() {
    return outputPointNodePath;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("AdFunctionInstanceData [energyExchangeId=")
        .append(energyExchangeId)
        .append(", adFunctionTemplateId=")
        .append(adFunctionTemplateId)
        .append(", ignore=")
        .append(ignore)
        .append(", dataByConstantIds=")
        .append(dataByConstantIds)
        .append("]")
        .toString();
  }

  @JsonIgnore
  public String getKey() {
    return buildKey(energyExchangeId, adFunctionTemplateId);
  }
  
  @JsonIgnore
  public static String buildKey(Integer energyExchangeId, Integer adFunctionTemplateId) {
    return Integer.toString(energyExchangeId) 
        + "_" 
        + Integer.toString(adFunctionTemplateId);
  }

  @JsonPOJOBuilder
  public static class Builder {
    
    private Integer adFunctionInstanceId;
    private Integer energyExchangeId;
    private Integer adFunctionTemplateId;
    private String nodePath;
    private Integer energyExchangeTypeId;
    private String adFunctionTemplateName;
    private String adFunctionTemplateFaultNumber;
    private String adFunctionTemplateDescription;    
    private boolean ignore;
    private Map<Integer, String> dataByConstantIds;
    
    // Used by the query/view
    private List<AdFunctionInstanceInputConstant> inputConstants;
    private List<AdFunctionInstanceInputPoint> inputPoints;
    private Integer outputPointId;
    private String outputPointDescription;
    private String outputPointNodePath;

    private Builder() {}

    private Builder(AdFunctionInstanceData adFunctionInstanceData) {
      requireNonNull(adFunctionInstanceData, "adFunctionInstanceData cannot be null");
      this.adFunctionInstanceId = adFunctionInstanceData.adFunctionInstanceId;
      this.energyExchangeId = adFunctionInstanceData.energyExchangeId;
      this.adFunctionTemplateId = adFunctionInstanceData.adFunctionTemplateId;
      this.nodePath = adFunctionInstanceData.nodePath;
      this.energyExchangeTypeId = adFunctionInstanceData.energyExchangeTypeId;
      this.adFunctionTemplateName = adFunctionInstanceData.adFunctionTemplateName;
      this.adFunctionTemplateFaultNumber = adFunctionInstanceData.adFunctionTemplateFaultNumber;
      this.adFunctionTemplateDescription = adFunctionInstanceData.adFunctionTemplateDescription;
      this.ignore = adFunctionInstanceData.ignore;
      this.dataByConstantIds = adFunctionInstanceData.dataByConstantIds;
      this.inputConstants = adFunctionInstanceData.inputConstants;
      this.inputPoints = adFunctionInstanceData.inputPoints;
      this.outputPointId = adFunctionInstanceData.outputPointId;
      this.outputPointDescription = adFunctionInstanceData.outputPointDescription;
      this.outputPointNodePath = adFunctionInstanceData.outputPointNodePath;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withAdFunctionInstanceId(Integer adFunctionInstanceId) {
      this.adFunctionInstanceId = adFunctionInstanceId;
      return this;
    }

    public Builder withEnergyExchangeId(Integer energyExchangeId) {
      requireNonNull(energyExchangeId, "energyExchangeId cannot be null");
      this.energyExchangeId = energyExchangeId;
      return this;
    }

    public Builder withAdFunctionTemplateId(Integer adFunctionTemplateId) {
      requireNonNull(adFunctionTemplateId, "adFunctionTemplateId cannot be null");
      this.adFunctionTemplateId = adFunctionTemplateId;
      return this;
    }

    public Builder withNodePath(String nodePath) {
      this.nodePath = nodePath;
      return this;
    }
    
    public Builder withEnergyExchangeTypeId(Integer energyExchangeTypeId) {
      requireNonNull(energyExchangeTypeId, "energyExchangeTypeId cannot be null");
      this.energyExchangeTypeId = energyExchangeTypeId;
      return this;
    }
    
    public Builder withAdFunctionTemplateName(String adFunctionTemplateName) {
      this.adFunctionTemplateName = adFunctionTemplateName;
      return this;
    }

    public Builder withAdFunctionTemplateFaultNumber(String adFunctionTemplateFaultNumber) {
      this.adFunctionTemplateFaultNumber = adFunctionTemplateFaultNumber;
      return this;
    }
    
    public Builder withAdFunctionTemplateDescription(String adFunctionTemplateDescription) {
      this.adFunctionTemplateDescription = adFunctionTemplateDescription;
      return this;
    }
    
    public Builder withIgnore(Boolean ignore) {
      if (ignore != null) {
        this.ignore = ignore;
      } else {
        this.ignore = false;
      }
      return this;
    }

    public Builder withDataByConstantIds(Map<Integer, String> dataByConstantIds) {
      requireNonNull(dataByConstantIds, "dataByConstantIds cannot be null");
      this.dataByConstantIds = ImmutableMap.copyOf(dataByConstantIds);  
      return this;
    }

    public Builder withInputConstants(List<AdFunctionInstanceInputConstant> inputConstants) {
      this.inputConstants = inputConstants;  
      return this;
    }
    
    public Builder withInputPoints(List<AdFunctionInstanceInputPoint> inputPoints) {
      this.inputPoints = inputPoints;  
      return this;
    }
    
    public Builder withOutputPointId(Integer outputPointId) {
      this.outputPointId = outputPointId;
      return this;
    }

    public Builder withOutputPointDescription(String outputPointDescription) {
      this.outputPointDescription = outputPointDescription;
      return this;
    }

    public Builder withOutputPointNodePath(String outputPointNodePath) {
      this.outputPointNodePath = outputPointNodePath;
      return this;
    }
    
    public AdFunctionInstanceData build() {
      requireNonNull(energyExchangeId, "energyExchangeId cannot be null");
      requireNonNull(adFunctionTemplateId, "adFunctionTemplateId cannot be null");
      return new AdFunctionInstanceData(this);
    }
  }
}
//@formatter:on