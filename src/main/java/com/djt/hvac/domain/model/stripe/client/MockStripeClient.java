//@formatter:off
package com.djt.hvac.domain.model.stripe.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.PaymentPlansContainer;
import com.djt.hvac.domain.model.dictionary.enums.PaymentInterval;
import com.djt.hvac.domain.model.distributor.paymentmethod.CreditCardPaymentMethodEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.utils.BuildingSubscriptionTemporalAdjuster;
import com.djt.hvac.domain.model.stripe.dto.StripeClientResponse;
import com.djt.hvac.domain.model.stripe.dto.StripeInvoice;
import com.djt.hvac.domain.model.stripe.dto.StripeSubscription;
import com.djt.hvac.domain.model.stripe.exception.StripeClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class MockStripeClient extends AbstractStripeClient {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MockStripeClient.class);
  
  private static final MockStripeClient INSTANCE = new MockStripeClient();
  public static final MockStripeClient getInstance() {
    return INSTANCE;
  }

  private static Integer MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextPersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }
  
  private static final String STRIPE_CUSTOMER_ID_PREFIX = "stripe_customer_id_";
  private static final String STRIPE_SUBSCRIPTION_ID_PREFIX = "stripe_subscription_id_";
  private static final String STRIPE_INVOICE_ID_PREFIX = "stripe_invoice_id_";
  private static final String STRIPE_SOURCE_ID_PREFIX = "stripe_source_id_";
  
  private static final String MOCK_API_KEY = "REDACTED";
  
  private Map<String, Map<String, Object>> customers = new TreeMap<>();
  
  private MockStripeClient() {
    super(MOCK_API_KEY);
  }
  
  public void reset() {
    this.customers.clear();
  }
  
  @Override
  public StripeClientResponse createStripeCustomer(String name, String email) throws StripeClientException {
    
    String stripeCustomerId = STRIPE_CUSTOMER_ID_PREFIX + Integer.toString(getNextPersistentIdentityValue());
    
    Map<String, Object> customerMap = new TreeMap<>();
    customerMap.put(NAME, name);
    customerMap.put(EMAIL, email);
    customers.put(stripeCustomerId, customerMap);
    
    Map<String, Object> responseObjects = new HashMap<>();
    responseObjects.put(STRIPE_CUSTOMER_ID, stripeCustomerId);
    
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @Override
  public StripeClientResponse deleteStripeCustomer(String stripeCustomerId) throws StripeClientException {
    
    Map<String, Object> customerMap = customers.get(stripeCustomerId);
    if (customerMap != null) {
      
      customers.remove(stripeCustomerId);
      return StripeClientResponse.STRIPE_CLIENT_SUCCESS_OBJECT;
    }
    throw new StripeClientException("Stripe customer with id: " + stripeCustomerId + " does not exist");
  }
  
  @Override
  public StripeClientResponse getAllStripeCustomers() throws StripeClientException {
    
    Map<String, Object> responseObjects = new HashMap<>();
    responseObjects.put(CUSTOMERS, customers.values());
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @Override
  public StripeClientResponse getStripeCustomer(String stripeCustomerId) throws StripeClientException {

    Map<String, Object> customerMap = customers.get(stripeCustomerId);
    if (customerMap != null) {
      
      Map<String, Object> responseObjects = new HashMap<>();
      responseObjects.put(CUSTOMER, customerMap);
      return StripeClientResponse.buildSuccessResponse(responseObjects);
    }
    throw new StripeClientException("Stripe customer with id: " + stripeCustomerId + " does not exist");
  }
  
  @Override
  public StripeClientResponse updateStripeCustomerForNewAccountManager(String stripeCustomerId, String email) throws StripeClientException {
    
    Map<String, Object> customerMap = customers.get(stripeCustomerId);
    customerMap.put(EMAIL, email);
    return StripeClientResponse.STRIPE_CLIENT_SUCCESS_OBJECT;
  }
  
  @Override
  public StripeClientResponse getAllStripeInvoices(
      String stripeCustomerId,
      String stripeSubscriptionId,
      String status,
      int limit,
      LocalDate endingBefore,
      LocalDate startingAfter)
  throws 
      StripeClientException {
    
    Map<String, Object> responseObjects = new HashMap<>();
    
    List<StripeInvoice> invoices = new ArrayList<>();
    
    invoices.add(StripeInvoice
        .builder()
        .withStripeSubscriptionId(stripeSubscriptionId)
        .withStripeInvoiceId(UUID.randomUUID().toString())
        .withIsPaid(Boolean.TRUE)
        .withIsAttempted(Boolean.TRUE)
        .withAttemptCount(Long.valueOf(1))
        .withInvoiceStatus(StripeClient.PAID)
        .withAmountDue(Long.valueOf(300))
        .withAmountPaid(Long.valueOf(300))
        .withCreated(AbstractEntity.getTimeKeeper().getCurrentInstant().getEpochSecond())
        .withPeriodStart("2020-12-01")
        .withPeriodEnd("2020-12-31")
        .withInvoicePdf("https://www.resolutebi.com/dummy/dummy_invoice.pdf")
        .build());

    invoices.add(StripeInvoice
        .builder()
        .withStripeSubscriptionId(stripeSubscriptionId)
        .withStripeInvoiceId(UUID.randomUUID().toString())
        .withIsPaid(Boolean.TRUE)
        .withIsAttempted(Boolean.TRUE)
        .withAttemptCount(Long.valueOf(1))
        .withInvoiceStatus(StripeClient.PAID)
        .withAmountDue(Long.valueOf(300))
        .withAmountPaid(Long.valueOf(300))
        .withCreated(AbstractEntity.getTimeKeeper().getCurrentInstant().getEpochSecond())
        .withPeriodStart("2020-12-01")
        .withPeriodEnd("2020-12-31")
        .withInvoicePdf("https://www.resolutebi.com/dummy/dummy_invoice.pdf")
        .build());

    responseObjects.put(HAS_MORE, Boolean.FALSE);
    responseObjects.put(INVOICES, invoices);
    
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @Override
  public StripeClientResponse getStripeInvoice(String stripeInvoiceId) throws StripeClientException {
    
    Map<String, Object> responseObjects = new HashMap<>();

    responseObjects.put(INVOICE, StripeInvoice
        .builder()
        .withStripeSubscriptionId("stripeSubscriptionId")
        .withStripeInvoiceId(UUID.randomUUID().toString())
        .withIsPaid(Boolean.TRUE)
        .withIsAttempted(Boolean.TRUE)
        .withAttemptCount(Long.valueOf(1))
        .withInvoiceStatus(StripeClient.PAID)
        .withAmountDue(Long.valueOf(300))
        .withAmountPaid(Long.valueOf(300))
        .withCreated(AbstractEntity.getTimeKeeper().getCurrentInstant().getEpochSecond())
        .withPeriodStart("2020-12-01")
        .withPeriodEnd("2020-12-31")
        .withInvoicePdf("https://www.resolutebi.com/dummy/dummy_invoice.pdf")
        .build());

    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @Override
  public StripeClientResponse getUpcomingStripeInvoice(String stripeSubscriptionId) throws StripeClientException {
    
    Map<String, Object> responseObjects = new HashMap<>();
    responseObjects.put(INVOICE, new HashMap<>());
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }

  /*
  @Override
  public StripeClientResponse createStripeInvoice(
      String stripeCustomerId, 
      Map<String, Object> invoiceParams) 
  throws 
      StripeClientException {
    // TODO: TDM: Implement
    throw new RuntimeException("Not implemented yet.");
  }
  
  @Override
  public StripeClientResponse payStripeInvoice(
      String stripeInvoiceId, 
      String stripeSourceId) 
  throws 
      StripeClientException {
    // TODO: TDM: Implement
    throw new RuntimeException("Not implemented yet.");
  }
  */
  
  @Override
  public StripeClientResponse deleteStripePaymentMethod(String stripeCustomerId, String stripeSourceId) throws StripeClientException {

    Map<String, Object> responseObjects = new HashMap<>();
    responseObjects.put(PAYMENT_METHOD, new HashMap<>());
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @Override
  public StripeClientResponse getAllStripePaymentMethods(String stripeCustomerId) throws StripeClientException {
    
    Map<String, Object> responseObjects = new HashMap<>();
    responseObjects.put(PAYMENT_METHODS, new HashMap<>());
    responseObjects.put(HAS_MORE, Boolean.FALSE);
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public StripeClientResponse attachStripePaymentMethod(String stripeCustomerId, String stripeSourceId) throws StripeClientException {
    
    Map<String, Object> customerMap = customers.get(stripeCustomerId);
    if (customerMap == null) {
      
      throw new StripeClientException("Stripe customer with id: "
          + stripeCustomerId 
          + " cannot be found.");
    }
    
    // Get the list of payment methods associated with the customer.
    List<String> customerPaymentMethods = null;
    Object object = customerMap.get("customer_payment_methods");
    if (object == null) {
      
      customerPaymentMethods = new ArrayList<>();
      customerMap.put("customer_payment_methods", customerPaymentMethods);
      
    } else {
      
      customerPaymentMethods = (List<String>)object;
      
    }
    customerPaymentMethods.add(stripeSourceId);
    
    Map<String, Object> responseObjects = new HashMap<>();
    responseObjects.put(STRIPE_SOURCE_ID, stripeSourceId);
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @Override
  public StripeClientResponse getStripePaymentMethod(String stripeSourceId) throws StripeClientException {
    
    Map<String, Object> responseObjects = new HashMap<>();
    responseObjects.put(PAYMENT_METHOD, new HashMap<>());
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @Override
  public StripeClientResponse getAllStripeProducts() throws StripeClientException {
    
    Map<String, Object> responseObjects = new HashMap<>();
    responseObjects.put(PRODUCTS, new HashMap<>());
    responseObjects.put(PLANS, new HashMap<>());
    responseObjects.put(HAS_MORE, Boolean.FALSE);
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @Override
  public StripeClientResponse deleteStripeSubscription(String stripeSubscriptionId) throws StripeClientException {
    
    Iterator<Entry<String, Map<String, Object>>> iterator = customers.entrySet().iterator();
    while (iterator.hasNext()) {
      
      Entry<String, Map<String, Object>> entry = iterator.next();
      Map<String, Object> customerMap = entry.getValue();
      if (customerMap.get(STRIPE_SUBSCRIPTION_ID).equals(stripeSubscriptionId)) {
        
        // When a subscription is created, this value is false.  
        // When a subscription is cancelled, this value is true.
        customerMap.put(CANCEL_AT_PERIOD_END, "true");
        
        Map<String, Object> responseObjects = new HashMap<>();
        responseObjects.put(SUBSCRIPTION, new HashMap<>());
        return StripeClientResponse.buildSuccessResponse(responseObjects);
      }
    }
    throw new StripeClientException("Stripe subscription with id: " + stripeSubscriptionId + " does not exist");
  }
  
  @Override
  public StripeClientResponse getAllStripeSubscriptions(String stripeCustomerId) throws StripeClientException {
    
    Map<String, Object> responseObjects = new HashMap<>();
    responseObjects.put(SUBSCRIPTIONS, new HashMap<>());
    responseObjects.put(HAS_MORE, Boolean.FALSE);
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @Override
  public StripeClientResponse getStripeSubscription(String stripeSubscriptionId) throws StripeClientException {
    
    Iterator<Entry<String, Map<String, Object>>> iterator = customers.entrySet().iterator();
    while (iterator.hasNext()) {
      
      Entry<String, Map<String, Object>> entry = iterator.next();
      Map<String, Object> customerMap = entry.getValue();
      if (customerMap.get(STRIPE_SUBSCRIPTION_ID).equals(stripeSubscriptionId)) {
        
        Map<String, Object> responseObjects = new HashMap<>();
        responseObjects.put(SUBSCRIPTION, new HashMap<>());
        return StripeClientResponse.buildSuccessResponse(responseObjects);
      }
    }
    throw new StripeClientException("Stripe subscription with id: " + stripeSubscriptionId + " does not exist");
  }
  
  @Override
  public StripeClientResponse updateStripeSubscriptionForNewPaymentMethod(
      String stripeSubscriptionId,
      String stripeSourceId) throws StripeClientException {
    
    Iterator<Entry<String, Map<String, Object>>> iterator = customers.entrySet().iterator();
    while (iterator.hasNext()) {
      
      Entry<String, Map<String, Object>> entry = iterator.next();
      Map<String, Object> customerMap = entry.getValue();
      if (customerMap.get(STRIPE_SUBSCRIPTION_ID).equals(stripeSubscriptionId)) {
        
        customerMap.put(STRIPE_SOURCE_ID, stripeSourceId);
        
        Map<String, Object> responseObjects = new HashMap<>();
        responseObjects.put(SUBSCRIPTION, new HashMap<>());
        return StripeClientResponse.buildSuccessResponse(responseObjects);
      }
    }
    throw new StripeClientException("Stripe subscription with id: " + stripeSubscriptionId + " does not exist");
  }
  
  @Override
  public StripeClientResponse updateStripeSubscriptionForNewProductDifferentInterval(
      String stripeSubscriptionId,
      String stripePlanId) throws StripeClientException {
    
    Iterator<Entry<String, Map<String, Object>>> iterator = customers.entrySet().iterator();
    while (iterator.hasNext()) {
      
      Entry<String, Map<String, Object>> entry = iterator.next();
      Map<String, Object> customerMap = entry.getValue();
      if (customerMap.get(STRIPE_SUBSCRIPTION_ID).equals(stripeSubscriptionId)) {
        
        customerMap.put(STRIPE_PLAN_ID, stripePlanId);
        
        Map<String, Object> responseObjects = new HashMap<>();
        responseObjects.put(SUBSCRIPTION, new HashMap<>());
        return StripeClientResponse.buildSuccessResponse(responseObjects);
      }
    }
    throw new StripeClientException("Stripe subscription with id: " + stripeSubscriptionId + " does not exist");
  }
  
  @Override
  public StripeClientResponse updateStripeSubscriptionForNewProductSameInterval(
      String stripeSubscriptionId,
      String stripePlanId) throws StripeClientException {
    
    Iterator<Entry<String, Map<String, Object>>> iterator = customers.entrySet().iterator();
    while (iterator.hasNext()) {
      
      Entry<String, Map<String, Object>> entry = iterator.next();
      Map<String, Object> customerMap = entry.getValue();
      if (customerMap.get(STRIPE_SUBSCRIPTION_ID).equals(stripeSubscriptionId)) {
        
        customerMap.put(STRIPE_PLAN_ID, stripePlanId);
        
        Map<String, Object> responseObjects = new HashMap<>();
        responseObjects.put(SUBSCRIPTION, new HashMap<>());
        return StripeClientResponse.buildSuccessResponse(responseObjects);
      }
    }
    throw new StripeClientException("Stripe subscription with id: " + stripeSubscriptionId + " does not exist");
  }
  
  @Override
  public StripeClientResponse createStripeSubscription(
      String stripeCustomerId,
      String stripeSourceId,
      String stripePlanId,
      String customerName,
      String buildingDisplayName,
      String buildingUuid)
  throws 
      StripeClientException {
    
    String stripeSubscriptionId = STRIPE_SUBSCRIPTION_ID_PREFIX + Integer.toString(getNextPersistentIdentityValue());
    String stripeInvoiceId = STRIPE_INVOICE_ID_PREFIX + Integer.toString(getNextPersistentIdentityValue());
    
    validateStripePaymentMethodExistsForStripeCustomer(stripeCustomerId, stripeSourceId);
    
    Map<String, Object> customerMap = customers.get(stripeCustomerId);
    customerMap.put(STRIPE_SOURCE_ID, stripeSourceId);
    customerMap.put(STRIPE_PLAN_ID, stripePlanId);
    customerMap.put(STRIPE_SUBSCRIPTION_ID, stripeSubscriptionId);
    customerMap.put(CUSTOMER_NAME, customerName);
    customerMap.put(BUILDING_DISPLAY_NAME, buildingDisplayName);
    customerMap.put(BUILDING_UUID, buildingUuid);
    
    // The subscription billing anchor is the date on which it was created.  The next 
    // payment is determined by the plan, which is either monthly or yearly.
    // Stripe won't allow us to use a date in the past, so in order to
    // be able to set the start to tbe at the start of the day, we must
    // advance to the next day.
    Long billingAnchor = getEpochSecondsForStartOfNextDay();
    LocalDate billingAnchorDate = getDate(billingAnchor);
    customerMap.put(BILLING_CYCLE_ANCHOR, billingAnchor);
    
    LocalDate subscriptionStartedAt = AbstractEntity.getTimeKeeper().getCurrentLocalDate();
    customerMap.put(SUBSCRIPTION_START_DATE, subscriptionStartedAt);
    
    LocalDate subscriptionCurrentIntervalStartedAt = subscriptionStartedAt;
    customerMap.put(SUBSCRIPTION_INTERVAL_START_DATE, subscriptionCurrentIntervalStartedAt);
    
    PaymentPlansContainer paymentPlansContainer = DictionaryContext.getPaymentPlansContainer();
    PaymentPlanEntity resolutePaymentPlan = paymentPlansContainer.getPaymentPlanForStripePlanId(stripePlanId);
    
    LocalDate subscriptionCurrentIntervalEndsAt = BuildingSubscriptionEntity.getCurrentIntervalEndsAt(
        subscriptionCurrentIntervalStartedAt, 
        resolutePaymentPlan);
    customerMap.put(SUBSCRIPTION_INTERVAL_END_DATE, subscriptionCurrentIntervalEndsAt);

    LocalDate nextIntervalStartsAt = BuildingSubscriptionEntity.getNextIntervalStartsAt(
        subscriptionCurrentIntervalStartedAt, 
        resolutePaymentPlan);
    customerMap.put(SUBSCRIPTION_NEXT_INTERVAL_START_DATE, nextIntervalStartsAt);
    
    // When a subscription is created, this value is false.  
    // When a subscription is cancelled, this value is true.
    customerMap.put(CANCEL_AT_PERIOD_END, "false");
    
    Boolean isPaid = null;      
    String invoiceStatus = null;
    Boolean isAttempted = null;
    Long attemptCount =  null;
    Long amountDue = Long.valueOf(resolutePaymentPlan.getCostPerInterval().longValue());
    
    String cardExpiry = extractCardExpiry(stripeSourceId);
    if (CreditCardPaymentMethodEntity.isCardExpired(cardExpiry)) {
      
      isPaid = Boolean.FALSE;      
      invoiceStatus = UNCOLLECTIBLE;
      isAttempted = Boolean.TRUE;
      attemptCount =  Long.valueOf(5L);
      
    } else {

      isPaid = Boolean.TRUE;      
      invoiceStatus = PAID;
      isAttempted = Boolean.TRUE;
      attemptCount =  Long.valueOf(1L);
      
    }

    // ONLY SUCCEED IF IS PAID IS TRUE.
    Map<String, Object> responseObjects = new HashMap<>();

    StripeInvoice stripeInvoice = StripeInvoice
        .builder()
        .withStripeSubscriptionId(stripeSubscriptionId)
        .withStripeInvoiceId(stripeInvoiceId)
        .withIsPaid(isPaid)
        .withIsAttempted(isAttempted)
        .withAttemptCount(attemptCount)
        .withInvoiceStatus(invoiceStatus)
        .withCreated(subscriptionCurrentIntervalStartedAt.toEpochDay()*86400L)
        .withAmountDue(amountDue)
        .withAmountPaid(Long.valueOf(0))
        .withPeriodStart(subscriptionCurrentIntervalStartedAt.format(LOCAL_DATE_FORMATTER.get()))
        .withPeriodEnd(subscriptionCurrentIntervalEndsAt.format(LOCAL_DATE_FORMATTER.get()))
        .withInvoicePdf("https://www.stripe.com/mock_invoice_pdf")
        .build();
    
    responseObjects.put(INVOICE, stripeInvoice);
    customerMap.put(INVOICE, stripeInvoice);
    
    if (!isPaid) {
      return StripeClientResponse.buildFailureResponse("Invoice was not paid, status: " + invoiceStatus, responseObjects);  
    } 

    StripeSubscription stripeSubscription = StripeSubscription
        .builder()
        .withStripeSubscriptionId(stripeSubscriptionId)
        .withStartedAt(subscriptionStartedAt.format(LOCAL_DATE_FORMATTER.get()))
        .withBillingAnchor(billingAnchorDate.format(LOCAL_DATE_FORMATTER.get()))
        .withCurrentIntervalStartedAt(subscriptionCurrentIntervalStartedAt.format(LOCAL_DATE_FORMATTER.get()))
        .withCurrentIntervalEndsAt(subscriptionCurrentIntervalEndsAt.format(LOCAL_DATE_FORMATTER.get()))
        .withNextIntervalStartsAt(nextIntervalStartsAt.format(LOCAL_DATE_FORMATTER.get()))
        .withStripeInvoiceId(stripeInvoiceId)
        .withInvoiceStatus(invoiceStatus)
        .withIsPaid(isPaid)
        .withIsAttempted(isAttempted)
        .withAttemptCount(attemptCount)
        .build();

    responseObjects.put(SUBSCRIPTION, stripeSubscription);
    customerMap.put(SUBSCRIPTION, stripeSubscription);
    
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }
  
  @Override
  public StripeClientResponse getLatestStripeInvoice(String stripeSubscriptionId) throws StripeClientException {

    String stripeInvoiceId = STRIPE_INVOICE_ID_PREFIX + Integer.toString(getNextPersistentIdentityValue());

    // See if we need to transition to a new subscription interval.  If so, then we need to
    // simulate making a payment, so we call the "isCardExpired()" method that does this.
    Map<String, Object> customerMap = getCustomerMapForStripeSubscriptionId(stripeSubscriptionId);
    String stripeSourceId = customerMap.get(STRIPE_SOURCE_ID).toString();
    String stripePlanId = customerMap.get(STRIPE_PLAN_ID).toString();
    
    LocalDate subscriptionIntervalStartDate = (LocalDate)customerMap.get(SUBSCRIPTION_INTERVAL_START_DATE);
    LocalDate subscriptionIntervalEndDate = (LocalDate)customerMap.get(SUBSCRIPTION_INTERVAL_END_DATE);
    
    PaymentPlansContainer paymentPlansContainer = DictionaryContext.getPaymentPlansContainer();
    PaymentPlanEntity resolutePaymentPlan = paymentPlansContainer.getPaymentPlanForStripePlanId(stripePlanId);
    PaymentPlanEntity pendingPaymentPlan = null;  
    
    LocalDate newSubscriptionIntervalStartDate = null;
    LocalDate newSubscriptionIntervalEndDate = null;
    boolean hasCurrentPaymentIntervalExpired = BuildingSubscriptionEntity.hasCurrentPaymentIntervalExpired(subscriptionIntervalEndDate);
    if (hasCurrentPaymentIntervalExpired) {
      
      newSubscriptionIntervalStartDate = transitionToNewPaymentInterval(subscriptionIntervalStartDate, resolutePaymentPlan, pendingPaymentPlan);
      customerMap.put(SUBSCRIPTION_INTERVAL_START_DATE, newSubscriptionIntervalStartDate);
      
      newSubscriptionIntervalEndDate = BuildingSubscriptionEntity.getCurrentIntervalEndsAt(
          newSubscriptionIntervalStartDate, 
          resolutePaymentPlan);
      customerMap.put(SUBSCRIPTION_INTERVAL_END_DATE, newSubscriptionIntervalEndDate);
    } else {
      customerMap.put(SUBSCRIPTION_INTERVAL_START_DATE, subscriptionIntervalStartDate);
      customerMap.put(SUBSCRIPTION_INTERVAL_END_DATE, subscriptionIntervalEndDate);
    }
    
    Boolean isPaid = null;      
    String invoiceStatus = null;
    Boolean isAttempted = null;
    Long attemptCount =  null;
    Long amountDue = Long.valueOf(resolutePaymentPlan.getCostPerInterval().longValue());
    
    String cardExpiry = extractCardExpiry(stripeSourceId);
    if (CreditCardPaymentMethodEntity.isCardExpired(cardExpiry)) {
      
      isPaid = Boolean.FALSE;      
      invoiceStatus = UNCOLLECTIBLE;
      isAttempted = Boolean.TRUE;
      attemptCount =  Long.valueOf(5L);
      
    } else {

      isPaid = Boolean.TRUE;      
      invoiceStatus = PAID;
      isAttempted = Boolean.TRUE;
      attemptCount =  Long.valueOf(1L);
      
    }
    
    Boolean cancelAtPeriodEnd = Boolean.parseBoolean(customerMap.get(CANCEL_AT_PERIOD_END).toString());

    String strNextIntervalStartsAt = null;
    LocalDate nextIntervalStartsAt = null;
    if (!cancelAtPeriodEnd) {
      if (hasCurrentPaymentIntervalExpired) {
        nextIntervalStartsAt = BuildingSubscriptionEntity.getNextIntervalStartsAt(
            newSubscriptionIntervalStartDate, 
            resolutePaymentPlan);
      } else {
        nextIntervalStartsAt = BuildingSubscriptionEntity.getNextIntervalStartsAt(
            subscriptionIntervalStartDate, 
            resolutePaymentPlan);
      }
    }
    if (nextIntervalStartsAt == null) {
      strNextIntervalStartsAt = "CANCELED AT PERIOD END";
    } else {
      
      strNextIntervalStartsAt = nextIntervalStartsAt.format(LOCAL_DATE_FORMATTER.get());
      customerMap.put(SUBSCRIPTION_NEXT_INTERVAL_START_DATE, nextIntervalStartsAt);
      
    }
    
    Map<String, Object> responseObjects = new HashMap<>();
    
    LocalDate periodStart = null;
    LocalDate periodEnd = null;
    if (hasCurrentPaymentIntervalExpired) {
      periodStart = newSubscriptionIntervalStartDate;
      periodEnd = newSubscriptionIntervalEndDate;
    } else {
      periodStart = subscriptionIntervalStartDate;
      periodEnd = subscriptionIntervalEndDate;
    }

    StripeInvoice stripeInvoice = StripeInvoice
        .builder()
        .withStripeSubscriptionId(stripeSubscriptionId)
        .withStripeInvoiceId(stripeInvoiceId)
        .withIsPaid(isPaid)
        .withIsAttempted(isAttempted)
        .withAttemptCount(attemptCount)
        .withInvoiceStatus(invoiceStatus)
        .withCreated(periodStart.toEpochDay()*86400L)
        .withAmountDue(amountDue)
        .withAmountPaid(Long.valueOf(0))
        .withPeriodStart(periodStart.format(LOCAL_DATE_FORMATTER.get()))
        .withPeriodEnd(periodEnd.format(LOCAL_DATE_FORMATTER.get()))
        .withInvoicePdf("https://www.stripe.com/mock_invoice_pdf")
        .build();
    
    responseObjects.put(INVOICE, stripeInvoice);
    customerMap.put(INVOICE, stripeInvoice);
    
    StripeSubscription stripeSubscription = (StripeSubscription)customerMap.get(SUBSCRIPTION);
    
    stripeSubscription = StripeSubscription
        .builder(stripeSubscription)
        .withStripeSubscriptionId(stripeSubscriptionId)
        .withCurrentIntervalStartedAt(periodStart.format(LOCAL_DATE_FORMATTER.get()))
        .withCurrentIntervalEndsAt(periodEnd.format(LOCAL_DATE_FORMATTER.get()))
        .withNextIntervalStartsAt(strNextIntervalStartsAt)
        .withStripeInvoiceId(stripeInvoiceId)
        .withInvoiceStatus(invoiceStatus)
        .withIsPaid(isPaid)
        .withIsAttempted(isAttempted)
        .withAttemptCount(attemptCount)
        .build();

    responseObjects.put(SUBSCRIPTION, stripeSubscription);
    customerMap.put(SUBSCRIPTION, stripeSubscription);
    
    return StripeClientResponse.buildSuccessResponse(responseObjects);
  }   
  
  /*
   * We need to simulate the behavior of Stripe handling the creation of payment methods
   * 
   * @param stripeCustomerId
   * @param cardExpiry
   * @param cardLastFour
   * @return
   * @throws StripeClientException
   */
  public String createStripePaymentMethod(
      String stripeCustomerId,
      String cardExpiry,
      String cardLastFour) throws StripeClientException {

    Map<String, Object> customerMap = customers.get(stripeCustomerId);
    if (customerMap == null) {
      
      throw new StripeClientException("Stripe customer with id: "
          + stripeCustomerId 
          + " cannot be found.");
    }
    
    if (CreditCardPaymentMethodEntity.isCardExpired(cardExpiry)) {

      LocalDate currentLocalDate = AbstractEntity
          .getTimeKeeper()
          .getCurrentTimestamp()
          .toLocalDateTime()
          .toLocalDate();
      
      throw new StripeClientException("Credit card with last four: "
          + cardLastFour 
          + " is not valid, as it has expiry: "
          + cardExpiry
          + ", but the current date is: "
          + currentLocalDate);
    }
    
    String stripeSourceId = buildStripeSourceId(cardExpiry, cardLastFour);
    
    return stripeSourceId;
  }
  
  @SuppressWarnings("unchecked")
  private void validateStripePaymentMethodExistsForStripeCustomer(
      String stripeCustomerId,
      String stripeSourceId) throws StripeClientException {
    
    Map<String, Object> customerMap = customers.get(stripeCustomerId);
    if (customerMap == null) {
      
      throw new StripeClientException("Stripe customer with id: "
          + stripeCustomerId 
          + " cannot be found.");
    }
    
    // Get the list of payment methods associated with the customer.
    List<String> customerPaymentMethods = null;
    Object object = customerMap.get("customer_payment_methods");
    if (object != null) {
      
      customerPaymentMethods = (List<String>)object;
      for (String s: customerPaymentMethods) {
        
        if (s.equals(stripeSourceId)) {
          return;
        }
      }
    }
    
    throw new StripeClientException("Stripe source with id: "
        + stripeSourceId
        + " is not associated with stripe customer with id: "
        + stripeCustomerId 
        + " cannot be found.");
  }
  
  private String buildStripeSourceId(String cardExpiry, String cardLastFour) {
    
    return STRIPE_SOURCE_ID_PREFIX
        + cardExpiry
        + "_"
        + cardLastFour
        + "-"
        + Integer.toString(getNextPersistentIdentityValue());
  }
  
  private String extractCardExpiry(String stripeSourceId) {
    
    try {
      String s = stripeSourceId.replace(STRIPE_SOURCE_ID_PREFIX, "");
      int idx = s.indexOf("_");
      return s.substring(0, idx);
    } catch (Exception e) {
      LOGGER.error("Unable to extract card expiry embedded in stripe source id (mock): ["
          + stripeSourceId
          + "], expected a string of the form: [stripe_source_id_MM/YYYY_1234-X], where MM/YYYY is the card expiry, 1234 is the card last four and X is some unique integer");
      return "09/2024";
    }
  }
  
  private Map<String, Object> getCustomerMapForStripeSubscriptionId(String stripeSubscriptionId) throws StripeClientException {
    
    Iterator<Entry<String, Map<String, Object>>> iterator = customers.entrySet().iterator();
    while (iterator.hasNext()) {
      
      Entry<String, Map<String, Object>> entry = iterator.next();
      Map<String, Object> customerMap = entry.getValue();
      if (customerMap.get(STRIPE_SUBSCRIPTION_ID).equals(stripeSubscriptionId)) {
        
        return customerMap;
      }
    }
    throw new StripeClientException("Stripe subscription with id: " + stripeSubscriptionId + " does not exist");
  }
  
  public LocalDate transitionToNewPaymentInterval(
      LocalDate currentIntervalStartedAtLocalDate,
      PaymentPlanEntity parentPaymentPlan,
      PaymentPlanEntity pendingPaymentPlan) {
    
    LocalDate newCurrentIntervalStartedAtLocalDate = null;
    LocalDate currentIntervalEndsAtLocalDate = null;
    if (parentPaymentPlan.getPaymentInterval().equals(PaymentInterval.MONTHLY) && pendingPaymentPlan == null) {

      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);
      
      newCurrentIntervalStartedAtLocalDate = currentIntervalEndsAtLocalDate;
      
    } else if (parentPaymentPlan.getPaymentInterval().equals(PaymentInterval.MONTHLY) && pendingPaymentPlan != null) {

      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);
      
      newCurrentIntervalStartedAtLocalDate = currentIntervalEndsAtLocalDate;
      
      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfYearForCurrentLocalDate(
          currentIntervalStartedAtLocalDate.plusMonths(1), 
          currentIntervalStartedAtLocalDate);
      
    } else if (parentPaymentPlan.getPaymentInterval().equals(PaymentInterval.YEARLY) && pendingPaymentPlan == null) {
      
      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfYearForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);
      
      newCurrentIntervalStartedAtLocalDate = currentIntervalEndsAtLocalDate;
      
    } else if (parentPaymentPlan.getPaymentInterval().equals(PaymentInterval.YEARLY) && pendingPaymentPlan != null) {
      
      currentIntervalEndsAtLocalDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);
      
      newCurrentIntervalStartedAtLocalDate = currentIntervalEndsAtLocalDate;
      
    }
    
    return newCurrentIntervalStartedAtLocalDate;
  }  
  
  public Map<String, Map<String, Object>> getState() {
    
    return this.customers;
  }

  public String getStateAsJson() throws JsonProcessingException {
    
    return AbstractEntity.OBJECT_WRITER.get().writeValueAsString(this.customers);
  }
  
  public void loadState() {
    
    String basePath = System.getProperty("user.home") + "/";
    File file = new File(basePath + "/MockStripeClient.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          Map<String, Map<String, Object>> state = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<Map<String, Map<String, Object>>>() {});
          this.customers = state;
  
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOGGER.error("Mock stripe client state does not exist: " + file.getAbsolutePath());
    }
  }
  
  public void storeState() {
    
    String basePath = System.getProperty("user.home") + "/";
    File file = new File(basePath + "/MockStripeClient.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, this.customers);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }   
}
//@formatter:on