package com.djt.hvac.domain.model.notification.dto;

import static java.util.Objects.requireNonNull;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = UpdateDraftNotificationEventOptions.Builder.class)
public class UpdateDraftNotificationEventOptions {
  
  public static final int NUM_DAYS_TO_RETAIN_NOTIFICATION_EVENT = 90;
  
  private final Integer customerId;
  private final String expirationDate;
  private final SortedMap<String, String> substitutionTokenValues;
  private final String details;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (UpdateDraftNotificationEventOptions eventOptions) {
    return new Builder(eventOptions);
  }

  private UpdateDraftNotificationEventOptions (Builder builder) {
    this.customerId = builder.customerId;
    this.expirationDate = builder.expirationDate;
    this.substitutionTokenValues = builder.substitutionTokenValues;
    this.details = builder.details;
  }

  public Integer getCustomerId() {
    return customerId;
  }
  
  public String getExpirationDate() {
    return expirationDate;
  }
  
  public SortedMap<String, String> getSubstitutionTokenValues() {
    return this.substitutionTokenValues;
  }

  public String getDetails() {
    return details;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer customerId;
    private String expirationDate;
    private SortedMap<String, String> substitutionTokenValues = new TreeMap<>();
    private String details = "";

    private Builder() {}

    private Builder(UpdateDraftNotificationEventOptions eventOptions) {
      requireNonNull(eventOptions, "eventOptions cannot be null");
      this.customerId = eventOptions.customerId;
      this.expirationDate = eventOptions.expirationDate;
      this.substitutionTokenValues = eventOptions.substitutionTokenValues;
      this.details = eventOptions.details;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }
    
    public Builder withCustomerId(Integer customerId) {
      this.customerId = customerId;
      return this;
    }
    
    public Builder withExpirationDate(String expirationDate) {
      this.expirationDate = expirationDate;
      return this;
    }

    public Builder withSubstitutionTokenValues(SortedMap<String, String> substitutionTokenValues) {
      if (substitutionTokenValues != null) {
        this.substitutionTokenValues = substitutionTokenValues;  
      }
      return this;
    }
    
    public Builder withDetails(String details) {
      if (details != null) {
        this.details = details;  
      }
      return this;
    }

    public UpdateDraftNotificationEventOptions build() {
      
      setExpirationDate(expirationDate);
      return new UpdateDraftNotificationEventOptions(this);
    }
    
    private void setExpirationDate(String expirationDate) {
      
      if (expirationDate != null) {
        if (!expirationDate.contains(":")) {
          this.expirationDate = expirationDate + " 00:00:00";
        } else {
          this.expirationDate = expirationDate;  
        }
      } else {
        this.expirationDate = AbstractEntity.formatTimestamp(AbstractEntity.getTimeKeeper().getTimestampForDaysFromCurrent(NUM_DAYS_TO_RETAIN_NOTIFICATION_EVENT));
      }
    }
  }
}