package com.djt.hvac.domain.model.stripe.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = StripeSubscription.Builder.class)
public class StripeSubscription {
  private final String stripeSubscriptionId;
  private final String startedAt;
  private final String billingAnchor;
  private final String currentIntervalStartedAt;
  private final String currentIntervalEndsAt;
  private final String nextIntervalStartsAt;
  private final String stripeInvoiceId;
  private final String invoiceStatus;
  private final Boolean isPaid;
  private final Boolean isAttempted;
  private final Long attemptCount;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (StripeSubscription stripeSubscription) {
    return new Builder(stripeSubscription);
  }

  private StripeSubscription (Builder builder) {
    this.stripeSubscriptionId = builder.stripeSubscriptionId;
    this.startedAt = builder.startedAt;
    this.billingAnchor = builder.billingAnchor;
    this.currentIntervalStartedAt = builder.currentIntervalStartedAt;
    this.currentIntervalEndsAt = builder.currentIntervalEndsAt;
    this.nextIntervalStartsAt = builder.nextIntervalStartsAt;
    this.stripeInvoiceId = builder.stripeInvoiceId;
    this.invoiceStatus = builder.invoiceStatus;
    this.isPaid = builder.isPaid;
    this.isAttempted = builder.isAttempted;
    this.attemptCount = builder.attemptCount;
  }

  public String getStripeSubscriptionId() {
    return stripeSubscriptionId;
  }

  public String getStartedAt() {
    return startedAt;
  }

  public String getBillingAnchor() {
    return billingAnchor;
  }

  public String getCurrentIntervalStartedAt() {
    return currentIntervalStartedAt;
  }

  public String getCurrentIntervalEndsAt() {
    return currentIntervalEndsAt;
  }

  public String getNextIntervalStartsAt() {
    return nextIntervalStartsAt;
  }

  public String getStripeInvoiceId() {
    return stripeInvoiceId;
  }

  public String getInvoiceStatus() {
    return invoiceStatus;
  }

  public Boolean getIsPaid() {
    return isPaid;
  }

  public Boolean getIsAttempted() {
    return isAttempted;
  }

  public Long getAttemptCount() {
    return attemptCount;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String stripeSubscriptionId;
    private String startedAt;
    private String billingAnchor;
    private String currentIntervalStartedAt;
    private String currentIntervalEndsAt;
    private String nextIntervalStartsAt;
    private String stripeInvoiceId;
    private String invoiceStatus;
    private Boolean isPaid;
    private Boolean isAttempted;
    private Long attemptCount;

    private Builder() {}

    private Builder(StripeSubscription stripeSubscription) {
      requireNonNull(stripeSubscription, "stripeSubscription cannot be null");
      this.stripeSubscriptionId = stripeSubscription.stripeSubscriptionId;
      this.startedAt = stripeSubscription.startedAt;
      this.billingAnchor = stripeSubscription.billingAnchor;
      this.currentIntervalStartedAt = stripeSubscription.currentIntervalStartedAt;
      this.currentIntervalEndsAt = stripeSubscription.currentIntervalEndsAt;
      this.nextIntervalStartsAt = stripeSubscription.nextIntervalStartsAt;
      this.stripeInvoiceId = stripeSubscription.stripeInvoiceId;
      this.invoiceStatus = stripeSubscription.invoiceStatus;
      this.isPaid = stripeSubscription.isPaid;
      this.isAttempted = stripeSubscription.isAttempted;
      this.attemptCount = stripeSubscription.attemptCount;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withStripeSubscriptionId(String stripeSubscriptionId) {
      requireNonNull(stripeSubscriptionId, "stripeSubscriptionId cannot be null");
      this.stripeSubscriptionId = stripeSubscriptionId;
      return this;
    }

    public Builder withStartedAt(String startedAt) {
      requireNonNull(startedAt, "startedAt cannot be null");
      this.startedAt = startedAt;
      return this;
    }

    public Builder withBillingAnchor(String billingAnchor) {
      requireNonNull(billingAnchor, "billingAnchor cannot be null");
      this.billingAnchor = billingAnchor;
      return this;
    }

    public Builder withCurrentIntervalStartedAt(String currentIntervalStartedAt) {
      requireNonNull(currentIntervalStartedAt, "currentIntervalStartedAt cannot be null");
      this.currentIntervalStartedAt = currentIntervalStartedAt;
      return this;
    }

    public Builder withCurrentIntervalEndsAt(String currentIntervalEndsAt) {
      requireNonNull(currentIntervalEndsAt, "currentIntervalEndsAt cannot be null");
      this.currentIntervalEndsAt = currentIntervalEndsAt;
      return this;
    }

    public Builder withNextIntervalStartsAt(String nextIntervalStartsAt) {
      requireNonNull(nextIntervalStartsAt, "nextIntervalStartsAt cannot be null");
      this.nextIntervalStartsAt = nextIntervalStartsAt;
      return this;
    }

    public Builder withStripeInvoiceId(String stripeInvoiceId) {
      requireNonNull(stripeInvoiceId, "stripeInvoiceId cannot be null");
      this.stripeInvoiceId = stripeInvoiceId;
      return this;
    }

    public Builder withInvoiceStatus(String invoiceStatus) {
      requireNonNull(invoiceStatus, "invoiceStatus cannot be null");
      this.invoiceStatus = invoiceStatus;
      return this;
    }

    public Builder withIsPaid(Boolean isPaid) {
      requireNonNull(isPaid, "isPaid cannot be null");
      this.isPaid = isPaid;
      return this;
    }

    public Builder withIsAttempted(Boolean isAttempted) {
      requireNonNull(isAttempted, "isAttempted cannot be null");
      this.isAttempted = isAttempted;
      return this;
    }

    public Builder withAttemptCount(Long attemptCount) {
      requireNonNull(attemptCount, "attemptCount cannot be null");
      this.attemptCount = attemptCount;
      return this;
    }

    public StripeSubscription build() {
      requireNonNull(stripeSubscriptionId, "stripeSubscriptionId cannot be null");
      requireNonNull(startedAt, "startedAt cannot be null");
      requireNonNull(billingAnchor, "billingAnchor cannot be null");
      requireNonNull(currentIntervalStartedAt, "currentIntervalStartedAt cannot be null");
      requireNonNull(currentIntervalEndsAt, "currentIntervalEndsAt cannot be null");
      requireNonNull(nextIntervalStartsAt, "nextIntervalStartsAt cannot be null");
      requireNonNull(stripeInvoiceId, "stripeInvoiceId cannot be null");
      requireNonNull(invoiceStatus, "invoiceStatus cannot be null");
      requireNonNull(isPaid, "isPaid cannot be null");
      requireNonNull(isAttempted, "isAttempted cannot be null");
      requireNonNull(attemptCount, "attemptCount cannot be null");
      return new StripeSubscription(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((attemptCount == null) ? 0 : attemptCount.hashCode());
    result = prime * result + ((billingAnchor == null) ? 0 : billingAnchor.hashCode());
    result =
        prime * result + ((currentIntervalEndsAt == null) ? 0 : currentIntervalEndsAt.hashCode());
    result = prime * result
        + ((currentIntervalStartedAt == null) ? 0 : currentIntervalStartedAt.hashCode());
    result = prime * result + ((invoiceStatus == null) ? 0 : invoiceStatus.hashCode());
    result = prime * result + ((isAttempted == null) ? 0 : isAttempted.hashCode());
    result = prime * result + ((isPaid == null) ? 0 : isPaid.hashCode());
    result =
        prime * result + ((nextIntervalStartsAt == null) ? 0 : nextIntervalStartsAt.hashCode());
    result = prime * result + ((startedAt == null) ? 0 : startedAt.hashCode());
    result = prime * result + ((stripeInvoiceId == null) ? 0 : stripeInvoiceId.hashCode());
    result =
        prime * result + ((stripeSubscriptionId == null) ? 0 : stripeSubscriptionId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StripeSubscription other = (StripeSubscription) obj;
    if (attemptCount == null) {
      if (other.attemptCount != null)
        return false;
    } else if (!attemptCount.equals(other.attemptCount))
      return false;
    if (billingAnchor == null) {
      if (other.billingAnchor != null)
        return false;
    } else if (!billingAnchor.equals(other.billingAnchor))
      return false;
    if (currentIntervalEndsAt == null) {
      if (other.currentIntervalEndsAt != null)
        return false;
    } else if (!currentIntervalEndsAt.equals(other.currentIntervalEndsAt))
      return false;
    if (currentIntervalStartedAt == null) {
      if (other.currentIntervalStartedAt != null)
        return false;
    } else if (!currentIntervalStartedAt.equals(other.currentIntervalStartedAt))
      return false;
    if (invoiceStatus == null) {
      if (other.invoiceStatus != null)
        return false;
    } else if (!invoiceStatus.equals(other.invoiceStatus))
      return false;
    if (isAttempted == null) {
      if (other.isAttempted != null)
        return false;
    } else if (!isAttempted.equals(other.isAttempted))
      return false;
    if (isPaid == null) {
      if (other.isPaid != null)
        return false;
    } else if (!isPaid.equals(other.isPaid))
      return false;
    if (nextIntervalStartsAt == null) {
      if (other.nextIntervalStartsAt != null)
        return false;
    } else if (!nextIntervalStartsAt.equals(other.nextIntervalStartsAt))
      return false;
    if (startedAt == null) {
      if (other.startedAt != null)
        return false;
    } else if (!startedAt.equals(other.startedAt))
      return false;
    if (stripeInvoiceId == null) {
      if (other.stripeInvoiceId != null)
        return false;
    } else if (!stripeInvoiceId.equals(other.stripeInvoiceId))
      return false;
    if (stripeSubscriptionId == null) {
      if (other.stripeSubscriptionId != null)
        return false;
    } else if (!stripeSubscriptionId.equals(other.stripeSubscriptionId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("\n\nStripeSubscription [\nstripeSubscriptionId=")
        .append(stripeSubscriptionId)
        .append(",\n startedAt=")
        .append(startedAt)
        .append(",\n billingAnchor=")
        .append(billingAnchor)
        .append(",\n currentIntervalStartedAt=")
        .append(currentIntervalStartedAt)
        .append(",\n currentIntervalEndsAt=")
        .append(currentIntervalEndsAt)
        .append(",\n nextIntervalStartsAt=")
        .append(nextIntervalStartsAt)
        .append(",\n stripeInvoiceId=")
        .append(stripeInvoiceId)
        .append(",\n invoiceStatus=")
        .append(invoiceStatus)
        .append(",\n isPaid=")
        .append(isPaid)
        .append(",\n isAttempted=")
        .append(isAttempted)
        .append(",\n attemptCount=")
        .append(attemptCount)
        .append("]\n")
        .toString();
  }
}