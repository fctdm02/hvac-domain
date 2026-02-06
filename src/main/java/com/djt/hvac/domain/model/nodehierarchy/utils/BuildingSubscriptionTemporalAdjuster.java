package com.djt.hvac.domain.model.nodehierarchy.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Calendar;

/**
 * 
  <pre> 
      1
      January
      Jan 31
      
      2   
      February
      Feb 28 (29 in leap years)
      
      3   
      March
      Mar 31
      
      4   
      April
      Apr 30
      
      5   
      May
      May 31
      
      6   
      June
      Jun 30
      
      7   
      July
      Jul 31
      
      8   
      August
      Aug 31
      
      9   
      September
      Sep 30
      
      10  
      October
      Oct 31
      
      11  
      November
      Nov 30
      
      12  
      December
      Dec 31 
  </pre>
 * 
 * @author tmyers
 *
 */
public class BuildingSubscriptionTemporalAdjuster {

  public static LocalDate currentDayOfMonthForCurrentLocalDate(
      LocalDate sourceLocalDate,
      LocalDate targetLocalDate) {
    
    Temporal temporal = targetLocalDate
        .with(ChronoField.DAY_OF_MONTH, sourceLocalDate.getDayOfMonth());    
    
    LocalDate ld = (LocalDate)temporal; 
    return ld;
  }  
  
  public static LocalDate nextDayOfMonthForCurrentLocalDate(
      LocalDate sourceLocalDate,
      LocalDate targetLocalDate) {
    
    int sourceDayOfMonth = sourceLocalDate.getDayOfMonth();
    
    int targetMonth = targetLocalDate.getMonthValue();
    int targetYear = targetLocalDate.getYear();
    
    int nextMonth = 0;
    int nextYear = 0;
    if (targetMonth < 12) {

      nextMonth = targetMonth + 1;
      nextYear = targetYear;
      
    } else {

      nextMonth = 1;
      nextYear = targetYear + 1;
      
    }
    
    if (nextMonth == 2 && sourceDayOfMonth > 28) {
      if (isLeapYear(nextYear)) {
        sourceDayOfMonth = 29;  
      } else {
        sourceDayOfMonth = 28;
      }
    }
    
    if (sourceDayOfMonth == 31 && (nextMonth == 4 || nextMonth == 6 || nextMonth == 9 || nextMonth == 11)) {
      sourceDayOfMonth = 30;
    }
    
    Temporal temporal = targetLocalDate
        .with(ChronoField.MONTH_OF_YEAR, nextMonth)
        .with(ChronoField.DAY_OF_MONTH, sourceDayOfMonth)
        .with(ChronoField.YEAR, nextYear);
    
    LocalDate ld = (LocalDate)temporal; 
    return ld;
  }
  
  public static LocalDate nextDayOfYearForCurrentLocalDate(
      LocalDate sourceLocalDate,
      LocalDate targetLocalDate) {
    
    int sourceDayOfMonth = sourceLocalDate.getDayOfMonth();
    int sourceMonth = sourceLocalDate.getMonthValue();
    
    int targetYear = targetLocalDate.getYear() + 1;
    
    Temporal temporal = targetLocalDate
        .with(ChronoField.MONTH_OF_YEAR, sourceMonth)
        .with(ChronoField.DAY_OF_MONTH, sourceDayOfMonth)
        .with(ChronoField.YEAR, targetYear);

    if (isLeapYear(targetYear) && sourceMonth == 2 && sourceDayOfMonth == 28) {
      
      temporal = temporal
          .with(ChronoField.MONTH_OF_YEAR, sourceMonth)
          .with(ChronoField.DAY_OF_MONTH, 29)
          .with(ChronoField.YEAR, targetYear);      
    }
    
    LocalDate ld = (LocalDate)temporal; 
    return ld;    
  }
  
  public static LocalDate currentDayOfYearForCurrentLocalDate(
      LocalDate sourceLocalDate,
      LocalDate targetLocalDate) {
    
    Temporal temporal = targetLocalDate
        .with(ChronoField.DAY_OF_YEAR, sourceLocalDate.getDayOfYear())
        .with(ChronoField.YEAR, targetLocalDate.getYear());    
    
    LocalDate ld = (LocalDate)temporal; 
    return ld;
  }  

  public static boolean isLeapYear(int year) {
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
  }
  
  private BuildingSubscriptionTemporalAdjuster() { }
}
