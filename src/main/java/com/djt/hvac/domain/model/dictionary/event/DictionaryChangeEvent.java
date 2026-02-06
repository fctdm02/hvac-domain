package com.djt.hvac.domain.model.dictionary.event;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.djt.hvac.domain.model.common.event.AbstractEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 *
 * Subscribers should invalidate and reload dictionary data when this 
 * event is published.  If they only wanted to reload the bare minimum
 * of what changed, then all they would do is deal with category, which
 * is one of:
 * <ol>
 * <li>AD function templates</li>
 * <li>Node tag templates, includes equipment types</li>
 * <li>Payment plans</li>
 * <li>Report Templates</li>
 * <li>Scheduled Event Types</li>
 * <li>Tags</li>
 * <li>Units</li>
 * <li>Weather stations</li>
 * </ol>
 * 
 * @author tmyers
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = DictionaryChangeEvent.Builder.class)
public class DictionaryChangeEvent extends AbstractEvent {

  private static final long serialVersionUID = 1L;

  public static final String CATEGORY_KEY = "operationCategory";
  
  public static final String AD_FUNCTION_TEMPLATES = "AD_FUNCTION_TEMPLATES";
  public static final String NODE_TAG_TEMPLATES = "NODE_TAG_TEMPLATES";
  public static final String PAYMENT_PLANS = "PAYMENT_PLANS";
  public static final String REPORT_TEMPLATES = "REPORT_TEMPLATES";
  public static final String TAGS = "TAGS";
  public static final String UNITS = "UNITS";
  public static final String WEATHER_STATIONS = "WEATHER_STATIONS";
  
  private final String category;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (DictionaryChangeEvent event) {
    return new Builder(event);
  }

  private DictionaryChangeEvent (Builder builder) {
    super(builder);
    
    this.category = builder.category;
  }

  public String getCategory() {
    return category;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((getEventUuid() == null) ? 0 : getEventUuid().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    DictionaryChangeEvent other = (DictionaryChangeEvent) obj;
    if (getEventUuid() == null) {
      if (other.getEventUuid() != null)
        return false;
    } else if (!getEventUuid().equals(other.getEventUuid()))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DictionaryChangeEvent [category=").append(category)
        .append(", getEventUuid()=").append(getEventUuid()).append(", getOccurredOnDate()=")
        .append(getOccurredOnDate()).append(", getOwner()=").append(getOwner()).append("]");
    return builder.toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractEvent.Builder<DictionaryChangeEvent, Builder> {
    
    private String category;
    
    private Builder() {}

    private Builder(DictionaryChangeEvent event) {
      requireNonNull(event, "event cannot be null");
      this.category = event.category;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withCategory(String category) {
      requireNonNull(category, "category cannot be null");
      this.category = category;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected DictionaryChangeEvent newInstance() {
      return new DictionaryChangeEvent(this);
    }
  }
}
