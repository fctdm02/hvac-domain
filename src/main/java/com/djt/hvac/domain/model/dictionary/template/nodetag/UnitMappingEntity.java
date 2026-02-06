//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.nodetag;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.dto.UnitMappingDto;

public class UnitMappingEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final UnitEntity ipUnit;
  private final UnitEntity siUnit;
  private final String ipToSiConversionFactor;
  private final String siToIpConversionFactor;
  
  public UnitMappingEntity(
      Integer persistentIdentity,
      UnitEntity ipUnit,
      UnitEntity siUnit,
      String ipToSiConversionFactor,
      String siToIpConversionFactor) {
    super(persistentIdentity);
    requireNonNull(ipUnit, "ipUnit cannot be null");
    requireNonNull(siUnit, "siUnit cannot be null");
    requireNonNull(ipToSiConversionFactor, "ipToSiConversionFactor cannot be null");
    requireNonNull(siToIpConversionFactor, "siToIpConversionFactor cannot be null");
    this.ipUnit = ipUnit;
    this.siUnit = siUnit;
    this.ipToSiConversionFactor = ipToSiConversionFactor;
    this.siToIpConversionFactor = siToIpConversionFactor;
  }
  
  public UnitEntity getIpUnit() {
    return ipUnit;
  }

  public UnitEntity getSiUnit() {
    return siUnit;
  }

  public String getIpToSiConversionFactor() {
    return ipToSiConversionFactor;
  }

  public String getSiToIpConversionFactor() {
    return siToIpConversionFactor;
  }
  
  public Double getIpToSiConversionFactorAsDouble() {
    return Double.parseDouble(ipToSiConversionFactor);
  }

  public Double getSiToIpConversionFactorAsDouble() {
    return Double.parseDouble(siToIpConversionFactor);
  }  
  
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(ipUnit.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(siUnit.getNaturalIdentity())
        .toString();
  }

  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public static class Mapper implements DtoMapper<NodeTagTemplatesContainer, UnitMappingEntity, UnitMappingDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<UnitMappingDto> mapEntitiesToDtos(List<UnitMappingEntity> entities) {

      List<UnitMappingDto> list = new ArrayList<>();
      for (UnitMappingEntity entity: entities) {
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }

    @Override
    public UnitMappingDto mapEntityToDto(UnitMappingEntity e) {

      return new UnitMappingDto(
          e.getPersistentIdentity(),
          e.getIpUnit().getPersistentIdentity(),
          e.getSiUnit().getPersistentIdentity(),
          e.getIpToSiConversionFactor().toString(),
          e.getSiToIpConversionFactor().toString());
    }

    public List<UnitMappingEntity> mapDtosToEntities(
        NodeTagTemplatesContainer nodeTagTemplatesContainer,
        List<UnitMappingDto> dtos) {

      List<UnitMappingEntity> list = new ArrayList<>();
      for (UnitMappingDto dto: dtos) {
        list.add(mapDtoToEntity(nodeTagTemplatesContainer, dto));
      }
      return list;
    }
    
    @Override
    public UnitMappingEntity mapDtoToEntity(
        NodeTagTemplatesContainer nodeTagTemplatesContainer,
        UnitMappingDto d) {
      
      try {
        
        UnitEntity ipUnit = null;
        UnitEntity siUnit = null;

        if (d.getIpUnitId() != null) {
          ipUnit = DictionaryContext.getUnitsContainer().getUnit(d.getIpUnitId());
        } else {
          ipUnit = DictionaryContext.getUnitsContainer().getUnitNullIfNotExists(d.getIpUnitName());
        }
        
        if (d.getSiUnitId() != null) {
          siUnit = DictionaryContext.getUnitsContainer().getUnit(d.getSiUnitId());
        } else {
          siUnit = DictionaryContext.getUnitsContainer().getUnitNullIfNotExists(d.getSiUnitName());
        }

        return new UnitMappingEntity(
            d.getId(),
            ipUnit,
            siUnit,
            d.getIpToSiConversionFactor(),
            d.getSiToIpConversionFactor());
        
      } catch (Exception e) {
        throw new RuntimeException("Unable to map DTO: ["
            + d, e);
      }      
    }
  }  
}
//@formatter:on