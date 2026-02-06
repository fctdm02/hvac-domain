package com.djt.hvac.domain.model.user;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.user.dto.UserDto;
import com.djt.hvac.domain.model.user.enums.UserRoleType;
import com.djt.hvac.domain.model.user.enums.UserType;

public abstract class AbstractUserEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractUserEntity.class);
  
  private final UserRoleType userRole;
  private final UserType userType;
  private final String email;
  private final String firstName;
  private final String lastName;
  private boolean acceptedTerms;
  private boolean enableReportNotifications;
  private final Set<String> disabledEmailNotifications;
  
  public AbstractUserEntity(
      Integer persistentIdentity,
      UserRoleType userRole,
      UserType userType,
      String email,
      String firstName,
      String lastName,
      boolean acceptedTerms,
      boolean enableReportNotifications) {
    super(persistentIdentity);
    requireNonNull(userRole, "userRole cannot be null");
    requireNonNull(userType, "userType cannot be null");
    requireNonNull(email, "email cannot be null");
    requireNonNull(firstName, "firstName cannot be null");
    requireNonNull(lastName, "lastName cannot be null");
    this.userRole = userRole;
    this.userType = userType;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.acceptedTerms = acceptedTerms;
    this.enableReportNotifications = enableReportNotifications;
    
    this.disabledEmailNotifications = new TreeSet<>();
  }
  
  public UserRoleType getUserRole() {
    return userRole;
  }
  
  public UserType getUserType() {
    return userType;
  }
  
  public String getEmail() {
    return email;
  }
  
  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }
  
  public boolean getAcceptedTerms() {
    return acceptedTerms;
  }

  public boolean getEnableReportNotifications() {
    return enableReportNotifications;
  }
  
  public Set<String> getDisabledEmailNotifications() {
    return disabledEmailNotifications;
  }

  public void disableEmailNotification(String eventType) {
    
    disabledEmailNotifications.add(eventType);
    setIsModified("disabledEmailNotifications");
  }
  
  public void setDisabledEmailNotifications(Collection<String> disabledEmailNotifications) {
    
    if (disabledEmailNotifications == null || disabledEmailNotifications.isEmpty()) {
      
      this.disabledEmailNotifications.clear();
      setIsModified("disabledEmailNotifications");
      
    } else if (!this.disabledEmailNotifications.equals(disabledEmailNotifications)) {
      
      this.disabledEmailNotifications.addAll(disabledEmailNotifications);
      setIsModified("disabledEmailNotifications");
      
    }
  }

  @Override
  public String getNaturalIdentity() {
    return email;
  } 
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public static class Mapper implements DtoMapper<AbstractDistributorEntity, AbstractUserEntity, UserDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    public List<UserDto> mapEntitiesToDtos(List<AbstractUserEntity> entities) {
      
      List<UserDto> list = new ArrayList<>();
      Iterator<AbstractUserEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {
        
        AbstractUserEntity entity = iterator.next();
        list.add(mapEntityToDto(entity));  
      }
      return list;
    }
    
    public List<AbstractUserEntity> mapDtosToEntities(AbstractDistributorEntity rootDistributor, List<UserDto> dtos) {
      
      List<AbstractUserEntity> list = new ArrayList<>();
      Iterator<UserDto> iterator = dtos.iterator();
      while (iterator.hasNext()) {
        
        UserDto dto = iterator.next();
        list.add(mapDtoToEntity(rootDistributor, dto));  
      }
      return list;
    }    
    
    @Override
    public UserDto mapEntityToDto(AbstractUserEntity e) {
      
      if (e instanceof DistributorUserEntity) {
        
        DistributorUserEntity de = (DistributorUserEntity)e;
        UserDto d = new UserDto();
        d.setId(e.getPersistentIdentity());
        d.setRoleId(e.getUserRole().getId());
        d.setEmail(e.getEmail());
        d.setFirstName(e.getFirstName());
        d.setLastName(e.getLastName());
        d.setDistributorId(de.getParentDistributor().getPersistentIdentity());
        d.setAcceptedTerms(de.getAcceptedTerms());
        d.setEnableReportNotifications(de.getEnableReportNotifications());
        d.setAccountManager(de.isAccountManager());
        
        List<String> list = new ArrayList<>();
        list.addAll(e.getDisabledEmailNotifications());
        d.setDisabledEmailNotifications(list);
        
        return d;
        
      } else if (e instanceof CustomerUserEntity) {
        
        CustomerUserEntity ce = (CustomerUserEntity)e;
        UserDto d = new UserDto();
        d.setId(e.getPersistentIdentity());
        d.setRoleId(e.getUserRole().getId());
        d.setEmail(e.getEmail());
        d.setFirstName(e.getFirstName());
        d.setLastName(e.getLastName());
        d.setCustomerId(ce.getParentCustomer().getPersistentIdentity());
        d.setAcceptedTerms(ce.getAcceptedTerms());
        d.setEnableReportNotifications(ce.getEnableReportNotifications());
        d.setAccountManager(false);
        
        List<String> list = new ArrayList<>();
        list.addAll(e.getDisabledEmailNotifications());
        d.setDisabledEmailNotifications(list);
        
        return d;
        
      } else {
        
        throw new RuntimeException("Unsupported user type: " + e.getClassAndNaturalIdentity());
        
      }
    }

    @Override
    public AbstractUserEntity mapDtoToEntity(AbstractDistributorEntity rootDistributor, UserDto d) {
      
      if (d.getCustomerId() != null) {
        
        AbstractCustomerEntity parentCustomer = rootDistributor.getDescendantCustomerNullIfNotExists(d.getCustomerId());

        CustomerUserEntity cu = new CustomerUserEntity(
            d.getId(),
            UserRoleType.get(d.getRoleId()),
            d.getEmail(),
            d.getFirstName(),
            d.getLastName(),
            d.getAcceptedTerms(),
            d.getEnableReportNotifications(),
            parentCustomer);
        
        if (d.getDisabledEmailNotifications() != null && !d.getDisabledEmailNotifications().isEmpty()) {
          cu.setDisabledEmailNotifications(d.getDisabledEmailNotifications());
        }
        
        return cu;
      
      } else if (d.getRoleId().equals(UserRoleType.DISTRIBUTOR_ADMIN.getId()) || d.getRoleId().equals(UserRoleType.DISTRIBUTOR_USER.getId())) {
        
        Integer parentDistributorId = d.getDistributorId();
        AbstractDistributorEntity parentDistributor = null;
        if (rootDistributor.getPersistentIdentity().equals(parentDistributorId)) {
          
          parentDistributor = rootDistributor;
          
        } else {
          
          try {
            parentDistributor = rootDistributor.getDescendantDistributor(parentDistributorId);
          } catch (EntityDoesNotExistException e) {
            throw new RuntimeException("Distributor with id does not exist: " + parentDistributorId);
          }
          
        }
        
        DistributorUserEntity du = new DistributorUserEntity(
            d.getId(),
            UserRoleType.get(d.getRoleId()),
            d.getEmail(),
            d.getFirstName(),
            d.getLastName(),
            d.getAcceptedTerms(),
            d.getEnableReportNotifications(),
            parentDistributor,
            d.getAccountManager());
        
        if (d.getDisabledEmailNotifications() != null && !d.getDisabledEmailNotifications().isEmpty()) {
          du.setDisabledEmailNotifications(d.getDisabledEmailNotifications());
        }
        
        return du;
        
      } else
        
      throw new RuntimeException("Unsupported user: " + d);
      
    }
  }    
}
