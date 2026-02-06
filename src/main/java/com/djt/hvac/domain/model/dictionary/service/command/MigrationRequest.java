package com.djt.hvac.domain.model.dictionary.service.command;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = MigrationRequest.Builder.class)
public class MigrationRequest {
  private final String armsDbUserId;
  private final String armsDbPassword;
  private final String armsDbJdbcUrl;
  private final List<String> naturalIdentities;
  private final Boolean performDelete;
  private final Boolean processRuleSignatureIssuesOnly;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (MigrationRequest adFunctionTemplateMigrationRequest) {
    return new Builder(adFunctionTemplateMigrationRequest);
  }

  private MigrationRequest (Builder builder) {
    this.armsDbUserId = builder.armsDbUserId;
    this.armsDbPassword = builder.armsDbPassword;
    this.armsDbJdbcUrl = builder.armsDbJdbcUrl;
    this.naturalIdentities = builder.naturalIdentities;
    this.performDelete = builder.performDelete;
    this.processRuleSignatureIssuesOnly = builder.processRuleSignatureIssuesOnly;
  }

  public String getArmsDbUserId() {
    return armsDbUserId;
  }

  public String getArmsDbPassword() {
    return armsDbPassword;
  }

  public String getArmsDbJdbcUrl() {
    return armsDbJdbcUrl;
  }

  public List<String> getNaturalIdentities() {
    return naturalIdentities;
  }
  
  public Boolean getPerformDelete() {
    return performDelete;
  }
  
  public Boolean getProcessRuleSignatureIssuesOnly() {
    return processRuleSignatureIssuesOnly;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String armsDbUserId;
    private String armsDbPassword;
    private String armsDbJdbcUrl;
    private List<String> naturalIdentities;
    private Boolean performDelete;
    private Boolean processRuleSignatureIssuesOnly = Boolean.FALSE;

    private Builder() {}

    private Builder(MigrationRequest adFunctionTemplateMigrationRequest) {
      requireNonNull(adFunctionTemplateMigrationRequest, "adFunctionTemplateMigrationRequest cannot be null");
      this.armsDbUserId = adFunctionTemplateMigrationRequest.armsDbUserId;
      this.armsDbPassword = adFunctionTemplateMigrationRequest.armsDbPassword;
      this.armsDbJdbcUrl = adFunctionTemplateMigrationRequest.armsDbJdbcUrl;
      this.naturalIdentities = adFunctionTemplateMigrationRequest.naturalIdentities;
      this.performDelete = adFunctionTemplateMigrationRequest.performDelete;
      this.processRuleSignatureIssuesOnly = adFunctionTemplateMigrationRequest.processRuleSignatureIssuesOnly;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withArmsDbUserId(String armsDbUserId) {
      requireNonNull(armsDbUserId, "armsDbUserId cannot be null");
      this.armsDbUserId = armsDbUserId;
      return this;
    }

    public Builder withArmsDbPassword(String armsDbPassword) {
      requireNonNull(armsDbPassword, "armsDbPassword cannot be null");
      this.armsDbPassword = armsDbPassword;
      return this;
    }

    public Builder withArmsDbJdbcUrl(String armsDbJdbcUrl) {
      requireNonNull(armsDbJdbcUrl, "armsDbJdbcUrl cannot be null");
      this.armsDbJdbcUrl = armsDbJdbcUrl;
      return this;
    }

    public Builder withNaturalIdentities(List<String> naturalIdentities) {
      requireNonNull(naturalIdentities, "naturalIdentities cannot be null");
      List<String> list = new ArrayList<>();
      for (String s: naturalIdentities) {
        if (s.contains(",")) {
          String[] arr = s.split(",");
          for (int i=0; i < arr.length; i++) {
            list.add(arr[i]);
          }
        } else {
          list.add(s);
        }
      }
      this.naturalIdentities = ImmutableList.copyOf(list);
      return this;
    }

    public Builder withPerformDelete(Boolean performDelete) {
      requireNonNull(performDelete, "performDelete cannot be null");
      this.performDelete = performDelete;
      return this;
    }    

    public Builder withProcessRuleSignatureIssuesOnly(Boolean processRuleSignatureIssuesOnly) {
      requireNonNull(processRuleSignatureIssuesOnly, "processRuleSignatureIssuesOnly cannot be null");
      this.processRuleSignatureIssuesOnly = processRuleSignatureIssuesOnly;
      return this;
    }    
    
    public MigrationRequest build() {
      requireNonNull(armsDbUserId, "armsDbUserId cannot be null");
      requireNonNull(armsDbPassword, "armsDbPassword cannot be null");
      requireNonNull(armsDbJdbcUrl, "armsDbJdbcUrl cannot be null");
      requireNonNull(naturalIdentities, "naturalIdentities cannot be null");
      requireNonNull(performDelete, "performDelete cannot be null");
      return new MigrationRequest(this);
    }
  }
}