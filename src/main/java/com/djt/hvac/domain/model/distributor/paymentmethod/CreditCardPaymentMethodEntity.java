package com.djt.hvac.domain.model.distributor.paymentmethod;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.YearMonth;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.PaymentMethodType;

/**
 *
 * @see <a href="https://stripe.com/docs/payments/payment-methods">https://stripe.com/docs/payments/payment-methods</a>
 *
 */
public class CreditCardPaymentMethodEntity extends AbstractPaymentMethodEntity {
  private static final long serialVersionUID = 1L;
  public static final String EXPIRY_DATE_FORMAT_PATTERN = "MM/YYYY";
  public static final String EXPIRED = "Expired";
  
  private final String brand;
  private final String expiry;
  private final String lastFour;
  
  public CreditCardPaymentMethodEntity(
      OnlineDistributorEntity parentDistributor,
      PaymentMethodType paymentMethodType,
      String name,
      String stripeSourceId,
      String accountHolderName,
      String address,
      String city,
      String state,
      String zipCode,
      String phoneNumber,
      String brand,
      String expiry,
      String lastFour) {
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
        phoneNumber,
        brand,
        expiry,
        lastFour);
  }
  
  public CreditCardPaymentMethodEntity(
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
      String phoneNumber,
      String brand,
      String expiry,
      String lastFour) {
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
    requireNonNull(brand, "brand cannot be null");
    requireNonNull(expiry, "expiry cannot be null");
    requireNonNull(lastFour, "lastFour cannot be null");
    this.brand = brand;
    this.expiry = expiry;
    this.lastFour = lastFour;
  }
  
  public String getBrand() {
    return brand;
  }

  public String getExpiry() {
    return expiry;
  }

  public String getLastFour() {
    return lastFour;
  }
  
  public String getDisplayName() {
    
    StringBuilder sb = new StringBuilder();
    sb.append(getPaymentMethodType().getDisplayName());
    sb.append(": xxxx-xxxx-xxxx-");
    sb.append(lastFour);
    return sb.toString();
  }
  
  /**
   * Assumes expiry is in MM/YYYY format
   * 
   * @return Either the expiry or "Expired" if it has expired
   */
  public String getExpiresOnDisplayName() {
    
    boolean isExpired = isCardExpired(expiry);
    if (isExpired) {
      return EXPIRED;
    }
    return expiry;
  }
  
  /**
   * 
   * @param cardExpiry MM/YYYY format
   * 
   * @return Either the expiry or "Expired" if it has expired 
   */
  public static boolean isCardExpired(String cardExpiry) {
    
    try {
      
      LocalDate currentLocalDate = AbstractEntity
          .getTimeKeeper()
          .getCurrentTimestamp()
          .toLocalDateTime()
          .toLocalDate();
      
      int idx = cardExpiry.indexOf("/");
      int cardExpiryMonth = Integer.parseInt(cardExpiry.substring(0, idx));
      int cardExpiryYear = Integer.parseInt(cardExpiry.substring(idx + 1));
      
      LocalDate lastDayofMonthYear = YearMonth.of(cardExpiryYear, cardExpiryMonth).atEndOfMonth();
      
      if (currentLocalDate.isAfter(lastDayofMonthYear)) {
        return true;
      }
      return false;
      
    } catch (Exception e) {
      throw new IllegalArgumentException("Card expiry: ["
          + cardExpiry
          + "] could not be parsed in 'MM/YYYY' format");
    }
  }  
}
