package com.djt.hvac.domain.model.dictionary.container;

public class DictionaryContext {

  private static UnitsContainer UNITS_CONTAINER;
  private static NodeTagTemplatesContainer NODE_TAG_TEMPLATES_CONTAINER;
  private static TagsContainer TAGS_CONTAINER;
  private static ScheduledEventTypesContainer SCHEDULED_EVENT_TYPES_CONTAINER;
  private static AdFunctionTemplatesContainer AD_FUNCTION_TEMPLATES_CONTAINER;
  private static ReportTemplatesContainer REPORT_TEMPLATES_CONTAINER;
  private static PaymentPlansContainer PAYMENT_PLANS_CONTAINER;
  private static WeatherStationsContainer WEATHER_STATIONS_CONTAINER;

  public static UnitsContainer getUnitsContainer() {
    return UNITS_CONTAINER;
  }
  
  public static NodeTagTemplatesContainer getNodeTagTemplatesContainer() {
    return NODE_TAG_TEMPLATES_CONTAINER;
  }
  
  public static TagsContainer getTagsContainer() {
    return TAGS_CONTAINER;
  }

  public static ScheduledEventTypesContainer getScheduledEventTypesContainer() {
    return SCHEDULED_EVENT_TYPES_CONTAINER;
  }
  
  public static AdFunctionTemplatesContainer getAdFunctionTemplatesContainer() {
    return AD_FUNCTION_TEMPLATES_CONTAINER;
  }

  public static ReportTemplatesContainer getReportTemplatesContainer() {
    return REPORT_TEMPLATES_CONTAINER;
  }
  
  public static PaymentPlansContainer getPaymentPlansContainer() {
    return PAYMENT_PLANS_CONTAINER;
  }
  
  public static WeatherStationsContainer getWeatherStationsContainer() {
    return WEATHER_STATIONS_CONTAINER;
  }
  
  public static void setUnitsContainer(UnitsContainer unitsContainer) {
    UNITS_CONTAINER = unitsContainer;
  }

  public static void setNodeTagTemplatesContainer(NodeTagTemplatesContainer nodeTagTemplatesContainer) {
    NODE_TAG_TEMPLATES_CONTAINER = nodeTagTemplatesContainer;
  }
  
  public static void setTagsContainer(TagsContainer tagsContainer) {
    TAGS_CONTAINER = tagsContainer;
  }

  public static void setScheduledEventTypesContainer(ScheduledEventTypesContainer scheduledEventTypesContainer) {
    SCHEDULED_EVENT_TYPES_CONTAINER = scheduledEventTypesContainer;
  }

  public static void setAdFunctionTemplatesContainer(AdFunctionTemplatesContainer adFunctionTemplatesContainer) {
    AD_FUNCTION_TEMPLATES_CONTAINER = adFunctionTemplatesContainer;
  }

  public static void setReportTemplatesContainer(ReportTemplatesContainer reportTemplatesContainer) {
    REPORT_TEMPLATES_CONTAINER = reportTemplatesContainer;
  }
 
  public static void setPaymentPlansContainer(PaymentPlansContainer paymentPlansContainer) {
    PAYMENT_PLANS_CONTAINER = paymentPlansContainer;
  }
  
  public static void setWeatherStationsContainer(WeatherStationsContainer weatherStationsContainer) {
    WEATHER_STATIONS_CONTAINER = weatherStationsContainer;
  }
}