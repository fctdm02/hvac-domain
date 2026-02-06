//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.report;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage.MessageType;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;

public class ReportTemplateRulePointSpecEntity extends AbstractReportTemplatePointSpecEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdRuleFunctionTemplateEntity ruleTemplate;

  public ReportTemplateRulePointSpecEntity(
      Integer persistentIdentity,
      ReportTemplateEquipmentSpecEntity parentEquipmentSpec,
      String name,
      boolean isRequired,
      String currentObjectExpression,
      String errorMessage,
      Integer requiredDataTypeId,
      AdRuleFunctionTemplateEntity ruleTemplate) {
    super(
        persistentIdentity,
        parentEquipmentSpec,
        name,
        isRequired,
        false, // isArray has no meaning for rule point spec
        currentObjectExpression,
        errorMessage,
        requiredDataTypeId);
    requireNonNull(ruleTemplate, "ruleTemplate cannot be null");
    this.ruleTemplate = ruleTemplate;
  }

  public AdRuleFunctionTemplateEntity getRuleTemplate() {
    return ruleTemplate;
  }

  @Override
  public void validateSimple(List<SimpleValidationMessage> simpleValidationMessages) {

    if (getCurrentObjectExpression() == null) {

      AbstractEnergyExchangeTypeEntity energyExchangeType = getParentEquipmentSpec().getEnergyExchangeType();
      if (energyExchangeType != null) {

        AbstractEnergyExchangeTypeEntity ruleTemplateEnergyExchangeType = ruleTemplate.getEnergyExchangeType();

        if (!energyExchangeType.equals(ruleTemplateEnergyExchangeType)) {

          simpleValidationMessages.add(new SimpleValidationMessage(
              MessageType.ERROR,
              this.getNaturalIdentity(),
              "ruleTemplate",
              "Rule energy exchange type does not match parent equipment spec energy exchange type"));
        }
      }
    } else {
      // TDM: Figure out a way to get the desired energy exchange type from the current object expression
      // and then validate that against the given rule.
    }
  }
}
//@formatter:on