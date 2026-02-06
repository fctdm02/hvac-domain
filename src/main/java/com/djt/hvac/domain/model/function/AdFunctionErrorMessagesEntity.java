//@formatter:off
package com.djt.hvac.domain.model.function;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.function.dto.AdFunctionErrorMessagesDto;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;

/**
 *   
 * Contains a list of error messages for a non-compliant combination of eligible equipment
 * and AD function template id. An eligible combination consists of an AD function template
 * and a piece of equipment that matches the equipment type and node filter expression given
 * by the AD function template.
 * 
 * @author tmyers
 *
 */
public class AdFunctionErrorMessagesEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  private final AbstractAdFunctionTemplateEntity adFunctionTemplate;
  private final EnergyExchangeEntity energyExchangeEntity;
  private final List<Integer> errorMessages;
  
  public AdFunctionErrorMessagesEntity(
      AbstractAdFunctionTemplateEntity adFunctionTemplate,
      EnergyExchangeEntity energyExchangeEntity,
      List<Integer> errorMessages) {
  
    requireNonNull(adFunctionTemplate, "adFunctionTemplate cannot be null");
    requireNonNull(energyExchangeEntity, "energyExchangeEntity cannot be null");
    requireNonNull(errorMessages, "errorMessages cannot be null");
    this.adFunctionTemplate = adFunctionTemplate;
    this.energyExchangeEntity = energyExchangeEntity;
    this.errorMessages = errorMessages;
  }
  
  public AbstractAdFunctionTemplateEntity getAdFunctionTemplate() {
    return adFunctionTemplate;
  }
  
  public EnergyExchangeEntity getEnergyExchangeEntity() {
    return energyExchangeEntity;
  }
  
  public List<Integer> getErrorMessages() {
    return errorMessages;
  }
  
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new LinkedHashMap<>();
    parentIdentities.put("adFunctionTemplate", adFunctionTemplate.getPersistentIdentity());
    parentIdentities.put("energyExchangeEntity", energyExchangeEntity.getPersistentIdentity());
    return parentIdentities;
  }  

  @Override
  public String getNaturalIdentity() {
    
    if (_naturalIdentity == null) {

      _naturalIdentity = buildNaturalIdentity(adFunctionTemplate, energyExchangeEntity, errorMessages);
    }
    return _naturalIdentity;
  }
  
  public static String buildNaturalIdentity(
      AbstractAdFunctionTemplateEntity adFunctionTemplate,
      EnergyExchangeEntity energyExchangeEntity,
      List<Integer> errorMessages) {

    return new StringBuilder()
        .append(adFunctionTemplate.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(energyExchangeEntity.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(errorMessages.toString())
        .toString();
  }
  
  public static class Mapper implements DtoMapper<EnergyExchangeEntity, AdFunctionErrorMessagesEntity, AdFunctionErrorMessagesDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    @Override
    public AdFunctionErrorMessagesDto mapEntityToDto(AdFunctionErrorMessagesEntity entity) {
      
      return AdFunctionErrorMessagesDto
          .builder()
          .withEnergyExchangeId(entity.getEnergyExchangeEntity().getPersistentIdentity())
          .withAdFunctionTemplateId(entity.getAdFunctionTemplate().getPersistentIdentity())
          .withErrorMessages(entity.getErrorMessages())
          .build();
    }
    
    @Override
    public AdFunctionErrorMessagesEntity mapDtoToEntity(EnergyExchangeEntity energyExchangeEntity, AdFunctionErrorMessagesDto dto) {

      return new AdFunctionErrorMessagesEntity(
          DictionaryContext.getAdFunctionTemplatesContainer().getAdFunctionTemplate(dto.getAdFunctionTemplateId()),
          energyExchangeEntity,
          dto.getErrorMessages());
    }
  }
}
//@formatter:on