package com.djt.hvac.domain.model.referralagent;

public class ExternalOrganizationalReferralAgentEntity extends AbstractReferralAgentEntity {
  private static final long serialVersionUID = 1L;
  public ExternalOrganizationalReferralAgentEntity(
      Integer persistentIdentity,
      ExternalOrganizationalReferralAgentEntity parentReferralAgent,
      String name,
      String code) {
    super(
        persistentIdentity,
        parentReferralAgent,
        name,
        code,
        true,
        false);
  }
}
