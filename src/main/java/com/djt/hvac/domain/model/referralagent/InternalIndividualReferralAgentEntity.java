package com.djt.hvac.domain.model.referralagent;

public class InternalIndividualReferralAgentEntity extends AbstractReferralAgentEntity {
  private static final long serialVersionUID = 1L;
  public InternalIndividualReferralAgentEntity(
      Integer persistentIdentity,
      InternalIndividualReferralAgentEntity parentReferralAgent,
      String name,
      String code) {
    super(
        persistentIdentity,
        parentReferralAgent,
        name,
        code,
        false,
        true);
  }
}
