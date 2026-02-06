package com.djt.hvac.domain.model.nodehierarchy.utils;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepository;
import com.djt.hvac.domain.model.nodehierarchy.event.ModelChangeEventPublisher;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = MockModelServiceProviderOptions.Builder.class)
public class MockModelServiceProviderOptions {
  private final String basePath;
  private final DictionaryRepository dictionaryRepository;
  private final ModelChangeEventPublisher modelChangeEventPublisher;
  private final Boolean performAutomaticConfiguration;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (MockModelServiceProviderOptions mockModelServiceProviderOptions) {
    return new Builder(mockModelServiceProviderOptions);
  }

  private MockModelServiceProviderOptions (Builder builder) {
    this.basePath = builder.basePath;
    this.dictionaryRepository = builder.dictionaryRepository;
    this.modelChangeEventPublisher = builder.modelChangeEventPublisher;
    this.performAutomaticConfiguration = builder.performAutomaticConfiguration;
  }

  public String getBasePath() {
    return basePath;
  }

  public DictionaryRepository getDictionaryRepository() {
    return dictionaryRepository;
  }

  public ModelChangeEventPublisher getModelChangeEventPublisher() {
    return modelChangeEventPublisher;
  }

  public Boolean getPerformAutomaticConfiguration() {
    return performAutomaticConfiguration;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String basePath;
    private DictionaryRepository dictionaryRepository;
    private ModelChangeEventPublisher modelChangeEventPublisher;
    private Boolean performAutomaticConfiguration = Boolean.FALSE;

    private Builder() {}

    private Builder(MockModelServiceProviderOptions mockModelServiceProviderOptions) {
      requireNonNull(mockModelServiceProviderOptions, "mockModelServiceProviderOptions cannot be null");
      this.basePath = mockModelServiceProviderOptions.basePath;
      this.dictionaryRepository = mockModelServiceProviderOptions.dictionaryRepository;
      this.modelChangeEventPublisher = mockModelServiceProviderOptions.modelChangeEventPublisher;
      this.performAutomaticConfiguration = mockModelServiceProviderOptions.performAutomaticConfiguration;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withBasePath(String basePath) {
      requireNonNull(basePath, "basePath cannot be null");
      this.basePath = basePath;
      return this;
    }

    public Builder withDictionaryRepository(DictionaryRepository dictionaryRepository) {
      this.dictionaryRepository = dictionaryRepository;
      return this;
    }

    public Builder withModelChangeEventPublisher(ModelChangeEventPublisher modelChangeEventPublisher) {
      this.modelChangeEventPublisher = modelChangeEventPublisher;
      return this;
    }

    public Builder withPerformAutomaticConfiguration(Boolean performAutomaticConfiguration) {
      requireNonNull(performAutomaticConfiguration, "performAutomaticConfiguration cannot be null");
      this.performAutomaticConfiguration = performAutomaticConfiguration;
      return this;
    }

    public MockModelServiceProviderOptions build() {
      requireNonNull(basePath, "basePath cannot be null");
      return new MockModelServiceProviderOptions(this);
    }
  }
}