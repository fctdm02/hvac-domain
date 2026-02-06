//@formatter:off
// This class loads the dictionary data from the file system (dev/test only)
package com.djt.hvac.domain.model.dictionary.repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.AdFunctionTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.PaymentPlansContainer;
import com.djt.hvac.domain.model.dictionary.container.ReportTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.ScheduledEventTypesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.container.UnitsContainer;
import com.djt.hvac.domain.model.dictionary.container.WeatherStationsContainer;
import com.djt.hvac.domain.model.dictionary.dto.AdFunctionTemplateInputConstantPointTemplateMappingDto;
import com.djt.hvac.domain.model.dictionary.dto.IdNameDto;
import com.djt.hvac.domain.model.dictionary.dto.NodeTagTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.PaymentPlanDto;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateUnitMappingDto;
import com.djt.hvac.domain.model.dictionary.dto.ScheduledEventTypeDto;
import com.djt.hvac.domain.model.dictionary.dto.TagDto;
import com.djt.hvac.domain.model.dictionary.dto.UnitMappingDto;
import com.djt.hvac.domain.model.dictionary.dto.function.DatabaseWrapperDto;
import com.djt.hvac.domain.model.dictionary.dto.report.ReportTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.weather.WeatherStationDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.service.command.MigrationRequest;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;
import com.djt.hvac.domain.model.dictionary.weather.WeatherStationEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class DictionaryRepositoryFileSystemImpl extends AbstractDictionaryRepository {

  private static boolean USE_PRETTY_PRINT = true;
  public static boolean getPrettyPrint() {
    return USE_PRETTY_PRINT;
  }
  public static void setPrettyPrint(boolean prettyPrint) {
    USE_PRETTY_PRINT = prettyPrint;
  }

  private String basePath;

  public DictionaryRepositoryFileSystemImpl() {
    this(null);
  }

  public DictionaryRepositoryFileSystemImpl(String basePath) {

    super();
    if (basePath != null) {
      this.basePath = basePath;
    } else {
      this.basePath = System.getProperty("user.home") + "/";
    }
    ensureDictionaryDataIsLoaded();
  }
  
  public String basePath() {
    return basePath;
  }
  
  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }
    
  public void saveDictionaryDataToFilesystem() {

    try {
      
      storeTags(DictionaryContext.getTagsContainer());
      storeUnits(DictionaryContext.getUnitsContainer());
      storeNodeTagTemplates(DictionaryContext.getNodeTagTemplatesContainer());
      storeScheduledEventTypes(DictionaryContext.getScheduledEventTypesContainer());
      storeAdFunctionTemplates(DictionaryContext.getAdFunctionTemplatesContainer());
      storeReportTemplates(DictionaryContext.getReportTemplatesContainer());
      storePaymentPlans(DictionaryContext.getPaymentPlansContainer());
      storeWeatherStations(DictionaryContext.getWeatherStationsContainer());
      
    } catch (Exception e) {
      throw new RuntimeException("Unable to store dictionary data", e);
    }
  }

  @Override
  protected TagsContainer loadTagsContainer() {

    File file = new File(basePath + "/TagList.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        List<TagDto> tagDtoList =
            AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<TagDto>>() {});
        TagsContainer tagsContainer = TagsContainer.mapFromDtos(tagDtoList);
        return tagsContainer;

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return TagsContainer.mapFromDtos(new ArrayList<>());
  }

  private void storeTags(TagsContainer tagsContainer) {

    File file = new File(basePath + "/TagList.json");
    OutputStream out = null;
    try {

      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, TagsContainer.mapToDtos(tagsContainer));

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  @Override
  protected UnitsContainer loadUnitsContainer() {

    File file = new File(basePath + "/UnitList.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        List<IdNameDto> idNameDtoList =
            AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<IdNameDto>>() {});
        UnitsContainer unitsContainer = UnitsContainer.mapFromDtos(idNameDtoList);
        return unitsContainer;

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return UnitsContainer.mapFromDtos(new ArrayList<>());
  }

  private void storeUnits(UnitsContainer unitsContainer) {

    File file = new File(basePath + "/UnitList.json");
    OutputStream out = null;
    try {

      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, UnitsContainer.mapToDtos(unitsContainer));

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  @Override
  protected NodeTagTemplatesContainer loadNodeTagTemplatesContainer(
      TagsContainer tagsContainer,
      UnitsContainer unitsContainer) {

    List<UnitMappingDto> unitMappingDtoList = loadUnitMappings();

    List<PointTemplateUnitMappingDto> pointTemplateUnitMappingDtoList = loadPointTemplateUnitMappings();
    
    File file = new File(basePath + "/NodeTagTemplateList.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        List<NodeTagTemplateDto> dtoList = AbstractEntity.OBJECT_MAPPER.get()
            .readValue(compactJson, new TypeReference<List<NodeTagTemplateDto>>() {});
        
        NodeTagTemplatesContainer container = NodeTagTemplatesContainer.mapFromDtos(
            tagsContainer,
            unitsContainer,
            dtoList,
            unitMappingDtoList,
            pointTemplateUnitMappingDtoList);
        
        return container;

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    
    return NodeTagTemplatesContainer.mapFromDtos(
        tagsContainer,
        unitsContainer, 
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>());
  }
  
  private List<UnitMappingDto> loadUnitMappings() {
    
    List<UnitMappingDto> unitMappingDtoList = null;
    File unitMappingsFile = new File(basePath + "/UnitMappings.json");
    if (unitMappingsFile.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(unitMappingsFile));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        unitMappingDtoList = AbstractEntity.OBJECT_MAPPER.get()
            .readValue(compactJson, new TypeReference<List<UnitMappingDto>>() {});
        
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      unitMappingDtoList = new ArrayList<>();
    }
    return unitMappingDtoList;
  }
  
  private void storeUnitMappings(List<UnitMappingDto> unitMappingDtoList) {
    
    File unitMappingsFile = new File(basePath + "/UnitMappings.json");
    OutputStream unitMappingsOs = null;
    try {

      unitMappingsOs = new BufferedOutputStream(new FileOutputStream(unitMappingsFile));
      AbstractEntity.OBJECT_WRITER.get().writeValue(unitMappingsOs, unitMappingDtoList);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (unitMappingsOs != null) {
        try {
          unitMappingsOs.flush();
          unitMappingsOs.close();
        } catch (IOException ioe) {
        }
      }
    }    
  }

  private List<PointTemplateUnitMappingDto> loadPointTemplateUnitMappings() {
    
    List<PointTemplateUnitMappingDto> pointTemplateUnitMappingDtoList = null;
    File pointTemplateUnitMappingsFile = new File(basePath + "/PointTemplateUnitMappings.json");
    if (pointTemplateUnitMappingsFile.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(pointTemplateUnitMappingsFile));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        pointTemplateUnitMappingDtoList = AbstractEntity.OBJECT_MAPPER.get()
            .readValue(compactJson, new TypeReference<List<PointTemplateUnitMappingDto>>() {});
        
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      pointTemplateUnitMappingDtoList = new ArrayList<>();
    }
    return pointTemplateUnitMappingDtoList;
  }
  
  private void storePointTemplateUnitMappings(List<PointTemplateUnitMappingDto> pointTemplateUnitMappingDtoList) {
    
    File pointTemplateUnitMappingsFile = new File(basePath + "/PointTemplateUnitMappings.json");
    OutputStream pointTemplateUnitMappingsOs = null;
    try {

      pointTemplateUnitMappingsOs = new BufferedOutputStream(new FileOutputStream(pointTemplateUnitMappingsFile));
      AbstractEntity.OBJECT_WRITER.get().writeValue(pointTemplateUnitMappingsOs, pointTemplateUnitMappingDtoList);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (pointTemplateUnitMappingsOs != null) {
        try {
          pointTemplateUnitMappingsOs.flush();
          pointTemplateUnitMappingsOs.close();
        } catch (IOException ioe) {
        }
      }
    }   
  }
  
  private List<AdFunctionTemplateInputConstantPointTemplateMappingDto> loadInputConstantPointTemplateMappings() {
    
    List<AdFunctionTemplateInputConstantPointTemplateMappingDto> inputConstantPointTemplateMappingsDtoList = null;
    File unitMappingsFile = new File(basePath + "/AdFunctionTemplateInputConstantPointTemplateMappings.json");
    if (unitMappingsFile.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(unitMappingsFile));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        inputConstantPointTemplateMappingsDtoList = AbstractEntity.OBJECT_MAPPER.get()
            .readValue(compactJson, new TypeReference<List<AdFunctionTemplateInputConstantPointTemplateMappingDto>>() {});
        
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      inputConstantPointTemplateMappingsDtoList = new ArrayList<>();
    }
    return inputConstantPointTemplateMappingsDtoList;
  }
  
  private void storeInputConstantPointTemplateMappings(List<AdFunctionTemplateInputConstantPointTemplateMappingDto> inputConstantPointTemplateMappingsDtoList) {
    
    File adFunctionTemplateInputConstantPointTemplateMappingsFile = new File(basePath + "/AdFunctionTemplateInputConstantPointTemplateMappings.json");
    OutputStream adFunctionTemplateInputConstantPointTemplateMappingsOs = null;
    try {

      adFunctionTemplateInputConstantPointTemplateMappingsOs = new BufferedOutputStream(new FileOutputStream(adFunctionTemplateInputConstantPointTemplateMappingsFile));
      AbstractEntity.OBJECT_WRITER.get().writeValue(adFunctionTemplateInputConstantPointTemplateMappingsOs, inputConstantPointTemplateMappingsDtoList);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (adFunctionTemplateInputConstantPointTemplateMappingsOs != null) {
        try {
          adFunctionTemplateInputConstantPointTemplateMappingsOs.flush();
          adFunctionTemplateInputConstantPointTemplateMappingsOs.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  private void storeNodeTagTemplates(NodeTagTemplatesContainer container) {
    
    Map<String, Object> map = NodeTagTemplatesContainer.mapToDtos(container);

    @SuppressWarnings("unchecked")
    List<UnitMappingDto> unitMappingDtoList = (List<UnitMappingDto>)map.get("unitMappings");
    storeUnitMappings(unitMappingDtoList);
    
    @SuppressWarnings("unchecked")
    List<PointTemplateUnitMappingDto> pointTemplateUnitMappingDtoList = (List<PointTemplateUnitMappingDto>)map.get("pointTemplateUnitMappings");
    storePointTemplateUnitMappings(pointTemplateUnitMappingDtoList);
    
    @SuppressWarnings("unchecked")
    List<NodeTagTemplateDto> pointTemplatesDtoList = (List<NodeTagTemplateDto>)map.get("pointTemplates");

    File file = new File(basePath + "/NodeTagTemplateList.json");
    OutputStream out = null;
    try {

      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, pointTemplatesDtoList);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  @Override
  protected ScheduledEventTypesContainer loadScheduledEventTypesContainer() {

    File file = new File(basePath + "/ScheduledEventTypeList.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        List<ScheduledEventTypeDto> dtos =
            AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<ScheduledEventTypeDto>>() {});
        ScheduledEventTypesContainer scheduledEventTypesContainer =
            ScheduledEventTypesContainer.mapFromDtos(dtos);
        return scheduledEventTypesContainer;

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return ScheduledEventTypesContainer.mapFromDtos(new ArrayList<>());
  }

  private void storeScheduledEventTypes(ScheduledEventTypesContainer scheduledEventTypesContainer) {

    File file = new File(basePath + "/ScheduledEventTypeList.json");
    OutputStream out = null;
    try {

      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out,
          ScheduledEventTypesContainer.mapToDtos(scheduledEventTypesContainer));

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }

  @Override
  protected AdFunctionTemplatesContainer loadAdFunctionTemplatesContainer() {

    List<AdFunctionTemplateInputConstantPointTemplateMappingDto> inputConstantPointTemplateMappingsDtoList = loadInputConstantPointTemplateMappings();
    
    File file = new File(basePath + "/FunctionTemplateList.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        List<DatabaseWrapperDto> dtoList =
            AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<DatabaseWrapperDto>>() {});

        return AdFunctionTemplatesContainer.mapFromDtos(dtoList, inputConstantPointTemplateMappingsDtoList);

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return AdFunctionTemplatesContainer.mapFromDtos(new ArrayList<>(), new ArrayList<>());
  }

  private void storeAdFunctionTemplates(AdFunctionTemplatesContainer container) {

    Map<String, Object> map = AdFunctionTemplatesContainer.mapToDtos(container);

    @SuppressWarnings("unchecked")
    List<AdFunctionTemplateInputConstantPointTemplateMappingDto> inputConstantPointTemplateMappingsDtoList = (List<AdFunctionTemplateInputConstantPointTemplateMappingDto>)map.get("adFunctionTemplateInputConstantPointTemplateMappings");
    storeInputConstantPointTemplateMappings(inputConstantPointTemplateMappingsDtoList);

    @SuppressWarnings("unchecked")
    List<DatabaseWrapperDto> adFunctionTemplatesDtoList = (List<DatabaseWrapperDto>)map.get("adFunctionTemplates");

    File file = new File(basePath + "/FunctionTemplateList.json");
    OutputStream out = null;
    try {

      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, adFunctionTemplatesDtoList);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  @Override
  protected ReportTemplatesContainer loadReportTemplatesContainer() {

    File file = new File(basePath + "/ReportTemplateList.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        List<ReportTemplateDto> dtoList =
            AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<ReportTemplateDto>>() {});

        return ReportTemplatesContainer.mapFromDtos(dtoList);

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return ReportTemplatesContainer.mapFromDtos(new ArrayList<>());
  }

  private void storeReportTemplates(ReportTemplatesContainer reportTemplatesContainer) {

    File file = new File(basePath + "/ReportTemplateList.json");
    OutputStream out = null;
    try {

      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out,
          ReportTemplatesContainer.mapToDtos(reportTemplatesContainer));

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  @Override
  protected PaymentPlansContainer loadPaymentPlansContainer() {

    File file = new File(basePath + "/PaymentPlanList.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        List<PaymentPlanDto> dtoList =
            AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<PaymentPlanDto>>() {});

        return PaymentPlansContainer.mapFromDtos(dtoList);

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new PaymentPlansContainer(new ArrayList<>());
  }
  
  private void storePaymentPlans(PaymentPlansContainer paymentPlansContainer) {

    File file = new File(basePath + "/PaymentPlanList.json");
    OutputStream out = null;
    try {

      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out,
          PaymentPlansContainer.mapToDtos(paymentPlansContainer));

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  @Override
  protected WeatherStationsContainer loadWeatherStationsContainer(UnitsContainer unitsContainer) {

    File file = new File(basePath + "/WeatherStationList.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));

          Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String fullJson = s.hasNext() ? s.next() : "";

        JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
        String compactJson = jsonNode.toString();

        List<WeatherStationDto> dtoList =
            AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<WeatherStationDto>>() {});

        return WeatherStationsContainer.mapFromDtos(unitsContainer, dtoList);

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return WeatherStationsContainer.mapFromDtos(unitsContainer, new ArrayList<>());
  }
  
  private void storeWeatherStations(WeatherStationsContainer weatherStationsContainer) {

    File file = new File(basePath + "/WeatherStationList.json");
    OutputStream out = null;
    try {

      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out,
          WeatherStationsContainer.mapToDtos(weatherStationsContainer));

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  } 
  
  @Override
  public int storeNewlyCreatedWeatherStationGlobalPoints(Integer weatherStationId, Integer customerId, String buildingUnitSystem, String submittedBy) throws EntityDoesNotExistException {
    
    int numPoints = 0;
    WeatherStationEntity weatherStation = getWeatherStationsContainer().getWeatherStationById(weatherStationId);
    if (weatherStation.getIsModified()) {
      
      numPoints = numPoints + 3;
      weatherStation.setNotModified();
    }
    
    storeWeatherStations(getWeatherStationsContainer());
    
    return numPoints;
  }
  
  @Override
  public void updateAdFunctionTemplateVersion(Integer adFunctionTemplateId, Integer version) {
    
    AdFunctionTemplatesContainer adFunctionTemplatesContainer = getAdFunctionTemplatesContainer();
    
    AbstractAdFunctionTemplateEntity adFunctionTemplate = adFunctionTemplatesContainer.getAdFunctionTemplate(adFunctionTemplateId);
    adFunctionTemplate.setVersion(version);
    
    storeAdFunctionTemplates(adFunctionTemplatesContainer);
  }
  
  @Override
  public void updatePointTemplate(
      Integer pointTemplateId,
      String name,
      String description,
      Boolean isPublic,
      Set<TagEntity> tags,
      Integer unitId,
      Boolean isDeprecated,
      Integer replacementPointTemplateId,
      Set<EquipmentEnergyExchangeTypeEntity> parentEquipmentTypes,
      Set<PlantEnergyExchangeTypeEntity> parentPlantTypes,
      Set<LoopEnergyExchangeTypeEntity> parentLoopTypes)
  throws 
      EntityDoesNotExistException {
    
    NodeTagTemplatesContainer nodeTagTemplatesContainer = getNodeTagTemplatesContainer();
    
    AbstractNodeTagTemplateEntity pointTemplate = nodeTagTemplatesContainer.getPointTemplate(pointTemplateId);
    
    pointTemplate.setName(name);
    pointTemplate.setDescription(description);
    pointTemplate.setIsPublic(isPublic);
    pointTemplate.setTags(tags);
    
    UnitEntity unit = null;
    if (unitId != null && pointTemplate instanceof PointTemplateEntity) {
    
      UnitsContainer unitsContainer = getUnitsContainer();
      unit = unitsContainer.getUnit(unitId);
      ((PointTemplateEntity)pointTemplate).setUnit(unit);
    }
    
    pointTemplate.setIsDeprecated(isDeprecated);
    
    if (replacementPointTemplateId != null) {
    
      // Verify the replacement point template exists.
      nodeTagTemplatesContainer.getPointTemplate(replacementPointTemplateId);
      
      pointTemplate.setReplacementPointTemplateId(replacementPointTemplateId);
    }
    
    if (pointTemplate instanceof PointTemplateEntity && parentEquipmentTypes != null && parentPlantTypes != null && parentLoopTypes != null) {
      
      ((PointTemplateEntity)pointTemplate).setParentEnergyExchangeTypes(parentEquipmentTypes, parentPlantTypes, parentLoopTypes);
    }
    
    storeNodeTagTemplates(nodeTagTemplatesContainer);
  }  
  
  @Override
  public UnitMappingDto createUnitMapping(
      UnitEntity ipUnit,
      UnitEntity siUnit,
      String ipToSiConversionFactor,
      String siToIpConversionFactor) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    List<UnitMappingDto> unitMappingDtoList = loadUnitMappings();
    
    UnitMappingDto dto = new UnitMappingDto();
    dto.setId(unitMappingDtoList.size());
    dto.setIpUnitId(ipUnit.getPersistentIdentity());
    dto.setSiUnitId(siUnit.getPersistentIdentity());
    dto.setIpToSiConversionFactor(ipToSiConversionFactor);
    dto.setSiToIpConversionFactor(siToIpConversionFactor);
    unitMappingDtoList.add(dto);
    
    storeUnitMappings(unitMappingDtoList);
    
    return dto;
  }
  
  @Override
  public PointTemplateUnitMappingDto createPointTemplateUnitMapping(
      PointTemplateEntity pointTemplate,
      UnitMappingEntity unitMapping,
      Integer priority) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    List<PointTemplateUnitMappingDto> pointTemplateUnitMappingDtoList = loadPointTemplateUnitMappings();
    
    PointTemplateUnitMappingDto dto = new PointTemplateUnitMappingDto();
    dto.setPointTemplateId(pointTemplate.getPersistentIdentity());
    dto.setUnitMappingId(unitMapping.getPersistentIdentity());
    dto.setPriority(priority);
    pointTemplateUnitMappingDtoList.add(dto);
    
    storePointTemplateUnitMappings(pointTemplateUnitMappingDtoList);
    
    return dto;
  }

  @Override
  public AdFunctionTemplateInputConstantPointTemplateMappingDto createAdFunctionTemplateInputConstantPointTemplateMapping(
      Integer adFunctionTemplateInputConstantId,
      Integer pointTemplateId) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    List<AdFunctionTemplateInputConstantPointTemplateMappingDto> inputConstantPointTemplateMappingsDtoList = loadInputConstantPointTemplateMappings();
    
    AdFunctionTemplateInputConstantPointTemplateMappingDto dto = new AdFunctionTemplateInputConstantPointTemplateMappingDto();
    dto.setAdFunctionTemplateInputConstId(adFunctionTemplateInputConstantId);
    dto.setPointTemplateId(pointTemplateId.toString());
    inputConstantPointTemplateMappingsDtoList.add(dto);
    
    storeInputConstantPointTemplateMappings(inputConstantPointTemplateMappingsDtoList);
    
    return dto;    
  }
  
  @Override
  public List<DatabaseWrapperDto> getRuleManagerAppRuleDefinitions(MigrationRequest request, boolean retrieveImplementedOnly) {
    throw new IllegalStateException("This method is not supported by this implementation.");
  }
}
//@formatter:on