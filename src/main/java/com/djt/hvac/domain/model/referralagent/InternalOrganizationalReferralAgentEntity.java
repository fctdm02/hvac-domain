package com.djt.hvac.domain.model.referralagent;

public class InternalOrganizationalReferralAgentEntity extends AbstractReferralAgentEntity {
  private static final long serialVersionUID = 1L;
  public static InternalOrganizationalReferralAgentEntity buildResoluteReferralAgentStub() {
    
    return new InternalOrganizationalReferralAgentEntity(
        Integer.valueOf(1),
        null,
        "Resolute BI",
        "YvxhIYrN");    
  }

  
  
  public InternalOrganizationalReferralAgentEntity(
      Integer persistentIdentity,
      InternalOrganizationalReferralAgentEntity parentReferralAgent,
      String name,
      String code) {
    super(
        persistentIdentity,
        parentReferralAgent,
        name,
        code,
        true,
        true);
  }
}
