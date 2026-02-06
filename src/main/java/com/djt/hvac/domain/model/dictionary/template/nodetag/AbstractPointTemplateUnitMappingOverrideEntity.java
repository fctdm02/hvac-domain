//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.nodetag;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.CustomerLevelPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateUnitMappingOverrideDto;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.DistributorLevelPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingLevelPointTemplateUnitMappingOverrideEntity;

/**
 * There are two types of overrides: The first is where the IP unit system is kept. That is, when SI unit system is
 * configured for the distributor/customer/building, the user can override any given point template so that the IP
 * unit is still used.  The second is that for any given point template, the user can configure unit mapping for SI
 * to IP be different from that of the default (which is priority 1), assuming that more than one unit mapping exists
 * for any given IP unit to SI unit pair.  NOTE: only one of these two overrides can be "active" simultaneously, so
 * if "keepIpUnitSystem" is true, then "unitMapping" must be NULL (the converse is also true, if 
 * "keepIpUnitSystem" is false, then "unitMapping" must be NON-NULL.
 * 
 * @author tmyers
 *
 */
public abstract class AbstractPointTemplateUnitMappingOverrideEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final PointTemplateEntity pointTemplate;
  private final boolean keepIpUnitSystem;
  private final UnitMappingEntity unitMapping;
  
  public AbstractPointTemplateUnitMappingOverrideEntity(
      Integer persistentIdentity,
      PointTemplateEntity pointTemplate,
      Boolean keepIpUnitSystem,
      UnitMappingEntity unitMapping) {
    super(persistentIdentity);
    requireNonNull(pointTemplate, "pointTemplate cannot be null");
    this.pointTemplate = pointTemplate;
    
    if (keepIpUnitSystem && unitMapping != null) {
      throw new IllegalStateException("keepIpUnitSystem cannot be true when unitMapping: ["
          + unitMapping
          + "] is non null for point template unit mapping override with id: ["
          + persistentIdentity
          + "].");
    } else if (!keepIpUnitSystem && unitMapping == null) {
      throw new IllegalStateException("keepIpUnitSystem cannot be false when unitMapping is null for point template unit mapping override with id: ["
          + persistentIdentity
          + "].");
    }
    
    this.keepIpUnitSystem = keepIpUnitSystem;
    this.unitMapping = unitMapping;
  }
  
  public PointTemplateEntity getPointTemplate() {
    return pointTemplate;
  }
  
  public boolean getKeepIpUnitSystem() {
    return keepIpUnitSystem;
  }

  public UnitMappingEntity getUnitMapping() {
    return unitMapping;
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }

  public static class Mapper implements DtoMapper<AbstractDistributorEntity, AbstractPointTemplateUnitMappingOverrideEntity, PointTemplateUnitMappingOverrideDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<PointTemplateUnitMappingOverrideDto> mapEntitiesToDtos(List<AbstractPointTemplateUnitMappingOverrideEntity> entities) {

      List<PointTemplateUnitMappingOverrideDto> list = new ArrayList<>();
      for (AbstractPointTemplateUnitMappingOverrideEntity entity: entities) {
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }

    @Override
    public PointTemplateUnitMappingOverrideDto mapEntityToDto(AbstractPointTemplateUnitMappingOverrideEntity e) {

      Integer parentDistributorId = null;
      Integer parentCustomerId = null;
      Integer parentBuildingId = null;
      
      if (e instanceof DistributorLevelPointTemplateUnitMappingOverrideEntity) {
        
        parentDistributorId = ((DistributorLevelPointTemplateUnitMappingOverrideEntity)e).getParentDistributor().getPersistentIdentity();
        
      } else if (e instanceof CustomerLevelPointTemplateUnitMappingOverrideEntity) {
        
        parentCustomerId = ((CustomerLevelPointTemplateUnitMappingOverrideEntity)e).getParentCustomer().getPersistentIdentity();
        
      } else if (e instanceof BuildingLevelPointTemplateUnitMappingOverrideEntity) {
        
        parentBuildingId = ((BuildingLevelPointTemplateUnitMappingOverrideEntity)e).getParentBuilding().getPersistentIdentity();
        
      }
      
      return new PointTemplateUnitMappingOverrideDto(
          e.getPersistentIdentity(),
          e.getPointTemplate().getPersistentIdentity(),
          e.getKeepIpUnitSystem(),
          e.getUnitMapping().getPersistentIdentity(),
          parentDistributorId,
          parentCustomerId,
          parentBuildingId);
    }

    public List<AbstractPointTemplateUnitMappingOverrideEntity> mapDtosToEntities(
        AbstractDistributorEntity rootDistributor,
        List<PointTemplateUnitMappingOverrideDto> dtos) {

      List<AbstractPointTemplateUnitMappingOverrideEntity> list = new ArrayList<>();
      for (PointTemplateUnitMappingOverrideDto dto: dtos) {
        list.add(mapDtoToEntity(rootDistributor, dto));
      }
      return list;
    }
    
    @Override
    public AbstractPointTemplateUnitMappingOverrideEntity mapDtoToEntity(
        AbstractDistributorEntity rootDistributor,
        PointTemplateUnitMappingOverrideDto d) {
      
      try {
      
        Integer id = d.getId();
        Integer pointTemplateId = d.getPointTemplateId();
        boolean keepIpUnitSystem = d.getKeepIpUnitSystem();
        Integer unitMappingId = d.getUnitMappingId();
        Integer parentDistributorId = d.getParentDistributorId();
        Integer parentCustomerId = d.getParentCustomerId();
        Integer parentBuildingId = d.getParentBuildingId();
        
        PointTemplateEntity pointTemplate = DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateId);
        
        UnitMappingEntity unitMapping = null;
        if (unitMappingId != null) {
        
          unitMapping = DictionaryContext.getNodeTagTemplatesContainer().getUnitMapping(unitMappingId);
        }
        
        if (parentDistributorId != null) {
          
          return new DistributorLevelPointTemplateUnitMappingOverrideEntity(
              id,
              pointTemplate,
              keepIpUnitSystem,
              unitMapping,
              rootDistributor.getDescendantDistributor(parentDistributorId));
          
        } else if (parentCustomerId != null) {

          return new CustomerLevelPointTemplateUnitMappingOverrideEntity(
              id,
              pointTemplate,
              keepIpUnitSystem,
              unitMapping,
              rootDistributor.getDescendantCustomer(parentCustomerId));
          
        } else if (parentBuildingId != null) {

          return new BuildingLevelPointTemplateUnitMappingOverrideEntity(
              id,
              pointTemplate,
              keepIpUnitSystem,
              unitMapping,
              rootDistributor.getDescendantBuilding(parentBuildingId));
          
        } else {
          throw new IllegalStateException("One of the following must be specified: [parentDistributorId, parentCustomerId, parentBuildingId)");
        }
        
      } catch (Exception e) {
        throw new RuntimeException("Unable to map DTO: ["
            + d, e);
      }      
    }
  }    
}
//@formatter:on