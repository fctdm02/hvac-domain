//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.dsl.tagquery.TagQueryExpression;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;

/**
 * @author tommyers
 *
 */
public class AdFunctionTemplateEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdFunctionModuleEntity adFunction;
  private final AbstractEnergyExchangeTypeEntity energyExchangeType;
  private final String referenceNumber;
  private final String nodeFilterExpression;
  
  public AdFunctionTemplateEntity(
      Integer persistentIdentity,
      AdFunctionModuleEntity adFunction,
      AbstractEnergyExchangeTypeEntity energyExchangeType,
      String referenceNumber,
      String nodeFilterExpression) {
    super(persistentIdentity);
    requireNonNull(adFunction, "adFunction cannot be null");
    requireNonNull(energyExchangeType, "energyExchangeType cannot be null");
    requireNonNull(referenceNumber, "referenceNumber cannot be null");
    this.adFunction = adFunction;
    this.energyExchangeType = energyExchangeType;
    this.referenceNumber = referenceNumber;
    this.nodeFilterExpression = nodeFilterExpression;

    // Validate the node filter expression.
    if (nodeFilterExpression != null && !nodeFilterExpression.trim().isEmpty()) {
      TagQueryExpression.parse(nodeFilterExpression);
    }
  }

  public AdFunctionModuleEntity getAdFunction() {
    return adFunction;
  }
  
  public AbstractEnergyExchangeTypeEntity getEnergyExchangeType() {
    return energyExchangeType;
  }

  public String getReferenceNumber() {
    return referenceNumber;
  }
  
  public String getNodeFilterExpression() {
    return nodeFilterExpression;
  }
  
  public String getFullDisplayName() {
    
    return new StringBuilder()
        .append(referenceNumber)
        .append(" ")
        .append(getAdFunction().getDisplayName())
        .append(" (")
        .append(energyExchangeType.getName())
        .append(")")
        .toString();
  }
  
  @Override
  public String getNaturalIdentity() {

    return new StringBuilder()
        .append(referenceNumber)
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(getAdFunction().getName())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(energyExchangeType.getNaturalIdentity())
        .toString();
  }   
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
}
//@formatter:on