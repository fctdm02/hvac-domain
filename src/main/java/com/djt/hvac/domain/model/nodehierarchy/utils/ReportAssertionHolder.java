package com.djt.hvac.domain.model.nodehierarchy.utils;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ReportAssertionHolder.Builder.class)
public class ReportAssertionHolder {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ReportAssertionHolder.class);
  
  private final Boolean expectedIsEnabled;
  private final Boolean expectedIsValid;
  private final Integer expectedNumGreenEquipment;
  private final Integer expectedNumRedEquipment;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportAssertionHolder reportAssertionHolder) {
    return new Builder(reportAssertionHolder);
  }

  private ReportAssertionHolder (Builder builder) {
    this.expectedIsEnabled = builder.expectedIsEnabled;
    this.expectedIsValid = builder.expectedIsValid;
    this.expectedNumGreenEquipment = builder.expectedNumGreenEquipment;
    this.expectedNumRedEquipment = builder.expectedNumRedEquipment;
  }

  public Boolean getExpectedIsEnabled() {
    return expectedIsEnabled;
  }

  public Boolean getExpectedIsValid() {
    return expectedIsValid;
  }

  public Integer getExpectedNumGreenEquipment() {
    return expectedNumGreenEquipment;
  }

  public Integer getExpectedNumRedEquipment() {
    return expectedNumRedEquipment;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Boolean expectedIsEnabled;
    private Boolean expectedIsValid;
    private Integer expectedNumGreenEquipment;
    private Integer expectedNumRedEquipment;

    private Builder() {}

    private Builder(ReportAssertionHolder reportAssertionHolder) {
      requireNonNull(reportAssertionHolder, "reportAssertionHolder cannot be null");
      this.expectedIsEnabled = reportAssertionHolder.expectedIsEnabled;
      this.expectedIsValid = reportAssertionHolder.expectedIsValid;
      this.expectedNumGreenEquipment = reportAssertionHolder.expectedNumGreenEquipment;
      this.expectedNumRedEquipment = reportAssertionHolder.expectedNumRedEquipment;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withExpectedIsEnabled(Boolean expectedIsEnabled) {
      this.expectedIsEnabled = expectedIsEnabled;
      return this;
    }

    public Builder withExpectedIsValid(Boolean expectedIsValid) {
      this.expectedIsValid = expectedIsValid;
      return this;
    }

    public Builder withExpectedNumGreenEquipment(Integer expectedNumGreenEquipment) {
      this.expectedNumGreenEquipment = expectedNumGreenEquipment;
      return this;
    }

    public Builder withExpectedNumRedEquipment(Integer expectedNumRedEquipment) {
      this.expectedNumRedEquipment = expectedNumRedEquipment;
      return this;
    }

    public ReportAssertionHolder build() {
      return new ReportAssertionHolder(this);
    }
  }
  
  public static void assertReportState(
      PortfolioEntity portfolio,
      ReportAssertionHolder ah) {
    
    for (ReportInstanceEntity reportInstance: portfolio.getAllReportInstances()) {
      
      Integer reportInstanceId = reportInstance.getPersistentIdentity();
      Integer reportTemplateId = reportInstance.getReportTemplate().getPersistentIdentity();
      Integer parentBuildingId = reportInstance.getBuilding().getPersistentIdentity();
      
      boolean isEnabled = reportInstance.isEnabled();
      boolean isValid = reportInstance.isValid();
      int numGreenEquipment = reportInstance.getNumEquipmentInGreenStatus();
      int numRedEquipment = reportInstance.getNumEquipmentInRedStatus();
      int numEquipmentTotal = reportInstance.getNumEquipmentTotal();
      
      if (LOGGER.isDebugEnabled()) {

        LOGGER.debug("=======================================================");
        LOGGER.debug("reportInstance: " + reportInstance);
        LOGGER.debug("reportInstanceId: " + reportInstanceId);
        LOGGER.debug("reportTemplateId: " + reportTemplateId);
        LOGGER.debug("parentBuildingId: " + parentBuildingId);
        LOGGER.debug("isEnabled: " + isEnabled);
        LOGGER.debug("isValid: " + isValid);
        LOGGER.debug("numGreenEquipment: " + numGreenEquipment);
        LOGGER.debug("numRedEquipment: " + numRedEquipment);
        LOGGER.debug("numEquipmentTotal: " + numEquipmentTotal);
      }
      
      if (ah.getExpectedIsEnabled() != null) {
        assertEquals("reportInstance isEnabled is incorrect for: " + reportInstance,
            Boolean.toString(ah.getExpectedIsEnabled()),
            Boolean.toString(isEnabled));
      }

      if (ah.getExpectedIsValid() != null) {
        assertEquals("reportInstance isValid is incorrect for: " + reportInstance,
            Boolean.toString(ah.getExpectedIsValid()),
            Boolean.toString(isValid));
      }
      
      if (ah.getExpectedNumGreenEquipment() != null) {
        if (ah.getExpectedNumGreenEquipment() == 0) {
          assertTrue("reportInstance numGreenEquipment is incorrect for: " + reportInstance,
              numGreenEquipment == 0);
          
        } else {
          assertTrue("reportInstance numGreenEquipment is incorrect for: " + reportInstance,
              numGreenEquipment > 0);
        }
      }
      
      if (ah.getExpectedNumRedEquipment() != null) {
        if (ah.getExpectedNumRedEquipment() == 0) {
          assertTrue("reportInstance numRedEquipment is incorrect for: " + reportInstance,
              numRedEquipment == 0);
        } else {
          assertTrue("reportInstance numRedEquipment is incorrect for: " + reportInstance,
              numRedEquipment > 0);
        }
      }

      assertTrue("reportInstance numEquipmentTotal is incorrect for: " + reportInstance,
          numEquipmentTotal == (numRedEquipment + numGreenEquipment));
    }
  }

  private static void assertTrue(String message, boolean one) {
    
    if (!one) {
      throw new IllegalStateException(message);
    }
  }
  
  private static void assertEquals(String message, Object one, Object two) {
    
    if (!one.equals(two)) {
      throw new IllegalStateException(message 
          + ": expected [" 
          + one 
          + "] to be equal to: [" 
          + two
          + "].");
    }
  }  
}