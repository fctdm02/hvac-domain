package com.djt.hvac.domain.model.dictionary;

import static java.util.Objects.requireNonNull;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.enums.PaymentInterval;

public class PaymentPlanEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final Integer pointCap;
  private final PaymentInterval paymentInterval;
  private final Double costPerInterval;
  private final String stripeProductId;
  private final String stripePlanId;
  private final String stripeTestProductId;
  private final String stripeTestPlanId;
  private final Boolean isDeprecated;
  
  public PaymentPlanEntity(
      Integer persistentIdentity,
      Integer pointCap,
      PaymentInterval paymentInterval,
      Double costPerInterval,
      String stripeProductId,
      String stripePlanId,
      String stripeTestProductId,
      String stripeTestPlanId,
      Boolean isDeprecated) {
    super(persistentIdentity);
    requireNonNull(pointCap, "pointCap cannot be null");
    requireNonNull(paymentInterval, "paymentInterval cannot be null");
    requireNonNull(costPerInterval, "costPerInterval cannot be null");
    requireNonNull(stripeProductId, "stripeProductId cannot be null");
    requireNonNull(stripePlanId, "stripePlanId cannot be null");
    requireNonNull(stripeTestProductId, "stripeTestProductId cannot be null");
    requireNonNull(stripeTestPlanId, "stripeTestPlanId cannot be null");
    requireNonNull(isDeprecated, "isDeprecated cannot be null");
    this.pointCap = pointCap;
    this.paymentInterval = paymentInterval;
    this.costPerInterval = costPerInterval;
    this.stripeProductId = stripeProductId;
    this.stripePlanId = stripePlanId;
    this.stripeTestProductId = stripeTestProductId;
    this.stripeTestPlanId = stripeTestPlanId;
    this.isDeprecated = isDeprecated;
  }
  
  public Integer getPointCap() {
    return pointCap;
  }

  public PaymentInterval getPaymentInterval() {
    return paymentInterval;
  }

  public Double getCostPerInterval() {
    return costPerInterval;
  }

  public String getStripeProductId() {
    return stripeProductId;
  }

  public String getStripePlanId() {
    return stripePlanId;
  }

  public String getStripeTestProductId() {
    return stripeTestProductId;
  }

  public String getStripeTestPlanId() {
    return stripeTestPlanId;
  }
  
  public Boolean isDeprecated() {
    return isDeprecated;
  }

  /**
   * 
   * @return A UI friendly display name for the payment plan.
   */
  public String getDisplayName() {
    
    DecimalFormat formatter = new DecimalFormat("###,###,###");
    StringBuilder sb = new StringBuilder();
    sb.append("$");
    sb.append(formatter.format(costPerInterval));
    sb.append("/");
    sb.append(paymentInterval.getDisplayName());
    sb.append(" up to ");
    sb.append(formatter.format(pointCap));
    sb.append(" points");
    return sb.toString();
  }
  
  /**
   * 
   * @return The savings per month for the yearly plan versus
   * the same monthly plan for the same point cap.  If this is 
   * a monthly plan, then the return value will be zero.
   */
  public Double calculateSavingsVersusMonthlyCost() {
    
    if (paymentInterval.equals(PaymentInterval.YEARLY)) {
      return Double.valueOf(0.0);
    }
    
    PaymentPlanEntity monthlyPlan = DictionaryContext
        .getPaymentPlansContainer()
        .getMonthlyPaymentPlan(pointCap);

    double monthlyCostPerMonth = monthlyPlan.getCostPerInterval();
    
    double yearlyCostPerYear = costPerInterval.doubleValue();
    double yearlyCostPerMonth = yearlyCostPerYear / 12;
    
    double yearlySavingsPerMonth = monthlyCostPerMonth - yearlyCostPerMonth;
    
    return Double.valueOf(yearlySavingsPerMonth);
  }

  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(paymentInterval.getName())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(pointCap)
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
  
  public int compareTo(AbstractEntity that) {
    
    if (that instanceof PaymentPlanEntity) {
      return this.costPerInterval.compareTo(((PaymentPlanEntity)that).costPerInterval);  
    }
    throw new IllegalStateException("Cannot compare to non PaymentPlanEntity entity: " + that.getClass().getSimpleName());
  }
}
