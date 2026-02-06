//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.cache.client.CacheClient;
import com.djt.hvac.domain.model.cache.client.MockCacheClient;
import com.djt.hvac.domain.model.cache.kryo.KryoSerialize;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.BuildingCustomAsyncComputedPointState;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.CustomAsyncComputedPointState;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.ComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.FillPolicy;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.utils.NodeHierarchyTestDataBuilderOptions;
import com.djt.hvac.domain.model.timeseries.client.MockTimeSeriesServiceClient;
import com.djt.hvac.domain.model.timeseries.client.TimeSeriesServiceClient;

public class CustomComputedPointEvaluationTest extends AbstractResoluteDomainModelTest {

  private static enum BuildingEnum {
    PROCESS_ALL_BUILDINGS,
    NO_PROCESS_ALL_BUILDINGS,
    NON_EXISTENT_BUILDING
  }
  
  private static enum StateEnum {
    NO_STATE_AT_ALL,
    BUILDING_STATE_NO_POINT_STATE,
    BUILDING_STATE_POINT_STATE
  }
  
  private static enum PerformRecalculateEnum {
    PERFORM_RECALCULATE,
    NO_PERFORM_RECALCULATE
  }

  private static enum ExerciseFillPolicyEnum {
    EXERCISE_FILL_POLICY,
    NO_EXERCISE_FILL_POLICY
  }

  private static enum LargeBatchEnum {
    LARGE_BATCH,
    NO_LARGE_BATCH
  }

  private static enum UseDeltaFunctionEnum {
    USE_DELTA_FUNCTION,
    NO_USE_DELTA_FUNCTION
  }
  
  private static final CacheClient cacheClient = MockCacheClient.getInstance();
  private static final TimeSeriesServiceClient timeSeriesServiceClient = MockTimeSeriesServiceClient.getInstance();

  protected AbstractDistributorEntity distributor;
  protected Integer distributorId;
  protected AbstractCustomerEntity customer;
  protected Integer customerId;
  protected PortfolioEntity portfolio;
  protected Integer portfolioId;
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    AbstractResoluteDomainModelTest.beforeClass();
  }
  
  @Before
  public void before() throws Exception {
    
    super.before();
  }
  
  @After
  public void after() throws Exception {

    cacheClient.removeAllCacheEntries();
    timeSeriesServiceClient.deleteCustomerMetricValues(customer);
  }  
  
  
  
  @Test
  public void evaluateCustomAsyncPoints_quarterHour_noStateAtAll() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.QUARTER_HOUR, 
        StateEnum.NO_STATE_AT_ALL);
  }

  @Test
  public void evaluateCustomAsyncPoints_quarterHour_buildingStateNoPointState() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.QUARTER_HOUR, 
        StateEnum.BUILDING_STATE_NO_POINT_STATE);
  }

  @Test
  public void evaluateCustomAsyncPoints_quarterHour_buildingStatePointState() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.QUARTER_HOUR, 
        StateEnum.BUILDING_STATE_POINT_STATE);
  }
  

  
  
  @Test
  public void evaluateCustomAsyncPoints_daily_noStateAtAll() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.DAILY, 
        StateEnum.NO_STATE_AT_ALL);
  }

  @Test
  public void evaluateCustomAsyncPoints_daily_buildingStateNoPointState() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.DAILY, 
        StateEnum.BUILDING_STATE_NO_POINT_STATE);
  }

  @Test
  public void evaluateCustomAsyncPoints_daily_buildingStatePointState() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.DAILY, 
        StateEnum.BUILDING_STATE_POINT_STATE);
  }

  
  
  
  @Test
  public void evaluateCustomAsyncPoints_monthly_noStateAtAll() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.MONTHLY, 
        StateEnum.NO_STATE_AT_ALL);
  }

  @Test
  public void evaluateCustomAsyncPoints_monthly_buildingStateNoPointState() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.MONTHLY, 
        StateEnum.BUILDING_STATE_NO_POINT_STATE);
  }

  @Test
  public void evaluateCustomAsyncPoints_monthly_buildingStatePointState() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.MONTHLY, 
        StateEnum.BUILDING_STATE_POINT_STATE);
  }
  
  private void evaluateCustomAsyncPoints(
      ComputationInterval computationInterval,
      StateEnum initialState) throws Exception {
    
    evaluateCustomAsyncPoints(
        computationInterval, 
        initialState,
        BuildingEnum.NO_PROCESS_ALL_BUILDINGS,
        PerformRecalculateEnum.NO_PERFORM_RECALCULATE,
        ExerciseFillPolicyEnum.NO_EXERCISE_FILL_POLICY, 
        LargeBatchEnum.NO_LARGE_BATCH,
        UseDeltaFunctionEnum.NO_USE_DELTA_FUNCTION);
  }
  
  @Test
  public void evaluateCustomAsyncPoints_quarterHour_performRecalculate() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.QUARTER_HOUR,
        StateEnum.NO_STATE_AT_ALL,
        BuildingEnum.NO_PROCESS_ALL_BUILDINGS,
        PerformRecalculateEnum.PERFORM_RECALCULATE,
        ExerciseFillPolicyEnum.NO_EXERCISE_FILL_POLICY, 
        LargeBatchEnum.LARGE_BATCH,
        UseDeltaFunctionEnum.NO_USE_DELTA_FUNCTION);
  }

  @Test
  public void evaluateCustomAsyncPoints_daily_performRecalculate() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.DAILY,
        StateEnum.NO_STATE_AT_ALL,
        BuildingEnum.NO_PROCESS_ALL_BUILDINGS,
        PerformRecalculateEnum.PERFORM_RECALCULATE,
        ExerciseFillPolicyEnum.NO_EXERCISE_FILL_POLICY, 
        LargeBatchEnum.LARGE_BATCH,
        UseDeltaFunctionEnum.NO_USE_DELTA_FUNCTION);
  }

  @Test
  public void evaluateCustomAsyncPoints_monthly_performRecalculate() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.MONTHLY,
        StateEnum.NO_STATE_AT_ALL,
        BuildingEnum.NO_PROCESS_ALL_BUILDINGS,
        PerformRecalculateEnum.PERFORM_RECALCULATE,
        ExerciseFillPolicyEnum.NO_EXERCISE_FILL_POLICY, 
        LargeBatchEnum.LARGE_BATCH,
        UseDeltaFunctionEnum.NO_USE_DELTA_FUNCTION);
  }
  
  @Test
  public void evaluateCustomAsyncPoints_quarterHour_noStateAtAll_exerciseFillPolicy() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.QUARTER_HOUR,
        StateEnum.NO_STATE_AT_ALL,
        BuildingEnum.NO_PROCESS_ALL_BUILDINGS,
        PerformRecalculateEnum.PERFORM_RECALCULATE,
        ExerciseFillPolicyEnum.EXERCISE_FILL_POLICY, 
        LargeBatchEnum.NO_LARGE_BATCH,
        UseDeltaFunctionEnum.NO_USE_DELTA_FUNCTION);
  }
  
  @Test
  public void evaluateCustomAsyncPoints_quarterHour_noStateAtAll_statefulComputedPointExpression() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.QUARTER_HOUR,
        StateEnum.NO_STATE_AT_ALL,
        BuildingEnum.NO_PROCESS_ALL_BUILDINGS,
        PerformRecalculateEnum.PERFORM_RECALCULATE,
        ExerciseFillPolicyEnum.EXERCISE_FILL_POLICY, 
        LargeBatchEnum.NO_LARGE_BATCH,
        UseDeltaFunctionEnum.USE_DELTA_FUNCTION);
  }
  
  @Test
  public void evaluateCustomAsyncPoints_quarterHour_noStateAtAll_allBuildings() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.QUARTER_HOUR,
        StateEnum.NO_STATE_AT_ALL,
        BuildingEnum.PROCESS_ALL_BUILDINGS,
        PerformRecalculateEnum.NO_PERFORM_RECALCULATE,
        ExerciseFillPolicyEnum.NO_EXERCISE_FILL_POLICY, 
        LargeBatchEnum.NO_LARGE_BATCH,
        UseDeltaFunctionEnum.NO_USE_DELTA_FUNCTION);
  }

  @Test
  public void evaluateCustomAsyncPoints_errorState_non_existent_building() throws Exception {

    evaluateCustomAsyncPoints(
        ComputationInterval.QUARTER_HOUR,
        StateEnum.NO_STATE_AT_ALL,
        BuildingEnum.NON_EXISTENT_BUILDING,
        PerformRecalculateEnum.NO_PERFORM_RECALCULATE,
        ExerciseFillPolicyEnum.NO_EXERCISE_FILL_POLICY, 
        LargeBatchEnum.NO_LARGE_BATCH,
        UseDeltaFunctionEnum.NO_USE_DELTA_FUNCTION);
  }  
    
  private void evaluateCustomAsyncPoints(
      ComputationInterval computationInterval,
      StateEnum initialState,
      BuildingEnum buildingEnum,
      PerformRecalculateEnum performRecalculate,
      ExerciseFillPolicyEnum exerciseFillPolicy,
      LargeBatchEnum largeBatch,
      UseDeltaFunctionEnum useDeltaFunction) throws Exception {

    // STEP 1: ARRANGE
    // CREATE PORTFOLIO WITH 1 EQUIPMENT AND 2 MAPPABLE POINTS
    customerId = nodeHierarchyTestDataBuilder.createNodeHierarchy(NodeHierarchyTestDataBuilderOptions
        .builder()
        .withNumFloors(0)
        .withNumEquipmentPerEquipmentType(1)
        .withNumPointsPerEquipmentType(1)
        .withEquipmentTypeNames(new HashSet<>(Arrays.asList("ahu")))
        .withPointTemplateNames(new HashSet<>(Arrays.asList("ClgCmd", "HtgCmd")))
        .withPerformPointMapping(true)
        .withPerformEquipmentTagging(true)
        .withPerformPointTagging(true)
        .build());
    
    boolean loadDistributorPaymentMethods = false;
    boolean loadDistributorUsers = false; 
    customer = customerService.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
    timeSeriesServiceClient.deleteCustomerMetricValues(customer);
    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    
    customer = portfolio.getParentCustomer();
    Timestamp customerStartTimestamp = customer.getStartDate();
    LocalDateTime customerStartLocalDateTime = AbstractEntity.convertTimestampToLocalDateTime(customerStartTimestamp);
    
    BuildingEntity building = portfolio.getAllBuildings().get(0);
    Integer buildingId = null;
    if (buildingEnum.equals(BuildingEnum.NON_EXISTENT_BUILDING)) {
      buildingId = 999999;
    } else if (buildingEnum.equals(BuildingEnum.NO_PROCESS_ALL_BUILDINGS)) {
      buildingId = building.getPersistentIdentity();  
    }
    
    EquipmentEntity equipment = portfolio.getAllEquipment().get(0);
    List<MappablePointEntity> mappablePoints = portfolio.getAllMappablePoints();
    MappablePointEntity mappablePoint1 = mappablePoints.get(0);
    MappablePointEntity mappablePoint2 = mappablePoints.get(1);
    
    // POPULATE TIME SERIES DATA FOR THE TWO CHILD VARIABLES AND LAST PROCESSED TIME FOR THE CUSTOM POINT.
    Map<String, String> tags = new HashMap<>();
    tags.put("tag", "dummy");
    Map<String, Map<Long, Double>> pointMetricValues = new LinkedHashMap<>();
    Map<Long, Double> mappablePoint1MetricValues = new TreeMap<>();
    pointMetricValues.put(mappablePoint1.getMetricIdForTsdb(), mappablePoint1MetricValues);
    Map<Long, Double> mappablePoint2MetricValues = new TreeMap<>();
    pointMetricValues.put(mappablePoint2.getMetricIdForTsdb(), mappablePoint2MetricValues);

    // CREATE CUSTOM ASYNC COMPUTED POINT
    String name = "Custom_Async_Computed_Point";
    String displayName = "Custom Async Computed Point";
    String metricId = "custom_async_computed_point_metric_id";
    Integer pointTemplateId = null;
    Integer unitId = null;
    String formula = null;
    if (useDeltaFunction.equals(UseDeltaFunctionEnum.USE_DELTA_FUNCTION)) {
      formula = "delta($1 + $2)";      
    } else {
      formula = "$1 + $2";  
    }
    String formulaEffectiveDate = AbstractEntity.LOCAL_DATE_FORMATTER.get().format(customerStartLocalDateTime.toLocalDate());
    String formulaDescription = "Custom Async Computed Point Description";
    List<Map<String, Object>> childVariables = new ArrayList<>();
    Map<String, Object> childVariable1 = new HashMap<>();
    childVariable1.put("name", "$1");
    childVariable1.put("pointId", mappablePoint1.getPersistentIdentity());
    childVariable1.put("fillPolicyId", FillPolicy.ZERO.getId());
    childVariables.add(childVariable1);
    Map<String, Object> childVariable2 = new HashMap<>();
    childVariable2.put("name", "$2");
    childVariable2.put("pointId", mappablePoint2.getPersistentIdentity());
    childVariable2.put("fillPolicyId", FillPolicy.LAST_KNOWN.getId());
    childVariables.add(childVariable2);
    CustomAsyncComputedPointEntity customAsyncComputedPoint = (CustomAsyncComputedPointEntity)nodeHierarchyService.createNode(nodeHierarchyTestDataBuilder.createCustomAsyncPointRequest(
        equipment, 
        name, 
        displayName, 
        metricId, 
        pointTemplateId, 
        unitId, 
        computationInterval, 
        formula, 
        formulaEffectiveDate, 
        formulaDescription, 
        childVariables));
    Integer customAsyncComputedPointId = customAsyncComputedPoint.getPersistentIdentity();
    
    
    // DETERMINE THE START/END BASED ON THE COMPUTATION INTERVAL
    LocalDateTime currentLocalDateTime = AbstractEntity.getTimeKeeper().getCurrentLocalDateTime();
    
    LocalDateTime startLocalDateTime = null;
    LocalDateTime endLocalDateTime = null;
    
    if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {
      
      if (initialState.equals(StateEnum.NO_STATE_AT_ALL)) {
        
        if (!largeBatch.equals(LargeBatchEnum.LARGE_BATCH)) {
          customerStartLocalDateTime = AbstractEntity.adjustTimeIntoFifteenMinuteFloor(currentLocalDateTime.minusHours(1)); // Process last hour (4 intervals of 15 minutes each)  
        } else {
          customerStartLocalDateTime = AbstractEntity.adjustTimeIntoFifteenMinuteFloor(currentLocalDateTime.minusDays(45)); // Process last 45 days (45X24*96=103,680 intervals of 15 minutes each)
        }
        
      } else if (initialState.equals(StateEnum.BUILDING_STATE_NO_POINT_STATE)) {
        
        customerStartLocalDateTime = AbstractEntity.adjustTimeIntoFifteenMinuteFloor(currentLocalDateTime.minusHours(1)); // Process last hour (4 intervals of 15 minutes each)
        
        storeBuildingState(new BuildingCustomAsyncComputedPointState(buildingId));
        
      } else if (initialState.equals(StateEnum.BUILDING_STATE_POINT_STATE)) {

        customerStartLocalDateTime = AbstractEntity.adjustTimeIntoFifteenMinuteFloor(currentLocalDateTime.minusHours(1)); // Process last hour (4 intervals of 15 minutes each)

        BuildingCustomAsyncComputedPointState buildingState = new BuildingCustomAsyncComputedPointState(buildingId);
        CustomAsyncComputedPointState pointState = new CustomAsyncComputedPointState(Timestamp.valueOf(customerStartLocalDateTime), Double.valueOf(1.0d));
        buildingState.addPointState(customAsyncComputedPointId, pointState);
        storeBuildingState(buildingState);
        
      } else {
        throw new RuntimeException("Unsupported initial state: " + initialState);
      }
      
      customer.setStartDate(AbstractEntity.convertLocalDateTimeToTimestamp(customerStartLocalDateTime));
      customerService.updateCustomer(customer);
      customer = customerService.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
      customAsyncComputedPoint.getChildTemporalConfigsAsList().get(0).setEffectiveDate(AbstractEntity.convertTimestampToLocalDateTime(customer.getStartDate()).toLocalDate());
      nodeHierarchyService.updateCustomAsyncComputedPointNodes(nodeHierarchyTestDataBuilder.createUpdateCustomAsyncComputedPointNodesRequest(customAsyncComputedPoint));
      
      startLocalDateTime = customerStartLocalDateTime;
      endLocalDateTime = AbstractEntity.adjustTimeIntoFifteenMinuteFloor(currentLocalDateTime);
      
      
    } else if (computationInterval.equals(ComputationInterval.DAILY)) {

      
      if (initialState.equals(StateEnum.NO_STATE_AT_ALL)) {
        
        if (!largeBatch.equals(LargeBatchEnum.LARGE_BATCH)) {
          customerStartLocalDateTime = AbstractEntity.adjustTimeIntoDailyFloor(currentLocalDateTime.minusDays(4)); // Process last 4 days (4 daily intervals)          
        } else {
          customerStartLocalDateTime = AbstractEntity.adjustTimeIntoDailyFloor(currentLocalDateTime.minusDays(365*10)); // Process last 10 years
        }
        
      } else if (initialState.equals(StateEnum.BUILDING_STATE_NO_POINT_STATE)) {

        customerStartLocalDateTime = AbstractEntity.adjustTimeIntoDailyFloor(currentLocalDateTime.minusDays(4)); // Process last 4 days (4 daily intervals)
        
        storeBuildingState(new BuildingCustomAsyncComputedPointState(buildingId));
        
      } else if (initialState.equals(StateEnum.BUILDING_STATE_POINT_STATE)) {

        customerStartLocalDateTime = AbstractEntity.adjustTimeIntoDailyFloor(currentLocalDateTime.minusDays(4)); // Process last 4 days (4 daily intervals)

        BuildingCustomAsyncComputedPointState buildingState = new BuildingCustomAsyncComputedPointState(buildingId);
        CustomAsyncComputedPointState pointState = new CustomAsyncComputedPointState(Timestamp.valueOf(customerStartLocalDateTime), Double.valueOf(1.0d));
        buildingState.addPointState(customAsyncComputedPointId, pointState);
        storeBuildingState(buildingState);
        
      } else {
        throw new RuntimeException("Unsupported initial state: " + initialState);
      }
      
      customer.setStartDate(AbstractEntity.convertLocalDateTimeToTimestamp(customerStartLocalDateTime));
      customerService.updateCustomer(customer);
      customer = customerService.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
      customAsyncComputedPoint.getChildTemporalConfigsAsList().get(0).setEffectiveDate(AbstractEntity.convertTimestampToLocalDateTime(customer.getStartDate()).toLocalDate());
      nodeHierarchyService.updateCustomAsyncComputedPointNodes(nodeHierarchyTestDataBuilder.createUpdateCustomAsyncComputedPointNodesRequest(customAsyncComputedPoint));
      
      startLocalDateTime = customerStartLocalDateTime;
      endLocalDateTime = AbstractEntity.adjustTimeIntoDailyFloor(currentLocalDateTime);
      
      
    } else {

      
      if (initialState.equals(StateEnum.NO_STATE_AT_ALL)) {

        if (!largeBatch.equals(LargeBatchEnum.LARGE_BATCH)) {
          customerStartLocalDateTime = AbstractEntity.adjustTimeIntoMonthlyFloor(currentLocalDateTime.minusMonths(4)); // Process last 4 months (4 monthly intervals)  
        } else {
          customerStartLocalDateTime = AbstractEntity.adjustTimeIntoMonthlyFloor(currentLocalDateTime.minusMonths(10*12)); // Process last 10 years
        }
        
      } else if (initialState.equals(StateEnum.BUILDING_STATE_NO_POINT_STATE)) {

        customerStartLocalDateTime = AbstractEntity.adjustTimeIntoMonthlyFloor(currentLocalDateTime.minusMonths(4)); // Process last 4 months (4 monthly intervals)
        
        storeBuildingState(new BuildingCustomAsyncComputedPointState(buildingId));
        
      } else if (initialState.equals(StateEnum.BUILDING_STATE_POINT_STATE)) {

        customerStartLocalDateTime = AbstractEntity.adjustTimeIntoMonthlyFloor(currentLocalDateTime.minusMonths(4)); // Process last 4 months (4 monthly intervals)

        BuildingCustomAsyncComputedPointState buildingState = new BuildingCustomAsyncComputedPointState(buildingId);
        CustomAsyncComputedPointState pointState = new CustomAsyncComputedPointState(Timestamp.valueOf(customerStartLocalDateTime), Double.valueOf(1.0d));
        buildingState.addPointState(customAsyncComputedPointId, pointState);
        storeBuildingState(buildingState);
        
      } else {
        throw new RuntimeException("Unsupported initial state: " + initialState);
      }
      
      customer.setStartDate(AbstractEntity.convertLocalDateTimeToTimestamp(customerStartLocalDateTime));
      customerService.updateCustomer(customer);
      customer = customerService.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
      customAsyncComputedPoint.getChildTemporalConfigsAsList().get(0).setEffectiveDate(AbstractEntity.convertTimestampToLocalDateTime(customer.getStartDate()).toLocalDate());
      nodeHierarchyService.updateCustomAsyncComputedPointNodes(nodeHierarchyTestDataBuilder.createUpdateCustomAsyncComputedPointNodesRequest(customAsyncComputedPoint));
      
      startLocalDateTime = customerStartLocalDateTime;
      endLocalDateTime = AbstractEntity.adjustTimeIntoMonthlyFloor(currentLocalDateTime);      
      
    }
    
    Timestamp startTimestamp = null;
    if (performRecalculate.equals(PerformRecalculateEnum.PERFORM_RECALCULATE)) {
      startTimestamp = customer.getStartDate();
    } else {
      startTimestamp = Timestamp.valueOf(startLocalDateTime);
    }
    
    Timestamp endTimestamp = Timestamp.valueOf(endLocalDateTime);
    
    long fromMillis = startTimestamp.getTime();
    long toMillis = endTimestamp.getTime();
    long timeMillis = fromMillis;
    int numIntervals = 0;
    
    // PUT IN A VALUE FOR THE CUSTOM ASYNC COMPUTED POINT FOR THE LAST PROCESSED TIME.
    Map<Long, Double> customAsyncComputedPointMetricValues = new TreeMap<>();
    customAsyncComputedPointMetricValues.put(Long.valueOf(timeMillis), Double.parseDouble("0.0"));
    pointMetricValues.put(customAsyncComputedPoint.getMetricIdForTsdb(), customAsyncComputedPointMetricValues);
    
    while (timeMillis <= toMillis) {

      numIntervals++;
      if (!exerciseFillPolicy.equals(ExerciseFillPolicyEnum.EXERCISE_FILL_POLICY) || numIntervals <= 1) {
        mappablePoint1MetricValues.put(Long.valueOf(timeMillis), Double.parseDouble("1." + Integer.toString(numIntervals)));
        mappablePoint2MetricValues.put(Long.valueOf(timeMillis), Double.parseDouble("2." + Integer.toString(numIntervals)));
      }
      
      if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {

        timeMillis = timeMillis + TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES;
        
      } else if (computationInterval.equals(ComputationInterval.DAILY)) {
        
        timeMillis = timeMillis + TimeSeriesServiceClient.NUM_MILLISECONDS_IN_1_DAY;
        
      } else {
        
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), AbstractEntity.UTC_ZONE_ID);
        LocalDateTime nextIntervalLdt = ldt.plusMonths(1);
        long deltaMillis = nextIntervalLdt.toEpochSecond(ZoneOffset.UTC) * 1000 - timeMillis;
        timeMillis = timeMillis + deltaMillis;
        
      }      
    }
    timeSeriesServiceClient.submitMetricValuesBatchToTsdb(customer, tags, pointMetricValues);
    
    Boolean performRecalc = Boolean.FALSE;
    if (performRecalculate.equals(PerformRecalculateEnum.PERFORM_RECALCULATE)) {
      
      performRecalc = Boolean.TRUE;
      startTimestamp = null;
      endTimestamp = null;
    }
    Set<ComputationInterval> computationIntervals = new HashSet<>();
    computationIntervals.add(computationInterval);
    

    
    // STEP 2: ACT
    List<String> errors = nodeHierarchyService.evaluateCustomAsyncPoints(
        customerId,
        buildingId,
        customAsyncComputedPointId,
        computationIntervals,
        performRecalc,
        startTimestamp,
        endTimestamp);
    
    
    
    // STEP 3: ASSERT
    if (buildingEnum.equals(BuildingEnum.NON_EXISTENT_BUILDING)) {
      
      Assert.assertEquals("errors should be non-empty", "1", Integer.toString(errors.size()));
      
    } else {
      Assert.assertEquals("errors should be empty, but was: " + errors, "0", Integer.toString(errors.size()));
      if (performRecalculate.equals(PerformRecalculateEnum.NO_PERFORM_RECALCULATE)) {
        
        String metricQueryPrefix = null;
        if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {
          metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_FIFTEEN_MIN_INTERVAL;
        } else if (computationInterval.equals(ComputationInterval.DAILY)) {
          metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_ONE_DAY_INTERVAL;
        } else {
          metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_ONE_MONTH_INTERVAL;
        }      
        Collection<String> metricIds = Arrays.asList(metricQueryPrefix + customAsyncComputedPoint.getMetricIdForTsdb());
        
        Long start = startTimestamp.getTime();
        Long end = endTimestamp.getTime();
        Map<String, Map<Long, Double>> allMetricValues = timeSeriesServiceClient.retrieveMetricValuesFromTsdb(customer, metricIds, start, end);
        customAsyncComputedPointMetricValues = allMetricValues.get(customAsyncComputedPoint.getMetricIdForTsdb());
        Assert.assertNotNull("customAsyncComputedPointMetricValues is null", customAsyncComputedPointMetricValues);
        
        int numIntervalsProcessed = customAsyncComputedPointMetricValues.size();
        Assert.assertEquals("customAsyncComputedPointMetricValues size is incorrect", 
            Integer.toString(numIntervals), 
            Integer.toString(numIntervalsProcessed));
      }
    }
  }
  
  private void storeBuildingState(BuildingCustomAsyncComputedPointState buildingState) {

    String key = NodeHierarchyService.CUSTOM_COMPUTED_POINT_CACHE_KEY_PREFIX + buildingState.getBuildingId();
    cacheClient.set(
        key, 
        KryoSerialize.getInstance().encode(buildingState), 
        CacheClient.ONE_WEEK_TIME_TO_LIVE);
  }
}
//@formatter:on