package com.djt.hvac.domain.model.referralagent;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;

/*

CREATE OR REPLACE VIEW public.referral_agents
AS SELECT referral_agent_tbl.id,
    referral_agent_tbl.referral_agent_type_id,
    referral_agent_tbl.parent_id,
    referral_agent_tbl.name,
    referral_agent_tbl.code
   FROM referral_agent_tbl;
   
INSERT into referral_agent_tbl (
  id,
  referral_agent_type_id, 
  parent_id, 
  "name", 
  code
) values (
  4, 
  4,
  3,
  'joe.smith@external.com', 
  random_string(8)
);

{
    "id" : 1,
    "referral_agent_type_id" : 1,
    "parent_id" : null,
    "name" : "Resolute BI",
    "code" : "YvxhIYrN"
}

 */
public abstract class AbstractReferralAgentEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private final AbstractReferralAgentEntity parentReferralAgent;
  private final String name;
  private final String code;
  private final Boolean isOrganization;
  private final Boolean isInternal;
  private Set<AbstractReferralAgentEntity> childReferralAgents = new TreeSet<>();

  public AbstractReferralAgentEntity(
      Integer persistentIdentity,
      AbstractReferralAgentEntity parentReferralAgent,
      String name,
      String code,
      Boolean isOrganization,
      Boolean isInternal) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    requireNonNull(code, "code cannot be null");
    requireNonNull(isOrganization, "isOrganization cannot be null");
    requireNonNull(isInternal, "isInternal cannot be null");
    this.parentReferralAgent = parentReferralAgent;
    this.name = name;
    this.code = code;
    this.isOrganization = isOrganization;
    this.isInternal = isInternal;
  }
  
  public Set<AbstractReferralAgentEntity> getChildReferralAgents() {
    return childReferralAgents;
  }
  
  public boolean addChildReferralAgent(AbstractReferralAgentEntity referralAgent) throws EntityAlreadyExistsException {
    return addChild(childReferralAgents, referralAgent, this);
  }

  public AbstractReferralAgentEntity getChildReferralAgent(Integer persistentIdentity) throws EntityDoesNotExistException {
    return getChild(AbstractReferralAgentEntity.class, childReferralAgents, persistentIdentity, this);
  }

  public AbstractReferralAgentEntity removeChildReferralAgent(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    AbstractReferralAgentEntity childReferralAgent = getChildReferralAgent(persistentIdentity);
    childReferralAgent.setIsDeleted();
    return childReferralAgent;
  }

  public AbstractReferralAgentEntity getParentReferralAgent() {
    return parentReferralAgent;
  }
  
  public String getName() {
    return name;
  }

  public String getCode() {
    return code;
  }

  public Boolean isOrganization() {
    return isOrganization;
  }

  public Boolean isInternal() {
    return isInternal;
  }
  
  public String getEnumValue() {
    
    if (!isInternal && !isOrganization) {
      return "EXTERNAL_INDIVIDUAL";
    } else if (!isInternal && isOrganization) {
      return "EXTERNAL_ORGANIZATION";
    } else if (isInternal && !isOrganization) {
      return "INTERNAL_INDIVIDUAL";
    }
    return "INTERNAL_ORGANIZATION";
  }

  @Override
  public String getNaturalIdentity() {
    
    if (this.parentReferralAgent == null) {
      return name;
    }
    return new StringBuilder()
        .append(parentReferralAgent.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(name)
        .toString();
  } 
  
  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
   
    for (AbstractReferralAgentEntity referralAgent : childReferralAgents) {
      referralAgent.validate(issueTypes, validationMessages, remediate);  
    }
  }
}
