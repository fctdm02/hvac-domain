package com.djt.hvac.domain.model.stripe.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = StripeInvoice.Builder.class)
public class StripeInvoice {
  private final String stripeSubscriptionId;
  private final String stripeInvoiceId;
  private final Boolean isPaid;
  private final Boolean isAttempted;
  private final Long attemptCount;
  private final String invoiceStatus;
  private final Long created;
  private final Long amountDue;
  private final Long amountPaid;
  private final String periodStart;
  private final String periodEnd;
  private final String invoicePdf;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (StripeInvoice stripeInvoice) {
    return new Builder(stripeInvoice);
  }

  private StripeInvoice (Builder builder) {
    this.stripeSubscriptionId = builder.stripeSubscriptionId;
    this.stripeInvoiceId = builder.stripeInvoiceId;
    this.isPaid = builder.isPaid;
    this.isAttempted = builder.isAttempted;
    this.attemptCount = builder.attemptCount;
    this.invoiceStatus = builder.invoiceStatus;
    this.created = builder.created;
    this.amountDue = builder.amountDue;
    this.amountPaid = builder.amountPaid;
    this.periodStart = builder.periodStart;
    this.periodEnd = builder.periodEnd;
    this.invoicePdf = builder.invoicePdf;
  }

  public String getStripeSubscriptionId() {
    return stripeSubscriptionId;
  }

  public String getStripeInvoiceId() {
    return stripeInvoiceId;
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

  public String getInvoiceStatus() {
    return invoiceStatus;
  }
  
  public Long getCreated() {
    return created;
  }

  public Long getAmountDue() {
    return amountDue;
  }

  public Long getAmountPaid() {
    return amountPaid;
  }

  public String getPeriodStart() {
    return periodStart;
  }

  public String getPeriodEnd() {
    return periodEnd;
  }

  public String getInvoicePdf() {
    return invoicePdf;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String stripeSubscriptionId;
    private String stripeInvoiceId;
    private Boolean isPaid;
    private Boolean isAttempted;
    private Long attemptCount;
    private String invoiceStatus;
    private Long created;
    private Long amountDue;
    private Long amountPaid;
    private String periodStart;
    private String periodEnd;
    private String invoicePdf;

    private Builder() {}

    private Builder(StripeInvoice stripeInvoice) {
      requireNonNull(stripeInvoice, "stripeInvoice cannot be null");
      this.stripeSubscriptionId = stripeInvoice.stripeSubscriptionId;
      this.stripeInvoiceId = stripeInvoice.stripeInvoiceId;
      this.isPaid = stripeInvoice.isPaid;
      this.isAttempted = stripeInvoice.isAttempted;
      this.attemptCount = stripeInvoice.attemptCount;
      this.invoiceStatus = stripeInvoice.invoiceStatus;
      this.created = stripeInvoice.created;
      this.amountDue = stripeInvoice.amountDue;
      this.amountPaid = stripeInvoice.amountPaid;
      this.periodStart = stripeInvoice.periodStart;
      this.periodEnd = stripeInvoice.periodEnd;
      this.invoicePdf = stripeInvoice.invoicePdf;
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

    public Builder withStripeInvoiceId(String stripeInvoiceId) {
      requireNonNull(stripeInvoiceId, "stripeInvoiceId cannot be null");
      this.stripeInvoiceId = stripeInvoiceId;
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

    public Builder withCreated(Long created) {
      requireNonNull(created, "created cannot be null");
      this.created = created;
      return this;
    }

    public Builder withAttemptCount(Long attemptCount) {
      requireNonNull(attemptCount, "attemptCount cannot be null");
      this.attemptCount = attemptCount;
      return this;
    }

    public Builder withInvoiceStatus(String invoiceStatus) {
      requireNonNull(invoiceStatus, "invoiceStatus cannot be null");
      this.invoiceStatus = invoiceStatus;
      return this;
    }

    public Builder withAmountDue(Long amountDue) {
      requireNonNull(amountDue, "amountDue cannot be null");
      this.amountDue = amountDue;
      return this;
    }

    public Builder withAmountPaid(Long amountPaid) {
      requireNonNull(amountPaid, "amountPaid cannot be null");
      this.amountPaid = amountPaid;
      return this;
    }

    public Builder withPeriodStart(String periodStart) {
      requireNonNull(periodStart, "periodStart cannot be null");
      this.periodStart = periodStart;
      return this;
    }

    public Builder withPeriodEnd(String periodEnd) {
      requireNonNull(periodEnd, "periodEnd cannot be null");
      this.periodEnd = periodEnd;
      return this;
    }

    public Builder withInvoicePdf(String invoicePdf) {
      requireNonNull(invoicePdf, "invoicePdf cannot be null");
      this.invoicePdf = invoicePdf;
      return this;
    }

    public StripeInvoice build() {
      requireNonNull(stripeSubscriptionId, "stripeSubscriptionId cannot be null");
      requireNonNull(stripeInvoiceId, "stripeInvoiceId cannot be null");
      requireNonNull(isPaid, "isPaid cannot be null");
      requireNonNull(isAttempted, "isAttempted cannot be null");
      requireNonNull(attemptCount, "attemptCount cannot be null");
      requireNonNull(invoiceStatus, "invoiceStatus cannot be null");
      requireNonNull(created, "created cannot be null");
      requireNonNull(amountDue, "amountDue cannot be null");
      requireNonNull(amountPaid, "amountPaid cannot be null");
      requireNonNull(periodStart, "periodStart cannot be null");
      requireNonNull(periodEnd, "periodEnd cannot be null");
      requireNonNull(invoicePdf, "invoicePdf cannot be null");
      return new StripeInvoice(this);
    }
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((created == null) ? 0 : created.hashCode());
    result = prime * result + ((amountDue == null) ? 0 : amountDue.hashCode());
    result = prime * result + ((amountPaid == null) ? 0 : amountPaid.hashCode());
    result = prime * result + ((attemptCount == null) ? 0 : attemptCount.hashCode());
    result = prime * result + ((invoicePdf == null) ? 0 : invoicePdf.hashCode());
    result = prime * result + ((invoiceStatus == null) ? 0 : invoiceStatus.hashCode());
    result = prime * result + ((isAttempted == null) ? 0 : isAttempted.hashCode());
    result = prime * result + ((isPaid == null) ? 0 : isPaid.hashCode());
    result = prime * result + ((periodEnd == null) ? 0 : periodEnd.hashCode());
    result = prime * result + ((periodStart == null) ? 0 : periodStart.hashCode());
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
    StripeInvoice other = (StripeInvoice) obj;
    if (created == null) {
      if (other.created != null)
        return false;
    } else if (!created.equals(other.created))
      return false;
    if (amountDue == null) {
      if (other.amountDue != null)
        return false;
    } else if (!amountDue.equals(other.amountDue))
      return false;
    if (amountPaid == null) {
      if (other.amountPaid != null)
        return false;
    } else if (!amountPaid.equals(other.amountPaid))
      return false;
    if (attemptCount == null) {
      if (other.attemptCount != null)
        return false;
    } else if (!attemptCount.equals(other.attemptCount))
      return false;
    if (invoicePdf == null) {
      if (other.invoicePdf != null)
        return false;
    } else if (!invoicePdf.equals(other.invoicePdf))
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
    if (periodEnd == null) {
      if (other.periodEnd != null)
        return false;
    } else if (!periodEnd.equals(other.periodEnd))
      return false;
    if (periodStart == null) {
      if (other.periodStart != null)
        return false;
    } else if (!periodStart.equals(other.periodStart))
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
        .append("\n\nStripeInvoice [\nstripeSubscriptionId=")
        .append(stripeSubscriptionId)
        .append(",\n stripeInvoiceId=")
        .append(stripeInvoiceId)
        .append(",\n isPaid=")
        .append(isPaid)
        .append(",\n isAttempted=")
        .append(isAttempted)
        .append(",\n attemptCount=")
        .append(attemptCount)
        .append(",\n invoiceStatus=")
        .append(invoiceStatus)
        .append(",\n created=")
        .append(created)
        .append(",\n amountDue=")
        .append(amountDue)
        .append(",\n amountPaid=")
        .append(amountPaid)
        .append(",\n periodStart=")
        .append(periodStart)
        .append(",\n periodEnd=")
        .append(periodEnd)
        .append(",\n invoicePdf=")
        .append(invoicePdf)
        .append("]\n")
        .toString();
  }  
}