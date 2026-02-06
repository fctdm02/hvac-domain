//@formatter:off
package com.djt.hvac.domain.model.dictionary;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.container.UnitsContainer;
import com.djt.hvac.domain.model.dictionary.dto.IdNameDto;
import com.djt.hvac.domain.model.dictionary.enums.AggregatorType;

public class UnitEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  public static final UnitEntity EMPTY_UNIT = new UnitEntity(1, "", AggregatorType.AVG);
  
  private String name;
  private AggregatorType aggregatorType; 
  
  public UnitEntity() {}
  
  public UnitEntity(
      Integer persistentIdentity,
      String name,
      AggregatorType aggregatorType) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    this.name = name;
    if (aggregatorType == null) {
      aggregatorType = AggregatorType.AVG;
    }
    this.aggregatorType = aggregatorType;
  }
  
  public String getName() {
    return name;
  }
  
  public String getNaturalIdentity() {
    return name;
  }
  
  public AggregatorType getAggregatorType() {
    return aggregatorType;
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public static class Mapper implements DtoMapper<UnitsContainer, UnitEntity, IdNameDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<IdNameDto> mapEntitiesToDtos(List<UnitEntity> entities) {

      List<IdNameDto> list = new ArrayList<>();
      for (UnitEntity entity: entities) {
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }

    @Override
    public IdNameDto mapEntityToDto(UnitEntity e) {

      return IdNameDto
          .builder()
          .withId(e.getPersistentIdentity())
          .withName(e.getName())
          .withAggregator_id(e.getAggregatorType().getId())
          .build();
    }

    public List<UnitEntity> mapDtosToEntities(
        UnitsContainer unitsContainer,
        List<IdNameDto> dtos) {

      List<UnitEntity> list = new ArrayList<>();
      for (IdNameDto dto: dtos) {
        list.add(mapDtoToEntity(unitsContainer, dto));
      }
      return list;
    }
    
    @Override
    public UnitEntity mapDtoToEntity(
        UnitsContainer unitsContainer,
        IdNameDto d) {
      
      try {
        
        return new UnitEntity(
            d.getId(),
            d.getName(),
            AggregatorType.get(d.getAggregator_id()));
        
      } catch (Exception e) {
        throw new RuntimeException("Unable to map DTO: ["
            + d, e);
      }      
    }
  }  
}
//@formatter:on