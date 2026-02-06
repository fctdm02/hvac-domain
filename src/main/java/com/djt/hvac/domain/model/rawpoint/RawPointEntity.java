package com.djt.hvac.domain.model.rawpoint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.rawpoint.dto.RawPointDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RawPointEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(RawPointEntity.class);

  private Integer customerId;
  private Integer componentId;
  private String metricId;
  private String pointType;
  private String range;
  private String unitType;
  private Boolean ignored;
  private Boolean deleted;
  private String createdAt;
  
  public RawPointEntity() {}
  
  public RawPointEntity(
      Integer persistentIdentity,
      Integer customerId,
      Integer componentId,
      String metricId,
      String pointType,
      String range,
      String unitType,
      Boolean ignored,
      Boolean deleted) {
    super(persistentIdentity);
    this.customerId = customerId;
    this.componentId = componentId;
    this.metricId = metricId;
    this.pointType = pointType;
    this.range = range;
    this.unitType = null;
    this.ignored = ignored;
    this.deleted = deleted;
    this.createdAt = null;
  }
  
  public RawPointEntity(
      Integer persistentIdentity,
      Integer customerId,
      Integer componentId,
      String metricId,
      String pointType,
      String range,
      String unitType,
      Boolean ignored,
      Boolean deleted,
      String createdAt) {
    super(persistentIdentity);
    this.customerId = customerId;
    this.componentId = componentId;
    this.metricId = metricId;
    this.pointType = pointType;
    this.range = range;
    this.unitType = unitType;
    this.ignored = ignored;
    this.deleted = deleted;
    this.createdAt = createdAt;
  }
  
  public String getNaturalIdentity() {
    return new StringBuilder()
        .append("customerId: ")
        .append(customerId)
        .append(", componentId: ")
        .append(componentId)
        .append(", metricId: ")
        .append(this.metricId)
        .toString();
  }

  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    // DO NOTHING
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public Integer getComponentId() {
    return componentId;
  }

  public String getMetricId() {
    return metricId;
  }

  public String getPointType() {
    return pointType;
  }

  public String getRange() {
    return range;
  }

  public String getUnitType() {
    return unitType;
  }

  public Boolean getIgnored() {
    return ignored;
  }
  
  public void setIgnored(Boolean ignored) {
    
    if (ignored != null && !this.ignored.equals(ignored)) {

      this.ignored = ignored;
      this.setIsModified("ignored");
    }
  }

  public Boolean getDeleted() {
    return deleted;
  }

  public String getCreatedAtAsString() {
    return createdAt;
  }
  
  @JsonIgnore
  public Timestamp getCreatedAt() {
    
    if (createdAt != null && !createdAt.isEmpty()) {
      return AbstractEntity.parseTimestamp(createdAt);  
    }
    return null;
  }
  
  public static class Mapper implements DtoMapper<AbstractCustomerEntity, RawPointEntity, RawPointDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    public List<RawPointDto> mapEntitiesToDtos(List<RawPointEntity> entities) {
      
      List<RawPointDto> list = new ArrayList<>();
      for (RawPointEntity entity: entities) {
        list.add(mapEntityToDto(entity));  
      }
      return list;
    }
    
    @Override
    public RawPointDto mapEntityToDto(RawPointEntity entity) {
      
      RawPointDto dto = new RawPointDto();
      dto.setId(entity.getPersistentIdentity());
      dto.setComponentId(entity.getComponentId());
      dto.setMetricId(entity.getMetricId());
      dto.setPointType(entity.getPointType());
      dto.setRange(entity.getRange());
      dto.setUnitType(entity.getUnitType());
      dto.setIgnore(entity.getIgnored());
      dto.setDeleted(entity.getDeleted());
      dto.setCreatedAt(entity.getCreatedAtAsString());
      return dto;
    }
    
    public List<RawPointEntity> mapDtosToEntities(Integer customerId, List<RawPointDto> dtos) {
      
      List<RawPointEntity> list = new ArrayList<>();
      for (RawPointDto dto: dtos) {
        list.add(mapDtoToEntity(customerId, dto));  
      }
      return list;
    }
    
    @Override
    public RawPointEntity mapDtoToEntity(AbstractCustomerEntity parentCustomer, RawPointDto dto) {
     
      return this.mapDtoToEntity(parentCustomer.getPersistentIdentity(), dto);
    }
    
    public RawPointEntity mapDtoToEntity(Integer customerId, RawPointDto dto) {
      
      RawPointEntity entity = null;
      try {
         entity = new RawPointEntity(
            dto.getId(),
            customerId,
            dto.getComponentId(),
            dto.getMetricId(),
            dto.getPointType(),
            dto.getRange(),
            dto.getUnitType(),
            dto.getIgnore(),
            dto.getDeleted(),
            dto.getCreatedAt());        
        
      } catch (Exception e) {
        LOGGER.error("Error: [{}], unable to map: [{}]",
            e.getMessage(),
            dto,
            e);
      }
      return entity;
    }
  }    
}