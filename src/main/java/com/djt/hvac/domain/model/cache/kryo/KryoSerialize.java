package com.djt.hvac.domain.model.cache.kryo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.dto.EntityIndex;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.CustomerLevelPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.customer.DemoCustomerEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.customer.OutOfBandCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerPaymentStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.ScheduledEventTypeEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.dictionary.enums.AggregatorType;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.PaymentInterval;
import com.djt.hvac.domain.model.dictionary.enums.PointType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.dictionary.enums.TagType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.repository.migrations.AdFunctionTemplateInputConstantPointTemplateMappingJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.AdFunctionTemplateJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.DictionaryDataJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.PointTemplateJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.PointTemplateUnitMappingJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.TagJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.UnitJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.UnitMappingJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantPointTemplateMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateOutputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.computedpoint.AdComputedPointFunctionCategoryEntity;
import com.djt.hvac.domain.model.dictionary.template.function.computedpoint.AdComputedPointFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionEquipmentCategoryEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionSystemCategoryEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateUnitMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.report.AbstractReportTemplatePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEquipmentSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateRulePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateStandardPointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportPriority;
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionModuleEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.AbstractAdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.AdFunctionTemplateInputPointGroupEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.BooleanAdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.DoubleAdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.LongAdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.StringAdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceOutputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.inputpoint.AbstractAdEngineAdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.inputpoint.BooleanAdEngineAdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.inputpoint.DoubleAdEngineAdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.inputpoint.LongAdEngineAdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.inputpoint.StringAdEngineAdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.dictionary.weather.GlobalComputedPointEntity;
import com.djt.hvac.domain.model.dictionary.weather.WeatherStationEntity;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.DistributorLevelPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.OutOfBandDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorPaymentStatus;
import com.djt.hvac.domain.model.distributor.enums.DistributorStatus;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.distributor.enums.PaymentMethodType;
import com.djt.hvac.domain.model.distributor.enums.PaymentTransactionStatus;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.paymentmethod.AchPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.paymentmethod.CreditCardPaymentMethodEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionErrorMessagesEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceInputConstantEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceOutputPointEntity;
import com.djt.hvac.domain.model.function.computedpoint.AdComputedPointFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.rule.AdRuleFunctionInstanceEntity;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.FloorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.SubBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingLevelPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingTemporalConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingTemporalUtilityEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingUtilityType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.DayOfWeek;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.EveryMonth;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnDayQualifier;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnNonDayQualifier;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnQualifier;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OperationType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.UtilityComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.AbstractOnDayNonDayQualifiedScheduledEventEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.AbstractRecurringScheduledEventEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.AbstractScheduledEventEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.DailyScheduledEventEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.MonthlyScheduledEventEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.OneTimeScheduledEventEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.WeeklyScheduledEventEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.YearlyScheduledEventEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.recurrenceexception.AbstractRecurrenceExceptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.recurrenceexception.CancelledRecurrenceExceptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.recurrenceexception.RescheduledRecurrenceExceptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.BuildingCustomAsyncComputedPointState;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.CustomAsyncComputedPointState;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.AbstractEnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.LoopEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.PlantEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.AbstractComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AdFunctionAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ManualAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.SystemAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.WeatherAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.FormulaVariableEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.TemporalAsyncComputedPointConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.ComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.FillPolicy;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.AsyncOperationLockEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.AsyncOperationEntity;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.notification.entity.UserNotificationEntity;
import com.djt.hvac.domain.model.notification.enums.EmailType;
import com.djt.hvac.domain.model.notification.enums.NotificationApplicationType;
import com.djt.hvac.domain.model.notification.enums.NotificationAttentionLevel;
import com.djt.hvac.domain.model.notification.enums.NotificationCategory;
import com.djt.hvac.domain.model.notification.enums.NotificationEventAppType;
import com.djt.hvac.domain.model.notification.enums.NotificationEventType;
import com.djt.hvac.domain.model.notification.enums.NotificationPresentationType;
import com.djt.hvac.domain.model.notification.enums.NotificationProducer;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.referralagent.AbstractReferralAgentEntity;
import com.djt.hvac.domain.model.referralagent.ExternalIndividualReferralAgentEntity;
import com.djt.hvac.domain.model.referralagent.ExternalOrganizationalReferralAgentEntity;
import com.djt.hvac.domain.model.referralagent.InternalIndividualReferralAgentEntity;
import com.djt.hvac.domain.model.referralagent.InternalOrganizationalReferralAgentEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEquipmentEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEquipmentErrorMessagesEntity;
import com.djt.hvac.domain.model.report.ReportInstancePointEntity;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.CustomerUserEntity;
import com.djt.hvac.domain.model.user.DistributorUserEntity;
import com.djt.hvac.domain.model.user.enums.ApplicationType;
import com.djt.hvac.domain.model.user.enums.UserRoleType;
import com.djt.hvac.domain.model.user.enums.UserType;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

public class KryoSerialize implements SerializeByteApi {
  
  public static final int BUFFER_SIZE = 4096; 

  private static KryoSerialize INSTANCE = new KryoSerialize();
  
  public static KryoSerialize getInstance() {
    if (INSTANCE == null) {
      synchronized (KryoSerialize.class) {
        if (INSTANCE == null) {
          INSTANCE = new KryoSerialize();
        }
      }
    }
    return INSTANCE;
  }  

  private final Kryo kryo;
  
  @SuppressWarnings({"rawtypes"})
  private final Map<Class, JavaSerializer> map;
  
  private KryoSerialize() {
    
    map = new HashMap<>();
    
    kryo = new Kryo();
    
    kryo.register(LoadPortfolioOptions.class);
    kryo.register(java.util.ArrayList.class);
    kryo.register(java.util.TreeSet.class);
    kryo.register(java.util.HashSet.class);
    kryo.register(java.util.TreeMap.class);
    kryo.register(java.util.HashMap.class);
    kryo.register(java.util.LinkedHashSet.class);
    kryo.register(java.util.LinkedHashMap.class);
    kryo.register(java.time.LocalDate.class);
    kryo.register(java.time.LocalDateTime.class);
    kryo.register(java.sql.Timestamp.class);
    kryo.register(AbstractAssociativeEntity.class);
    kryo.register(AbstractEntity.class);
    kryo.register(AbstractPersistentEntity.class);
    kryo.register(EntityIndex.class);
    kryo.register(EntityAlreadyExistsException.class);
    kryo.register(EntityDoesNotExistException.class);
    kryo.register(AbstractCustomerEntity.class);
    kryo.register(CustomerLevelPointTemplateUnitMappingOverrideEntity.class);
    kryo.register(DemoCustomerEntity.class);
    kryo.register(OnlineCustomerEntity.class);
    kryo.register(OutOfBandCustomerEntity.class);
    kryo.register(PaymentPlanEntity.class);
    kryo.register(ScheduledEventTypeEntity.class);
    kryo.register(TagEntity.class);
    kryo.register(UnitEntity.class);
    kryo.register(AbstractEnergyExchangeTypeEntity.class);
    kryo.register(EquipmentEnergyExchangeTypeEntity.class);
    kryo.register(LoopEnergyExchangeTypeEntity.class);
    kryo.register(PlantEnergyExchangeTypeEntity.class);
    kryo.register(AdFunctionTemplateInputConstantPointTemplateMappingJsonToEntityMapper.class);
    kryo.register(AdFunctionTemplateJsonToEntityMapper.class);
    kryo.register(DictionaryDataJsonToEntityMapper.class);
    kryo.register(PointTemplateJsonToEntityMapper.class);
    kryo.register(PointTemplateUnitMappingJsonToEntityMapper.class);
    kryo.register(TagJsonToEntityMapper.class);
    kryo.register(UnitJsonToEntityMapper.class);
    kryo.register(UnitMappingJsonToEntityMapper.class);
    kryo.register(AbstractAdFunctionTemplateEntity.class);
    kryo.register(AdFunctionEntity.class);
    kryo.register(AdFunctionTemplateInputConstantEntity.class);
    kryo.register(AdFunctionTemplateInputConstantPointTemplateMappingEntity.class);
    kryo.register(AdFunctionTemplateInputPointEntity.class);
    kryo.register(AdFunctionTemplateOutputPointEntity.class);
    kryo.register(AdComputedPointFunctionCategoryEntity.class);
    kryo.register(AdComputedPointFunctionTemplateEntity.class);
    kryo.register(AdRuleFunctionEquipmentCategoryEntity.class);
    kryo.register(AdRuleFunctionSystemCategoryEntity.class);
    kryo.register(AdRuleFunctionTemplateEntity.class);
    kryo.register(AbstractNodeTagTemplateEntity.class);
    kryo.register(AbstractPointTemplateUnitMappingOverrideEntity.class);
    kryo.register(PointTemplateEntity.class);
    kryo.register(PointTemplateUnitMappingEntity.class);
    kryo.register(UnitMappingEntity.class);
    kryo.register(AbstractReportTemplatePointSpecEntity.class);
    kryo.register(ReportTemplateEntity.class);
    kryo.register(ReportTemplateEquipmentSpecEntity.class);
    kryo.register(ReportTemplateRulePointSpecEntity.class);
    kryo.register(ReportTemplateStandardPointSpecEntity.class);
    kryo.register(AdFunctionModuleEntity.class);
    kryo.register(AdFunctionTemplateEntity.class);
    kryo.register(AdFunctionTemplateInputConstantEntity.class);
    kryo.register(AdFunctionTemplateOutputPointEntity.class);
    kryo.register(AbstractAdFunctionTemplateInputPointEntity.class);
    kryo.register(AdFunctionTemplateInputPointGroupEntity.class);
    kryo.register(BooleanAdFunctionTemplateInputPointEntity.class);
    kryo.register(DoubleAdFunctionTemplateInputPointEntity.class);
    kryo.register(LongAdFunctionTemplateInputPointEntity.class);
    kryo.register(StringAdFunctionTemplateInputPointEntity.class);
    kryo.register(AdEngineAdFunctionInstanceEntity.class);
    kryo.register(AdEngineAdFunctionInstanceInputConstantEntity.class);
    kryo.register(AdEngineAdFunctionInstanceInputPointEntity.class);
    kryo.register(AdEngineAdFunctionInstanceOutputPointEntity.class);
    kryo.register(AbstractAdEngineAdFunctionInstanceInputPointEntity.class);
    kryo.register(BooleanAdEngineAdFunctionInstanceInputPointEntity.class);
    kryo.register(DoubleAdEngineAdFunctionInstanceInputPointEntity.class);
    kryo.register(LongAdEngineAdFunctionInstanceInputPointEntity.class);
    kryo.register(StringAdEngineAdFunctionInstanceInputPointEntity.class);
    kryo.register(GlobalComputedPointEntity.class);
    kryo.register(WeatherStationEntity.class);
    kryo.register(AbstractDistributorEntity.class);
    kryo.register(DistributorLevelPointTemplateUnitMappingOverrideEntity.class);
    kryo.register(OnlineDistributorEntity.class);
    kryo.register(OutOfBandDistributorEntity.class);
    kryo.register(AbstractPaymentMethodEntity.class);
    kryo.register(AchPaymentMethodEntity.class);
    kryo.register(CreditCardPaymentMethodEntity.class);
    kryo.register(AbstractAdFunctionInstanceEntity.class);
    kryo.register(AdFunctionErrorMessagesEntity.class);
    kryo.register(AdFunctionInstanceInputConstantEntity.class);
    kryo.register(AdFunctionInstanceInputPointEntity.class);
    kryo.register(AdFunctionInstanceOutputPointEntity.class);
    kryo.register(AdComputedPointFunctionInstanceEntity.class);
    kryo.register(AdRuleFunctionInstanceEntity.class);
    kryo.register(AbstractNodeEntity.class);
    kryo.register(FloorEntity.class);
    kryo.register(PortfolioEntity.class);
    kryo.register(SubBuildingEntity.class);
    kryo.register(BillableBuildingEntity.class);
    kryo.register(BuildingEntity.class);
    kryo.register(BuildingLevelPointTemplateUnitMappingOverrideEntity.class);
    kryo.register(BuildingSubscriptionEntity.class);
    kryo.register(BuildingTemporalConfigEntity.class);
    kryo.register(BuildingTemporalUtilityEntity.class);
    kryo.register(AbstractOnDayNonDayQualifiedScheduledEventEntity.class);
    kryo.register(AbstractRecurringScheduledEventEntity.class);
    kryo.register(AbstractScheduledEventEntity.class);
    kryo.register(DailyScheduledEventEntity.class);
    kryo.register(MonthlyScheduledEventEntity.class);
    kryo.register(OneTimeScheduledEventEntity.class);
    kryo.register(WeeklyScheduledEventEntity.class);
    kryo.register(YearlyScheduledEventEntity.class);
    kryo.register(AbstractRecurrenceExceptionEntity.class);
    kryo.register(CancelledRecurrenceExceptionEntity.class);
    kryo.register(RescheduledRecurrenceExceptionEntity.class);
    kryo.register(AbstractEnergyExchangeEntity.class);
    kryo.register(EnergyExchangeEntity.class);
    kryo.register(EquipmentEntity.class);
    kryo.register(LoopEntity.class);
    kryo.register(PlantEntity.class);
    kryo.register(AbstractPointEntity.class);
    kryo.register(AbstractComputedPointEntity.class);
    kryo.register(AdFunctionAsyncComputedPointEntity.class);
    kryo.register(AsyncComputedPointEntity.class);
    kryo.register(ManualAsyncComputedPointEntity.class);
    kryo.register(ScheduledAsyncComputedPointEntity.class);
    kryo.register(SystemAsyncComputedPointEntity.class);
    kryo.register(WeatherAsyncComputedPointEntity.class);
    kryo.register(CustomAsyncComputedPointEntity.class);
    kryo.register(FormulaVariableEntity.class);
    kryo.register(TemporalAsyncComputedPointConfigEntity.class);
    kryo.register(MappablePointEntity.class);
    kryo.register(AsyncOperationLockEntity.class);
    kryo.register(AsyncOperationEntity.class);
    kryo.register(NotificationEventEntity.class);
    kryo.register(UserNotificationEntity.class);
    kryo.register(RawPointEntity.class);
    kryo.register(AbstractReferralAgentEntity.class);
    kryo.register(ExternalIndividualReferralAgentEntity.class);
    kryo.register(ExternalOrganizationalReferralAgentEntity.class);
    kryo.register(InternalIndividualReferralAgentEntity.class);
    kryo.register(InternalOrganizationalReferralAgentEntity.class);
    kryo.register(ReportInstanceEntity.class);
    kryo.register(ReportInstanceEquipmentEntity.class);
    kryo.register(ReportInstanceEquipmentErrorMessagesEntity.class);
    kryo.register(ReportInstancePointEntity.class);
    kryo.register(AbstractUserEntity.class);
    kryo.register(CustomerUserEntity.class);
    kryo.register(DistributorUserEntity.class);
    kryo.register(CustomerPaymentStatus.class);
    kryo.register(CustomerStatus.class);
    kryo.register(CustomerType.class);
    kryo.register(AggregatorType.class);
    kryo.register(DataType.class);
    kryo.register(FunctionType.class);
    kryo.register(NodeSubType.class);
    kryo.register(NodeType.class);
    kryo.register(PaymentInterval.class);
    kryo.register(PointType.class);
    kryo.register(TagGroupType.class);
    kryo.register(TagType.class);
    kryo.register(UnitSystem.class);
    kryo.register(DistributorPaymentStatus.class);
    kryo.register(DistributorStatus.class);
    kryo.register(DistributorType.class);
    kryo.register(PaymentMethodType.class);
    kryo.register(PaymentTransactionStatus.class);
    kryo.register(BuildingPaymentStatus.class);
    kryo.register(BuildingPaymentType.class);
    kryo.register(BuildingStatus.class);
    kryo.register(BuildingUtilityType.class);
    kryo.register(DayOfWeek.class);
    kryo.register(EveryMonth.class);
    kryo.register(OnDayQualifier.class);
    kryo.register(OnNonDayQualifier.class);
    kryo.register(OnQualifier.class);
    kryo.register(OperationType.class);
    kryo.register(UtilityComputationInterval.class);
    kryo.register(EmailType.class);
    kryo.register(NotificationApplicationType.class);
    kryo.register(NotificationAttentionLevel.class);
    kryo.register(NotificationCategory.class);
    kryo.register(NotificationEventAppType.class);
    kryo.register(NotificationEventType.class);
    kryo.register(NotificationPresentationType.class);
    kryo.register(NotificationProducer.class);
    kryo.register(ReportPriority.class);
    kryo.register(ApplicationType.class);
    kryo.register(UserRoleType.class);
    kryo.register(UserType.class);
    kryo.register(ComputationInterval.class);
    kryo.register(FillPolicy.class);
    kryo.register(EnergyExchangeSystemType.class);
    kryo.register(CustomAsyncComputedPointState.class);
    kryo.register(BuildingCustomAsyncComputedPointState.class);
  }

  @Override
  public byte[] encode(Object t) {
    
    kryo.setReferences(false);
    
    @SuppressWarnings("rawtypes")
    Class clazz = t.getClass();
    JavaSerializer javaSerializer = getJavaSerializer(t);
    kryo.register(clazz, javaSerializer);
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    Output output = null;
    OutputStream outputStream = null;
    
    try {
      
      outputStream = new GZIPOutputStream(new BufferedOutputStream(baos, BUFFER_SIZE));
      
      output = new Output(outputStream);
      
      kryo.writeClassAndObject(output, t);
      output.flush();
      output.close();
      
      return baos.toByteArray();
      
    } catch (IOException e) {
      throw new RuntimeException("Unable to encode with compression: " + t, e);
    } finally {
      try {
        baos.flush();
        baos.close();
      } catch (IOException e) {
      }
    }
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public <T> T decode(byte[] bytes, Class<T> clazz) {
    
    kryo.setReferences(false);
    
    JavaSerializer javaSerializer = getJavaSerializer(clazz);
    kryo.register(clazz, javaSerializer);

    InputStream inputStream = null;
    Input input = null;
    
    try {
      
      inputStream = new GZIPInputStream(new BufferedInputStream(new ByteArrayInputStream(bytes), BUFFER_SIZE));
      
      input = new Input(inputStream);
      
      return (T)kryo.readClassAndObject(input);
      
    } catch (IOException e) {
      throw new RuntimeException("Unable to decode with compression", e);
    } finally {
      if (input != null) {
        input.close();  
      }
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
        }
      }
    }
  }
  
  private JavaSerializer getJavaSerializer(Object t) {
    
    @SuppressWarnings("rawtypes")
    Class clazz = t.getClass();
    JavaSerializer javaSerializer = map.get(clazz);
    if (javaSerializer == null) {
      javaSerializer = new JavaSerializer();
      map.put(clazz, new JavaSerializer());
    }
    kryo.register(clazz, javaSerializer);
    
    return javaSerializer;
  }
}