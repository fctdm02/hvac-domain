package com.djt.hvac.domain.model.common.validation;

import java.io.IOException;

import org.junit.Test;

import com.djt.hvac.domain.model.common.utils.ObjectMappers;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.equalTo;

public class ValidationMessageTest {
  
  private static ObjectMapper MAPPER = ObjectMappers.create();

  @Test
  public void test_serialization_and_deserialization() throws IOException {
    ValidationMessage expected = ValidationMessage.builder()
        .withDetails("details")
        .withEntityType("entityType")
        .withIssueId(1)
        .withNaturalIdentity("5")
        .withRemediationDescription("remediationDesc")
        .build();
    
    String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
    
    ValidationMessage actual = MAPPER.readValue(json, ValidationMessage.class);
    
    assertThat(actual, equalTo(expected));    
  }
 }