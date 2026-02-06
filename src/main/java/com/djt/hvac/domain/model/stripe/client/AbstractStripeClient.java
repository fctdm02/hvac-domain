//@formatter:off
package com.djt.hvac.domain.model.stripe.client;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import com.djt.hvac.domain.model.common.AbstractEntity;

public abstract class AbstractStripeClient implements StripeClient{
  
  protected static final ThreadLocal<DateTimeFormatter> LOCAL_DATE_FORMATTER = new ThreadLocal<DateTimeFormatter>() {

    @Override
    protected DateTimeFormatter initialValue() {
      return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }
  };
  
  private final String apiKey;
  
  public AbstractStripeClient(String apiKey) {
    requireNonNull(apiKey, "apiKey cannot be null");
    this.apiKey = apiKey;
  }

  @Override
  public String getApiKey() {
    return apiKey;
  }
  
  @Override
  public Boolean isLiveMode() {
    
    if (apiKey.startsWith(PROD_API_KEY_PREFIX)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
  
  protected Long getEpochSecondsForStartOfNextDay() {
    
    /*
    OLD CODE FROM RAINMAKER:
    return Timestamp
        .from(Instant.now())
        .toLocalDateTime()
        .toEpochSecond(ZoneOffset.MIN);
    */
    
    // Stripe won't allow us to use a date in the past, so in order to
    // be able to set the start to tbe at the start of the day, we must
    // advance to the next day.
    LocalDate now = AbstractEntity.getTimeKeeper().getCurrentLocalDate();
    ZonedDateTime startOfNextDay = now.plusDays(1).atStartOfDay(
        TimeZone.getTimeZone("GMT")
        .toZoneId());
    return Long.valueOf(startOfNextDay.toEpochSecond());
  }
  
  public LocalDate getDate(Long epochSeconds) {
    
    return new Timestamp(Instant
        .ofEpochSecond(epochSeconds)
        .toEpochMilli())
        .toLocalDateTime()
        .toLocalDate();
  }    
}
//@formatter:on