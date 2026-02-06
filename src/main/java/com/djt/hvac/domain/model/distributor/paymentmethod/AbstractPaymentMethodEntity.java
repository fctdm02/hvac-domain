package com.djt.hvac.domain.model.distributor.paymentmethod;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.dto.PaymentMethodDto;
import com.djt.hvac.domain.model.distributor.enums.PaymentMethodType;

/**
 *
 * @see <a href="https://stripe.com/docs/payments/payment-methods">https://stripe.com/docs/payments/payment-methods</a>
 *
 */
public abstract class AbstractPaymentMethodEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentMethodEntity.class);
  
  
  private final OnlineDistributorEntity parentDistributor;
  private final PaymentMethodType paymentMethodType;
  private final String name;
  private final String stripeSourceId;
  private final String accountHolderName;
  private final String address;
  private final String city;
  private final String state;
  private final String zipCode;
  private final String phoneNumber;
  
  public AbstractPaymentMethodEntity(
      Integer persistentIdentity,
      OnlineDistributorEntity parentDistributor,
      PaymentMethodType paymentMethodType,
      String name,
      String stripeSourceId,
      String accountHolderName,
      String address,
      String city,
      String state,
      String zipCode,
      String phoneNumber) {
    super(persistentIdentity);
    requireNonNull(parentDistributor, "parentDistributor cannot be null");
    requireNonNull(paymentMethodType, "paymentMethodType cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(accountHolderName, "accountHolderName cannot be null");
    requireNonNull(address, "address cannot be null");
    requireNonNull(city, "city cannot be null");
    requireNonNull(state, "state cannot be null");
    requireNonNull(zipCode, "zipCode cannot be null");
    requireNonNull(phoneNumber, "phoneNumber cannot be null");
    this.parentDistributor = parentDistributor;
    this.paymentMethodType = paymentMethodType;
    this.name = name;
    this.stripeSourceId = stripeSourceId;
    this.accountHolderName = accountHolderName;
    this.address = address;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
    this.phoneNumber = phoneNumber;
  }

  public OnlineDistributorEntity getParentDistributor() {
    return parentDistributor;
  }

  public PaymentMethodType getPaymentMethodType() {
    return paymentMethodType;
  }

  public String getName() {
    return name;
  }   
  
  public String getStripeSourceId() {
    return stripeSourceId;
  }

  public String getAccountHolderName() {
    return accountHolderName;
  }

  public String getAddress() {
    return address;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentDistributor.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(paymentMethodType)
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(name)
        .toString();
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  @Override
  public void evaluateState() {
  }
  
  public static class Mapper implements DtoMapper<AbstractDistributorEntity, AbstractPaymentMethodEntity, PaymentMethodDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    public List<PaymentMethodDto> mapEntitiesToDtos(List<AbstractPaymentMethodEntity> entities) {
      
      List<PaymentMethodDto> list = new ArrayList<>();
      Iterator<AbstractPaymentMethodEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {
        
        AbstractPaymentMethodEntity entity = iterator.next();
        list.add(mapEntityToDto(entity));  
      }
      return list;
    }
    
    @Override
    public PaymentMethodDto mapEntityToDto(AbstractPaymentMethodEntity e) {
      
      PaymentMethodDto dto = new PaymentMethodDto();
      
      dto.setId(e.getPersistentIdentity());
      dto.setDistributorId(e.getParentDistributor().getPersistentIdentity());
      dto.setType(e.getPaymentMethodType().getName());
      dto.setName(e.getName());
      dto.setStripeSourceId(e.getStripeSourceId());
      dto.setAccountHolderName(e.getAccountHolderName());
      dto.setAddress(e.getAddress());
      dto.setCity(e.getCity());
      dto.setState(e.getState());
      dto.setZipCode(e.getZipCode());
      dto.setPhoneNumber(e.getPhoneNumber());
      
      if (e instanceof CreditCardPaymentMethodEntity) {
        
        CreditCardPaymentMethodEntity cc = (CreditCardPaymentMethodEntity)e;
        
        dto.setCardBrand(cc.getBrand());
        dto.setCardExpiry(cc.getExpiry());
        dto.setCardLastFour(cc.getLastFour());
      }

      return dto;
    }

    @Override
    public AbstractPaymentMethodEntity mapDtoToEntity(AbstractDistributorEntity rootDistributor, PaymentMethodDto dto) {
      
      AbstractPaymentMethodEntity paymentMethod = null;
      try {
        
        OnlineDistributorEntity parentOnlineDistributor = (OnlineDistributorEntity)rootDistributor.getDescendantDistributor(dto.getDistributorId());
        
        PaymentMethodType paymentMethodType = PaymentMethodType.valueOf(dto.getType());
        if (paymentMethodType.equals(PaymentMethodType.CREDIT_CARD)) {
          
          paymentMethod = new CreditCardPaymentMethodEntity(
              dto.getId(),
              parentOnlineDistributor,
              paymentMethodType,
              dto.getName(),
              dto.getStripeSourceId(),
              dto.getAccountHolderName(),
              dto.getAddress(),
              dto.getCity(),
              dto.getState(),
              dto.getZipCode(),
              dto.getPhoneNumber(),
              dto.getCardBrand(),
              dto.getCardExpiry(),
              dto.getCardLastFour());
          
        } else  if (paymentMethodType.equals(PaymentMethodType.ACH)) {

          paymentMethod = new AchPaymentMethodEntity(
              dto.getId(),
              parentOnlineDistributor,
              paymentMethodType,
              dto.getName(),
              dto.getStripeSourceId(),
              dto.getAccountHolderName(),
              dto.getAddress(),
              dto.getCity(),
              dto.getState(),
              dto.getZipCode(),
              dto.getPhoneNumber());
          
        } else {
          throw new RuntimeException("Unsupported payment method type: [" 
              + paymentMethodType
              + "]");
        }
        
        parentOnlineDistributor.addChildPaymentMethod(paymentMethod);
        
      } catch (Exception e) {
        LOGGER.error("Error: ["
            + e.getMessage()
            + "], unable to map payment method with id: ["
            + dto.getId()
            + "]");
      }
      return paymentMethod;
    }
  }
}
