package com.djt.hvac.domain.model.payment.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.djt.hvac.domain.model.common.timekeeper.impl.TestTimeKeeperImpl;
import com.djt.hvac.domain.model.nodehierarchy.utils.BuildingSubscriptionTemporalAdjuster;

public class BuildingSubscriptionTemporalAdjusterTest {
  
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("GMT"));    
      System.err.println("Using default time zone:" + TimeZone.getDefault());
      System.err.println();
    } catch (Exception e) {
    }
  }
   
  @Before
  public void before() throws Exception {
   
  }
  
  @Test
  public void currentDayOfMonthForCurrentLocalDate_targetDayOfMonthLessThanSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-10";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2022-02-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.currentDayOfMonthForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  }
  
  @Test
  public void currentDayOfMonthForCurrentLocalDate_targetDayOfMonthGreaterThanSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-20";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2022-02-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.currentDayOfMonthForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  }  
  
  @Test
  public void currentDayOfMonthForCurrentLocalDate_targetDayOfMonthEqualToSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-15";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2022-02-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.currentDayOfMonthForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  } 
  
  @Test
  public void currentDayOfYearForCurrentLocalDate_targetDayOfYearLessThanSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-10";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2022-01-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.currentDayOfYearForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());
  }
  
  @Test
  public void currentDayOfYearForCurrentLocalDate_targetDayOfYearGreaterThanSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-20";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2022-01-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.currentDayOfYearForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());
  }
  
  @Test
  public void currentDayOfYearForCurrentLocalDate_targetDayOfYearEqualToSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-15";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2022-01-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.currentDayOfYearForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());
  }  
  
  @Test
  public void nextDayOfMonthForCurrentLocalDate_targetDayOfMonthLessThanSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-10";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2022-03-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  }

  @Test
  public void nextDayOfMonthForCurrentLocalDate_targetDayOfMonthGreaterThanSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-20";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2022-03-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  }

  @Test
  public void nextDayOfMonthForCurrentLocalDate_targetDayOfMonthEqualToSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-15";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2022-03-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  }
  
  @Test
  public void nextDayOfMonthForCurrentLocalDate_everyDayOfYear() throws Exception {
    
    TestTimeKeeperImpl timeKeeper = new TestTimeKeeperImpl();
    LocalDate endDate = LocalDate.parse(
        "2020-12-31", 
        DATE_TIME_FORMATTER);
    
    List<LocalDate> datesBetween = timeKeeper.getDatesBetween(endDate);
    for (LocalDate localDate: datesBetween) {

      LocalDate sourceDate = localDate;
      LocalDate targetDate = localDate.plusMonths(1);
      
      System.err.println("sourceDate: " + sourceDate + " targetDate: " + targetDate);
      LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          sourceDate, 
          targetDate);
      
      Assert.assertNotNull("adjustedDate is null", adjustedDate);
    }    
  }  
  
  @Test
  public void nextDayOfMonthForCurrentLocalDate_endOfYear() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-11-01";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2020-12-01";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
  }
  
  // ***********************************************************************************************************
  
  @Test
  public void nextDayOfYearForCurrentLocalDate_everyDayOfYear() throws Exception {
    
    TestTimeKeeperImpl timeKeeper = new TestTimeKeeperImpl();
    LocalDate endDate = LocalDate.parse(
        "2020-12-31", 
        DATE_TIME_FORMATTER);
    
    List<LocalDate> datesBetween = timeKeeper.getDatesBetween(endDate);
    for (LocalDate localDate: datesBetween) {

      LocalDate sourceDate = localDate;
      LocalDate targetDate = localDate.plusYears(1);
      
      LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          sourceDate, 
          targetDate);
      
      Assert.assertNotNull("adjustedDate is null", adjustedDate);
    }    
  } 
  
  @Test
  public void nextDayOfYearForCurrentLocalDate_targetDayOfYearLessThanSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-10";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2023-01-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfYearForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  }
  
  @Test
  public void nextDayOfYearForCurrentLocalDate_targetDayOfYearGreaterThanSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-20";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2023-01-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfYearForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  }

  @Test
  public void nextDayOfYearForCurrentLocalDate_targetDayOfYearEqualToSource() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-15";
    LocalDate sourceDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String target = "2022-02-15";
    LocalDate targetDate = LocalDate.parse(target, DATE_TIME_FORMATTER);
    
    String expectedAdjustedDate = "2023-01-15";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfYearForCurrentLocalDate(
        sourceDate, 
        targetDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  } 
  
  @Test
  public void nextDayOfMonthForCurrentLocalDate() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2021-01-31";
    LocalDate currentIntervalStartedAtLocalDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String expectedAdjustedDate = "2021-02-28";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  }   

  @Test
  public void nextDayOfMonthForCurrentLocalDate_leapYear() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2020-01-31";
    LocalDate currentIntervalStartedAtLocalDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String expectedAdjustedDate = "2020-02-29";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfMonthForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  }
  
  @Test
  public void nextDayOfYearForCurrentLocalDate_leapYear() throws Exception {
    
    // STEP 1: ARRANGE
    String source = "2019-02-28";
    LocalDate currentIntervalStartedAtLocalDate = LocalDate.parse(source, DATE_TIME_FORMATTER);

    String expectedAdjustedDate = "2020-02-29";
    

    
    // STEP 2: ACT
    LocalDate adjustedDate = BuildingSubscriptionTemporalAdjuster.nextDayOfYearForCurrentLocalDate(
          currentIntervalStartedAtLocalDate, 
          currentIntervalStartedAtLocalDate);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adjustedDate is null", adjustedDate);
    Assert.assertEquals("adjustedDate is incorrect", expectedAdjustedDate, adjustedDate.toString());  
  }   
}
