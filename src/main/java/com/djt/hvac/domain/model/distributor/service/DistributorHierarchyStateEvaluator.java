//@formatter:off
package com.djt.hvac.domain.model.distributor.service;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.StaleDataException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.customer.service.CustomerService;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.email.client.EmailClient;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.command.EvaluatePaymentProcessingRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;
import com.djt.hvac.domain.model.stripe.exception.StripeClientException;
import com.djt.hvac.domain.model.user.DistributorUserEntity;

/**
 * This class is the wrapper object for all of the state evaluations, as it
 * has the ability to persist changes via the injected domain services.
 *   
 * @author tmyers
 *
 */
public class DistributorHierarchyStateEvaluator {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(DistributorHierarchyStateEvaluator.class);
  
  private final DistributorService distributorService;
  private final CustomerService customerService;
  private final NodeHierarchyService nodeHierarchyService;
  private final EmailClient emailClient;
  
  // The state evaluator invocation frequency will be more than once per day (currently, every 2 hours),
  // but we only want to perform payment processing nightly ONCE (so, we keep track of those invocations).
  // So, for each date, we keep a set of customer ids.
  private final Map<LocalDate, Set<Integer>> nightlyPaymentProcessingInvocations = new LinkedHashMap<>();

  public DistributorHierarchyStateEvaluator(
      DistributorService distributorService,
      CustomerService customerService,
      NodeHierarchyService nodeHierarchyService,
      EmailClient emailClient) {

    requireNonNull(distributorService, "distributorService cannot be null");
    requireNonNull(customerService, "customerService be null");
    requireNonNull(nodeHierarchyService, "nodeHierarchyService be null");
    requireNonNull(emailClient, "emailClient cannot be null");
    this.distributorService = distributorService;
    this.customerService = customerService;
    this.nodeHierarchyService = nodeHierarchyService;
    this.emailClient = emailClient;
  }

  /**
   * Only the billable buildings in the given portfolio and their parent customer
   * are evaluated. Distributors are evaluated separately once all of their ancestor
   * customers are evaluated.
   * 
   * NOTE: The ancestor distributor(s) can only be evaluator once all their 
   * descendant customer(s) are evaluated, so we need to ensure that all customers
   * are evaluated before evaluating their parent distributor.  We do this by using the
   * a 'last updated at" attribute on each customer.
   * 
   * (It would be better to have a dedicated attribute, such as 'last evaluated at', 
   * but for now, use the existing attribute and hope there's not a lot of contention 
   * on updating this value.)  
   * 
   * @param portfolio The portfolio to evaluate
   * 
   * @return true if nightly payment processing was performed, false otherwise
   * 
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   * 
   * NOTES:
   * + The portfolio is evaluated ONLY if the parent customer is online.
   * 
   * + Anytime a status change is made, the corresponding status updated at field is changed
   *   too.
   *   
   * + If the building payment status is currently DELIQUENT, then it is up to the user
   *   to take action in order to rectify the failed payment(s).  When these actions occur, 
   *   Stripe payments are initiated by the user.  If these payments succeed, then the 
   *   controlling service will make the necessary state transitions/propagations, then 
   *   store to the repository.
   *   
   * + Config state and pending deletion state evaluation are performed with each invocation.
   *   "nightly" payment processing, however, is only performed once per night.  When it
   *   is performed, though, the following is done:
   * 
   *   1: For buildings with config status of PENDING_ACTIVATION and the payment status is
   *      UP_TO_DATE and the grace period has expired, then change the payment status to
   *      DELINQUENT.
   * 
   *   2: For buildings with config status of ACTIVE and the payment status is UP_TO_DATE
   *      and the payment interval has expired and the subscription has not been cancelled,
   *      then make a payment attempt to Stripe.  If it succeeds, then update the payment
   *      status updated at field.  If it fails, then change the payment status to DELINQUENT
   */
  public boolean evaluatePortfolioState(PortfolioEntity portfolio) throws StaleDataException {
    
    // Only demo customers that are expired are first soft deleted after 90 days
    // and then hard deleted 30 days after that (assuming all buildings are in CREATED status).
    if (portfolio.getParentCustomer().shouldBeHardDeleted()) {

      customerService.hardDeleteCustomer(portfolio.getParentCustomer());
      return false;
      
    } else if (portfolio.getParentCustomer().shouldBeSoftDeleted() ) {

      customerService.softDeleteCustomer(portfolio.getParentCustomer());
      return false;
      
    }    
    
    AbstractCustomerEntity customer = portfolio.getParentCustomer();
    
    // We only care about online customers for payment processing.
    if (!(customer instanceof OnlineCustomerEntity)) {
      return false;
    }
    
    // We only do payment processing once per night for a given customer's set of buildings.
    boolean performPaymentProcessing = false;
    LocalDate now = AbstractEntity
        .getTimeKeeper()
        .getCurrentLocalDate();
    
    Integer customerId = customer.getPersistentIdentity();
    Set<Integer> paymentEvaluatedCustomerIds = nightlyPaymentProcessingInvocations.get(now);
    if (paymentEvaluatedCustomerIds == null) {
      
      paymentEvaluatedCustomerIds = new HashSet<>();
      nightlyPaymentProcessingInvocations.put(now, paymentEvaluatedCustomerIds);
      performPaymentProcessing = true;
      paymentEvaluatedCustomerIds.add(customerId);
      
    } else {
      
      if (paymentEvaluatedCustomerIds.contains(customerId)) {
        performPaymentProcessing = true;  
      }
      paymentEvaluatedCustomerIds.add(customerId);
    }
    
    // Evaluates all child billable buildings for their config status, 
    // which is the number of mapped points that the building has.  All parent customer
    // and distributor config states are derived from their descendant building 
    // config states.
    portfolio.evaluateConfigState();
        
    // If there are subscriptions with delinquent payment statuses, then we poll stripe with every
    // invocation of the evaluator to see the status of any payments made since the last check.
    // This can either be automatic subscription payments or manual payments by the user.
    try {
      nodeHierarchyService.performStripeDelinquentPaymentProcessing(portfolio);
    } catch (StripeClientException e) {
      throw new IllegalStateException("Unable to perform Stripe delinquent payment processing for portfolio: ["
          + portfolio
          + "], error: ["
          + e.getMessage()
          + "].", e);
    }      
    
    // Make any state changes based on what happened with Stripe.
    portfolio.evaluatePaymentState();        
    
    // Evaluates all child billable buildings for their payment status,
    // which is the status of the building subscription, if any.  All parent customer
    // and distributor payment states are derived from their descendant building 
    // payment states. Only deals with subscriptions whose intervals are expired.
    if (performPaymentProcessing) {
      
      // Interact with Stripe to see the status of any payments made since the last check.
      // This can either be automatic subscription payments or manual payments by the user.
      try {
        nodeHierarchyService.performStripePaymentProcessing(portfolio);
      } catch (StripeClientException e) {
        throw new IllegalStateException("Unable to perform Stripe payment processing for portfolio: ["
            + portfolio
            + "], error: ["
            + e.getMessage()
            + "].", e);
      }      
      
      // Make any state changes based on what happened with Stripe.
      portfolio.evaluatePaymentState();  
    }

    // Evaluates to see whether any billable buildings that have had their pending deletion 
    // flag set for long enough are eligible for deletion (it is assumed that 
    // everything has been archived in the time between the pending deletion flag
    // set and the transition to being hard deleted.
    portfolio.evaluatePendingDeletionState();
    
    // See if we need to send the grace period expiration warning email notification.
    for (BuildingEntity building: portfolio.getChildBuildings()) {
      
      if (building instanceof BillableBuildingEntity) {

        BillableBuildingEntity billableBuilding = (BillableBuildingEntity)building;
        if (billableBuilding.getBuildingStatus().equals(BuildingStatus.PENDING_ACTIVATION) 
            && billableBuilding.shouldGracePeriodExpirationWarningNotificationBeSent()) {
          
          try {
            DistributorUserEntity accountManager = customer
                .getParentDistributor()
                .getAccountManagerDistributorUserNullIfNotExists();
            
            if (accountManager != null) {

              LOGGER.info("{}: grace period is expiring on: [{}], so a warning email notification is being sent to: [{}]",
                  billableBuilding.getPersistentIdentity(),
                  billableBuilding.getGracePeriodExpiration(),
                  accountManager.getEmail());
              
              Integer emailNotificationId = emailClient.sendGracePeriodExpirationWarningEmail(
                  accountManager, 
                  billableBuilding);
              
              billableBuilding.setBuildingGracePeriodWarningNotificationId(emailNotificationId);
              
            } else {
              LOGGER.info("Unable to send grace period expiration warning message for building: [{}] because an account manager has not been specified yet.",
                  billableBuilding.getPersistentIdentity());
            }
          } catch (Exception e) {
            throw new IllegalStateException("Unable to send grace period expiration warning message for building: ["
                + billableBuilding.getNodePath()
                + "] error: "
                + e.getMessage(), e);
          }
        }
      }
    }
    
    // Save any changes made to the node hierarchy.
    if (portfolio.getIsModified()) {
      
      LOGGER.info("Updating portfolio [{}] for changes [{}]",
          portfolio,
          portfolio.getModifiedAttributes());
      
      nodeHierarchyService.updatePortfolio(
          portfolio,
          EvaluatePaymentProcessingRequest
              .builder()
              .withCustomerId(portfolio.getCustomerId())
              .withSubmittedBy(NodeHierarchyCommandRequest.SYSTEM)
              .build());
    }
    
    // Save any changes made to the parent customer.
    if (customer.getIsModified()) {
      
      LOGGER.info("Updating customer [{}] for changes [{}]",
          portfolio,
          customer.getModifiedAttributes());
      
      /*
      CUSTOMER/DISTRIBUTOR AUTOMATIC STATE TRANSITIONS FOR THE JDBC REPOSITORY IMPL:
      =================================================================================================
      status:           CREATED -> BILLABLE         (at least one billable building)    Yes (automatic)
      status:           BILLABLE -> CREATED         (no billable buildings)             Yes (automatic)
      
      payment_status:   UP_TO_DATE -> DELINQUENT    (at least one delinquent building)  Yes (automatic)
      payment_status:   DELINQUENT -> UP_TO_DATE    (no delinquent buildings)           Yes (automatic)
      
      DISTRIBUTOR ONLY:
      payment_status:   DELINQUENT -> PAST_DUE      (in delinquent state for 90 days)   Yes (automatic)
      payment_status:   PAST_DUE -> UP_TO_DATE      (no delinquent buildings)           Yes (automatic)
     */
      // AUTOMATIC IN DB
      if (!customerService.hasDatabaseRepository()) {
        customerService.updateCustomer(customer);  
      }
    }
    
    // NOTE: We perform distributor level evaluations when we evaluate the Resolute root distributor, 
    // as there, we need to load all customers for a given distributor so that we can properly evaluate
    // the distributor state.
    
    return performPaymentProcessing;
  }

  /**
   * 
   * Evaluates online distributors by starting at the root Resolute distributor.
   * NOTE: Only the distributors in the given list are evaluated, as we may only be 
   * performing portfolio maintenance/payment processing for a specific customer/distributor. 
   * 
   * @param distributors The distributors to process
   */
  public void evaluateRootDistributorState(List<AbstractDistributorEntity> distributors) {
    
    try {

      boolean loadDistributorPaymentMethods = true;
      boolean loadDistributorUsers = true;
      boolean loadChildCustomers = true;
      AbstractDistributorEntity rootDistributor = distributorService.getResoluteRootDistributor(loadDistributorUsers);
      for (AbstractDistributorEntity cd: rootDistributor.getAllDescendantDistributors()) {
        
        // Only process the distributor if we are instructed to.
        if (!rootDistributor.equals(cd) && (distributors.isEmpty() || distributors.contains(cd))) {

          if (cd.shouldBeHardDeleted()) {
            
            distributorService.hardDeleteDistributor(cd);
            
          } else if (cd.shouldBeSoftDeleted()) {
            
            distributorService.softDeleteDistributor(cd);
            
          } else {

            if (cd instanceof OnlineDistributorEntity) {
              
              AbstractDistributorEntity d = distributorService.loadDistributor(
                  cd.getPersistentIdentity(),
                  loadDistributorPaymentMethods,
                  loadDistributorUsers,
                  loadChildCustomers);

              OnlineDistributorEntity onlineDistributor = (OnlineDistributorEntity)d;
              
              // Evaluates all child distributors and child customers for their config state 
              // and propagates any changes up to the ancestor distributor(s) if needed.
              onlineDistributor.evaluateConfigState();
                  
              // Evaluates all child distributors and child customers for their payment state 
              // and propagates any changes up to the ancestor distributor(s) if needed.
              onlineDistributor.evaluatePaymentState();  

              // Evaluates all child distributors and child customers for their pending deletion state 
              // and propagates any changes up to the ancestor distributor(s) if needed.
              onlineDistributor.evaluatePendingDeletionState();          
              
              // See if any distributor should be hard deleted.  If so, then hard delete it.
              // NOTE: Archiving of data is not performed here.
              if (onlineDistributor.shouldBeHardDeleted()) {
                
                distributorService.hardDeleteDistributor(onlineDistributor);
                
              } else if (onlineDistributor.shouldBeSoftDeleted()) {
                
                distributorService.softDeleteDistributor(onlineDistributor);
                
              } else {

                // See if any child customer level changes need to be stored to the repository.
                Set<AbstractCustomerEntity> modifiedChildCustomers = onlineDistributor.getModifiedChildCustomers();
                if (!modifiedChildCustomers.isEmpty()) {
                  
                  for (AbstractCustomerEntity childCustomer: modifiedChildCustomers) {

                    // AUTOMATIC IN DB
                    if (!customerService.hasDatabaseRepository()) {
                      customerService.updateCustomer(childCustomer);  
                    }
                    
                    PortfolioEntity portfolio = childCustomer.getChildPortfolio();
                    if (portfolio != null && portfolio.getIsModified()) {
                      
                      nodeHierarchyService.updatePortfolio(
                          portfolio,
                          EvaluatePaymentProcessingRequest
                              .builder()
                              .withCustomerId(portfolio.getCustomerId())
                              .withSubmittedBy(NodeHierarchyCommandRequest.SYSTEM)
                              .build());
                    }
                  }
                }

                // See if any child customers need to be hard deleted (after some period of being soft deleted).
                Set<AbstractCustomerEntity> hardDeletedChildCustomers = onlineDistributor.getHardDeletedChildCustomers();
                if (!hardDeletedChildCustomers.isEmpty()) {
                  
                  for (AbstractCustomerEntity childCustomer: hardDeletedChildCustomers) {

                    if (childCustomer.shouldBeHardDeleted()) {
                      customerService.hardDeleteCustomer(childCustomer);  
                    }
                  }
                }
                
                // See if any child distributor level changes need to be stored to the repository.
                Set<AbstractDistributorEntity> modifiedChildDistributors = onlineDistributor.getModifiedChildDistributors();
                if (!modifiedChildDistributors.isEmpty()) {
                  
                  for (AbstractDistributorEntity childDistributor: modifiedChildDistributors) {

                    // AUTOMATIC IN DB
                    if (!distributorService.hasDatabaseRepository()) {
                      distributorService.updateDistributor(childDistributor);  
                    }
                  }
                }

                // See if any child distributors need to be hard deleted (after some period of being soft deleted).
                Set<AbstractDistributorEntity> hardDeletedChildDistributors = onlineDistributor.getHardDeletedChildDistributors();
                if (!hardDeletedChildDistributors.isEmpty()) {
                  
                  for (AbstractDistributorEntity childDistributor: hardDeletedChildDistributors) {

                    if (childDistributor.shouldBeHardDeleted()) {
                      distributorService.hardDeleteDistributor(childDistributor);  
                    }
                  }
                }
                
                // See if the online distributor itself has changes that need to be stored to the repository.
                if (onlineDistributor.getIsModified()) {
                  
                  distributorService.updateDistributor(onlineDistributor);  
                }
              }
            }
          }          
        }
      }
    } catch (Exception e) {
    LOGGER.error("Unable to perform evaluate distributor config/payment state/pending deletion state: error: {}",
        e.getMessage(), 
        e);
    }
  }
}
//@formatter:on