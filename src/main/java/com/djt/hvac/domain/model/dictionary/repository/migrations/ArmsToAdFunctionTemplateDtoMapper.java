//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateInputConstantDto;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateInputPointDto;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateOutputPointDto;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.arms.InputConst;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.arms.InputPoint;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.arms.Rule;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;

public class ArmsToAdFunctionTemplateDtoMapper {
  
  public AdFunctionTemplateDto migrateArmsToAdFunctionTemplate(
      Rule incomingRuleSpecification,
      AbstractAdFunctionTemplateEntity existingAdFunctionTemplate,
      Map<String, Integer> maxPersistentIdentityValues)
  throws 
      EntityDoesNotExistException {
    
    int maxAdFunctionTemplateId = maxPersistentIdentityValues.get("AdFunctionTemplate");
    int maxAdFunctionModuleId = maxPersistentIdentityValues.get("AdFunctionModule");
    int maxAdFunctionTemplateInputConstantId = maxPersistentIdentityValues.get("AdFunctionTemplateInputConstant");
    int maxAdFunctionTemplateInputPointId = maxPersistentIdentityValues.get("AdFunctionTemplateInputPoint");
    int maxAdFunctionTemplateOutputPointId = maxPersistentIdentityValues.get("AdFunctionTemplateOutputPoint");
    
    AdFunctionTemplateDto ad = new AdFunctionTemplateDto();
    
    if (existingAdFunctionTemplate == null) {
      maxAdFunctionTemplateId = maxAdFunctionTemplateId + 1;
      ad.setId(Integer.valueOf(maxAdFunctionTemplateId));  
    } else {
      ad.setId(existingAdFunctionTemplate.getPersistentIdentity());
    }
    
    if (existingAdFunctionTemplate == null) {
      maxAdFunctionModuleId = maxAdFunctionModuleId + 1;
      ad.setFunctionCodeModuleId(Integer.valueOf(maxAdFunctionModuleId));
      ad.setFunctionCodeModuleName(normalize(sqlEscape(incomingRuleSpecification.getName())));
      ad.setFunctionCodeModulDescription(sqlEscape(incomingRuleSpecification.getSummary()));
      if (isAdRuleFunctionTemplateReferenceNumber(incomingRuleSpecification.getNumber())) {
        ad.setFunctionTypeId(FunctionType.RULE.getId());  
      } else {
        ad.setFunctionTypeId(FunctionType.COMPUTED_POINT.getId());
      }
    } else {
      ad.setFunctionCodeModuleId(existingAdFunctionTemplate.getAdFunction().getPersistentIdentity());
      ad.setFunctionCodeModuleName(sqlEscape(existingAdFunctionTemplate.getAdFunction().getName()));
      ad.setFunctionCodeModulDescription(sqlEscape(existingAdFunctionTemplate.getAdFunction().getDescription()));
      ad.setFunctionTypeId(existingAdFunctionTemplate.getAdFunction().getFunctionType().getId());
    }    
    
    ad.setBeta(Boolean.FALSE);
    ad.setActive(Boolean.TRUE);
    ad.setName(incomingRuleSpecification.getName().replace(" ", "_"));
    ad.setDisplayName(normalize(sqlEscape(incomingRuleSpecification.getName())));
    ad.setDescription(sqlEscape(incomingRuleSpecification.getSummary()));
    ad.setReferenceNumber(incomingRuleSpecification.getNumber());
    ad.setNodeFilterExpression(incomingRuleSpecification.getNodeFilterExpression());
    ad.setTupleConstraint(incomingRuleSpecification.getTupleConstraint());
    ad.setEquipmentTypeId(incomingRuleSpecification.getEquipmentTypeId());
    
    if (existingAdFunctionTemplate == null) {
      ad.setVersion(Integer.valueOf(1));      
    } else {
      ad.setVersion(ad.getVersion().intValue() + 1);
    }
    
    List<AdFunctionTemplateInputConstantDto> adInputConsts = new ArrayList<>();
    if (incomingRuleSpecification.getInputConsts() != null) {
      
      int sequenceNumber = 1;
      for (InputConst ic: incomingRuleSpecification.getInputConsts()) {
        
        AdFunctionTemplateInputConstantDto adIc = new AdFunctionTemplateInputConstantDto();
        
        if (existingAdFunctionTemplate == null || existingAdFunctionTemplate.getInputPointByNameNullIfNotExists(ic.getName()) == null) {
          maxAdFunctionTemplateInputConstantId = maxAdFunctionTemplateInputConstantId + 1;
          adIc.setId(Integer.valueOf(maxAdFunctionTemplateInputConstantId));  
        } else {
          adIc.setId(existingAdFunctionTemplate.getInputConstant(ic.getName()).getPersistentIdentity());
        }        
        adIc.setSeqNo(Integer.valueOf(sequenceNumber++));
        adIc.setName(ic.getName());
        adIc.setDescription(ic.getLabel());
        adIc.setIsRequired(Boolean.TRUE);
        adIc.setDefaultValue(ic.getDefaultValue());
        adIc.setUnitId(Integer.valueOf(1));
        adIc.setDataTypeId(DataType.NUMERIC.getId());
        
        adInputConsts.add(adIc);
      }      
    }
    ad.setInputConsts(adInputConsts);
    
    int sequenceNumber = 1;
    List<AdFunctionTemplateInputPointDto> adInputPoints = new ArrayList<>();
    for (InputPoint ip: incomingRuleSpecification.getInputPoints()) {
      
      AdFunctionTemplateInputPointDto adIp = new AdFunctionTemplateInputPointDto();

      Integer pointTemplateId = ip.getPointTemplateId();
      PointTemplateEntity pointTemplate = DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateId); 
      
      String name = ip.getName();
      if (name != null) {
        adIp.setName(name);  
      } else {
        name = pointTemplate.getName();
        adIp.setName(name);
      }
      
      if (existingAdFunctionTemplate == null || existingAdFunctionTemplate.getInputPointByNameNullIfNotExists(name) == null) {
        maxAdFunctionTemplateInputPointId = maxAdFunctionTemplateInputPointId + 1;
        adIp.setId(Integer.valueOf(maxAdFunctionTemplateInputPointId)); 
      } else {
        adIp.setId(existingAdFunctionTemplate.getInputPointByName(name).getPersistentIdentity());
      }      
      adIp.setSeqNo(Integer.valueOf(sequenceNumber++));
            
      adIp.setTags(pointTemplate.getTagsAsStringList());
      
      String description = ip.getLabel();
      if (description == null) {
        description = pointTemplate.getDescription();
      }
      adIp.setDescription(description);
      
      adIp.setArray(ip.getArray());
      adIp.setRequired(ip.getRequired());
      adIp.setCurrentObjectExpression(ip.getCurrentObjectExpression());
      
      adInputPoints.add(adIp);
    }
    ad.setInputPoints(adInputPoints);
    
    List<AdFunctionTemplateOutputPointDto> adOutputPoints = new ArrayList<>();
    
    AdFunctionTemplateOutputPointDto adOp = new AdFunctionTemplateOutputPointDto();
    
    sequenceNumber = 1;
    if (existingAdFunctionTemplate == null) {
      maxAdFunctionTemplateOutputPointId = maxAdFunctionTemplateOutputPointId + 1;
      adOp.setId(Integer.valueOf(maxAdFunctionTemplateOutputPointId)); 
    } else {
      adOp.setId(existingAdFunctionTemplate.getOutputPointBySequenceNumber(sequenceNumber).getPersistentIdentity());
    }    
    adOp.setSeqNo(sequenceNumber);
    adOp.setDescription("Anomaly Detected");
    adOp.setDataTypeId(DataType.BOOLEAN.getId());
    adOp.setUnitId(Integer.valueOf(1));
    adOp.setRange("{\"trueText\":\"On\",\"falseText\":\"Off\"}");
    
    adOutputPoints.add(adOp);
    
    ad.setOutputPoints(adOutputPoints);
    
    maxPersistentIdentityValues.put("AdFunctionTemplate", maxAdFunctionTemplateId);
    maxPersistentIdentityValues.put("AdFunctionModule", maxAdFunctionModuleId);
    maxPersistentIdentityValues.put("AdFunctionTemplateInputConstant", maxAdFunctionTemplateInputConstantId);
    maxPersistentIdentityValues.put("AdFunctionTemplateInputPoint", maxAdFunctionTemplateInputPointId);
    maxPersistentIdentityValues.put("AdFunctionTemplateOutputPoint", maxAdFunctionTemplateOutputPointId);
    
    return ad;
  }

  private static String normalize(String text) {
    
    StringBuilder sb = new StringBuilder();
    
    for (int i = 0; i < text.length(); i++) {
      
      char ch = text.charAt(i);
      if (ch == ' ') {
        sb.append(' ');
      } else if (Character.isLetterOrDigit(ch)) {
        sb.append(ch);
      }
    }
    
    return sb.toString();
  }
  
  private static String sqlEscape(String text) {
    
    return text.replace("'", "''");
  }
  
  private static boolean isAdRuleFunctionTemplateReferenceNumber(final String input) {
    
    // Compile regular expression
    final Pattern pattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+", Pattern.CASE_INSENSITIVE);
    
    // Match regex against input
    final Matcher matcher = pattern.matcher(input);
    
    // Use results...
    boolean isRule = matcher.matches();
    return isRule;
  }  
}
//@formatter:on