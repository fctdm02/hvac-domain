package com.djt.hvac.domain.model.component;

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
import com.djt.hvac.domain.model.component.dto.CustomerComponentDto;
import com.djt.hvac.domain.model.component.enums.ComponentType;

public abstract class AbstractCustomerComponentEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCustomerComponentEntity.class);

  private final Integer customerId;
  private ComponentType componentType;
  private String uuid;
  private String name;
  private String ipAddress;
  private Timestamp createdAt;
  private Timestamp updatedAt;

  public AbstractCustomerComponentEntity(
      Integer customerId,
      ComponentType componentType,
      String uuid,
      String name,
      String ipAddress,
      Timestamp createdAt,
      Timestamp updatedAt) {
    this(
        null,
        customerId,
        componentType,
        uuid,
        name,
        ipAddress,
        createdAt,
        updatedAt);
  }
  
  public AbstractCustomerComponentEntity(
      Integer persistentIdentity,
      Integer customerId,
      ComponentType componentType,
      String uuid,
      String name,
      String ipAddress,
      Timestamp createdAt,
      Timestamp updatedAt) {
    super(persistentIdentity);
    this.customerId = customerId;
    this.componentType = componentType;
    this.uuid = uuid;
    this.name = name;
    this.ipAddress = ipAddress;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
  
  public String getNaturalIdentity() {
    return new StringBuilder()
        .append("customerId: ")
        .append(customerId)
        .append(", name: ")
        .append(this.name)
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

  public ComponentType getComponentType() {
    return componentType;
  }

  public void setComponentType(ComponentType componentType) {
    this.componentType = componentType;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }

  public static class Mapper implements DtoMapper<Integer, AbstractCustomerComponentEntity, CustomerComponentDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    public List<CustomerComponentDto> mapEntitiesToDtos(List<AbstractCustomerComponentEntity> entities) {
      
      List<CustomerComponentDto> list = new ArrayList<>();
      for (AbstractCustomerComponentEntity entity: entities) {
        list.add(mapEntityToDto(entity));  
      }
      return list;
    }
    
    @Override
    public CustomerComponentDto mapEntityToDto(AbstractCustomerComponentEntity entity) {
      
      CustomerComponentDto dto = new CustomerComponentDto();
      dto.setId(entity.getPersistentIdentity());
      dto.setCustomer(entity.getCustomerId());
      dto.setComponentType(entity.getComponentType().toString());
      dto.setUuid(entity.getUuid());
      dto.setName(entity.getName());
      dto.setIpAddress(entity.getIpAddress());
      dto.setCreatedAt(AbstractEntity.formatTimestamp(entity.getCreatedAt()));
      dto.setCreatedAt(AbstractEntity.formatTimestamp(entity.getUpdatedAt()));
      
      if (entity instanceof AbstractCloudfillCustomerComponentEntity) {
        
        AbstractCloudfillCustomerComponentEntity accc = (AbstractCloudfillCustomerComponentEntity)entity; 
        
        dto.setActive(accc.getActive());
        dto.setCloudfillConnectorStatus(accc.getCloudfillConnectorStatus().toString());
        dto.setDeleted(accc.getDeleted());
        
        if (entity instanceof AbstractConfigurableCloudfillCustomerComponentEntity) {
          
          dto.setConfigJson(((AbstractConfigurableCloudfillCustomerComponentEntity)entity).getConfigJson());
        }
      }
      
      return dto;
    }
    
    public List<AbstractCustomerComponentEntity> mapDtosToEntities(Integer customerId, List<CustomerComponentDto> dtos) {
      
      List<AbstractCustomerComponentEntity> list = new ArrayList<>();
      for (CustomerComponentDto dto: dtos) {
        list.add(mapDtoToEntity(customerId, dto));  
      }
      return list;
    }
    
    @Override
    public AbstractCustomerComponentEntity mapDtoToEntity(Integer customerId, CustomerComponentDto dto) {
      
      AbstractCustomerComponentEntity entity = null;
      try {

        ComponentType ct = ComponentType.valueOf(dto.getComponentType());
        if (ct.equals(ComponentType.RESOLUTE_AGENT_1)
            || ct.equals(ComponentType.RESOLUTE_SECURITY_GATEWAY_2)
            || ct.equals(ComponentType.RESOLUTE_NIAGARA_MODULES_3)
            || ct.equals(ComponentType.RESOLUTE_PROVISIONING_SERVICE_4)
            || ct.equals(ComponentType.RESOLUTE_SSO_PROXY_5)) {
          
          entity = new LegacyCustomerComponentEntity(
              dto.getId(),
              customerId,
              ComponentType.valueOf(dto.getComponentType()),
              dto.getUuid(),
              dto.getName(),
              dto.getIpAddress(),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()));
          
        } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_Verdigris_1)) {

          entity = new VerdigrisCloudfillCustomerComponentEntity(
              dto.getId(),
              customerId,
              ComponentType.valueOf(dto.getComponentType()),
              dto.getUuid(),
              dto.getName(),
              dto.getIpAddress(),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              dto.getActive(),
              dto.getDeleted());
          
        } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_KMC_2)) {

          entity = new KmcCloudfillCustomerComponentEntity(
              dto.getId(),
              customerId,
              ComponentType.valueOf(dto.getComponentType()),
              dto.getUuid(),
              dto.getName(),
              dto.getIpAddress(),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              dto.getActive(),
              dto.getDeleted(),
              dto.getConfigJson());
          
        } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_NHaystack_3)) {

          entity = new NHaystackCloudfillCustomerComponentEntity(
              dto.getId(),
              customerId,
              ComponentType.valueOf(dto.getComponentType()),
              dto.getUuid(),
              dto.getName(),
              dto.getIpAddress(),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              dto.getActive(),
              dto.getDeleted(),
              dto.getConfigJson());
          
        } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_Demo_4)) {

          entity = new DemoCloudfillCustomerComponentEntity(
              dto.getId(),
              customerId,
              ComponentType.valueOf(dto.getComponentType()),
              dto.getUuid(),
              dto.getName(),
              dto.getIpAddress(),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              dto.getActive(),
              dto.getDeleted());
          
        } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_Fin_5)) {

          entity = new FinCloudfillCustomerComponentEntity(
              dto.getId(),
              customerId,
              ComponentType.valueOf(dto.getComponentType()),
              dto.getUuid(),
              dto.getName(),
              dto.getIpAddress(),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              dto.getActive(),
              dto.getDeleted(),
              dto.getConfigJson());
          
        } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_Desigo_6)) {

          entity = new DesigoCcCloudfillCustomerComponentEntity(
              dto.getId(),
              customerId,
              ComponentType.valueOf(dto.getComponentType()),
              dto.getUuid(),
              dto.getName(),
              dto.getIpAddress(),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              dto.getActive(),
              dto.getDeleted(),
              dto.getConfigJson());
          
        } else if (ct.equals(ComponentType.CLOUDFILL_AGENT_6_Hawken_AQ_7)) {

          entity = new HawkenAcCloudfillCustomerComponentEntity(
              dto.getId(),
              customerId,
              ComponentType.valueOf(dto.getComponentType()),
              dto.getUuid(),
              dto.getName(),
              dto.getIpAddress(),
              AbstractEntity.parseTimestamp(dto.getCreatedAt()),
              AbstractEntity.parseTimestamp(dto.getUpdatedAt()),
              dto.getActive(),
              dto.getDeleted(),
              dto.getConfigJson());
          
        }
        
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