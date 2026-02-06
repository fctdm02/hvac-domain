package com.djt.hvac.domain.model.dictionary.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.dto.PaymentPlanDto;
import com.djt.hvac.domain.model.dictionary.enums.PaymentInterval;

public class PaymentPlansContainer {

  private final Map<Integer, PaymentPlanEntity> paymentPlans;
  
  private Integer maxPointCap = Integer.valueOf(-1);
  private Integer minPointCap = Integer.valueOf(Integer.MAX_VALUE);
  
  // Used for testing.
  private Integer originalMaxPointCap;
  public void setMaxPointCapForTesting(int maxPointCap) {
    
    originalMaxPointCap = maxPointCap;
    this.maxPointCap = maxPointCap;
  }
  
  public void resetMaxPointCap() {
    if (originalMaxPointCap != null) {
      this.maxPointCap = originalMaxPointCap;  
    }
  }
  
  public PaymentPlansContainer(List<PaymentPlanEntity> entityList) {
    
    super();
    paymentPlans = new TreeMap<>();
    for (PaymentPlanEntity e: entityList) {
      
      Integer pointCap = e.getPointCap();
      if (pointCap.intValue() > maxPointCap.intValue()) {
        maxPointCap = pointCap;
      }
      if (pointCap.intValue() < minPointCap.intValue()) {
        minPointCap = pointCap;
      }
      
      paymentPlans.put(e.getPersistentIdentity(), e);
    }
  }
  
  public Set<PaymentPlanEntity> getPaymentPlans() {
    
    Set<PaymentPlanEntity> set = new LinkedHashSet<>();
    set.addAll(paymentPlans.values());
    return set;
  }
  
  public PaymentPlanEntity getPaymentPlan(Integer paymentPlanId) {
    
    return paymentPlans.get(paymentPlanId);
  }
  
  public void addPaymentPlan(PaymentPlanEntity paymentPlan) {
    
    paymentPlans.put(paymentPlan.getPersistentIdentity(), paymentPlan);
  }
    
  @Override
  public String toString() {
    return new StringBuilder()
        .append("PaymentPlansContainer [maxPointCap: "
            + maxPointCap
            + ", paymentPlans=")
        .append(paymentPlans)
        .append("]")
        .toString();
  }

  public Integer getMaxPointCap() {
    return maxPointCap;
  }

  public Integer getMinPointCap() {
    return minPointCap;
  }
  
  public PaymentPlanEntity getMonthlyPaymentPlan(Integer pointCap) {
    
    for (PaymentPlanEntity e: paymentPlans.values()) {
      
      if (e.getPaymentInterval().equals(PaymentInterval.MONTHLY)
          && e.getPointCap().equals(pointCap)) {
        
        return e;
      }
    }
    throw new IllegalStateException("Payment plan with point cap: ["
        + pointCap
        + "] not found in: "
        + paymentPlans.values());
  }
  
  public PaymentPlanEntity getYearlyPaymentPlan(Integer pointCap) {
    
    for (PaymentPlanEntity e: paymentPlans.values()) {
      
      if (e.getPaymentInterval().equals(PaymentInterval.YEARLY)
          && e.getPointCap().equals(pointCap)) {
        
        return e;
      }
    }
    throw new IllegalStateException("Payment plan with point cap: ["
        + pointCap
        + "] not found in: "
        + paymentPlans.values());
  }
  
  public PaymentPlanEntity getPaymentPlanForStripePlanId(String stripePlanId) {
    
    for (PaymentPlanEntity e: paymentPlans.values()) {
      
      if (e.getStripePlanId().equalsIgnoreCase(stripePlanId)
          || e.getStripeTestPlanId().equalsIgnoreCase(stripePlanId)) {
        
        return e;
      }
    }
    throw new IllegalStateException("Payment plan with stripe plan id: ["
        + stripePlanId
        + "] not found in: "
        + paymentPlans.values());
  }
  
  public static PaymentPlansContainer mapFromDtos(List<PaymentPlanDto> dtoList) {
    
    List<PaymentPlanEntity> entityList = new ArrayList<>(); 
    Iterator<PaymentPlanDto> iterator = dtoList.iterator();
    while (iterator.hasNext()) {
      
      PaymentPlanDto dto = iterator.next();
      entityList.add(new PaymentPlanEntity(
          dto.getId(),
          dto.getPointCap(),
          PaymentInterval.get(dto.getInterval()),
          dto.getCostPerInterval(),
          dto.getStripeProductId(),
          dto.getStripePlanId(),
          dto.getStripeTestProductId(),
          dto.getStripeTestPlanId(),
          dto.getDeprecated()));
    }
    PaymentPlansContainer paymentPlansContainer = new PaymentPlansContainer(entityList);
    return paymentPlansContainer;
  }
  
  public static List<PaymentPlanDto> mapToDtos(PaymentPlansContainer paymentPlansContainer) {
    
    List<PaymentPlanDto> dtos = new ArrayList<>();
    Iterator<PaymentPlanEntity> iterator = paymentPlansContainer.getPaymentPlans().iterator();
    while (iterator.hasNext()) {
      
      PaymentPlanEntity e = iterator.next();
 
      PaymentPlanDto dto = new PaymentPlanDto();
      dto.setId(e.getPersistentIdentity());
      dto.setPointCap(e.getPointCap());
      dto.setInterval(e.getPaymentInterval().getName());
      dto.setCostPerInterval(e.getCostPerInterval());
      dto.setStripeProductId(e.getStripeProductId());
      dto.setStripePlanId(e.getStripePlanId());
      dto.setStripeTestProductId(e.getStripeTestProductId());
      dto.setStripeTestPlanId(e.getStripeTestPlanId());
      dto.setDeprecated(e.isDeprecated());
      dtos.add(dto);
    }
    return dtos;
  }  
}