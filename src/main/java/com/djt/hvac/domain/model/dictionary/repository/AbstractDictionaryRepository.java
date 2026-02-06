package com.djt.hvac.domain.model.dictionary.repository;

import com.djt.hvac.domain.model.dictionary.container.AdFunctionTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.PaymentPlansContainer;
import com.djt.hvac.domain.model.dictionary.container.ReportTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.ScheduledEventTypesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.container.UnitsContainer;
import com.djt.hvac.domain.model.dictionary.container.WeatherStationsContainer;

public abstract class AbstractDictionaryRepository implements DictionaryRepository {

  public AbstractDictionaryRepository() {
  }
  
  @Override
  public void ensureDictionaryDataIsLoaded() {
    
    if (DictionaryContext.getTagsContainer() == null) {
      DictionaryContext.setTagsContainer(loadTagsContainer());
    }
    
    if (DictionaryContext.getUnitsContainer() == null) {
      DictionaryContext.setUnitsContainer(loadUnitsContainer());
    }
    
    if (DictionaryContext.getNodeTagTemplatesContainer() == null) {
      DictionaryContext.setNodeTagTemplatesContainer(loadNodeTagTemplatesContainer(
          getTagsContainer(),
          getUnitsContainer()));
    }
    
    if (DictionaryContext.getScheduledEventTypesContainer() == null) {
      DictionaryContext.setScheduledEventTypesContainer(loadScheduledEventTypesContainer());
    }
    
    if (DictionaryContext.getAdFunctionTemplatesContainer() == null) {
      DictionaryContext.setAdFunctionTemplatesContainer(loadAdFunctionTemplatesContainer());
    }
    
    if (DictionaryContext.getReportTemplatesContainer() == null) {
      DictionaryContext.setReportTemplatesContainer(loadReportTemplatesContainer());
    }
    
    if (DictionaryContext.getPaymentPlansContainer() == null) {
      DictionaryContext.setPaymentPlansContainer(loadPaymentPlansContainer());
    }
    
    if (DictionaryContext.getWeatherStationsContainer() == null) {
      DictionaryContext.setWeatherStationsContainer(loadWeatherStationsContainer(getUnitsContainer()));
    }
  }
  
  @Override
  public void invalidateDictionaryData() {
   
    DictionaryContext.setTagsContainer(null);
    DictionaryContext.setUnitsContainer(null);
    DictionaryContext.setNodeTagTemplatesContainer(null);
    DictionaryContext.setScheduledEventTypesContainer(null);
    DictionaryContext.setAdFunctionTemplatesContainer(null);
    DictionaryContext.setReportTemplatesContainer(null);
    DictionaryContext.setPaymentPlansContainer(null);
    DictionaryContext.setWeatherStationsContainer(null);
  }

  @Override
  public TagsContainer getTagsContainer() {
    return DictionaryContext.getTagsContainer();
  }
  
  @Override
  public UnitsContainer getUnitsContainer() {
    return DictionaryContext.getUnitsContainer();
  }

  @Override
  public NodeTagTemplatesContainer getNodeTagTemplatesContainer() {
    return DictionaryContext.getNodeTagTemplatesContainer();
  }
  
  @Override
  public ScheduledEventTypesContainer getScheduledEventTypesContainer() {
    return DictionaryContext.getScheduledEventTypesContainer();
  }

  @Override
  public AdFunctionTemplatesContainer getAdFunctionTemplatesContainer() {
    return DictionaryContext.getAdFunctionTemplatesContainer();
  }

  @Override
  public ReportTemplatesContainer getReportTemplatesContainer() {
    return DictionaryContext.getReportTemplatesContainer();
  }

  @Override
  public PaymentPlansContainer getPaymentPlansContainer() {
    return DictionaryContext.getPaymentPlansContainer();
  }

  @Override
  public WeatherStationsContainer getWeatherStationsContainer() {
    return DictionaryContext.getWeatherStationsContainer();
  }
  
  protected abstract TagsContainer loadTagsContainer();
  protected abstract UnitsContainer loadUnitsContainer();
  protected abstract NodeTagTemplatesContainer loadNodeTagTemplatesContainer(TagsContainer tagsContainer, UnitsContainer unitsContainer);
  protected abstract ScheduledEventTypesContainer loadScheduledEventTypesContainer();
  protected abstract AdFunctionTemplatesContainer loadAdFunctionTemplatesContainer();
  protected abstract ReportTemplatesContainer loadReportTemplatesContainer();
  protected abstract PaymentPlansContainer loadPaymentPlansContainer();
  protected abstract WeatherStationsContainer loadWeatherStationsContainer(UnitsContainer unitsContainer);
}
