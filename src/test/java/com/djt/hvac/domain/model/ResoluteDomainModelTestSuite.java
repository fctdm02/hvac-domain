package com.djt.hvac.domain.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.djt.hvac.domain.model.dictionary.container.AdFunctionTemplatesContainerTest;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainerTest;
import com.djt.hvac.domain.model.dictionary.container.PaymentPlansContainerTest;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateDtoTest;
import com.djt.hvac.domain.model.dictionary.dto.report.ReportTemplateDtoTest;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntityTest;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEventTest;
import com.djt.hvac.domain.model.nodehierarchy.service.CanadianCustomerFunctionalTest;
import com.djt.hvac.domain.model.nodehierarchy.service.CustomComputedPointEvaluationTest;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyServiceTest;
import com.djt.hvac.domain.model.nodehierarchy.service.async.NodeHierarchyAsyncCommandServiceTest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateAdFunctionInstancesRequestTest;
import com.djt.hvac.domain.model.nodehierarchy.utils.BillableBuildingPointLimiterTest;
import com.djt.hvac.domain.model.nodehierarchy.utils.NodeHierarchyTestDataBuilderTest;
import com.djt.hvac.domain.model.nodehierarchy.utils.RawPointMappingNodeNameFilterTest;
import com.djt.hvac.domain.model.nodehierarchy.validation.RemediationStrategyTest;
import com.djt.hvac.domain.model.nodehierarchy.visitor.PortfolioVisitorTest;
import com.djt.hvac.domain.model.notification.dto.CreateNotificationEventOptionsTest;
import com.djt.hvac.domain.model.notification.entity.UserNotificationEntityTest;
import com.djt.hvac.domain.model.notification.repository.NotificationRepositoryTest;
import com.djt.hvac.domain.model.notification.service.NotificationServiceTest;
import com.djt.hvac.domain.model.payment.PaymentProcessingFunctionalTest;
import com.djt.hvac.domain.model.payment.utils.BuildingSubscriptionTemporalAdjusterTest;
import com.djt.hvac.domain.model.report.dto.ReportInstanceDtoTest;
import com.djt.hvac.domain.model.report.status.PortfolioReportStatusValueObjectTest;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageListResponseTest;
import com.djt.hvac.domain.model.stripe.client.MockStripeClientTest;
import com.djt.hvac.domain.model.timeseries.client.MockTimeSeriesServiceClientTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  CreateNotificationEventOptionsTest.class,
  //GetUserNotificationsResponseTest.class,
  //NotificationEventEntityTest.class,
  UserNotificationEntityTest.class,
  NotificationRepositoryTest.class,
  NotificationServiceTest.class,
  CreateAdFunctionInstancesRequestTest.class,
  AdFunctionTemplatesContainerTest.class,
  NodeTagTemplatesContainerTest.class,
  PaymentPlansContainerTest.class,
  AdFunctionTemplateDtoTest.class,
  ReportTemplateDtoTest.class,
  NodeHierarchyChangeEventTest.class,
  NodeHierarchyServiceTest.class,
  NodeHierarchyAsyncCommandServiceTest.class,
  BillableBuildingPointLimiterTest.class,
  RawPointMappingNodeNameFilterTest.class,
  NodeHierarchyTestDataBuilderTest.class,
  RemediationStrategyTest.class,
  PortfolioVisitorTest.class,
  PortfolioEntityTest.class,
  PaymentProcessingFunctionalTest.class,
  CanadianCustomerFunctionalTest.class,
  ReportInstanceDtoTest.class,
  PortfolioReportStatusValueObjectTest.class,
  ReportEquipmentErrorMessageListResponseTest.class,
  BuildingSubscriptionTemporalAdjusterTest.class,
  MockStripeClientTest.class,
  MockTimeSeriesServiceClientTest.class,
  CustomComputedPointEvaluationTest.class
})

public class ResoluteDomainModelTestSuite {
  
  // the class remains empty,
  // used only as a holder for the above annotations
}