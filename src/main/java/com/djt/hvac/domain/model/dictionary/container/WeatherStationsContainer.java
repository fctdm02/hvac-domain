package com.djt.hvac.domain.model.dictionary.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.dto.weather.WeatherStationDto;
import com.djt.hvac.domain.model.dictionary.weather.GlobalComputedPointEntity;
import com.djt.hvac.domain.model.dictionary.weather.WeatherStationEntity;

public class WeatherStationsContainer {

  private final Map<Integer, WeatherStationEntity> weatherStationsById;
  private final Map<String, WeatherStationEntity> weatherStationsByCode;
  private final Map<Integer, GlobalComputedPointEntity> globalComputedPointsById;
  
  public WeatherStationsContainer() {
    this(new HashMap<>());
  }

  public WeatherStationsContainer(
      Map<Integer, WeatherStationEntity> weatherStationsById) {
    super();
    this.weatherStationsById = weatherStationsById;
    this.weatherStationsByCode = new HashMap<>();
    this.globalComputedPointsById = new HashMap<>();
    for (WeatherStationEntity weatherStation: weatherStationsById.values()) {
      
      weatherStationsByCode.put(weatherStation.getCode(), weatherStation);
      
      GlobalComputedPointEntity oat = weatherStation.getOatCurrentGlobalPoint();
      if (oat != null) {
        globalComputedPointsById.put(oat.getPersistentIdentity(), oat);
      }
      
      GlobalComputedPointEntity rh = weatherStation.getRhCurrentGlobalPoint();
      if (rh != null) {
        globalComputedPointsById.put(rh.getPersistentIdentity(), rh);
      }
    }
  }
  
  public Set<WeatherStationEntity> getWeatherStations() {
    
    Set<WeatherStationEntity> set = new TreeSet<>();
    set.addAll(weatherStationsById.values());
    return set;
  }
  
  public WeatherStationEntity getWeatherStationById(Integer weatherStationId) throws EntityDoesNotExistException {
    
    WeatherStationEntity weatherStation = getWeatherStationByIdNullIfNotExists(weatherStationId);
    if (weatherStation == null) {
      
      throw new EntityDoesNotExistException("Weather station with id: ["
          + weatherStationId
          + "] does not exist");
      
    }
    return weatherStation;
  }

  public WeatherStationEntity getWeatherStationByIdNullIfNotExists(Integer weatherStationId) {
    
    return weatherStationsById.get(weatherStationId);
  }
  
  public WeatherStationEntity getWeatherStationByCode(String code) throws EntityDoesNotExistException {
    
    WeatherStationEntity weatherStation = weatherStationsByCode.get(code);
    if (weatherStation == null) {
      
      throw new EntityDoesNotExistException("Weather station with code: ["
          + code
          + "] does not exist");
      
    }
    return weatherStation;
  }
  
  public GlobalComputedPointEntity getGlobalComputedPointById(Integer globalComputedPointId) {
   
    return globalComputedPointsById.get(globalComputedPointId);
  }
  
  public void addWeatherStation(WeatherStationEntity weatherStation) {
    
    weatherStationsById.put(weatherStation.getPersistentIdentity(), weatherStation);
    weatherStationsByCode.put(weatherStation.getCode(), weatherStation);
    
    GlobalComputedPointEntity oat = weatherStation.getOatCurrentGlobalPoint();
    if (oat != null) {
      globalComputedPointsById.put(oat.getPersistentIdentity(), oat);
    }
    
    GlobalComputedPointEntity rh = weatherStation.getRhCurrentGlobalPoint();
    if (rh != null) {
      globalComputedPointsById.put(rh.getPersistentIdentity(), rh);
    }
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("WeatherStationsContainer [weatherStations=")
        .append(weatherStationsById.values())
        .append("]")
        .toString();
  }

  public static WeatherStationsContainer mapFromDtos(UnitsContainer unitsContainer, List<WeatherStationDto> dtos) {
    
    Map<Integer, WeatherStationEntity> map = new HashMap<>(); 
    Iterator<WeatherStationDto> iterator = dtos.iterator();
    while (iterator.hasNext()) {
      
      WeatherStationDto dto = iterator.next();
      
      GlobalComputedPointEntity oatCurrentGlobalPoint = null;
      Integer oatId = dto.getOatId();
      if (oatId != null && oatId.intValue() > 0) {

        oatCurrentGlobalPoint = new GlobalComputedPointEntity(
            oatId,
            dto.getOatName(),
            dto.getOatDisplayName(),
            dto.getOatDescription(),
            unitsContainer.getUnit(dto.getOatUnitId()),
            dto.getOatMetricId(),
            AbstractEntity.parseTimestamp(dto.getOatEarliestProcessedAt()),
            AbstractEntity.parseTimestamp(dto.getOatLastProcessedAt()));
      }

      GlobalComputedPointEntity oatCurrentSiGlobalPoint = null;
      Integer oatSiId = dto.getOatSiId();
      if (oatSiId != null && oatSiId.intValue() > 0) {

        oatCurrentSiGlobalPoint = new GlobalComputedPointEntity(
            oatSiId,
            dto.getOatSiName(),
            dto.getOatSiDisplayName(),
            dto.getOatSiDescription(),
            unitsContainer.getUnit(dto.getOatSiUnitId()),
            dto.getOatSiMetricId(),
            AbstractEntity.parseTimestamp(dto.getOatSiEarliestProcessedAt()),
            AbstractEntity.parseTimestamp(dto.getOatSiLastProcessedAt()));
      }
      
      GlobalComputedPointEntity rhCurrentGlobalPoint = null;
      Integer rhId = dto.getRhId();
      if (rhId != null && rhId.intValue() > 0) {

        rhCurrentGlobalPoint = new GlobalComputedPointEntity(
            rhId,
            dto.getRhName(),
            dto.getRhDisplayName(),
            dto.getRhDescription(),
            unitsContainer.getUnit(dto.getRhUnitId()),
            dto.getRhMetricId(),
            AbstractEntity.parseTimestamp(dto.getRhEarliestProcessedAt()),
            AbstractEntity.parseTimestamp(dto.getRhLastProcessedAt()));
      }
      
      Integer id = dto.getWsId();
      String timezone = dto.getTimezone();
      if (timezone == null) {
        timezone = WeatherStationEntity.DEFAULT_TIME_ZONE;
      }
      
      WeatherStationEntity entity = new WeatherStationEntity(
          id,
          dto.getCode(),
          dto.getCity(),
          dto.getStateOrProvince(),
          dto.getCountryCode(),
          timezone,
          dto.getLatitude(),
          dto.getLongitude(),
          oatCurrentGlobalPoint,
          oatCurrentSiGlobalPoint,
          rhCurrentGlobalPoint);
      
      map.put(id, entity);
    }
    WeatherStationsContainer weatherStationsContainer = new WeatherStationsContainer(map);
    return weatherStationsContainer;
  }
  
  public static List<WeatherStationDto> mapToDtos(WeatherStationsContainer weatherStationsContainer) {
    
    List<WeatherStationDto> dtos = new ArrayList<>();
    Iterator<WeatherStationEntity> iterator = weatherStationsContainer.getWeatherStations().iterator();
    while (iterator.hasNext()) {
      
      WeatherStationEntity e = iterator.next();
      
      WeatherStationDto dto = new WeatherStationDto();
      dto.setWsId(e.getPersistentIdentity());
      dto.setCode(e.getCode());
      dto.setCity(e.getCity());
      dto.setStateOrProvince(e.getStateOrProvince());
      dto.setCountryCode(e.getCountryCode());
      dto.setTimezone(e.getTimezone());
      dto.setLatitude(e.getLatitude());
      dto.setLongitude(e.getLongitude());
      
      GlobalComputedPointEntity oat = e.getOatCurrentGlobalPoint();
      if (oat != null) {

        dto.setOatId(oat.getPersistentIdentity());
        dto.setOatName(oat.getName());
        dto.setOatDisplayName(oat.getDisplayName());
        dto.setOatDescription(oat.getDescription());
        dto.setOatUnitId(oat.getUnit().getPersistentIdentity());
        dto.setOatMetricId(oat.getMetricId());
        dto.setOatEarliestProcessedAt(AbstractEntity.formatTimestamp(oat.getEarliestProcessedAt()));
        dto.setOatLastProcessedAt(AbstractEntity.formatTimestamp(oat.getLastProcessedAt()));
      }

      GlobalComputedPointEntity oatSi = e.getOatCurrentSiGlobalPoint();
      if (oatSi != null) {

        dto.setOatId(oatSi.getPersistentIdentity());
        dto.setOatName(oatSi.getName());
        dto.setOatDisplayName(oatSi.getDisplayName());
        dto.setOatDescription(oatSi.getDescription());
        dto.setOatUnitId(oatSi.getUnit().getPersistentIdentity());
        dto.setOatMetricId(oatSi.getMetricId());
        dto.setOatEarliestProcessedAt(AbstractEntity.formatTimestamp(oatSi.getEarliestProcessedAt()));
        dto.setOatLastProcessedAt(AbstractEntity.formatTimestamp(oatSi.getLastProcessedAt()));
      }
      
      GlobalComputedPointEntity rh = e.getRhCurrentGlobalPoint();
      if (rh != null) {

        dto.setRhId(rh.getPersistentIdentity());
        dto.setRhName(rh.getName());
        dto.setRhDisplayName(rh.getDisplayName());
        dto.setRhDescription(rh.getDescription());
        dto.setRhUnitId(rh.getUnit().getPersistentIdentity());
        dto.setRhMetricId(rh.getMetricId());
        dto.setRhEarliestProcessedAt(AbstractEntity.formatTimestamp(rh.getEarliestProcessedAt()));
        dto.setRhLastProcessedAt(AbstractEntity.formatTimestamp(rh.getLastProcessedAt()));
      }
      
      dtos.add(dto);
    }
    return dtos;
  }
}