//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.function;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.container.AdFunctionTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.AdFunctionTemplateInputConstantPointTemplateMappingDto;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;

public class AdFunctionTemplateInputConstantPointTemplateMappingEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  
  private AdFunctionTemplateInputConstantEntity adFunctionTemplateInputConstant;
  private PointTemplateEntity pointTemplate;
  
  public AdFunctionTemplateInputConstantPointTemplateMappingEntity(
      AdFunctionTemplateInputConstantEntity adFunctionTemplateInputConstant,
      PointTemplateEntity pointTemplate) {
    super();
    requireNonNull(adFunctionTemplateInputConstant, "adFunctionTemplateInputConstant cannot be null");
    requireNonNull(pointTemplate, "pointTemplate cannot be null");
    this.adFunctionTemplateInputConstant = adFunctionTemplateInputConstant;
    this.pointTemplate = pointTemplate;
  }
  
  public AdFunctionTemplateInputConstantEntity getAdFunctionTemplateInputConstant() {
    return adFunctionTemplateInputConstant;
  }

  public PointTemplateEntity getPointTemplate() {
    return pointTemplate;
  }
  
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new TreeMap<>();
    parentIdentities.put("adFunctionTemplateInputConstant", adFunctionTemplateInputConstant.getPersistentIdentity());
    parentIdentities.put("pointTemplate", pointTemplate.getPersistentIdentity());
    return parentIdentities;
  }

  @Override
  public String getNaturalIdentity() {
    return new StringBuilder()
        .append(adFunctionTemplateInputConstant.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(pointTemplate.getNaturalIdentity())
        .toString();
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public int compareTo(AbstractEntity that) {
    
    if (that instanceof AdFunctionTemplateInputConstantPointTemplateMappingEntity) {
      int compareTo = this.adFunctionTemplateInputConstant.getPersistentIdentity().compareTo(((AdFunctionTemplateInputConstantPointTemplateMappingEntity)that).adFunctionTemplateInputConstant.getPersistentIdentity());
      if (compareTo == 0) {
        compareTo = this.pointTemplate.getPersistentIdentity().compareTo(((AdFunctionTemplateInputConstantPointTemplateMappingEntity)that).pointTemplate.getPersistentIdentity());  
      }
      return compareTo;
    }
    throw new IllegalStateException("Cannot compare to non AdFunctionTemplateInputConstantPointTemplateMappingEntity entity: " + that.getClass().getSimpleName());
  }
  
  public static class Mapper implements DtoMapper<AdFunctionTemplatesContainer, AdFunctionTemplateInputConstantPointTemplateMappingEntity, AdFunctionTemplateInputConstantPointTemplateMappingDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<AdFunctionTemplateInputConstantPointTemplateMappingDto> mapEntitiesToDtos(List<AdFunctionTemplateInputConstantPointTemplateMappingEntity> entities) {

      List<AdFunctionTemplateInputConstantPointTemplateMappingDto> list = new ArrayList<>();
      for (AdFunctionTemplateInputConstantPointTemplateMappingEntity entity: entities) {
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }

    @Override
    public AdFunctionTemplateInputConstantPointTemplateMappingDto mapEntityToDto(AdFunctionTemplateInputConstantPointTemplateMappingEntity e) {

      return new AdFunctionTemplateInputConstantPointTemplateMappingDto(
          e.getAdFunctionTemplateInputConstant().getPersistentIdentity(),
          e.getPointTemplate().getPersistentIdentity().toString());
    }

    public List<AdFunctionTemplateInputConstantPointTemplateMappingEntity> mapDtosToEntities(
        AdFunctionTemplatesContainer container,
        List<AdFunctionTemplateInputConstantPointTemplateMappingDto> dtos) {

      List<AdFunctionTemplateInputConstantPointTemplateMappingEntity> list = new ArrayList<>();
      for (AdFunctionTemplateInputConstantPointTemplateMappingDto dto: dtos) {
        
        String pointTemplateId = dto.getPointTemplateId();
        if (pointTemplateId != null && !pointTemplateId.trim().equals("") && !pointTemplateId.startsWith("N")) {
          list.add(mapDtoToEntity(container, dto));  
        }
      }
      return list;
    }
    
    @Override
    public AdFunctionTemplateInputConstantPointTemplateMappingEntity mapDtoToEntity(
        AdFunctionTemplatesContainer container,
        AdFunctionTemplateInputConstantPointTemplateMappingDto d) {
      
      try {
        
        return new AdFunctionTemplateInputConstantPointTemplateMappingEntity(
            container.getAdFunctionTemplateInputConstant(d.getAdFunctionTemplateInputConstId()),
            DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(Integer.parseInt(d.getPointTemplateId())));
        
      } catch (Exception e) {
        throw new RuntimeException("Unable to map DTO: ["
            + d, e);
      }
    }
  }  
}
//@formatter:on