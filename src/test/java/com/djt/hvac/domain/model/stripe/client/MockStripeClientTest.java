//@formatter:off
package com.djt.hvac.domain.model.stripe.client;

import java.util.TimeZone;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.timekeeper.impl.TestTimeKeeperImpl;
import com.djt.hvac.domain.model.stripe.client.MockStripeClient;
import com.djt.hvac.domain.model.stripe.client.StripeClient;
import com.djt.hvac.domain.model.stripe.dto.StripeClientResponse;
import com.djt.hvac.domain.model.stripe.dto.StripeSubscription;

public class MockStripeClientTest extends AbstractResoluteDomainModelTest {
  
  private String name = "Online Distributor Name";
  private String email = "tmyers@resolutebi.com";
  
  private String stripeCustomerId;
  private String stripeSourceId;
  private String cardExpiry = "09/2024";
  private String cardLastFour = "1234";
  private String stripeSubscriptionId;
  private String stripePlanId;
  
  private StripeClient stripeClient;
  private StripeClientResponse stripeClientResponse;
  
  /*
   * REAL STRIPE CLIENT (ACTUAL CLOCK)
   * =================================
   * CURRENT LOCAL DATE TIME OBJECT: 2020-03-13T17:17:51.606
   * CURRENT EPOCH MILLIS: 1584119871612
   * 
   * MOCK STRIPE CLIENT (STATIC CLOCK)
   * =================================
   * CURRENT LOCAL DATE TIME OBJECT: 2020-03-13T17:17:51.612
   * CURRENT EPOCH MILLIS: 1584120724182
   * 
   * NOTE: ALL THE TIMESTAMPS/DATA HERE WERE TAKEN FROM THE ACTUAL
   *       STRIPE WEB SERVICE CLIENT INTEGRATION TEST, SO OUR GOAL
   *       HERE IS TO HAVE THE MOCK CLIENT EMULATE AS CLOSE AS
   *       POSSIBLE TO THE REAL THING.
   *       
   * TODO: 
   * 
   * 3. make sure dates/timestamps align from actual client integration
   * test with this test
   *         
   */
  private final long epochMillis = 1584119871612L;
  private final TestTimeKeeperImpl testTimeKeeper = new TestTimeKeeperImpl("2020-03-13");

  @Before
  public void before() throws Exception {
    
    super.before();
  
    dictionaryService.ensureDictionaryDataIsLoaded();
    
    stripeClient = MockStripeClient.getInstance();
    ((MockStripeClient)stripeClient).reset();

    // SET TIMEZONE TO GMT
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("GMT"));    
      System.err.println("Using default time zone:" + TimeZone.getDefault());
      System.err.println();
    } catch (Exception e) {
    }

    testTimeKeeper.setCurrentTime(epochMillis);
    AbstractEntity.setTimeKeeper(testTimeKeeper);

    System.err.println("CURRENT LOCAL DATE TIME OBJECT: " + AbstractEntity.getTimeKeeper().getCurrentLocalDateTime());
    System.err.println("CURRENT EPOCH MILLIS: " + System.currentTimeMillis());
  }
  
  @Test
  public void testStripeOperations() throws Exception {
    

    
    
    // PRODUCT
    stripeClientResponse = stripeClient.getAllStripeProducts();
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripeClientResponse result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());


    
    
    // CUSTOMER
    stripeClientResponse = stripeClient.createStripeCustomer(name, email);
    stripeCustomerId = stripeClientResponse.getResponseObjects().get(StripeClient.STRIPE_CUSTOMER_ID).toString();
    Assert.assertNotNull("stripeCustomerId is null", stripeCustomerId);
    Assert.assertEquals("stripeClientResponse result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    
    
    stripeClientResponse = stripeClient.getAllStripeCustomers();
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripeClientResponse result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    
    
    stripeClientResponse = stripeClient.getStripeCustomer(stripeCustomerId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripeClientResponse result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    
    
    String newEmail = "tmyers1@yahoo.com";
    stripeClientResponse = stripeClient.updateStripeCustomerForNewAccountManager(stripeCustomerId, newEmail);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripeClientResponse result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());

    
    
    
    // THIS SIMULATES STRIPE TEST PAYMENT METHODS
    stripeSourceId = ((MockStripeClient)stripeClient).createStripePaymentMethod(
        stripeCustomerId, 
        cardExpiry, 
        cardLastFour);
    
    
    
    
    // PAYMENT METHOD OPERATIONS
    stripeClientResponse = stripeClient.attachStripePaymentMethod(stripeCustomerId, stripeSourceId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    
    stripeClientResponse = stripeClient.getAllStripePaymentMethods(stripeCustomerId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());

    
    stripeClientResponse = stripeClient.getStripePaymentMethod(stripeSourceId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    
    
    
    
    // SUBSCRIPTION
    String customerName = name;
    String buildingDisplayName = "Building Display Name";
    String buildingUuid = UUID.randomUUID().toString();
    stripePlanId = "plan_GfSSjocBNNL12b"; // 3000 monthly
    stripeClientResponse = stripeClient.createStripeSubscription(
        stripeCustomerId, 
        stripeSourceId, 
        stripePlanId,
        customerName,
        buildingDisplayName,
        buildingUuid);
    
    System.err.println("SUBSCRIPTION: " + stripeClientResponse);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    StripeSubscription stripeSubscription = (StripeSubscription)stripeClientResponse.getResponseObjects().get(StripeClient.SUBSCRIPTION);
    stripeSubscriptionId = stripeSubscription.getStripeSubscriptionId();
    Assert.assertNotNull("stripeSubscriptionId is null", stripeSubscriptionId);
    
    
    stripeClientResponse = stripeClient.getStripeSubscription(stripeSubscriptionId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    

    String newStripePlanSameIntervalId = "plan_GfSVHf9pYqk2Tt"; // 5000 monthly
    stripeClientResponse = stripeClient.updateStripeSubscriptionForNewProductSameInterval(
        stripeSubscriptionId, 
        newStripePlanSameIntervalId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());

    
    String newStripePlanDifferentIntervalId = "plan_GfSWvz92t7PGut"; // 5000 yearly
    stripeClientResponse = stripeClient.updateStripeSubscriptionForNewProductDifferentInterval(
        stripeSubscriptionId, 
        newStripePlanDifferentIntervalId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    
    
    stripeClientResponse = stripeClient.getAllStripeSubscriptions(stripeCustomerId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());

    
    
    
    // INVOICE OPERATIONS
    /*
    int limit = StripeClient.DEFAULT_LIMIT;
    LocalDate endingBefore = null;
    LocalDate startingAfter = null;
    String status = StripeClient.PAID;
    stripeClientResponse = stripeClient.getAllStripeInvoices(
        stripeCustomerId,
        stripeSubscriptionId,
        status,
        limit,
        endingBefore,
        startingAfter);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    InvoiceCollection invoiceCollection = (InvoiceCollection)stripeClientResponse.getResponseObjects().get(StripeClient.INVOICES);
    List<Invoice> invoices = invoiceCollection.getData();
    Assert.assertFalse("no invoices exist", invoices.isEmpty());
    Invoice invoice = invoices.get(0);
    stripeInvoiceId = invoice.getId();

    
    stripeClientResponse = stripeClient.getStripeInvoice(stripeInvoiceId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    invoice = null;
    invoice = (Invoice)stripeClientResponse.getResponseObjects().get(StripeClient.INVOICE);
    Assert.assertNotNull("invoice is null", invoice);
    Assert.assertEquals("stripe invoice id is incorrect", stripeInvoiceId, invoice.getId());

    
    stripeClientResponse = stripeClient.getLatestStripeInvoice(stripeSubscriptionId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    invoice = null;
    invoice = (Invoice)stripeClientResponse.getResponseObjects().get(StripeClient.INVOICE);
    Assert.assertNotNull("invoice is null", invoice);
    Assert.assertEquals("stripe invoice id is incorrect", stripeInvoiceId, invoice.getId());
    System.err.println("LATEST INVOICE: " + invoice);

    
    stripeClientResponse = stripeClient.getUpcomingStripeInvoice(stripeSubscriptionId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    invoice = null;
    invoice = (Invoice)stripeClientResponse.getResponseObjects().get(StripeClient.INVOICE);
    Assert.assertNotNull("invoice is null", invoice);
    System.err.println("UPCOMING INVOICE: " + invoice);
    */

    
    
    
    // DELETE OPERATIONS
    stripeClientResponse = stripeClient.deleteStripeSubscription(stripeSubscriptionId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());

    
    stripeClientResponse = stripeClient.deleteStripePaymentMethod(stripeCustomerId, stripeSourceId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripe response result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
    
    
    stripeClientResponse = stripeClient.deleteStripeCustomer(stripeCustomerId);
    Assert.assertNotNull("stripeClientResponse is null", stripeClientResponse);
    Assert.assertEquals("stripeClientResponse result is incorrect", StripeClientResponse.RESULT_SUCCESS, stripeClientResponse.getResult());
  }  
 }
//@formatter:on







/*

Using default time zone:sun.util.calendar.ZoneInfo[id="GMT",offset=0,dstSavings=0,useDaylight=false,transitions=0,lastRule=null]

CURRENT LOCAL DATE TIME OBJECT: 2020-03-13T17:17:51.606
CURRENT EPOCH MILLIS: 1584119871612
13:17:56.856 [main] INFO com.djt.hvac.common.model.stripe.client.StripeClientWebServiceImpl - Successfully attached test payment method: pm_1GMH8xEyc0NCi7H2Hi5VXa8P to test customer: cus_Gu5J1SqdFtAsvT
SUBSCRIPTION: StripeClientResponse [result=SUCCESS, responseObjects={subscription_interval_start_date=1584119876, attempt_count=1, stripeSubscriptionId=sub_Gu5JTVwA18NadV, subscription_interval_end_date=1584144000, is_attempted=true, subscription_start_date=1584119876, stripeInvoiceId=in_1GMH8zEyc0NCi7H2LV2EgtXk, subscription=<com.stripe.model.Subscription@2030036700 id=sub_Gu5JTVwA18NadV> JSON: {
  "application_fee_percent": null,
  "billing_cycle_anchor": 1584144000,
  "billing_thresholds": null,
  "cancel_at": null,
  "cancel_at_period_end": false,
  "canceled_at": null,
  "collection_method": "charge_automatically",
  "created": 1584119876,
  "current_period_end": 1584144000,
  "current_period_start": 1584119876,
  "customer": "cus_Gu5J1SqdFtAsvT",
  "days_until_due": null,
  "default_payment_method": "pm_1GMH8xEyc0NCi7H2Hi5VXa8P",
  "default_source": null,
  "default_tax_rates": [],
  "discount": null,
  "ended_at": null,
  "id": "sub_Gu5JTVwA18NadV",
  "items": {
    "object": "list",
    "data": [
      {
        "billing_thresholds": null,
        "created": 1584119877,
        "deleted": null,
        "id": "si_Gu5JaLMb54R0LY",
        "metadata": {
          "Building": "Building Display Name",
          "Customer": "Online Distributor Name",
          "UUID": "75b431ee-9b2c-44f2-b141-30fed4585b9c"
        },
        "object": "subscription_item",
        "plan": {
          "active": true,
          "aggregate_usage": null,
          "amount": 30000,
          "amount_decimal": 30000,
          "billing_scheme": "per_unit",
          "created": 1583003655,
          "currency": "usd",
          "deleted": null,
          "id": "plan_GfSSjocBNNL12b",
          "interval": "month",
          "interval_count": 1,
          "livemode": false,
          "metadata": {},
          "nickname": "PointCap3000Monthly",
          "object": "plan",
          "product": "prod_GfSR0F9rOvF5Jz",
          "tiers": null,
          "tiers_mode": null,
          "transform_usage": null,
          "trial_period_days": null,
          "usage_type": "licensed"
        },
        "quantity": 1,
        "subscription": "sub_Gu5JTVwA18NadV",
        "tax_rates": []
      }
    ],
    "has_more": false,
    "url": "/v1/subscription_items?subscription\u003dsub_Gu5JTVwA18NadV",
    "request_options": null,
    "request_params": null
  },
  "latest_invoice": "in_1GMH8zEyc0NCi7H2LV2EgtXk",
  "livemode": false,
  "metadata": {
    "Building": "Building Display Name",
    "Customer": "Online Distributor Name",
    "UUID": "75b431ee-9b2c-44f2-b141-30fed4585b9c"
  },
  "next_pending_invoice_item_invoice": null,
  "object": "subscription",
  "pending_invoice_item_interval": null,
  "pending_setup_intent": null,
  "pending_update": null,
  "plan": {
    "active": true,
    "aggregate_usage": null,
    "amount": 30000,
    "amount_decimal": 30000,
    "billing_scheme": "per_unit",
    "created": 1583003655,
    "currency": "usd",
    "deleted": null,
    "id": "plan_GfSSjocBNNL12b",
    "interval": "month",
    "interval_count": 1,
    "livemode": false,
    "metadata": {},
    "nickname": "PointCap3000Monthly",
    "object": "plan",
    "product": "prod_GfSR0F9rOvF5Jz",
    "tiers": null,
    "tiers_mode": null,
    "transform_usage": null,
    "trial_period_days": null,
    "usage_type": "licensed"
  },
  "quantity": 1,
  "schedule": null,
  "start_date": 1584119876,
  "status": "active",
  "tax_percent": null,
  "transfer_data": null,
  "trial_end": null,
  "trial_start": null
}, invoice=<com.stripe.model.Invoice@752684363 id=in_1GMH8zEyc0NCi7H2LV2EgtXk> JSON: {
  "account_country": "US",
  "account_name": "Resolute Building Intelligence, LLC",
  "amount_due": 270,
  "amount_paid": 270,
  "amount_remaining": 0,
  "application_fee_amount": null,
  "attempt_count": 1,
  "attempted": true,
  "auto_advance": false,
  "billing_reason": "subscription_create",
  "charge": "ch_1GMH8zEyc0NCi7H21GSfO79Z",
  "collection_method": "charge_automatically",
  "created": 1584119877,
  "currency": "usd",
  "custom_fields": null,
  "customer": "cus_Gu5J1SqdFtAsvT",
  "customer_address": null,
  "customer_email": "tmyers@resolutebi.com",
  "customer_name": "Online Distributor Name",
  "customer_phone": null,
  "customer_shipping": null,
  "customer_tax_exempt": "none",
  "customer_tax_ids": [],
  "default_payment_method": null,
  "default_source": null,
  "default_tax_rates": [],
  "deleted": null,
  "description": null,
  "discount": null,
  "due_date": null,
  "ending_balance": 0,
  "footer": null,
  "hosted_invoice_url": "https://pay.stripe.com/invoice/acct_1FCnavEyc0NCi7H2/invst_Gu5JkGzC40kD6euujPdzxG77pJ4EURF",
  "id": "in_1GMH8zEyc0NCi7H2LV2EgtXk",
  "invoice_pdf": "https://pay.stripe.com/invoice/acct_1FCnavEyc0NCi7H2/invst_Gu5JkGzC40kD6euujPdzxG77pJ4EURF/pdf",
  "lines": {
    "object": "list",
    "data": [
      {
        "amount": 270,
        "currency": "usd",
        "description": "Time on $300/month up to 3,000 points after 13 Mar 2020",
        "discountable": false,
        "id": "il_1GMH8zEyc0NCi7H2aHdbTlzS",
        "invoice_item": "ii_1GMH8zEyc0NCi7H2AcRFbAF7",
        "livemode": false,
        "metadata": {},
        "object": "line_item",
        "period": {
          "end": 1584144000,
          "start": 1584119876
        },
        "plan": {
          "active": true,
          "aggregate_usage": null,
          "amount": 30000,
          "amount_decimal": 30000,
          "billing_scheme": "per_unit",
          "created": 1583003655,
          "currency": "usd",
          "deleted": null,
          "id": "plan_GfSSjocBNNL12b",
          "interval": "month",
          "interval_count": 1,
          "livemode": false,
          "metadata": {},
          "nickname": "PointCap3000Monthly",
          "object": "plan",
          "product": "prod_GfSR0F9rOvF5Jz",
          "tiers": null,
          "tiers_mode": null,
          "transform_usage": null,
          "trial_period_days": null,
          "usage_type": "licensed"
        },
        "proration": true,
        "quantity": 1,
        "subscription": "sub_Gu5JTVwA18NadV",
        "subscription_item": "si_Gu5JaLMb54R0LY",
        "tax_amounts": [],
        "tax_rates": [],
        "type": "invoiceitem",
        "unified_proration": null
      }
    ],
    "has_more": false,
    "url": "/v1/invoices/in_1GMH8zEyc0NCi7H2LV2EgtXk/lines",
    "request_options": null,
    "request_params": null
  },
  "livemode": false,
  "metadata": {},
  "next_payment_attempt": null,
  "number": "3A837DEA-0001",
  "object": "invoice",
  "paid": true,
  "payment_intent": "pi_1GMH8zEyc0NCi7H2n3oWWgIF",
  "period_end": 1584119876,
  "period_start": 1584119876,
  "post_payment_credit_notes_amount": 0,
  "pre_payment_credit_notes_amount": 0,
  "receipt_number": null,
  "starting_balance": 0,
  "statement_descriptor": null,
  "status": "paid",
  "status_transitions": {
    "finalized_at": 1584119877,
    "marked_uncollectible_at": null,
    "paid_at": 1584119878,
    "voided_at": null
  },
  "subscription": "sub_Gu5JTVwA18NadV",
  "subscription_proration_date": null,
  "subtotal": 270,
  "tax": null,
  "tax_percent": null,
  "threshold_reason": null,
  "total": 270,
  "total_tax_amounts": [],
  "transfer_data": null,
  "webhooks_delivered_at": 1584119878
}, is_paid=true, billing_cycle_anchor=1584144000, invoice_status=paid}, reason=null]
13:18:00.675 [main] INFO com.djt.hvac.common.model.stripe.client.StripeClientWebServiceImpl - Deleted old subscription item: <com.stripe.model.SubscriptionItem@773662650 id=si_Gu5JaLMb54R0LY> JSON: {
  "billing_thresholds": null,
  "created": 1584119877,
  "deleted": null,
  "id": "si_Gu5JaLMb54R0LY",
  "metadata": {
    "Building": "Building Display Name",
    "Customer": "Online Distributor Name",
    "UUID": "75b431ee-9b2c-44f2-b141-30fed4585b9c"
  },
  "object": "subscription_item",
  "plan": {
    "active": true,
    "aggregate_usage": null,
    "amount": 30000,
    "amount_decimal": 30000,
    "billing_scheme": "per_unit",
    "created": 1583003655,
    "currency": "usd",
    "deleted": null,
    "id": "plan_GfSSjocBNNL12b",
    "interval": "month",
    "interval_count": 1,
    "livemode": false,
    "metadata": {},
    "nickname": "PointCap3000Monthly",
    "object": "plan",
    "product": "prod_GfSR0F9rOvF5Jz",
    "tiers": null,
    "tiers_mode": null,
    "transform_usage": null,
    "trial_period_days": null,
    "usage_type": "licensed"
  },
  "quantity": 1,
  "subscription": "sub_Gu5JTVwA18NadV",
  "tax_rates": []
}
LATEST INVOICE: <com.stripe.model.Invoice@1944978632 id=in_1GMH93Eyc0NCi7H2O5x7P92o> JSON: {
  "account_country": "US",
  "account_name": "Resolute Building Intelligence, LLC",
  "amount_due": 499280,
  "amount_paid": 499280,
  "amount_remaining": 0,
  "application_fee_amount": null,
  "attempt_count": 1,
  "attempted": true,
  "auto_advance": false,
  "billing_reason": "subscription_update",
  "charge": "ch_1GMH93Eyc0NCi7H2QuFlVBMI",
  "collection_method": "charge_automatically",
  "created": 1584119881,
  "currency": "usd",
  "custom_fields": null,
  "customer": "cus_Gu5J1SqdFtAsvT",
  "customer_address": null,
  "customer_email": "tmyers@resolutebi.com",
  "customer_name": "Online Distributor Name",
  "customer_phone": null,
  "customer_shipping": null,
  "customer_tax_exempt": "none",
  "customer_tax_ids": [],
  "default_payment_method": null,
  "default_source": null,
  "default_tax_rates": [],
  "deleted": null,
  "description": null,
  "discount": null,
  "due_date": null,
  "ending_balance": 0,
  "footer": null,
  "hosted_invoice_url": "https://pay.stripe.com/invoice/acct_1FCnavEyc0NCi7H2/invst_Gu5J71NKvqH61shZXaxskiz9Wt8AkD9",
  "id": "in_1GMH93Eyc0NCi7H2O5x7P92o",
  "invoice_pdf": "https://pay.stripe.com/invoice/acct_1FCnavEyc0NCi7H2/invst_Gu5J71NKvqH61shZXaxskiz9Wt8AkD9/pdf",
  "lines": {
    "object": "list",
    "data": [
      {
        "amount": -450,
        "currency": "usd",
        "description": "Unused time on $500/month up to 5,000 points after 13 Mar 2020",
        "discountable": false,
        "id": "il_1GMH93Eyc0NCi7H2BmShuGZv",
        "invoice_item": "ii_1GMH93Eyc0NCi7H2Ezd2owY4",
        "livemode": false,
        "metadata": {},
        "object": "line_item",
        "period": {
          "end": 1584144000,
          "start": 1584119880
        },
        "plan": {
          "active": true,
          "aggregate_usage": null,
          "amount": 50000,
          "amount_decimal": 50000,
          "billing_scheme": "per_unit",
          "created": 1580746537,
          "currency": "usd",
          "deleted": null,
          "id": "plan_GfSVHf9pYqk2Tt",
          "interval": "month",
          "interval_count": 1,
          "livemode": false,
          "metadata": {},
          "nickname": "PointCap5000Monthly",
          "object": "plan",
          "product": "prod_GfSUsZiqaeRPP7",
          "tiers": null,
          "tiers_mode": null,
          "transform_usage": null,
          "trial_period_days": null,
          "usage_type": "licensed"
        },
        "proration": true,
        "quantity": 1,
        "subscription": "sub_Gu5JTVwA18NadV",
        "subscription_item": "si_Gu5J9VLmSfEl5X",
        "tax_amounts": [],
        "tax_rates": [],
        "type": "invoiceitem",
        "unified_proration": null
      },
      {
        "amount": -270,
        "currency": "usd",
        "description": "Unused time on $300/month up to 3,000 points after 13 Mar 2020",
        "discountable": false,
        "id": "il_1GMH92Eyc0NCi7H2COMRppHy",
        "invoice_item": "ii_1GMH92Eyc0NCi7H2aSjgFygO",
        "livemode": false,
        "metadata": {},
        "object": "line_item",
        "period": {
          "end": 1584144000,
          "start": 1584119880
        },
        "plan": {
          "active": true,
          "aggregate_usage": null,
          "amount": 30000,
          "amount_decimal": 30000,
          "billing_scheme": "per_unit",
          "created": 1583003655,
          "currency": "usd",
          "deleted": null,
          "id": "plan_GfSSjocBNNL12b",
          "interval": "month",
          "interval_count": 1,
          "livemode": false,
          "metadata": {},
          "nickname": "PointCap3000Monthly",
          "object": "plan",
          "product": "prod_GfSR0F9rOvF5Jz",
          "tiers": null,
          "tiers_mode": null,
          "transform_usage": null,
          "trial_period_days": null,
          "usage_type": "licensed"
        },
        "proration": true,
        "quantity": 1,
        "subscription": "sub_Gu5JTVwA18NadV",
        "subscription_item": "si_Gu5JaLMb54R0LY",
        "tax_amounts": [],
        "tax_rates": [],
        "type": "invoiceitem",
        "unified_proration": null
      },
      {
        "amount": 500000,
        "currency": "usd",
        "description": "1 × $5,000/year up to 5,000 points (at $5,000.00 / year)",
        "discountable": true,
        "id": "il_1GMH93Eyc0NCi7H2mGeKhjnZ",
        "invoice_item": null,
        "livemode": false,
        "metadata": {
          "Building": "Building Display Name",
          "Customer": "Online Distributor Name",
          "UUID": "75b431ee-9b2c-44f2-b141-30fed4585b9c"
        },
        "object": "line_item",
        "period": {
          "end": 1615655880,
          "start": 1584119880
        },
        "plan": {
          "active": true,
          "aggregate_usage": null,
          "amount": 500000,
          "amount_decimal": 500000,
          "billing_scheme": "per_unit",
          "created": 1580746617,
          "currency": "usd",
          "deleted": null,
          "id": "plan_GfSWvz92t7PGut",
          "interval": "year",
          "interval_count": 1,
          "livemode": false,
          "metadata": {},
          "nickname": "PointCap5000Yearly",
          "object": "plan",
          "product": "prod_GfSWbzv9EV4gPE",
          "tiers": null,
          "tiers_mode": null,
          "transform_usage": null,
          "trial_period_days": null,
          "usage_type": "licensed"
        },
        "proration": false,
        "quantity": 1,
        "subscription": "sub_Gu5JTVwA18NadV",
        "subscription_item": "si_Gu5J9VLmSfEl5X",
        "tax_amounts": [],
        "tax_rates": [],
        "type": "subscription",
        "unified_proration": null
      }
    ],
    "has_more": false,
    "url": "/v1/invoices/in_1GMH93Eyc0NCi7H2O5x7P92o/lines",
    "request_options": null,
    "request_params": null
  },
  "livemode": false,
  "metadata": {},
  "next_payment_attempt": null,
  "number": "3A837DEA-0002",
  "object": "invoice",
  "paid": true,
  "payment_intent": "pi_1GMH93Eyc0NCi7H2ufKVmxbg",
  "period_end": 1584119880,
  "period_start": 1584119880,
  "post_payment_credit_notes_amount": 0,
  "pre_payment_credit_notes_amount": 0,
  "receipt_number": null,
  "starting_balance": 0,
  "statement_descriptor": null,
  "status": "paid",
  "status_transitions": {
    "finalized_at": 1584119881,
    "marked_uncollectible_at": null,
    "paid_at": 1584119882,
    "voided_at": null
  },
  "subscription": "sub_Gu5JTVwA18NadV",
  "subscription_proration_date": null,
  "subtotal": 499280,
  "tax": null,
  "tax_percent": null,
  "threshold_reason": null,
  "total": 499280,
  "total_tax_amounts": [],
  "transfer_data": null,
  "webhooks_delivered_at": 1584119882
}
UPCOMING INVOICE: <com.stripe.model.Invoice@1987169128 id=null> JSON: {
  "account_country": "US",
  "account_name": "Resolute Building Intelligence, LLC",
  "amount_due": 500000,
  "amount_paid": 0,
  "amount_remaining": 500000,
  "application_fee_amount": null,
  "attempt_count": 0,
  "attempted": false,
  "auto_advance": null,
  "billing_reason": "upcoming",
  "charge": null,
  "collection_method": "charge_automatically",
  "created": 1615655880,
  "currency": "usd",
  "custom_fields": null,
  "customer": "cus_Gu5J1SqdFtAsvT",
  "customer_address": null,
  "customer_email": "tmyers@resolutebi.com",
  "customer_name": "Online Distributor Name",
  "customer_phone": null,
  "customer_shipping": null,
  "customer_tax_exempt": "none",
  "customer_tax_ids": [],
  "default_payment_method": null,
  "default_source": null,
  "default_tax_rates": [],
  "deleted": null,
  "description": null,
  "discount": null,
  "due_date": null,
  "ending_balance": 0,
  "footer": null,
  "hosted_invoice_url": null,
  "id": null,
  "invoice_pdf": null,
  "lines": {
    "object": "list",
    "data": [
      {
        "amount": 500000,
        "currency": "usd",
        "description": "1 × $5,000/year up to 5,000 points (at $5,000.00 / year)",
        "discountable": true,
        "id": "il_tmp_ffcecbfba51c8b",
        "invoice_item": null,
        "livemode": false,
        "metadata": {
          "Building": "Building Display Name",
          "Customer": "Online Distributor Name",
          "UUID": "75b431ee-9b2c-44f2-b141-30fed4585b9c"
        },
        "object": "line_item",
        "period": {
          "end": 1647191880,
          "start": 1615655880
        },
        "plan": {
          "active": true,
          "aggregate_usage": null,
          "amount": 500000,
          "amount_decimal": 500000,
          "billing_scheme": "per_unit",
          "created": 1580746617,
          "currency": "usd",
          "deleted": null,
          "id": "plan_GfSWvz92t7PGut",
          "interval": "year",
          "interval_count": 1,
          "livemode": false,
          "metadata": {},
          "nickname": "PointCap5000Yearly",
          "object": "plan",
          "product": "prod_GfSWbzv9EV4gPE",
          "tiers": null,
          "tiers_mode": null,
          "transform_usage": null,
          "trial_period_days": null,
          "usage_type": "licensed"
        },
        "proration": false,
        "quantity": 1,
        "subscription": "sub_Gu5JTVwA18NadV",
        "subscription_item": "si_Gu5J9VLmSfEl5X",
        "tax_amounts": [],
        "tax_rates": [],
        "type": "subscription",
        "unified_proration": null
      }
    ],
    "has_more": false,
    "url": "/v1/invoices/upcoming/lines?subscription\u003dsub_Gu5JTVwA18NadV",
    "request_options": null,
    "request_params": null
  },
  "livemode": false,
  "metadata": {},
  "next_payment_attempt": 1615659480,
  "number": "3A837DEA-0003",
  "object": "invoice",
  "paid": false,
  "payment_intent": null,
  "period_end": 1615655880,
  "period_start": 1584119880,
  "post_payment_credit_notes_amount": 0,
  "pre_payment_credit_notes_amount": 0,
  "receipt_number": null,
  "starting_balance": 0,
  "statement_descriptor": null,
  "status": "draft",
  "status_transitions": {
    "finalized_at": null,
    "marked_uncollectible_at": null,
    "paid_at": null,
    "voided_at": null
  },
  "subscription": "sub_Gu5JTVwA18NadV",
  "subscription_proration_date": null,
  "subtotal": 500000,
  "tax": null,
  "tax_percent": null,
  "threshold_reason": null,
  "total": 500000,
  "total_tax_amounts": [],
  "transfer_data": null,
  "webhooks_delivered_at": null
}
13:18:08.972 [main] INFO com.djt.hvac.common.model.stripe.client.StripeClientWebServiceImpl - Successfully attached test payment method to new customer: cus_Gu5J1SqdFtAsvT

*/