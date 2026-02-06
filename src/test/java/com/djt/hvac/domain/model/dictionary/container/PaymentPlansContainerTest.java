package com.djt.hvac.domain.model.dictionary.container;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.utils.ObjectMappers;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.container.PaymentPlansContainer;
import com.djt.hvac.domain.model.dictionary.dto.PaymentPlanDto;
import com.djt.hvac.domain.model.dictionary.enums.PaymentInterval;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PaymentPlansContainerTest extends AbstractResoluteDomainModelTest {

  private static final ObjectMapper MAPPER = AbstractEntity.OBJECT_MAPPER.get();
  
  private static final String JSON;
  
  static {
    try (InputStream in = PaymentPlansContainerTest.class.getResourceAsStream("PaymentPlanList.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
     JSON = s.hasNext() ? s.next() : "";

    } catch (IOException e) {
      throw new RuntimeException(e);
    }   
  }
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    AbstractResoluteDomainModelTest.beforeClass();
  }

  @Test
  public void deserialize() throws IOException {
    
    // STEP 1: ARRANGE
    
    
    // STEP 2: ACT
    List<PaymentPlanDto> dtoList = MAPPER.readValue(JSON, new TypeReference<List<PaymentPlanDto>>() {});
    PaymentPlansContainer container = PaymentPlansContainer.mapFromDtos(dtoList);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("container is null", container);
  }
  
  @Test
  public void serialize() throws IOException {
    
    // STEP 1: ARRANGE
    List<PaymentPlanEntity> entityList = new ArrayList<>();
    
    entityList.add(new PaymentPlanEntity(
        Integer.valueOf(1),
        Integer.valueOf(3000),
        PaymentInterval.get("MONTHLY"),
        Double.valueOf(300.0),
        "stripe_product_id_1",
        "stripe_plan_id_1",
        "stripe_test_product_id_1",
        "stripe_test_plan_id_1",
        Boolean.FALSE));
    
    entityList.add(new PaymentPlanEntity(
        Integer.valueOf(2),
        Integer.valueOf(3000),
        PaymentInterval.get("YEARLY"),
        Double.valueOf(3000.0),
        "stripe_product_id_2",
        "stripe_plan_id_2",
        "stripe_test_product_id_2",
        "stripe_test_plan_id_2",
        Boolean.FALSE));

    entityList.add(new PaymentPlanEntity(
        Integer.valueOf(3),
        Integer.valueOf(5000),
        PaymentInterval.get("MONTHLY"),
        Double.valueOf(500.0),
        "stripe_product_id_3",
        "stripe_plan_id_3",
        "stripe_test_product_id_3",
        "stripe_test_plan_id_3",
        Boolean.FALSE));
    
    entityList.add(new PaymentPlanEntity(
        Integer.valueOf(4),
        Integer.valueOf(5000),
        PaymentInterval.get("YEARLY"),
        Double.valueOf(5000.0),
        "stripe_product_id_4",
        "stripe_plan_id_4",
        "stripe_test_product_id_4",
        "stripe_test_plan_id_4",
        Boolean.FALSE));
    
    entityList.add(new PaymentPlanEntity(
        Integer.valueOf(5),
        Integer.valueOf(10000),
        PaymentInterval.get("MONTHLY"),
        Double.valueOf(700.0),
        "stripe_product_id_5",
        "stripe_plan_id_5",
        "stripe_test_product_id_5",
        "stripe_test_plan_id_5",
        Boolean.FALSE));

    entityList.add(new PaymentPlanEntity(
        Integer.valueOf(6),
        Integer.valueOf(10000),
        PaymentInterval.get("YEARLY"),
        Double.valueOf(7000.0),
        "stripe_product_id_6",
        "stripe_plan_id_6",
        "stripe_test_product_id_6",
        "stripe_test_plan_id_6",
        Boolean.FALSE));
    
    PaymentPlansContainer container = new PaymentPlansContainer(entityList);
    
    List<PaymentPlanDto> dtoList = PaymentPlansContainer.mapToDtos(container); 
    
    
    // STEP 2: ACT
    String json = ObjectMappers.create().writerWithDefaultPrettyPrinter().writeValueAsString(dtoList);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("json is null", json);
  }
}