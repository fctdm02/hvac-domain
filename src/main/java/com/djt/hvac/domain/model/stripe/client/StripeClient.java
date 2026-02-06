//@formatter:off
package com.djt.hvac.domain.model.stripe.client;

import java.time.LocalDate;

import com.djt.hvac.domain.model.stripe.dto.StripeClientResponse;
import com.djt.hvac.domain.model.stripe.exception.StripeClientException;

/**
 * 
 * The Stripe clients are used for CRUD operations on the following Stripe objects:
 * <ol>
 *   <li>Customer</li>
 *   <li>Invoice</li>
 *   <li>PaymentMethod</li>
 *   <li>Product</li>
 *   <li>Subscription</li>
 * </ol>
 * 
 * @see <a href="https://stripe.com/docs/api/">https://stripe.com/docs/api/</a>
 * @see <a href="https://github.com/stripe/stripe-java/">https://github.com/stripe/stripe-java</a>
 * 
 * @see <a href="https://stripe.com/docs/api/customers">https://stripe.com/docs/api/customers</a>
 * 
 * <pre>
  {
    "id": "cus_GeI1DzigoSv0Cn",
    "object": "customer",
    "address": null,
    "balance": 0,
    "created": 1580476936,
    "currency": "usd",
    "default_source": null,
    "delinquent": false,
    "description": null,
    "discount": null,
    "email": null,
    "invoice_prefix": "BE7F208",
    "invoice_settings": {
      "custom_fields": null,
      "default_payment_method": null,
      "footer": null
    },
    "livemode": false,
    "metadata": {},
    "name": null,
    "phone": null,
    "preferred_locales": [],
    "shipping": null,
    "sources": {
      "object": "list",
      "data": [],
      "has_more": false,
      "url": "/v1/customers/cus_GeI1DzigoSv0Cn/sources"
    },
    "subscriptions": {
      "object": "list",
      "data": [],
      "has_more": false,
      "url": "/v1/customers/cus_GeI1DzigoSv0Cn/subscriptions"
    },
    "tax_exempt": "none",
    "tax_ids": {
      "object": "list",
      "data": [],
      "has_more": false,
      "url": "/v1/customers/cus_GeI1DzigoSv0Cn/tax_ids"
    }
  }  
 * </pre> 
 * 
 * @see <a href="https://stripe.com/docs/api/invoices">https://stripe.com/docs/api/invoices</a>
 * <pre>
  {
    "id": "in_19yTU92eZvKYlo2C7uDjvu6v",
    "object": "invoice",
    "account_country": "US",
    "account_name": "Stripe.com",
    "amount_due": 999,
    "amount_paid": 999,
    "amount_remaining": 0,
    "application_fee_amount": null,
    "attempt_count": 1,
    "attempted": true,
    "auto_advance": false,
    "billing_reason": null,
    "charge": "ch_19yUQN2eZvKYlo2CQf7aWpSX",
    "collection_method": "charge_automatically",
    "created": 1489789429,
    "currency": "usd",
    "custom_fields": null,
    "customer": "cus_GeI1DzigoSv0Cn",
    "customer_address": null,
    "customer_email": "olivia.williams.03@example.com",
    "customer_name": null,
    "customer_phone": null,
    "customer_shipping": null,
    "customer_tax_exempt": "none",
    "customer_tax_ids": [],
    "default_payment_method": null,
    "default_source": null,
    "default_tax_rates": [],
    "description": null,
    "discount": null,
    "due_date": null,
    "ending_balance": 0,
    "footer": null,
    "hosted_invoice_url": "https://pay.stripe.com/invoice/invst_a7KV10HpLw2QxrihgVyuOkOjMZ",
    "invoice_pdf": "https://pay.stripe.com/invoice/invst_a7KV10HpLw2QxrihgVyuOkOjMZ/pdf",
    "lines": {
      "data": [
        {
          "id": "il_tmp_3823c48520d96d",
          "object": "line_item",
          "amount": 5000,
          "currency": "usd",
          "description": "1 × Emerald basic (at $50.00 / month)",
          "discountable": true,
          "livemode": false,
          "metadata": {},
          "period": {
            "end": 1486383876,
            "start": 1483705476
          },
          "plan": {
            "id": "emerald-basic-nba",
            "object": "plan",
            "active": true,
            "aggregate_usage": null,
            "amount": 5000,
            "amount_decimal": "5000",
            "billing_scheme": "per_unit",
            "created": 1480948390,
            "currency": "usd",
            "interval": "month",
            "interval_count": 1,
            "livemode": false,
            "metadata": {},
            "nickname": null,
            "product": "prod_BTftx04xthAjuR",
            "tiers": null,
            "tiers_mode": null,
            "transform_usage": null,
            "trial_period_days": null,
            "usage_type": "licensed"
          },
          "proration": false,
          "quantity": 1,
          "subscription": "sub_9h6CopvY0Fldnj",
          "subscription_item": "si_19Nhzs2eZvKYlo2CVCDaXmrl",
          "tax_amounts": [],
          "tax_rates": [],
          "type": "subscription"
        }
      ],
      "has_more": false,
      "object": "list",
      "url": "/v1/invoices/in_19yTU92eZvKYlo2C7uDjvu6v/lines"
    },
    "livemode": false,
    "metadata": {
      "order_id": "6735"
    },
    "next_payment_attempt": null,
    "number": "EF0A41E-0001",
    "paid": true,
    "payment_intent": null,
    "period_end": 1489789420,
    "period_start": 1487370220,
    "post_payment_credit_notes_amount": 0,
    "pre_payment_credit_notes_amount": 0,
    "receipt_number": "2277-9887",
    "starting_balance": 0,
    "statement_descriptor": null,
    "status": "paid",
    "status_transitions": {
      "finalized_at": 1489793039,
      "marked_uncollectible_at": null,
      "paid_at": 1489793039,
      "voided_at": null
    },
    "subscription": "sub_9lNL2lSXI8nYEQ",
    "subtotal": 999,
    "tax": null,
    "tax_percent": null,
    "total": 999,
    "total_tax_amounts": [],
    "webhooks_delivered_at": 1489789437
  }
 * </pre>  
 * 
 * @see <a href="https://stripe.com/docs/api/payment_methods">https://stripe.com/docs/api/payment_methods</a>
 * 
 * <pre>
    {
      "id": "pm_123456789",
      "object": "payment_method",
      "billing_details": {
        "address": {
          "city": null,
          "country": null,
          "line1": null,
          "line2": null,
          "postal_code": null,
          "state": null
        },
        "email": "jenny@example.com",
        "name": null,
        "phone": "+15555555555"
      },
      "card": {
        "brand": "visa",
        "checks": {
          "address_line1_check": null,
          "address_postal_code_check": null,
          "cvc_check": null
        },
        "country": "US",
        "exp_month": 8,
        "exp_year": 2021,
        "fingerprint": "Xt5EWLLDS7FJjR1c",
        "funding": "credit",
        "generated_from": null,
        "last4": "4242",
        "three_d_secure_usage": {
          "supported": true
        },
        "wallet": null
      },
      "created": 123456789,
      "customer": null,
      "livemode": false,
      "metadata": {
        "order_id": "123456789"
      },
      "type": "card"
    }
    
 NOTE: Payment methods, such as credit cards and ACH accounts, are created
       via an interaction between the Resolute frontend and the Stripe backend.
 
 https://stripe.com/docs/payments/payment-methods
 https://stripe.com/docs/payments/payment-intents/verifying-status
 https://stripe.com/docs/js/payment_intents/create_payment_method
 https://stripe.com/docs/payments/intents#intent-statuses
 https://stripe.com/docs/payments/more-payment-scenarios
 https://stripe.com/docs/saving-cards
  
 * </pre>
 * 
 * @see <a href="https://stripe.com/docs/api/products/list">https://stripe.com/docs/api/products</a>
 * @see <a href="https://stripe.com/docs/api/service_products">https://stripe.com/docs/api/service_products</a>
 * 
 * <pre>
  {
    "id": "prod_GeHZxIAuGTPBNG",
    "object": "product",
    "active": true,
    "attributes": [],
    "caption": null,
    "created": 1580475220,
    "deactivate_on": [],
    "description": null,
    "images": [],
    "livemode": false,
    "metadata": {},
    "name": "Diamond Startup",
    "package_dimensions": null,
    "shippable": null,
    "statement_descriptor": null,
    "type": "service",
    "unit_label": null,
    "updated": 1580475220,
    "url": null
  }
  
  https://stripe.com/docs/billing/subscriptions/products-and-plans#products
  
 * </pre>
 * 
 * @see <a href="https://stripe.com/docs/api/plans/list">https://stripe.com/docs/api/plans</a>
 * 
 * <pre>
  {
    "object": "list",
    "url": "/v1/plans",
    "has_more": false,
    "data": [
      {
        "id": "sapphire-small-871",
        "object": "plan",
        "active": true,
        "aggregate_usage": null,
        "amount": 999,
        "amount_decimal": "999",
        "billing_scheme": "per_unit",
        "created": 1568818111,
        "currency": "usd",
        "interval": "month",
        "interval_count": 1,
        "livemode": false,
        "metadata": {},
        "nickname": null,
        "product": "prod_Fpjwc44UyJYZJw",
        "tiers": null,
        "tiers_mode": null,
        "transform_usage": null,
        "trial_period_days": null,
        "usage_type": "licensed"
      },
      {...},
      {...}
    ]
  }  
 * </pre>
 * 
 * @see <a href="https://stripe.com/docs/api/subscriptions">https://stripe.com/docs/api/subscriptions</a>
 * @see <a href="https://stripe.com/docs/api/plans">https://stripe.com/docs/api/plans</a>
 * 
 * <pre>
  SUBSCRIPTION:
  {
    "id": "sub_9h6CopvY0Fldnj",
    "object": "subscription",
    "application_fee_percent": null,
    "billing_cycle_anchor": 1481027076,
    "billing_thresholds": null,
    "cancel_at": null,
    "cancel_at_period_end": true,
    "canceled_at": 1481027078,
    "collection_method": "charge_automatically",
    "created": 1481027076,
    "current_period_end": 1483705476,
    "current_period_start": 1481027076,
    "customer": "cus_9fYy2VJUHCLMB1",
    "days_until_due": null,
    "default_payment_method": null,
    "default_source": null,
    "default_tax_rates": [],
    "discount": null,
    "ended_at": 1481027085,
    "items": {
      "object": "list",
      "data": [
        {
          "id": "si_19Nhzs2eZvKYlo2CVCDaXmrl",
          "object": "subscription_item",
          "billing_thresholds": null,
          "created": 1481027076,
          "metadata": {},
          "plan": {
            "id": "emerald-basic-nba",
            "object": "plan",
            "active": true,
            "aggregate_usage": null,
            "amount": 5000,
            "amount_decimal": "5000",
            "billing_scheme": "per_unit",
            "created": 1480948390,
            "currency": "usd",
            "interval": "month",
            "interval_count": 1,
            "livemode": false,
            "metadata": {},
            "nickname": null,
            "product": "prod_BTftx04xthAjuR",
            "tiers": null,
            "tiers_mode": null,
            "transform_usage": null,
            "trial_period_days": null,
            "usage_type": "licensed"
          },
          "quantity": 1,
          "subscription": "sub_9h6CopvY0Fldnj",
          "tax_rates": []
        }
      ],
      "has_more": false,
      "url": "/v1/subscription_items?subscription=sub_9h6CopvY0Fldnj"
    },
    "latest_invoice": null,
    "livemode": false,
    "metadata": {},
    "next_pending_invoice_item_invoice": null,
    "pending_invoice_item_interval": null,
    "pending_setup_intent": null,
    "pending_update": null,
    "plan": {
      "id": "emerald-basic-nba",
      "object": "plan",
      "active": true,
      "aggregate_usage": null,
      "amount": 5000,
      "amount_decimal": "5000",
      "billing_scheme": "per_unit",
      "created": 1480948390,
      "currency": "usd",
      "interval": "month",
      "interval_count": 1,
      "livemode": false,
      "metadata": {},
      "nickname": null,
      "product": "prod_BTftx04xthAjuR",
      "tiers": null,
      "tiers_mode": null,
      "transform_usage": null,
      "trial_period_days": null,
      "usage_type": "licensed"
    },
    "quantity": 1,
    "schedule": null,
    "start_date": 1481027076,
    "status": "canceled",
    "tax_percent": null,
    "trial_end": null,
    "trial_start": null
  }  
  
  
  PLAN:
  {
    "id": "sapphire-small-871",
    "object": "plan",
    "active": true,
    "aggregate_usage": null,
    "amount": 999,
    "amount_decimal": "999",
    "billing_scheme": "per_unit",
    "created": 1568818111,
    "currency": "usd",
    "interval": "month",
    "interval_count": 1,
    "livemode": false,
    "metadata": {},
    "nickname": null,
    "product": "prod_Fpjwc44UyJYZJw",
    "tiers": null,
    "tiers_mode": null,
    "transform_usage": null,
    "trial_period_days": null,
    "usage_type": "licensed"
  }  
  
 * </pre>
 * 
 * NOTE:
 * Normally, the subscription will be set to have 'charge_automatically' set to true.
 * When charging automatically, Stripe will attempt to pay this subscription at the end 
 * 
 * We may never use 'send_invoice' = true, as when there is a need to manually fix failed payments.
 * When this is the case: When sending an invoice, Stripe will email your customer an invoice with 
 * payment instructions. Defaults to charge_automatically.
 * </pre>
 * 
 * @see <a href="https://stripe.com/docs/billing/subscriptions/multiple">https://stripe.com/docs/billing/subscriptions/multiple</a>
 * @see <a href="https://stripe.com/docs/api/subscription_schedules">https://stripe.com/docs/api/subscription_schedules</a> 
 * 
 * @author tmyers
 *
 */
public interface StripeClient {
  
  String PROD_API_KEY_PREFIX = "sk_live";
  
  String LIMIT = "limit";
  Integer DEFAULT_LIMIT = 1000;
  
  String STARTING_AFTER = "starting_after";
  String ENDING_BEFORE = "ending_before";
  String HAS_MORE = "has_more"; 
  
  String STRIPE_CUSTOMER_ID = "stripeCustomerId";
  String STRIPE_SOURCE_ID = "stripeSourceId";
  String STRIPE_PRODUCT_ID = "stripeProductId";
  String STRIPE_PLAN_ID = "stripePlanId";
  String STRIPE_SUBSCRIPTION_ID = "stripeSubscriptionId";
  String STRIPE_INVOICE_ID = "stripeInvoiceId";
  
  String NAME = "name";
  String EMAIL = "email";

  String CUSTOMER_NAME = "Customer";
  String BUILDING_DISPLAY_NAME = "Building";
  String BUILDING_UUID = "UUID";
  
  String METADATA = "metadata";
  String PRODUCT = "product";
  String PRODUCTS = "products";
  String PLAN = "plan";
  String PLANS = "plans";
  String CUSTOMER = "customer";
  String CUSTOMERS = "customers";
  String ITEMS = "items";
  String ITEMS_ZERO_INDEX = "0";
  String BILLING_CYCLE_ANCHOR = "billing_cycle_anchor";
  String DEFAULT_PAYMENT_METHOD = "default_payment_method";
  String PAYMENT_METHOD = "payment_method";
  String PAYMENT_METHODS = "payment_methods";
  String SUBSCRIPTION = "subscription";
  String SUBSCRIPTIONS = "subscriptions";
  String TYPE = "type";
  String CARD = "card";
  String INVOICE = "invoice";
  String INVOICES = "invoices";
  String INVOICE_STATUS = "invoice_status";
  String IS_PAID = "is_paid";
  String IS_ATTEMPTED = "is_attempted";
  String ATTEMPT_COUNT = "attempt_count";
  String PRORATION_BEHAVIOR = "proration_behavior";
  String PRORATION_BEHAVIOR_TRUE = "create_prorations";
  String PRORATION_BEHAVIOR_FALSE = "none";
  String PRORATION_DATE = "proration_date";
  String AMOUNT_DUE = "amount_due";
  String AMOUNT_PAID = "amount_paid";
  String PERIOD_START = "period_start";
  String PERIOD_END = "period_end";
  String INVOICE_PDF = "invoice_pdf";
  
  // The status of the invoice, one of `draft`, `open`, `paid`, `uncollectible`, or `void`. 
  // https://stripe.com/docs/billing/invoices/workflow#workflow-overview
  String STATUS = "status";
  String DRAFT = "draft";
  String OPEN = "open";
  String PAID = "paid";
  String UNCOLLECTIBLE = "uncollectible";
  String VOID = "void";
  
  String SUBSCRIPTION_START_DATE = "subscription_start_date";
  String SUBSCRIPTION_INTERVAL_START_DATE = "subscription_interval_start_date";
  String SUBSCRIPTION_INTERVAL_END_DATE = "subscription_interval_end_date";
  String SUBSCRIPTION_NEXT_INTERVAL_START_DATE = "subscription_next_interval_start_date";
  
  // When a subscription is created, this value is false.  
  // When a subscription is cancelled, this value is true.
  String CANCEL_AT_PERIOD_END = "cancel_at_period_end";
  
  /**
   * 
   * @return The Stripe API Key
   */
  String getApiKey();
  
  /**
   * NOTES: 
   * - Test API Key starts with: "sk_test_"
   * - Prod API starts with: "sk_prod_"
   * 
   * @return <code>true</code> If the injected API key is a live mode key.
   */
  Boolean isLiveMode();
  
  /**
   * @see <a href="https://stripe.com/docs/api/customers/create">https://stripe.com/docs/api/customers/create</a>
   * 
   * @param name name
   * @param email email
   * 
   * @return The customer is in the stripe response objects map under the "customer" key
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*

    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Map<String, Object> params = new HashMap<>();
    params.put(
      "description",
      "My First Test Customer (created for API docs)"
    );
    
    Customer customer = Customer.create(params);
 
   */
  StripeClientResponse createStripeCustomer(String name, String email) throws StripeClientException;
  
  /**
   * @see <a href="https://stripe.com/docs/api/customers/delete">https://stripe.com/docs/api/customers/delete</a>
   * 
   * @param stripeCustomerId The Stripe customer id
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*

    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Customer customer = Customer.retrieve("cus_GeI1DzigoSv0Cn");
    
    Customer deletedCustomer = customer.delete();
             
   */
  StripeClientResponse deleteStripeCustomer(String stripeCustomerId) throws StripeClientException;    
  
  /**
   * @see <a href="https://stripe.com/docs/api/customers/list">https://stripe.com/docs/api/customers/list</a>
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*
   
    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Map<String, Object> params = new HashMap<>();
    params.put("limit", 3);
    
    CustomerCollection customers = Customer.list(params);
    
   */
  StripeClientResponse getAllStripeCustomers() throws StripeClientException;
  
  /**
   * @see <a href="https://stripe.com/docs/api/customers/retrieve">https://stripe.com/docs/api/customers/retrieve</a>
   * 
   * @param stripeCustomerId The Stripe customer id
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*

    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Customer customer = Customer.retrieve("cus_GeI1DzigoSv0Cn");
     
   */
  StripeClientResponse getStripeCustomer(String stripeCustomerId) throws StripeClientException;
  
  /**
   * @see <a href="https://stripe.com/docs/api/customers/update">https://stripe.com/docs/api/customers/update</a>
   * 
   * @param stripeCustomerId The Stripe customer to update the email for
   * @param email The new email to associate with the Stripe customer
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*

    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Customer customer = Customer.retrieve("cus_GeI1DzigoSv0Cn");
    
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("email", "john.smith@man.in.the.high.castle.com");
    
    Map<String, Object> params = new HashMap<>();
    params.put("metadata", metadata);
    
    Customer customer = customer.update(params);
         
   */
  StripeClientResponse updateStripeCustomerForNewAccountManager(String stripeCustomerId, String email) throws StripeClientException;  
  
  /**
   * 
   * @see <a href="https://stripe.com/docs/api/invoices/list">https://stripe.com/docs/api/invoices/list</a>
   * 
   * @param stripeCustomerId stripeCustomerId
   * @param stripeSubscriptionId stripeSubscriptionId
   * @param status The status of the invoice, one of draft, open, paid, uncollectible, or void
   * @param limit limit
   * @param endingBefore endingBefore
   * @param startingAfter startingAfter
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   <pre>
    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Map<String, Object> params = new HashMap<>();
    params.put("limit", 3);
    
    InvoiceCollection invoices = Invoice.list(params);    
    
    Arguments
    customer
    optional
    Only return invoices for the customer specified by this customer ID.
    
    status
    optional
    The status of the invoice, one of draft, open, paid, uncollectible, or void. Learn more
    
    subscription
    optional
    Only return invoices for the subscription specified by this subscription ID.
    
    More arguments
    Collapse all
    collection_method
    optional
    The collection method of the invoice to retrieve. Either charge_automatically or send_invoice.
    
    created
    optional Map
    A filter on the list based on the object created field. The value can be a string with an integer Unix timestamp, or it can be a dictionary with the following options:
    
    Show child arguments
    due_date
    optional Map
    A filter on the list based on the object due_date field. The value can be a string with an integer Unix timestamp, or it can be a dictionary with the following options:
    
    Show child arguments
    ending_before
    optional
    A cursor for use in pagination. ending_before is an object ID that defines your place in the list. For instance, if you make a list request and receive 100 objects, starting with obj_bar, your subsequent call can include ending_before=obj_bar in order to fetch the previous page of the list.
    
    limit
    optional
    A limit on the number of objects to be returned. Limit can range between 1 and 100, and the default is 10.
    
    starting_after
    optional
    A cursor for use in pagination. starting_after is an object ID that defines your place in the list. For instance, if you make a list request and receive 100 objects, ending with obj_foo, your subsequent call can include starting_after=obj_foo in order to fetch the next page of the list.
    
    Returns
    A Map with a data property that contains an array of up to limit invoices, starting after invoice starting_after. Each entry in the array is a separate invoice object. If no more invoices are available, the resulting array will be empty. Throws an error if the customer ID is invalid.
    </pre>
   */

  StripeClientResponse getAllStripeInvoices(
      String stripeCustomerId,
      String stripeSubscriptionId,
      String status,
      int limit,
      LocalDate endingBefore,
      LocalDate startingAfter) 
  throws 
      StripeClientException;
  
  /**
   * 
   * @see <a href="https://stripe.com/docs/api/invoices/retrieve">https://stripe.com/docs/api/invoices/retrieve</a>
   * 
   * @param stripeInvoiceId The Stripe invoice id
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*
   
    Stripe.apiKey = "sk_test_HaTrTphyrziDXQRh2qegoW7700lrXTjENZ";
    
    Invoice invoice = Invoice.retrieve("in_1FGqe2Eyc0NCi7H2K2X73X1R");
    
   */
  StripeClientResponse getStripeInvoice(String stripeInvoiceId) throws StripeClientException;
  
  /**
   * 
   * @see <a href="https://stripe.com/docs/api/invoices/upcoming">https://stripe.com/docs/api/invoices/upcoming</a>
   * 
   * @param stripeSubscriptionId The Stripe subscription id
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*
   
    Stripe.apiKey = "sk_test_HaTrTphyrziDXQRh2qegoW7700lrXTjENZ";
    
    Map<String, Object> invoiceParams = new HashMap<String, Object>();
    invoiceParams.put("subscription", "cus_GeMIVmtO2zaJnt");
    
    Invoice.upcoming(invoiceParams);
    
   */
  StripeClientResponse getUpcomingStripeInvoice(String stripeSubscriptionId) throws StripeClientException;
  
  /**
   * 
   * @param stripeSubscriptionId The Stripe subscription id
   * 
   * @return The latest Stripe invoice (i.e. the payment that 
   * was done at the start of the new subscription interval)
   * 
   * @throws StripeClientException If any problem occurred
   */
  StripeClientResponse getLatestStripeInvoice(String stripeSubscriptionId) throws StripeClientException;
  
  /*
    Retrieve an invoice's line items
    When retrieving an invoice, you’ll get a lines property containing the total count of line items and the first handful of those items. There is also a URL where you can retrieve the full (paginated) list of line items.
    
    Arguments
    ending_before
    optional
    A cursor for use in pagination. ending_before is an object ID that defines your place in the list. For instance, if you make a list request and receive 100 objects, starting with obj_bar, your subsequent call can include ending_before=obj_bar in order to fetch the previous page of the list.
    
    limit
    optional
    A limit on the number of objects to be returned. Limit can range between 1 and 100, and the default is 10.
    
    starting_after
    optional
    A cursor for use in pagination. starting_after is an object ID that defines your place in the list. For instance, if you make a list request and receive 100 objects, ending with obj_foo, your subsequent call can include starting_after=obj_foo in order to fetch the next page of the list.

   
    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Invoice invoice = Invoice.retrieve("in_19yTU92eZvKYlo2C7uDjvu6v");
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("limit", 5);
    InvoiceLineItemCollection lines = invoice.getLines().list(params);    
   */  

  /**
   * @see <a href="https://stripe.com/docs/api/invoices/create">https://stripe.com/docs/api/invoices/create</a>
   * 
   * @param stripeCustomerId The Stripe customer id
   * @param invoiceParams The invoice parameters - See API Doc
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*

    Stripe.apiKey = "sk_test_HaTrTphyrziDXQRh2qegoW7700lrXTjENZ";
    
    Map<String, Object> invoiceParams = new HashMap<>();
    params.put("customer", "cus_GenBReS9ujasJF");
    
    Invoice invoice = Invoice.create(params);   
         
   */
  /*
  StripeClientResponse createStripeInvoice(
      String stripeCustomerId, 
      Map<String, Object> invoiceParams) 
  throws 
      StripeClientException;
  */
  /**
   * 
   * @see <https://stripe.com/docs/api/invoices/pay">https://stripe.com/docs/api/invoices/pay</a>
   * 
   * @param stripeInvoiceId The Stripe invoice id
   * @param stripeSourceId The Stripe payment source (i.e. payment method) to use
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*
  
    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Invoice invoice = Invoice.retrieve(stripeInvoiceId);
      
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("source", stripeSourceId);
      
    Invoice paidInvoice = invoice.pay(params);      
  
 */
  /*
  StripeClientResponse payStripeInvoice(
      String stripeInvoiceId, 
      String stripeSourceId) 
  throws 
      StripeClientException; 
  */
  /**
   * @see <a href="https://stripe.com/docs/api/payment_methods/detach">https://stripe.com/docs/api/payment_methods/detach</a>
   * 
   * @param stripeCustomerId The associated customer
   * @param stripeSourceId The stripe source id (i.e. payment method)
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*

    Stripe.apiKey = "sk_test_HaTrTphyrziDXQRh2qegoW7700lrXTjENZ";
    
    PaymentMethod paymentMethod = PaymentMethod.retrieve("pm_123456789");
    
    PaymentMethod paymentMethod = paymentMethod.detach();
                 
   */
  StripeClientResponse deleteStripePaymentMethod(String stripeCustomerId, String stripeSourceId) throws StripeClientException; 
  
  /**
   * 
   * @see <a href="https://stripe.com/docs/api/payment_methods/list">https://stripe.com/docs/api/payment_methods/list</a>
   * 
   * @param stripeCustomerId The stripe customer id
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*
   
    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Map<String, Object> params = new HashMap<>();
    params.put("customer", "cus_GaKUCtWghgOl2S");
    params.put("type", "card");
    
    PaymentMethodCollection paymentMethods = PaymentMethod.list(params);    
    
   */
  StripeClientResponse getAllStripePaymentMethods(String stripeCustomerId) throws StripeClientException;

  /**
   * Attaches a stripe payment method to a particular stripe customer
   * 
   * @see <a href="https://stripe.com/docs/api/payment_methods/attach">https://stripe.com/docs/api/payment_methods/attach</a>
   * 
   * @param stripeCustomerId The Stripe customer
   * @param stripeSourceId The Stripe source id (i.e. payment method)
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*

    Stripe.apiKey = "sk_test_HaTrTphyrziDXQRh2qegoW7700lrXTjENZ";
    
    PaymentMethod paymentMethod = PaymentMethod.retrieve("pm_123456789");
    
    Map<String, Object> params = new HashMap<>();
    params.put("customer", "cus_FvnP1wLrBU4JQr");
    
    PaymentMethod paymentMethod = paymentMethod.attach(params);
     
   */
  StripeClientResponse attachStripePaymentMethod(String stripeCustomerId, String stripeSourceId) throws StripeClientException;
  
  /**
   * @see <a href="https://stripe.com/docs/api/payment_methods/retrieve">https://stripe.com/docs/api/payment_methods/retrieve</a>
   * 
   * @param stripeSourceId The Stripe source id (i.e. payment method)
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*

    Stripe.apiKey = "sk_test_HaTrTphyrziDXQRh2qegoW7700lrXTjENZ";
    
    PaymentMethod paymentMethod = PaymentMethod.retrieve("pm_1G700Q2eZvKYlo2Cr3MWQkgU");
     
   */
  
  StripeClientResponse getStripePaymentMethod(String stripeSourceId) throws StripeClientException;

  /**
   * https://stripe.com/docs/api/products/list
   * https://stripe.com/docs/api/plans/list
   * 
   * This method will return all Stripe products, as well as the corresponding Stripe plans, of which,
   * there is a one-to-one relationship.
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*
   
    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Map<String, Object> params = new HashMap<>();
    params.put("limit", 3);
    
    ProductCollection products = Product.list(params);    
    
    PlanCollection plans = Plan.list(params);
    
   */
  StripeClientResponse getAllStripeProducts() throws StripeClientException;  
  
  /**
   * `
   * @see <a href="https://stripe.com/docs/api/subscriptions/create">https://stripe.com/docs/api/subscriptions/create</a>
   * @see <a href="https://stripe.com/docs/billing/subscriptions/billing-cycle">https://stripe.com/docs/billing/subscriptions/billing-cycle</a>
   * 
   * @param stripeCustomerId stripeCustomerId
   * @param stripeSourceId stripeSourceId
   * @param stripePlanId stripePlanId
   * @param customerName customerName
   * @param buildingDisplayName buildingDisplayName
   * @param buildingUuid buildingUuid In case the building 
   * 
   * @return The subscription is in the stripe response objects map under the "subscription" key
   * 
   * <pre>
    Arguments:
    ==========
    customer: REQUIRED: The identifier of the customer to subscribe.
    items: REQUIRED: List of subscription items, each with an attached plan.
    items.plan: REQUIRED: Plan ID for this item, as a string.
    
    Arguments
    customer
    REQUIRED
    The identifier of the customer to subscribe.
    
    items
    REQUIRED
    A list of up to 20 subscription items, each with an attached plan.
    
    Hide child arguments
    items.plan
    REQUIRED
    Plan ID for this item, as a string.
    
    items.billing_thresholds
    optional Map
    Define thresholds at which an invoice will be sent, and the subscription advanced to a new billing period. When updating, pass an empty string to remove previously-defined thresholds.
    
    Show child arguments
    items.metadata
    optional Map
    Set of key-value pairs that you can attach to an object. This can be useful for storing additional information about the object in a structured format. Individual keys can be unset by posting an empty value to them. All keys can be unset by posting an empty value to metadata.
    
    items.quantity
    optional
    Quantity for this item.
    
    items.tax_rates
    optional
    A list of Tax Rate ids. These Tax Rates will override the default_tax_rates on the Subscription. When updating, pass an empty string to remove previously-defined tax rates.
    
    cancel_at_period_end
    optional
    Boolean indicating whether this subscription should cancel at the end of the current period.
    
    default_payment_method
    optional
    ID of the default payment method for the subscription. It must belong to the customer associated with the subscription. If not set, invoices will use the default payment method in the customer’s invoice settings.
    
    metadata
    optional Map
    Set of key-value pairs that you can attach to an object. This can be useful for storing additional information about the object in a structured format. Individual keys can be unset by posting an empty value to them. All keys can be unset by posting an empty value to metadata.
    
    More arguments
    Collapse all
    application_fee_percent
    optional
    CONNECT ONLY
    A non-negative decimal between 0 and 100, with at most two decimal places. This represents the percentage of the subscription invoice subtotal that will be transferred to the application owner’s Stripe account. The request must be made by a platform account on a connected account in order to set an application fee percentage. For more information, see the application fees documentation.
    
    backdate_start_date
    optional
    For new subscriptions, a past timestamp to backdate the subscription’s start date to. If set, the first invoice will contain a proration for the timespan between the start date and the current time. Can be combined with trials and the billing cycle anchor.
    
    billing_cycle_anchor
    optional
    A future timestamp to anchor the subscription’s billing cycle. This is used to determine the date of the first full invoice, and, for plans with month or year intervals, the day of the month for subsequent invoices.
    
    billing_thresholds
    optional Map
    Define thresholds at which an invoice will be sent, and the subscription advanced to a new billing period. Pass an empty string to remove previously-defined thresholds.
    
    Show child arguments
    cancel_at
    optional
    A timestamp at which the subscription should cancel. If set to a date before the current period ends, this will cause a proration if prorations have been enabled using proration_behavior. If set during a future period, this will always cause a proration for that period.
    
    collection_method
    optional
    Either charge_automatically, or send_invoice. When charging automatically, Stripe will attempt to pay this subscription at the end of the cycle using the default source attached to the customer. When sending an invoice, Stripe will email your customer an invoice with payment instructions. Defaults to charge_automatically.
    
    coupon
    optional
    The code of the coupon to apply to this subscription. A coupon applied to a subscription will only affect invoices created for that particular subscription.
    
    days_until_due
    optional
    Number of days a customer has to pay invoices generated by this subscription. Valid only for subscriptions where collection_method is set to send_invoice.
    
    default_source
    optional
    ID of the default payment source for the subscription. It must belong to the customer associated with the subscription and be in a chargeable state. If not set, defaults to the customer’s default source.
    
    default_tax_rates
    optional
    The tax rates that will apply to any subscription item that does not have tax_rates set. Invoices created will have their default_tax_rates populated from the subscription.
    
    off_session
    optional
    Indicates if a customer is on or off-session while an invoice payment is attempted.
    
    payment_behavior
    optional enum
    Use allow_incomplete to create subscriptions with status=incomplete if the first invoice cannot be paid. Creating subscriptions with this status allows you to manage scenarios where additional user actions are needed to pay a subscription’s invoice. For example, SCA regulation may require 3DS authentication to complete payment. See the SCA Migration Guide for Billing to learn more. This is the default behavior.
    Use error_if_incomplete if you want Stripe to return an HTTP 402 status code if a subscription’s first invoice cannot be paid. For example, if a payment method requires 3DS authentication due to SCA regulation and further user action is needed, this parameter does not create a subscription and returns an error instead. This was the default behavior for API versions prior to 2019-03-14. See the changelog to learn more.
    
    pending_if_incomplete is only used with updates and cannot be passed when creating a subscription.
    
    
    Possible enum values
    allow_incomplete
    error_if_incomplete
    pending_if_incomplete
    pending_invoice_item_interval
    optional Map
    Specifies an interval for how often to bill for any pending invoice items. It is analogous to calling Create an invoice for the given subscription at the specified interval.
    
    Show child arguments
    prorate
    optional
    DEPRECATED
    Boolean (defaults to true) telling us whether to credit for unused time when the billing cycle changes (e.g. when switching plans, resetting billing_cycle_anchor=now, or starting a trial), or if an item’s quantity changes. If false, the anchor period will be free (similar to a trial) and no proration adjustments will be created. This field has been deprecated and will be removed in a future API version. Use proration_behavior=create_prorations as a replacement for prorate=true and proration_behavior=none for prorate=false.
    
    proration_behavior
    optional
    Determines how to handle prorations resulting from the billing_cycle_anchor. Valid values are create_prorations or none.
    Passing create_prorations will cause proration invoice items to be created when applicable. Prorations can be disabled by passing none. If no value is passed, the default is create_prorations.
    
    
    tax_percent
    optional
    DEPRECATED
    A non-negative decimal (with at most four decimal places) between 0 and 100. This represents the percentage of the subscription invoice subtotal that will be calculated and added as tax to the final amount in each billing period. For example, a plan which charges $10/month with a tax_percent of 20.0 will charge $12 per invoice. To unset a previously-set value, pass an empty string. This field has been deprecated and will be removed in a future API version, for further information view the migration docs for tax_rates.
    
    trial_end
    optional
    Unix timestamp representing the end of the trial period the customer will get before being charged for the first time. This will always overwrite any trials that might apply via a subscribed plan. If set, trial_end will override the default trial period of the plan the customer is being subscribed to. The special value now can be provided to end the customer’s trial immediately. Can be at most two years from billing_cycle_anchor.
    
    trial_from_plan
    optional
    Indicates if a plan’s trial_period_days should be applied to the subscription. Setting trial_end per subscription is preferred, and this defaults to false. Setting this flag to true together with trial_end is not allowed.
    
    trial_period_days
    optional
    Integer representing the number of trial period days before the customer is charged for the first time. This will always overwrite any trials that might apply via a subscribed plan.
    
    Returns
    The newly created Subscription object, if the call succeeded. If the attempted charge fails, the subscription is created in an incomplete status.       
    
   * </pre>
   */
  /*

    List<Object> items = new ArrayList<>();
    Map<String, Object> item1 = new HashMap<>();
    item1.put("plan", stripePlanId);
    items.add(item1);
    
    Map<String, Object> params = new HashMap<>();
    params.put("customer", stripeCustomerId);
    params.put("default_payment_method", stripeSourceId);
    params.put("items", items);

    Subscription subscription = Subscription.create(params);    
    
   */
  StripeClientResponse createStripeSubscription(
      String stripeCustomerId,
      String stripeSourceId,
      String stripePlanId,
      String customerName,
      String buildingDisplayName,
      String buildingUuid)
  throws 
      StripeClientException;

  /**
   * https://stripe.com/docs/api/subscriptions/cancel
   * 
   * where 'cancel_at_period_end' is set to true: 
   * "Boolean indicating whether this subscription should cancel at the end of the current period."
   * 
   * @param stripeSubscriptionId The Stripe subscription to delete (a.k.a. cancel)
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*
   
    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Subscription subscription = Subscription.retrieve("sub_9h6CopvY0Fldnj");
    
    Subscription deletedSubscription = subscription.cancel();
    
   */
  StripeClientResponse deleteStripeSubscription(String stripeSubscriptionId) throws StripeClientException;
  
  /**
   * @see <a href="https://stripe.com/docs/api/subscriptions/list">https://stripe.com/docs/api/subscriptions/list</a>
   * 
   * @param stripeCustomerId The Stripe customer id
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*
   
    Stripe.apiKey = "sk_test_HaTrTphyrziDXQRh2qegoW7700lrXTjENZ";
    
    Map<String, Object> params = new HashMap<>();
    
    params.put("customer", stripeCustomerId);
    
    SubscriptionCollection subscriptions = Subscription.list(params);
    
   */
  StripeClientResponse getAllStripeSubscriptions(String stripeCustomerId) throws StripeClientException;   
  
  /**
   * @see <a href="https://stripe.com/docs/api/subscriptions/retrieve">https://stripe.com/docs/api/subscriptions/retrieve</a>
   * 
   * @param stripeSubscriptionId The Stripe subscription id
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*

    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Subscription subscription = Subscription.retrieve("sub_9h6CopvY0Fldnj");
    
   */
  StripeClientResponse getStripeSubscription(String stripeSubscriptionId) throws StripeClientException;
  
  /**
   * GENERAL SETTING CHANGE
   * 
   * https://stripe.com/docs/api/subscriptions/update
   * 
   * NOTE: Set the 'default_payment_method' argument
   * "ID of the default payment method for the subscription. It must belong to the customer associated with the subscription. 
   * If not set, invoices will use the default payment method in the customer’s invoice settings."
   * 
   * 'default_source': ID of the default payment source for the subscription. It must belong to the customer associated with 
   * the subscription and be in a chargeable state. If not set, defaults to the customer’s default source."
   * 
   * FOR PRORATIONS:
   * @see <a href="https://stripe.com/docs/billing/subscriptions/prorations">https://stripe.com/docs/billing/subscriptions/prorations</a>
   * 
   * @param stripeSubscriptionId The Stripe subscription id
   * @param stripeSourceId THe Stripe payment source (i.e. payment method) id
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /* 
    
    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    
    Subscription subscription = Subscription.retrieve(stripeSubscriptionId);
    
    Map<String, Object> metadata = new HashMap<>();
    
    // Any "payment methods" created with this code are Stripe payment methods, 
    // and as such, will have a payment_method_id
    metadata.put("default_payment_method", stripeSourceId);
    metadata.put("default_source", stripeSourceId);
    
    Map<String, Object> params = new HashMap<>();
    params.put("metadata", metadata);
    
    Subscription updatedSubscription = subscription.update(params);    
    
   */
  StripeClientResponse updateStripeSubscriptionForNewPaymentMethod(
      String stripeSubscriptionId,
      String stripeSourceId) throws StripeClientException;
  
  
  /**
   * RENEWAL SETTING CHANGE
   *
   * https://stripe.com/docs/billing/subscriptions/upgrading-downgrading
   * 
   * NOTE: "If both plans have the same billing periods—combination of interval and interval_count, the subscription retains the same billing dates."
   * 
   * FOR PRORATIONS:
   * @see <a href="https://stripe.com/docs/billing/subscriptions/prorations">https://stripe.com/docs/billing/subscriptions/prorations</a>
   * 
   * @param stripeSubscriptionId The Stripe subscription id
   * @param stripePlanId The Stripe plan id to switch to
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*
  
    Subscription subscription = Subscription.retrieve(stripeSubscriptionId);
    
    List<Object> items = new ArrayList<>();
    Map<String, Object> item1 = new HashMap<>();
    item1.put("plan", stripePlanId);
    items.add(item1);
    
    Map<String, Object> params = new HashMap<>();
    params.put("items", items);      
    
    Subscription updatedSubscription = subscription.update(params);    
  
 */  
  StripeClientResponse updateStripeSubscriptionForNewProductDifferentInterval(
      String stripeSubscriptionId,
      String stripePlanId) throws StripeClientException;
  
  /**
   * GENERAL SETTING CHANGE
   *
   * https://stripe.com/docs/billing/subscriptions/upgrading-downgrading
   * https://stripe.com/docs/billing/subscriptions/changing
   * 
   * NOTE: "If both plans have the same billing periods—combination of interval and interval_count, the subscription retains the same billing dates."
   * params.put("cancel_at_period_end", false);
   * 
   * FOR PRORATIONS:
   * @see <a href="https://stripe.com/docs/billing/subscriptions/prorations">https://stripe.com/docs/billing/subscriptions/prorations</a>
   * 
   * @param stripeSubscriptionId The Stripe subscription id
   * @param stripePlanId The Stripe plan id to change to
   * 
   * @return The response from Stripe
   * 
   * @throws StripeClientException If any problem occurred
   */
  /*
  
    Subscription subscription = Subscription.retrieve(stripeSubscriptionId);
    
    List<Object> items = new ArrayList<>();
    Map<String, Object> item1 = new HashMap<>();
    item1.put("plan", stripePlanId);
    items.add(item1);
    
    Map<String, Object> params = new HashMap<>();
    params.put("items", items);      
    
    Subscription updatedSubscription = subscription.update(params);    
  
 */  
  StripeClientResponse updateStripeSubscriptionForNewProductSameInterval(
      String stripeSubscriptionId,
      String stripePlanId) 
  throws 
      StripeClientException;
  
  
  
  // https://www.firstofficer.io/blog/stripe-when-should-i-charge-prorated-subscriptions-immediately/
}
//@formatter:on