package com.djt.hvac.domain.model.distributor.paymentmethod;

import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.PaymentMethodType;

/**
 * 
 * @see <a href="https://stripe.com/docs/payments/payment-methods">https://stripe.com/docs/payments/payment-methods</a>
 *
 */
public class AchPaymentMethodEntity extends AbstractPaymentMethodEntity {
  private static final long serialVersionUID = 1L;
  public AchPaymentMethodEntity(
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
    this(
        null,
        parentDistributor,
        paymentMethodType,
        name,
        stripeSourceId,
        accountHolderName,
        address,
        city,
        state,
        zipCode,
        phoneNumber);
  }
  
  public AchPaymentMethodEntity(
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
    super(
        persistentIdentity,
        parentDistributor,
        paymentMethodType,
        name,
        stripeSourceId,
        accountHolderName,
        address,
        city,
        state,
        zipCode,
        phoneNumber);
  }
  
  public String getDisplayName() {
    
    StringBuilder sb = new StringBuilder();
    sb.append(getPaymentMethodType().getDisplayName());
    sb.append(": ");
    sb.append(getName());
    return sb.toString();
  }
}
