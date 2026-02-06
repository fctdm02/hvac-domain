package com.djt.hvac.domain.model.referralagent;

public class ExternalIndividualReferralAgentEntity extends AbstractReferralAgentEntity {
  private static final long serialVersionUID = 1L;
  public ExternalIndividualReferralAgentEntity(
      Integer persistentIdentity,
      ExternalIndividualReferralAgentEntity parentReferralAgent,
      String name,
      String code) {
    super(
        persistentIdentity,
        parentReferralAgent,
        name,
        code,
        false,
        false);
  }
}
