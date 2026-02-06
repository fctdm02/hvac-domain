//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.building;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.utils.TimezoneUtils;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.CustomerLevelPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.dictionary.ScheduledEventTypeEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.dictionary.weather.GlobalComputedPointEntity;
import com.djt.hvac.domain.model.dictionary.weather.WeatherStationEntity;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.DistributorLevelPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.OutOfBandDistributorEntity;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.FloorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.SubBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.AbstractScheduledEventEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.PlantEntity;
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.WeatherAsyncComputedPointEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;

public class BuildingEntity extends AbstractNodeEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(BuildingEntity.class);
  
  private static final String ADD_WEATHER_STATION = "add_weather_station";
  private static final String CHANGE_WEATHER_STATION = "change_weather_station";
  private static final String REMOVE_WEATHER_STATION = "remove_weather_station";

  public static final Integer OFF_PREM_WEATH_STATION_EQUIP_TYPE_ID = Integer.valueOf(101);
  public static final String OFF_PREM_WEATH_STATION_EQUIP_NAME = "off prem weather station";
  public static final String OFF_PREM_WEATH_STATION_EQUIP_DISP_NAME = "Off Prem Weather Station";
  
  private String timezone = "America/New_York";
  private String address;
  private String city;
  private String stateOrProvince;
  private String postalCode;
  private String countryCode;
  private UnitSystem unitSystem = UnitSystem.IP;
  private Set<BuildingLevelPointTemplateUnitMappingOverrideEntity> pointTemplateUnitMappingOverrides = new HashSet<>();
  private Float latitude = Float.valueOf(0.0f);
  private Float longitude = Float.valueOf(0.0f);
  
  private WeatherStationEntity weatherStation;

  // Can only be CREATED for out-of-band parent distributor
  private BuildingStatus buildingStatus = BuildingStatus.CREATED;
  private Timestamp buildingStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  
  // Unless manually changed, buildings with an out-of-band parent 
  // distributor will be in the UP_TO_DATE state.
  // 
  // For online, the state transitions will be controlled by the  
  // evaluation/business logic, per the specs
  private BuildingPaymentStatus buildingPaymentStatus = BuildingPaymentStatus.UP_TO_DATE;
  private Timestamp buildingPaymentStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  
  // RP-10818: If an online distributor and whose "allowOutOfBandBuildings" is true, then if a building is still
  // in the "trial" period, then it has a special attribute, an enum called "buildingPaymentType" whose values
  // are ONLINE(default for online customer) and OUT_OF_BAND, can be changed as follows:
  //
  // 1. If ONLINE, can be changed to OUT_OF_BAND
  // 2. If OUT_OF_BAND, can be changed back to ONLINE if, and only if, the building point cap is under the max point cap
  //    of the payment band with the highest cap.
  private BuildingPaymentType buildingPaymentType = BuildingPaymentType.OUT_OF_BAND;

  // RP-10667: Increase billing grace period from 7 to 30 days.
  private String billingGracePeriod = "30d";
  
  // An email is sent 2 days/hours before the grace period expires to warn the user (only when online).
  private Integer buildingGracePeriodWarningNotificationId; 

  private transient String _billingGracePeriodOrdinal;
  private transient String _billingGracePeriodTimeUnit;
  private transient Map<PointTemplateEntity, AbstractPointTemplateUnitMappingOverrideEntity> _allPointTemplateUnitMappingOverrides;
  
  @Override
  protected void resetTransientAttributes() {
    
    super.resetTransientAttributes();
    _billingGracePeriodOrdinal = null;
    _billingGracePeriodTimeUnit = null;
    _allPointTemplateUnitMappingOverrides = null;
  }
  
  private Set<SubBuildingEntity> childSubBuildings = new TreeSet<>();
  private Set<PlantEntity> childPlants = new TreeSet<>();
  private Set<FloorEntity> childFloors = new TreeSet<>();
  private Set<EquipmentEntity> childEquipment = new TreeSet<>();
  private Set<AbstractScheduledEventEntity> childScheduledEvents = new TreeSet<>();
  private Set<BuildingTemporalConfigEntity> childTemporalConfigs = new TreeSet<>();
  private Set<ReportInstanceEntity> reportInstances = new TreeSet<>();
  
  public BuildingEntity() {}

  // For new instances (i.e. have not been persisted yet)  
  public BuildingEntity(
      PortfolioEntity parentNode,
      String name,
      String displayName,
      BuildingPaymentType buildingPaymentType,
      UnitSystem unitSystem) {
    super(
        parentNode,
        name,
        displayName);
    
    this.buildingPaymentType = buildingPaymentType;
    this.unitSystem = unitSystem;
  }
  
  public BuildingEntity(
      Integer persistentIdentity,
      PortfolioEntity parentNode,
      String name,
      String displayName,
      String uuid,
      String createdAt,
      String updatedAt,
      Set <TagEntity> nodeTags,
      String timezone,
      String address,
      String city,
      String stateOrProvince,
      String postalCode,
      String countryCode,
      UnitSystem unitSystem,
      String latitude,
      String longitude,
      WeatherStationEntity weatherStation,
      BuildingStatus buildingStatus,
      Timestamp buildingStatusUpdatedAt,
      BuildingPaymentStatus buildingPaymentStatus,
      Timestamp buildingPaymentStatusUpdatedAt,
      String billingGracePeriod,
      Integer buildingGracePeriodWarningNotificationId,
      BuildingPaymentType buildingPaymentType) {
    super(
        persistentIdentity, 
        parentNode, 
        name, 
        displayName,
        uuid,
        createdAt,
        updatedAt,
        nodeTags);
    if (timezone != null) {
      this.timezone = timezone;  
    }
    this.address = address;
    this.city = city;
    this.stateOrProvince = stateOrProvince;
    this.postalCode = postalCode;
    this.countryCode = countryCode;
    this.unitSystem = unitSystem;
    if (latitude != null && !latitude.trim().equals("")) {
      this.latitude = Float.parseFloat(latitude);
    }
    if (longitude != null && !longitude.trim().equals("")) {
      this.longitude = Float.parseFloat(longitude);
    }
    this.weatherStation = weatherStation;
    this.buildingStatus = buildingStatus;
    this.buildingStatusUpdatedAt = buildingStatusUpdatedAt;
    this.buildingPaymentStatus = buildingPaymentStatus;
    this.buildingPaymentStatusUpdatedAt = buildingPaymentStatusUpdatedAt;
    
    // This handles the case when a building is switched from OUT_OF_BAND back to ONLINE
    if (billingGracePeriod != null) {
      this.billingGracePeriod = billingGracePeriod;
    }
    
    this.buildingGracePeriodWarningNotificationId = buildingGracePeriodWarningNotificationId;
    this.buildingPaymentType = buildingPaymentType;
  }
  
  public Set<BuildingLevelPointTemplateUnitMappingOverrideEntity> getPointTemplateUnitMappingOverrides() {
    return pointTemplateUnitMappingOverrides;
  }

  public BuildingLevelPointTemplateUnitMappingOverrideEntity getPointTemplateUnitMappingOverride(Integer pointTemplateUnitMappingOverrideId) throws EntityDoesNotExistException{
    return getChild(BuildingLevelPointTemplateUnitMappingOverrideEntity.class, pointTemplateUnitMappingOverrides, pointTemplateUnitMappingOverrideId, this);
  }
  
  public Map<PointTemplateEntity, AbstractPointTemplateUnitMappingOverrideEntity> getAllPointTemplateUnitMappingOverrides() {
    
    if (_allPointTemplateUnitMappingOverrides == null) {
      
      _allPointTemplateUnitMappingOverrides = new HashMap<>();
      
      PortfolioEntity portfolio = getRootPortfolioNode();
      AbstractCustomerEntity customer = portfolio.getParentCustomer();
      AbstractDistributorEntity distributor = customer.getParentDistributor();

      // Layer in distributor overrides first, overwriting overrides from the customer, then building.
      for (DistributorLevelPointTemplateUnitMappingOverrideEntity override: distributor.getPointTemplateUnitMappingOverrides()) {
        _allPointTemplateUnitMappingOverrides.put(override.getPointTemplate(), override); 
      }

      for (CustomerLevelPointTemplateUnitMappingOverrideEntity override: customer.getPointTemplateUnitMappingOverrides()) {
        _allPointTemplateUnitMappingOverrides.put(override.getPointTemplate(), override); 
      }
      
      for (BuildingLevelPointTemplateUnitMappingOverrideEntity override: pointTemplateUnitMappingOverrides) {
        _allPointTemplateUnitMappingOverrides.put(override.getPointTemplate(), override); 
      }
    }
    return _allPointTemplateUnitMappingOverrides;
  }
  
  public boolean addPointTemplateUnitMappingOverride(BuildingLevelPointTemplateUnitMappingOverrideEntity pointTemplateUnitMappingOverride)
      throws EntityAlreadyExistsException {
    
    boolean result = addChild(pointTemplateUnitMappingOverrides, pointTemplateUnitMappingOverride, this);
    setIsModified("add:pointTemplateUnitMappingOverride");
    return result;
  }
  
  public void removePointTemplateUnitMappingOverride(Integer pointTemplateUnitMappingOverrideId)
      throws EntityDoesNotExistException {
    
    BuildingLevelPointTemplateUnitMappingOverrideEntity pointTemplateUnitMappingOverride = getPointTemplateUnitMappingOverride(pointTemplateUnitMappingOverrideId);
    pointTemplateUnitMappingOverride.setIsDeleted();
    setIsModified("remove:pointTemplateUnitMappingOverride");
  }

  public void removeAllPointTemplateUnitMappingOverrides() {
    
    for (BuildingLevelPointTemplateUnitMappingOverrideEntity override: pointTemplateUnitMappingOverrides) {

      override.setIsDeleted();
      setIsModified("remove:pointTemplateUnitMappingOverride");
    }
  }
  
  public NodeType getNodeType() {
    return NodeType.BUILDING;
  }
  
  public NodeSubType getNodeSubType() {
    return NodeSubType.NONE;
  }
  
  public BuildingStatus getBuildingStatus() {
    return buildingStatus;
  }

  public void setBuildingStatus(BuildingStatus buildingStatus) {
    
    if (buildingStatus.equals(this.buildingStatus)) {
      return;
    }
    
    if (this instanceof BillableBuildingEntity 
        && (this.buildingStatus.equals(BuildingStatus.CREATED) && buildingStatus.equals(BuildingStatus.PENDING_ACTIVATION)
         || this.buildingStatus.equals(BuildingStatus.PENDING_ACTIVATION) && buildingStatus.equals(BuildingStatus.ACTIVE))) {

      this.buildingStatus = buildingStatus;
      this.buildingStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
      this.setIsModified("buildingStatus");
      
    } else {
      
      throw new IllegalStateException("Illegal buildingStatus state transition from: ["
          + this.buildingStatus
          + "] to: ["
          + buildingStatus
          + "] for building: ["
          + getClassAndNaturalIdentity()
          + "]");
    }
  }

  public Timestamp getBuildingStatusUpdatedAt() {
    return buildingStatusUpdatedAt;
  }

  // Only called for the grace period exploit.
  public void setBuildingStatusUpdatedAt(Timestamp buildingStatusUpdatedAt) {
    this.buildingStatusUpdatedAt = buildingStatusUpdatedAt;
    this.setIsModified("buildingStatus");
  }
  
  public BuildingPaymentStatus getBuildingPaymentStatus() {
    return buildingPaymentStatus;
  }

  public void setBuildingPaymentStatus(BuildingPaymentStatus buildingPaymentStatus) {

    if (buildingPaymentStatus.equals(this.buildingPaymentStatus)) {
      return;
    }
    
    if (this instanceof BillableBuildingEntity 
        && (this.buildingPaymentStatus.equals(BuildingPaymentStatus.UP_TO_DATE) && buildingPaymentStatus.equals(BuildingPaymentStatus.DELINQUENT)
         || this.buildingPaymentStatus.equals(BuildingPaymentStatus.DELINQUENT) && buildingPaymentStatus.equals(BuildingPaymentStatus.UP_TO_DATE))) {

      this.buildingPaymentStatus = buildingPaymentStatus;
      this.buildingPaymentStatusUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
      this.setIsModified("buildingPaymentStatus");
      
      // NOTE: Only the status is changed here.  The parent customer/distributor evaluatePaymentState()
      // methods will take care of propagating payment state changes if needed.  The batch job will
      // store any changes to the repository.
      
    } else {
      
      throw new IllegalStateException("Illegal buildingPaymentStatus state transition from: ["
          + this.buildingPaymentStatus
          + "] to: ["
          + buildingPaymentStatus
          + "] for building: ["
          + getClassAndNaturalIdentity()
          + "]");
    }
  }
  
  public Timestamp getBuildingPaymentStatusUpdatedAt() {
    return buildingPaymentStatusUpdatedAt;
  }

  public String getTimezone() {
    return timezone;
  }
  
  public void setTimezoneByRubyLabel(String rubyTimezoneLabel) {
    
    if (rubyTimezoneLabel != null) {

      TimeZone tz = TimezoneUtils.getTimezone(rubyTimezoneLabel);
      if (tz == null) {
        throw new IllegalStateException("Cannot set timezone for building: ["
            + getNodePath()
            + "] to: ["
            + rubyTimezoneLabel
            + "] because it does not correspond to a valid ruby timezone label");
      }
      
      String tzId = tz.getID();
      if (!this.timezone.equals(tzId)) {

        this.timezone = tzId;
        this.setIsModified("timezone");
      }
    }
  }
  
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    
    if (this.address == null && address == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.address == null && address != null) 
        || (this.address != null && address == null)) {
      
      this.address = address;
      setIsModified("address");
      
    } else if (this.address != null && address != null) {
      
      if (!this.address.equals(address)) {

        this.address = address;
        setIsModified("address");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }    
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    
    if (this.city == null && city == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.city == null && city != null) 
        || (this.city != null && city == null)) {
      
      this.city = city;
      setIsModified("city");
      
    } else if (this.city != null && city != null) {
      
      if (!this.city.equals(city)) {

        this.city = city;
        setIsModified("city");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }    
  }

  public String getStateOrProvince() {
    return stateOrProvince;
  }

  public void setStateOrProvince(String stateOrProvince) {
    
    if (this.stateOrProvince == null && stateOrProvince == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.stateOrProvince == null && stateOrProvince != null) 
        || (this.stateOrProvince != null && stateOrProvince == null)) {
      
      this.stateOrProvince = stateOrProvince;
      setIsModified("stateOrProvince");
      
    } else if (this.stateOrProvince != null && stateOrProvince != null) {
      
      if (!this.stateOrProvince.equals(stateOrProvince)) {

        this.stateOrProvince = stateOrProvince;
        setIsModified("stateOrProvince");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    } 
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    
    if (this.postalCode == null && postalCode == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.postalCode == null && postalCode != null) 
        || (this.postalCode != null && postalCode == null)) {
      
      this.postalCode = postalCode;
      setIsModified("postalCode");
      
    } else if (this.postalCode != null && postalCode != null) {
      
      if (!this.postalCode.equals(postalCode)) {

        this.postalCode = postalCode;
        setIsModified("postalCode");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    } 
  }
  
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    
    if (this.countryCode == null && countryCode == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.countryCode == null && countryCode != null) 
        || (this.countryCode != null && countryCode == null)) {
      
      this.countryCode = countryCode;
      setIsModified("countryCode");
      
    } else if (this.countryCode != null && countryCode != null) {
      
      if (!this.countryCode.equals(countryCode)) {

        this.countryCode = countryCode;
        setIsModified("countryCode");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    } 
  }  

  public UnitSystem getUnitSystem() {
    return unitSystem;
  }

  public void setUnitSystem(UnitSystem unitSystem) {
    
    if ((this.unitSystem == null && unitSystem != null) || (this.unitSystem != null && unitSystem == null)) {
      
      this.unitSystem = unitSystem;
      setIsModified("unitSystem");
      
    } else if (this.unitSystem != null && unitSystem != null && !this.unitSystem.equals(unitSystem)) {

      this.unitSystem = unitSystem;
      setIsModified("unitSystem");
      
    }
    
    if (this.unitSystem != null && this.unitSystem.equals(UnitSystem.IP)) {
      
      removeAllPointTemplateUnitMappingOverrides();
    }
  }    
  
  public Float getLatitude() {
    return latitude;
  }

  public String getLatitudeAsString() {
    if (latitude != null) {
      return latitude.toString();
    }
    return null;
  }
  
  public void setLatitude(String latitude) {
    if (latitude == null || latitude.trim().isEmpty()) {
      return;
    }
    this.setLatitude(Float.parseFloat(latitude));
  }

  public void setLatitude(Float latitude) {
    
    if (this.latitude == null && latitude == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.latitude == null && latitude != null) 
        || (this.latitude != null && latitude == null)) {
      
      this.latitude = latitude;
      setIsModified("latitude");
      
    } else if (this.latitude != null && latitude != null) {
      
      if (!this.latitude.equals(latitude)) {

        this.latitude = latitude;
        setIsModified("latitude");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }   
  }

  public Float getLongitude() {
    return longitude;
  }

  public String getLongitudeAsString() {
    if (longitude != null) {
      return longitude.toString();
    }
    return null;
  }
  
  public void setLongitude(String longitude) {
    if (longitude == null || longitude.trim().isEmpty()) {
      return;
    }
    this.setLongitude(Float.parseFloat(longitude));
  }
  
  public void setLongitude(Float longitude) {
    
    if (this.longitude == null && longitude == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.longitude == null && longitude != null) 
        || (this.longitude != null && longitude == null)) {
      
      this.longitude = longitude;
      setIsModified("longitude");
      
    } else if (this.longitude != null && longitude != null) {
      
      if (!this.longitude.equals(longitude)) {

        this.longitude = longitude;
        setIsModified("longitude");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }   
  }

  public WeatherStationEntity getWeatherStation() {
    return weatherStation;
  }

  public void setWeatherStation(WeatherStationEntity weatherStation, String strUnitSystem) {
    
    if (this.weatherStation == null) {
      if (weatherStation == null) {
        
        // BOTH NULL: DO NOTHING
        
      } else {

        // THIS NULL AND INCOMING NOT NULL
        this.weatherStation = weatherStation;
        setIsModified(ADD_WEATHER_STATION);
        setOffPremWeatherStationEquipment(ADD_WEATHER_STATION, strUnitSystem);
        
      }
    } else {
      if (weatherStation == null) {

        // THIS NOT NULL AND INCOMING NULL
        this.weatherStation = weatherStation;
        setIsModified(REMOVE_WEATHER_STATION);
        setOffPremWeatherStationEquipment(REMOVE_WEATHER_STATION, strUnitSystem);
        
      } else {

        if (!this.weatherStation.equals(weatherStation)) {

          // BOTH NOT NULL AND EQUAL TO NOT EQUAL TO EACH OTHER
          this.weatherStation = weatherStation;
          setIsModified(CHANGE_WEATHER_STATION);
          setOffPremWeatherStationEquipment(CHANGE_WEATHER_STATION, strUnitSystem);
          
        } else {
          
          // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING 
          
        }
      }
    }
  }
  
  private void setOffPremWeatherStationEquipment(String action, String strUnitSystem) {
    
    try {
      
      EquipmentEntity offPremWeatherStation = getChildEquipmentByNameNullIfNotExists(OFF_PREM_WEATH_STATION_EQUIP_NAME);
      
      if (action.equals(ADD_WEATHER_STATION) && offPremWeatherStation == null) {
        
        // ADD
        offPremWeatherStation = new EquipmentEntity(
            this,
            OFF_PREM_WEATH_STATION_EQUIP_NAME,
            OFF_PREM_WEATH_STATION_EQUIP_DISP_NAME);
        
        EquipmentEnergyExchangeTypeEntity equipmentType = DictionaryContext
            .getTagsContainer()
            .getEquipmentTypeById(OFF_PREM_WEATH_STATION_EQUIP_TYPE_ID);
        
        offPremWeatherStation.setEquipmentType(equipmentType);
        
        setOffPremWeatherStationEquipmentGlobalPoints(offPremWeatherStation, weatherStation, strUnitSystem);
        
        ((PortfolioEntity)getParentNode()).addNodeToParentAndIndex(this, offPremWeatherStation);
        
      } else if (action.equals(CHANGE_WEATHER_STATION) && offPremWeatherStation != null) {
        
        // CHANGE 
        for (AbstractPointEntity point: offPremWeatherStation.getChildPoints()) {
          
          point.setIsDeleted();
          offPremWeatherStation.setIsModified("remove:childPoint");
        }
        setOffPremWeatherStationEquipmentGlobalPoints(offPremWeatherStation, weatherStation, strUnitSystem);
        
      } else if (offPremWeatherStation != null) {
        
        // REMOVE
        removeChild(childEquipment, offPremWeatherStation);
      }      
    } catch (EntityAlreadyExistsException | EntityDoesNotExistException e) {
      LOGGER.error("Unable to set off prem weather station for building: {}, error: {}",
          this.getNodePath(),
          e.getMessage(),
          e);
    }    
  }
  
  private void setOffPremWeatherStationEquipmentGlobalPoints(
      EquipmentEntity offPremWeatherStation,
      WeatherStationEntity weatherStation,
      String strUnitSystem) 
  throws 
      EntityDoesNotExistException, 
      EntityAlreadyExistsException {
    
    // TEMPERATURE
    if (strUnitSystem.equals(UnitSystem.SI.toString())) {
      setOffPremWeatherStationEquipmentGlobalPoint(
          offPremWeatherStation, 
          weatherStation.getOatCurrentSiGlobalPoint());
    } else {
      setOffPremWeatherStationEquipmentGlobalPoint(
          offPremWeatherStation, 
          weatherStation.getOatCurrentGlobalPoint());
    }
    
    // HUMIDITY
    setOffPremWeatherStationEquipmentGlobalPoint(
        offPremWeatherStation, 
        weatherStation.getRhCurrentGlobalPoint());
  }

  private void setOffPremWeatherStationEquipmentGlobalPoint(
      EquipmentEntity offPremWeatherStation,
      GlobalComputedPointEntity gobalComputedPoint) 
  throws 
      EntityDoesNotExistException, 
      EntityAlreadyExistsException {

    PointTemplateEntity pointTemplate = DictionaryContext
        .getNodeTagTemplatesContainer()
        .getPointTemplate(
            gobalComputedPoint.getPointTemplateId());
    
    Set<TagEntity> nodeTags = DictionaryContext
        .getTagsContainer()
        .getHaystackTagsByName(
            pointTemplate.getNormalizedTagsAsSet());
    
    WeatherAsyncComputedPointEntity weatherPoint = new WeatherAsyncComputedPointEntity(
        offPremWeatherStation,
        gobalComputedPoint.getName(),
        gobalComputedPoint.getDisplayName(),
        nodeTags,
        gobalComputedPoint.getUnit(),
        pointTemplate,
        gobalComputedPoint.getMetricId(),
        gobalComputedPoint.getPersistentIdentity());
    
    offPremWeatherStation.setIsModified("add:childPoint");
    
    ((PortfolioEntity)this.getParentNode()).addNodeToParentAndIndex(offPremWeatherStation, weatherPoint);
  }
  
  public boolean addChildSubBuilding(SubBuildingEntity subBuilding) throws EntityAlreadyExistsException {
    return addChild(childSubBuildings, subBuilding, this);
  }

  public Set<SubBuildingEntity> getChildSubBuildings() {
    return childSubBuildings;
  }

  public SubBuildingEntity getChildSubBuilding(Integer persistentIdentity) throws EntityDoesNotExistException {
    return (SubBuildingEntity)getRootPortfolioNode().getChildNode(persistentIdentity);
  }
  
  public Optional<SubBuildingEntity> getChildSubBuildingEmptyIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return Optional.of((SubBuildingEntity)node);
    }
    return Optional.empty();
  }
  
  public SubBuildingEntity getChildSubBuildingByNameNullIfNotExists(String name) {
    
    for (SubBuildingEntity subBuilding: childSubBuildings) {
      if (subBuilding.getName().equals(name)) {
        return subBuilding;
      }
    } 
    return null;
  }
  
  public boolean addChildPlant(PlantEntity energyExchangePlant) throws EntityAlreadyExistsException {
    return addChild(childPlants, energyExchangePlant, this);
  }

  public Set<PlantEntity> getChildPlants() {
    return childPlants;
  }

  public PlantEntity getChildPlant(Integer persistentIdentity) throws EntityDoesNotExistException {
    return (PlantEntity)getRootPortfolioNode().getChildNode(persistentIdentity);
  }
  
  public PlantEntity getChildPlantByNameNullIfNotExists(String name) {
    
    for (PlantEntity energyExchangePlant: childPlants) {
      if (energyExchangePlant.getName().equals(name)) {
        return energyExchangePlant;
      }
    } 
    return null;
  }  
  
  public PlantEntity getChildPlantNullIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return (PlantEntity)node;
    }
    return null;
  }
  
  public Optional<PlantEntity> getChildPlantEmptyIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return Optional.of((PlantEntity)node);
    }
    return Optional.empty();
  }
  
  public FloorEntity getChildFloorByNameNullIfNotExists(String name) {
    
    for (FloorEntity floor: childFloors) {
      if (floor.getName().equals(name)) {
        return floor;
      }
    } 
    return null;
  }    
  
  public EquipmentEntity getChildEquipmentByNameNullIfNotExists(String name) {
    
    for (EquipmentEntity equipment: childEquipment) {
      if (equipment.getName().equals(name)) {
        return equipment;
      }
    } 
    return null;
  }
  
  public EquipmentEntity getChildEquipmentByName(String name) 
      throws EntityDoesNotExistException {
    
    for (EquipmentEntity equipment: childEquipment) {
      if (equipment.getName().equals(name)) {
        return equipment;
      }
    } 
    throw new EntityDoesNotExistException(
        "Equipment with name: ["
            + name
            + "] not found in ["
            + this.getNaturalIdentity()
            + "].");    
  }
  
  public boolean addChildFloor(FloorEntity floor) throws EntityAlreadyExistsException {
    return addChild(childFloors, floor, this);
  }

  public Set<FloorEntity> getChildFloors() {
    return childFloors;
  }

  public boolean addChildFloorIfNotExists(FloorEntity floor) {
    return addChildIfNotExists(childFloors, floor, this);
  }
  
  public FloorEntity getChildFloor(Integer persistentIdentity) throws EntityDoesNotExistException {
    return (FloorEntity)getRootPortfolioNode().getChildNode(persistentIdentity);
  }
  
  public boolean addChildEquipmentIfNotExists(EquipmentEntity equipment) {
    return addChildIfNotExists(childEquipment, equipment, this);
  }
  
  public boolean addChildEquipment(EquipmentEntity equipment) throws EntityAlreadyExistsException {
    return addChild(childEquipment, equipment, this);
  }

  public Set<EquipmentEntity> getChildEquipment() {
    return childEquipment;
  }
  
  public EquipmentEntity getChildEquipment(Integer persistentIdentity) throws EntityDoesNotExistException {
    return (EquipmentEntity)getRootPortfolioNode().getChildNode(persistentIdentity);
  }
    
  public Optional<FloorEntity> getChildFloorEmptyIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return Optional.of((FloorEntity)node);
    }
    return Optional.empty();
  }
  
  public Optional<EquipmentEntity> getChildEquipmentEmptyIfINotExists(Integer persistentIdentity) {
    
    AbstractNodeEntity node = getRootPortfolioNode().getChildNodeNullIfNotExists(persistentIdentity);
    if (node != null) {
      return Optional.of((EquipmentEntity)node);
    }
    return Optional.empty();
  } 
  
  public boolean addReportInstance(ReportInstanceEntity reportInstance) {
    return reportInstances.add(reportInstance);
  }
  
  public boolean addReportInstanceIfNotExists(ReportInstanceEntity reportInstance) {
    return addChildIfNotExists(reportInstances, reportInstance, this);
  }
  
  public Set<ReportInstanceEntity> getReportInstances() {
    
    // If empty, then we need to create one for every report template.
    Set<ReportTemplateEntity> reportTemplates = DictionaryContext.getReportTemplatesContainer().getReportTemplates();
    if (reportInstances.size() != reportTemplates.size()) {
      for (ReportTemplateEntity reportTemplate: reportTemplates) {
        
        Integer reportTemplateId = reportTemplate.getPersistentIdentity();
        ReportInstanceEntity reportInstance = getReportInstanceByReportTemplateIdNullIfNotExists(reportTemplateId);
        if (reportInstance == null) {

          reportInstance = new ReportInstanceEntity(
              null,
              reportTemplate,
              this,
              AbstractEntity.getTimeKeeper().getCurrentTimeInMillis(),
              AbstractEntity.getTimeKeeper().getCurrentTimeInMillis(),
              reportTemplate.getDefaultPriority());
          
          addReportInstanceIfNotExists(reportInstance);
        }
      }
    }
    return reportInstances;
  }
  
  public Set<ReportInstanceEntity> getEnabledReportInstances() {
    
    Set<ReportInstanceEntity> enabledReportInstances = new TreeSet<>();
    for (ReportInstanceEntity reportInstance: reportInstances) {
      
      if (reportInstance != null && reportInstance.isEnabled()) {
        
        enabledReportInstances.add(reportInstance);
      }
    }
    return enabledReportInstances;
  }
  
  @Override
  public void setNotModified() {
    
    super.setNotModified();
    for (ReportInstanceEntity entity: reportInstances) {
      
      entity.setNotModified();
    }
  }
  
  public Set<Integer> getCreatedReportInstances() {

    Set<Integer> ids = new HashSet<>();
    for (ReportInstanceEntity entity: reportInstances) {
      if (entity.getNeedsEnabling()) {
        ids.add(entity.getPersistentIdentity());
      }
    }
    return ids;
  }    
  
  public Set<Integer> getUpdatedReportInstances() {

    Set<Integer> ids = new HashSet<>();
    for (ReportInstanceEntity entity: reportInstances) {
      if (entity.getIsModified()) {
        ids.add(entity.getPersistentIdentity());
      }
    }
    return ids;
  }
  
  public Set<Integer> getDeletedReportInstances() {

    Set<Integer> ids = new HashSet<>();
    for (ReportInstanceEntity entity: reportInstances) {
      if (entity.getNeedsDisabling()) {
        ids.add(entity.getPersistentIdentity());
      }
    }
    return ids;
  }  
  
  public ReportInstanceEntity getReportInstanceByReportTemplateId(Integer reportTemplateId) throws EntityDoesNotExistException {
    
    Iterator<ReportInstanceEntity> iterator = reportInstances.iterator();
    while (iterator.hasNext()) {
      
      ReportInstanceEntity reportInstance = iterator.next();
      
      if (reportInstance.getReportTemplate().getPersistentIdentity().equals(reportTemplateId)) {
        return reportInstance;
      }
    }
    throw new EntityDoesNotExistException("Building: [" 
        + getNodePath()
        + "] does not have a report instance with reportTemplateId: ["
        + reportTemplateId
        + "]");
  }
  
  public ReportInstanceEntity getReportInstanceByReportTemplateIdNullIfNotExists(Integer reportTemplateId) {
    
    Iterator<ReportInstanceEntity> iterator = reportInstances.iterator();
    while (iterator.hasNext()) {
      
      ReportInstanceEntity reportInstance = iterator.next();
      
      if (reportInstance.getReportTemplate().getPersistentIdentity().equals(reportTemplateId)) {
        return reportInstance;
      }
    }
    return null;
  }
  
  public ReportInstanceEntity getReportInstanceNullIfNotExists(Integer persistentIdentity) {
    
    if (persistentIdentity == null) {
      throw new IllegalArgumentException("persistentIdentity must have a value that corresponds to some non-null/non-empty surrogate key value in the repository");
    }
    for (ReportInstanceEntity reportInstance: reportInstances) {
      // Report instances are the ONLY "persisted" entity that can have a null persistent identity.  See Tom for a rant.
      Integer reportInstancePersistentIdentity =  reportInstance.getPersistentIdentity();
      if (reportInstancePersistentIdentity != null && reportInstancePersistentIdentity.equals(persistentIdentity)) {
        return reportInstance;
      }
    }
    return null;
  }
  
  public void removeAllReportInstances() {
    
    reportInstances.clear();
  }
  
  private Set<EquipmentEntity> _allDescendantEquipment = null;
  public Set<EquipmentEntity> getAllDescendantEquipment() {
    
    if (_allDescendantEquipment == null) {
      
      _allDescendantEquipment = new HashSet<>();
      for (AbstractNodeEntity node: getRootPortfolioNode().getAllNodes()) {

        if (node instanceof EquipmentEntity) {
          
          EquipmentEntity equipment = (EquipmentEntity)node;
          if (isAncestor(equipment)) {
            
            _allDescendantEquipment.add(equipment);
          }
        }
      }
    }
    return _allDescendantEquipment;
  }
  public void resetAllDescendantEquipment() {
    _allDescendantEquipment = null;
  }
  
  private Map<AbstractEnergyExchangeTypeEntity, Set<EquipmentEntity>> _descendantEquipmentByType = null;
  public Map<AbstractEnergyExchangeTypeEntity, Set<EquipmentEntity>> getAllDescendantEquipmentByType() {
    
    if (_descendantEquipmentByType == null) {
      
      _descendantEquipmentByType = new HashMap<>();
      for (AbstractNodeEntity node: getRootPortfolioNode().getAllNodes()) {

        if (node instanceof EquipmentEntity) {
          
          EquipmentEntity equipment = (EquipmentEntity)node;
          if (isAncestor(equipment)) {
            
            AbstractEnergyExchangeTypeEntity et = equipment.getEnergyExchangeTypeNullIfNotExists();
            if (et != null) {
              
              Set<EquipmentEntity> set = _descendantEquipmentByType.get(et);
              if (set == null) {
                
                set = new HashSet<>();
                _descendantEquipmentByType.put(et, set);
              }
              set.add(equipment);
            }
          }
        }
      }
    }
    return _descendantEquipmentByType;
  }
  public void resetAllDescendantEquipmentByType() {
    _descendantEquipmentByType = null; 
  }  
  
  private Set<EnergyExchangeEntity> _allDescendantEnergyExchangeSystemNodes = null;
  public Set<EnergyExchangeEntity> getAllDescendantEnergyExchangeSystemNodes() {
    
    if (_allDescendantEnergyExchangeSystemNodes == null) {
      
      _allDescendantEnergyExchangeSystemNodes = new HashSet<>();
      for (AbstractNodeEntity node: getRootPortfolioNode().getAllNodes()) {

        if (node instanceof EnergyExchangeEntity && node.getAncestorBuilding().equals(this)) {
          
          EnergyExchangeEntity equipment = (EnergyExchangeEntity)node;
          if (isAncestorOfEnergyExchangeSystemNode(equipment)) {
            
            _allDescendantEnergyExchangeSystemNodes.add(equipment);
          }
        }
      }
    }
    return _allDescendantEnergyExchangeSystemNodes;
  }
  public void resetAllDescendantEnergyExchangeSystemNodes() {
    _allDescendantEnergyExchangeSystemNodes = null;
  }
  
  private Map<AbstractEnergyExchangeTypeEntity, Set<EnergyExchangeEntity>> _descendantEnergyExchangeSystemNodesByType = null;
  public Map<AbstractEnergyExchangeTypeEntity, Set<EnergyExchangeEntity>> getAllDescendantEnergyExchangeSystemNodesByType() {
    
    if (_descendantEnergyExchangeSystemNodesByType == null) {
      
      _descendantEnergyExchangeSystemNodesByType = new HashMap<>();
      for (AbstractNodeEntity node: getRootPortfolioNode().getAllNodes()) {

        if (node instanceof EnergyExchangeEntity) {
          
          EnergyExchangeEntity equipment = (EnergyExchangeEntity)node;
          if (isAncestorOfEnergyExchangeSystemNode(equipment)) {
            
            AbstractEnergyExchangeTypeEntity et = equipment.getEnergyExchangeTypeNullIfNotExists();
            if (et != null) {
              
              Set<EnergyExchangeEntity> set = _descendantEnergyExchangeSystemNodesByType.get(et);
              if (set == null) {
                
                set = new HashSet<>();
                _descendantEnergyExchangeSystemNodesByType.put(et, set);
              }
              set.add(equipment);
            }
          }
        }
      }
    }
    return _descendantEnergyExchangeSystemNodesByType;
  }
  public void resetAllDescendantEnergyExchangeSystemNodesByType() {
    _descendantEnergyExchangeSystemNodesByType = null; 
  }

  public Set<AbstractScheduledEventEntity> getChildScheduledEvents() {
    return childScheduledEvents;
  }

  public boolean addChildScheduledEvent(AbstractScheduledEventEntity childScheduledEvent) throws EntityAlreadyExistsException {
    return addChild(childScheduledEvents, childScheduledEvent, this);
  }

  public AbstractScheduledEventEntity getChildScheduledEvent(Integer persistentIdentity) throws EntityDoesNotExistException {
    return getChild(AbstractScheduledEventEntity.class, childScheduledEvents, persistentIdentity, this);
  }

  public AbstractScheduledEventEntity getChildScheduledEventByScheduledEventType(ScheduledEventTypeEntity scheduledEventType) throws EntityDoesNotExistException {
    for (AbstractScheduledEventEntity childScheduledEvent: childScheduledEvents) {
      if (childScheduledEvent.getParentScheduledPoint().getScheduledEventType().equals(scheduledEventType)) {
        return childScheduledEvent;
      }
    }
    throw new EntityDoesNotExistException("Building: ["
        + getNodePath()
        + "] does not have a scheduled event of type: ["
        + scheduledEventType
        + "]");  
  }
  
  public AbstractScheduledEventEntity removeChildScheduledEvent(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    AbstractScheduledEventEntity childScheduledEvent = getChildScheduledEvent(persistentIdentity);
    childScheduledEvent.setIsDeleted();
    return childScheduledEvent;
  }
  
  public Set<BuildingTemporalConfigEntity> getChildTemporalConfigs() {
    return childTemporalConfigs;
  }

  public boolean addChildTemporalConfig(BuildingTemporalConfigEntity childTemporalConfig) throws EntityAlreadyExistsException {
    return addChild(childTemporalConfigs, childTemporalConfig, this);
  }

  public BuildingTemporalConfigEntity getChildTemporalConfig(Integer persistentIdentity) throws EntityDoesNotExistException {
    return getChild(BuildingTemporalConfigEntity.class, childTemporalConfigs, persistentIdentity, this);
  }

  public BuildingTemporalConfigEntity getChildTemporalConfigByEffectiveDate(LocalDate effectiveDate) throws EntityDoesNotExistException {
    for (BuildingTemporalConfigEntity childTemporalConfig: childTemporalConfigs) {
      if (childTemporalConfig.getEffectiveDate().equals(effectiveDate)) {
        return childTemporalConfig;
      }
    }
    throw new EntityDoesNotExistException("Building: ["
        + getNodePath()
        + "] does not have a temporal config with effective date: ["
        + effectiveDate
        + "]");  
  }  
  
  public BuildingTemporalConfigEntity removeChildTemporalConfig(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    BuildingTemporalConfigEntity childTemporalConfig = getChildTemporalConfig(persistentIdentity);
    
    // The persistence layer will treat an inverted id as marked for deletion.
    childTemporalConfig.setPersistentIdentity(childTemporalConfig.getPersistentIdentity().intValue() * -1);
    
    return childTemporalConfig;
  }
  
  @Override
  public int calculateTotalMappedPointCount() {
    
    int childPointCount = getDirectChildMappedPointCount();

    for (SubBuildingEntity subBuilding : childSubBuildings) {
      childPointCount = childPointCount + subBuilding.getTotalMappedPointCount();  
    }    

    for (PlantEntity energyExchangePlant : childPlants) {
      childPointCount = childPointCount + energyExchangePlant.getTotalMappedPointCount();  
    }    

    for (FloorEntity floor : childFloors) {
      childPointCount = childPointCount + floor.getTotalMappedPointCount();  
    }    
    
    for (EquipmentEntity equipment : childEquipment) {
      childPointCount = childPointCount + equipment.getTotalMappedPointCount();  
    }    
    
    return childPointCount;
  }
  
  @Override
  public void evaluateState() {

    // NOTE:
    // Regular building and billable buildings are evaluated differently, 
    // see the subclass implementation for billable building.

    // Evaluates all descendant billable buildings for their config status, 
    // which is the number of mapped points that the building has.  All parent customer
    // and distributor config states are derived from their descendant building 
    // config states.
    evaluateConfigState();
 
    // Evaluates all descendant billable buildings for their payment status,
    // which is the status of the building subscription, if any.  All parent customer
    // and distributor payment states are derived from their descendant building 
    // payment states.
    evaluatePaymentState();
  }  
  
  public void evaluatePendingDeletionState() {
    
    // NO-OP: handled by parent portolio.
  }
  
  public void evaluatePaymentState() {
    
    // NO-OP: handled by parent portfolio.
  }
  
  public void evaluateConfigState() {
    
    // NO-OP: handled by parent portfolio.
  }
  
  public void validateDeletable() {

    // A non-billable building is always deletable.  
    // The billable building subclass will do its own validation.
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected <T extends AbstractNodeEntity> Set<T> getChildSet(AbstractNodeEntity t) {
    
    if (t instanceof AbstractPointEntity) {
      return (Set<T>)this.getChildPoints();
      
    } else if (t instanceof SubBuildingEntity) {
      return (Set<T>)this.childSubBuildings;

    } else if (t instanceof PlantEntity) {
      return (Set<T>)this.childPlants;

    } else if (t instanceof FloorEntity) {
      return (Set<T>)this.childFloors;
      
    } else if (t instanceof EquipmentEntity) {
      return (Set<T>)this.childEquipment;
      
    }
    throw new UnsupportedOperationException("Building: ["
        + getNodePath()
        + "] does not support having child: ["
        + t.getClassAndNaturalIdentity()
        + "]");
  }
  
  @Override
  public Set<AbstractNodeEntity> getAllChildNodes() {
    
    Set<AbstractNodeEntity> set = new HashSet<>();
    set.addAll(this.getChildPoints());
    set.addAll(this.childSubBuildings);
    set.addAll(this.childPlants);
    set.addAll(this.childFloors);
    set.addAll(this.childEquipment);
    return set;
  }
  
  @Override
  public AbstractNodeEntity duplicateNode(PortfolioEntity portfolio, AbstractNodeEntity parentNode, int duplicationIndex) {
    
    try {

      BuildingEntity duplicatedBuilding = new BuildingEntity(
          null,
          (PortfolioEntity)portfolio,
          Integer.toString(duplicationIndex) + "_" + getName(),
          Integer.toString(duplicationIndex) + "_" + getDisplayName(),
          UUID.randomUUID().toString(),
          AbstractEntity.formatTimestamp(getCreatedAt()),
          AbstractEntity.formatTimestamp(getUpdatedAt()),
          getNodeTags(),
          timezone,
          address,
          city,
          stateOrProvince,
          postalCode,
          countryCode,
          unitSystem,
          getLatitudeAsString(),
          getLongitudeAsString(),
          weatherStation,
          buildingStatus,
          buildingStatusUpdatedAt,
          buildingPaymentStatus,
          buildingPaymentStatusUpdatedAt,
          billingGracePeriod,
          buildingGracePeriodWarningNotificationId,
          buildingPaymentType);
      
      portfolio.addNodeToParentAndIndex(duplicatedBuilding);
      
      duplicatePointNodes(portfolio, duplicatedBuilding, duplicationIndex);
      
      for (SubBuildingEntity subBuilding: childSubBuildings) {
        subBuilding.duplicateNode(portfolio, duplicatedBuilding, duplicationIndex);
      }    

      for (PlantEntity plant: childPlants) {
        plant.duplicateNode(portfolio, duplicatedBuilding, duplicationIndex);
      }    

      for (FloorEntity floor : childFloors) {
        floor.duplicateNode(portfolio, duplicatedBuilding, duplicationIndex);
      }    
      
      for (EquipmentEntity equipment : childEquipment) {
        equipment.duplicateNode(portfolio, duplicatedBuilding, duplicationIndex);
      }
      
      return duplicatedBuilding;
      
    } catch (EntityAlreadyExistsException eaee) {
      throw new IllegalStateException("Unable to duplicate child nodes for: ["
          + getClassAndNaturalIdentity()
          + "] and duplicationIndex: ["
          + duplicationIndex
          + "].", eaee);
    }
  }
  
  public void duplicateEnergyExhangeSystemNodes(
      PortfolioEntity portfolio,
      BuildingEntity duplicatedBuilding,
      int duplicationIndex) {
    
    Set<EnergyExchangeEntity> sourceEnergyExchangeSystemNodes = getAllDescendantEnergyExchangeSystemNodes();

    duplicateEnergyExhangeSystemNodes(portfolio, duplicatedBuilding, sourceEnergyExchangeSystemNodes, EnergyExchangeSystemType.AIR_SUPPLY, duplicationIndex);
    duplicateEnergyExhangeSystemNodes(portfolio, duplicatedBuilding, sourceEnergyExchangeSystemNodes, EnergyExchangeSystemType.CHILLED_WATER, duplicationIndex);
    duplicateEnergyExhangeSystemNodes(portfolio, duplicatedBuilding, sourceEnergyExchangeSystemNodes, EnergyExchangeSystemType.HOT_WATER, duplicationIndex);
    duplicateEnergyExhangeSystemNodes(portfolio, duplicatedBuilding, sourceEnergyExchangeSystemNodes, EnergyExchangeSystemType.STEAM, duplicationIndex);
  }
  
  private void duplicateEnergyExhangeSystemNodes(
      PortfolioEntity portfolio,
      BuildingEntity duplicatedBuilding,
      Set<EnergyExchangeEntity> sourceEnergyExchangeSystemNodes,
      EnergyExchangeSystemType energyExchangeSystemType, 
      int duplicationIndex) {
    
    try {
      
      for (EnergyExchangeEntity energyExchangeSystemNode: sourceEnergyExchangeSystemNodes) {

        // Get the duplicate node.
        String duplicatedEnergyExchangeNaturalIdentity = AbstractNodeEntity.deriveDuplicatedNaturalIdentity((AbstractNodeEntity)energyExchangeSystemNode, duplicationIndex);
        EnergyExchangeEntity duplicatedEnergyExchangeSystemNode = portfolio.getEnergyExchangeEntityByNaturalIdentity(duplicatedEnergyExchangeNaturalIdentity);

        // Add the matching children energy exchange system nodes.
        List<EnergyExchangeEntity> duplicatedChildEnergyExchangeSystemNodes = new ArrayList<>();
        Set<EnergyExchangeEntity> childEnergyExchangeSystemNodes = energyExchangeSystemNode.getChildEnergyExchangeSystemNodes(energyExchangeSystemType);
        for (EnergyExchangeEntity childEnergyExchangeSystemNode: childEnergyExchangeSystemNodes) {
        
          // Get the duplicate child node.
          String duplicatedChildEnergyExchangeNaturalIdentity = AbstractNodeEntity.deriveDuplicatedNaturalIdentity((AbstractNodeEntity)childEnergyExchangeSystemNode, duplicationIndex);
          EnergyExchangeEntity duplicatedChildEnergyExchangeSystemNode = portfolio.getEnergyExchangeEntityByNaturalIdentity(duplicatedChildEnergyExchangeNaturalIdentity);
          duplicatedChildEnergyExchangeSystemNodes.add(duplicatedChildEnergyExchangeSystemNode);
        }
        
        // Now, add the duplicate child energy exchange system nodes to the duplicate node.
        duplicatedEnergyExchangeSystemNode.setChildEnergyExchangeSystemNodes(energyExchangeSystemType, duplicatedChildEnergyExchangeSystemNodes);
      }
      
    } catch (EntityDoesNotExistException | EntityAlreadyExistsException e) {
      throw new IllegalStateException("Unable to duplicate energy exchange system nodes for: ["
          + getClassAndNaturalIdentity()
          + "] and duplicationIndex: ["
          + duplicationIndex
          + "].", e);
    }
  }
  
  public BuildingPaymentType getBuildingPaymentType() {
    
    return buildingPaymentType;
  }
  
  // RP-10818: If an online distributor and whose "allowOutOfBandBuildings" is true, then if a building is still
  // in the "trial" period, then it has a special attribute, an enum called "buildingPaymentType" whose values
  // are ONLINE(default for online customer) and OUT_OF_BAND, can be changed as follows:
  //
  // 1. If ONLINE, can be changed to OUT_OF_BAND
  // 2. If OUT_OF_BAND, can be changed back to ONLINE if, and only if, the building point cap is under the max point cap
  //    of the payment band with the highest cap.
  // 
  // NOTE: The "trial period" is defined as the time period between the first point being mapped (transitioning building
  // status from CREATED to PENDING_ACTIVATION.  Any time the status is changed, there is a corresponding "status updated at"
  // timestamp that is changed.  This date is the trial period start date.  The end date is "billingGracePeriod" duration later.
  // The default is "30d", so if the trial started on 01-01-2021, then the trial period would expire on 01-31-2021.
  public void setBuildingPaymentType(BuildingPaymentType buildingPaymentType) {
    
    if (buildingPaymentType == null) {
      
      // Nothing to do.
      return;
    }
        
    AbstractDistributorEntity distributor = getRootPortfolioNode().getParentCustomer().getParentDistributor();
    if (distributor instanceof OutOfBandDistributorEntity) {
      
      throw new IllegalStateException("'buildingPaymentType' cannot be changed on out of band distributors for building: ["
          + getNodePath()
          + "]");
      
    } else if (buildingPaymentType.equals(this.buildingPaymentType)) {
      
      // Nothing to do.
      return;
    }

    if (((OnlineDistributorEntity)distributor).getAllowOutOfBandBuildings()) {
      if (getBuildingStatus().equals(BuildingStatus.PENDING_ACTIVATION) && !hasGracePeriodExpired()) {
        
        int mappedPointCount = this.getTotalMappedPointCount();
        int pointCap = DictionaryContext.getPaymentPlansContainer().getMaxPointCap().intValue();
        
        if ((buildingPaymentType.equals(BuildingPaymentType.OUT_OF_BAND) && this.buildingPaymentType.equals(BuildingPaymentType.ONLINE)) 
            || (buildingPaymentType.equals(BuildingPaymentType.ONLINE) && mappedPointCount <= pointCap)) {
          
          if (!buildingPaymentType.equals(this.buildingPaymentType)) {

            this.buildingPaymentType = buildingPaymentType;
            this.setIsModified("buildingPaymentType");
          }
        } else {
          throw new IllegalStateException("'buildingPaymentType' cannot be changed for building: ["
              + getNodePath()
              + "] because its mapped point count: ["
              + mappedPointCount
              + "] exceeds the online payment point cap value of: ["
              + pointCap
              + "]");
        }
      } else {
        if (!getBuildingStatus().equals(BuildingStatus.PENDING_ACTIVATION)) {
          throw new IllegalStateException("'buildingPaymentType' cannot be changed for building: ["
              + getNodePath()
              + "] because its trial period has not started yet (i.e. no points have been mapped yet)");
        }
        throw new IllegalStateException("'buildingPaymentType' cannot be changed for building: ["
            + getNodePath()
            + "] because it is no longer in the trial period");
      }
    } else {
      throw new IllegalStateException("'buildingPaymentType' cannot be changed on online distributors for building: ["
          + getNodePath()
          + "] because its 'allowOutOfBandBuildings' value is false");
    }
  }
  
  public boolean allowBuildingPaymentTypeChange() {
    
    boolean allowBuildingPaymentTypeChange = false;
    
    AbstractDistributorEntity distributor = getRootPortfolioNode()
        .getParentCustomer()
        .getParentDistributor();
    
    if (distributor instanceof OnlineDistributorEntity
        && ((OnlineDistributorEntity)distributor).getAllowOutOfBandBuildings()
        && (getBuildingStatus().equals(BuildingStatus.PENDING_ACTIVATION) && !hasGracePeriodExpired())) {

      int mappedPointCount = getTotalMappedPointCount();
      int pointCap = DictionaryContext.getPaymentPlansContainer().getMaxPointCap().intValue();
      
      if (this.buildingPaymentType.equals(BuildingPaymentType.ONLINE)
          || (buildingPaymentType.equals(BuildingPaymentType.OUT_OF_BAND) && mappedPointCount <= pointCap)) {
      
        allowBuildingPaymentTypeChange = true;
      }
    }
    
    return allowBuildingPaymentTypeChange;
  }
  
  public String getTrialPeriodDurationRemaining() {
    
    String trialPeriodDurationRemaining = null;
    
    AbstractDistributorEntity distributor = getRootPortfolioNode()
        .getParentCustomer()
        .getParentDistributor();
    
    if (distributor instanceof OnlineDistributorEntity
        && ((OnlineDistributorEntity)distributor).getAllowOutOfBandBuildings()
        && (getBuildingStatus().equals(BuildingStatus.PENDING_ACTIVATION) && !hasGracePeriodExpired())) {
      

    }
    
    return trialPeriodDurationRemaining;
  }
  
  public Integer getBuildingGracePeriodWarningNotificationId() {
    return buildingGracePeriodWarningNotificationId;
  }

  public void setBuildingGracePeriodWarningNotificationId(Integer buildingGracePeriodWarningNotificationId) {
    
    this.buildingGracePeriodWarningNotificationId = buildingGracePeriodWarningNotificationId;
    this.setIsModified("buildingGracePeriodWarningNotificationId");
  }
  
  public String getBillingGracePeriod() {
    return billingGracePeriod;
  }

  public void setBillingGracePeriod(String billingGracePeriod) {
    
    this.billingGracePeriod = billingGracePeriod;
    this.setIsModified("billingGracePeriod");
  }
  
  public boolean hasGracePeriodExpired() {
    
    if (!getBuildingStatus().equals(BuildingStatus.PENDING_ACTIVATION)) {
      
      throw new IllegalStateException("hasGracePeriodExpired() should only be invoked when the building is in the PENDING_ACTIVATION state. Building: "
          + getNodePath()
          + "] ");
    }
    
    String normalizedGracePeriod = billingGracePeriod.trim().toLowerCase();
    if (normalizedGracePeriod.endsWith("d")) {

      normalizedGracePeriod = billingGracePeriod.replace("d","");
      Period gracePeriod = null;
      try {
        gracePeriod = Period.ofDays(Integer.parseInt(normalizedGracePeriod)); 
      } catch (Exception e) {
        throw new IllegalStateException("Unable to parse billing grace period: ["
            + billingGracePeriod
            + "].  It should be 'Xd', where X is an integer and 'd' denotes days");
      }
      
      long currentTimeMillis = getTimeKeeper().getCurrentTimeInMillis();
      long buildingStatusUpdatedAtMillis = this.getBuildingStatusUpdatedAt().getTime();
      
      long durationMillis = currentTimeMillis - buildingStatusUpdatedAtMillis;
      
      long durationDays = TimeUnit.MILLISECONDS.toDays(durationMillis);
      long gracePeriodDays = gracePeriod.getDays();    
      
      boolean hasGracePeriodExpired = false;
      if (durationDays >= gracePeriodDays) {
        hasGracePeriodExpired = true;
      }
      return hasGracePeriodExpired;
      
    } else if (normalizedGracePeriod.endsWith("h")) {

      normalizedGracePeriod = billingGracePeriod.replace("h","");
      Duration gracePeriod = null;
      try {
        gracePeriod = Duration.ofHours(Integer.parseInt(normalizedGracePeriod));
      } catch (Exception e) {
        throw new IllegalStateException("Unable to parse billing grace period: ["
            + billingGracePeriod
            + "].  It should be 'Xh', where X is an integer and 'h' denotes hours");
      }
      
      long currentTimeMillis = getTimeKeeper().getCurrentTimeInMillis();
      long buildingStatusUpdatedAtMillis = this.getBuildingStatusUpdatedAt().getTime();
      
      long durationMillis = currentTimeMillis - buildingStatusUpdatedAtMillis;
      
      long durationHours = TimeUnit.MILLISECONDS.toHours(durationMillis);
      long gracePeriodHours = gracePeriod.toHours();
      
      boolean hasGracePeriodExpired = false;
      if (durationHours >= gracePeriodHours) {
        hasGracePeriodExpired = true;
      }
      return hasGracePeriodExpired;
    }
    throw new IllegalStateException("Unsupported billing grace period format: ["
        + billingGracePeriod
        + "] for building: ["
        + getNodePath()
        + "]. It should be 'Xd' or 'Xh', where X is an integer, 'd' denotes days and 'h' denotes hours");
  }
  
  public String getGracePeriodExpiration() {

    if (!getBuildingStatus().equals(BuildingStatus.PENDING_ACTIVATION)) {
      
      return null;
    }
    
    String normalizedGracePeriod = billingGracePeriod.trim().toLowerCase();
    if (normalizedGracePeriod.endsWith("d")) {

      normalizedGracePeriod = billingGracePeriod.replace("d","");
      Period gracePeriod = null;
      try {
        gracePeriod = Period.ofDays(Integer.parseInt(normalizedGracePeriod)); 
      } catch (Exception e) {
        throw new IllegalStateException("Unable to parse billing grace period: ["
            + billingGracePeriod
            + "].  It should be 'Xd', where X is an integer and 'd' denotes days");
      }
      
      _billingGracePeriodOrdinal = Integer.toString(gracePeriod.getDays());
      _billingGracePeriodTimeUnit = "day";
      
      LocalDate gracePeriodExpiration = getBuildingStatusUpdatedAt()
          .toLocalDateTime()
          .toLocalDate()
          .plusDays(gracePeriod.getDays());
      
      return LOCAL_DATE_FORMATTER.get().format(gracePeriodExpiration);
      
    } else if (normalizedGracePeriod.endsWith("h")) {

      normalizedGracePeriod = billingGracePeriod.replace("h","");
      Duration gracePeriod = null;
      try {
        gracePeriod = Duration.ofHours(Integer.parseInt(normalizedGracePeriod));
      } catch (Exception e) {
        throw new IllegalStateException("Unable to parse billing grace period: ["
            + billingGracePeriod
            + "].  It should be 'Xh', where X is an integer and 'h' denotes hours");
      }
      
      _billingGracePeriodOrdinal = Long.toString(gracePeriod.toHours());
      _billingGracePeriodTimeUnit = "hour";
      
      LocalDateTime gracePeriodExpiration = getBuildingStatusUpdatedAt()
          .toLocalDateTime()
          .plusHours(gracePeriod.toHours());
      
      ZonedDateTime zdt = ZonedDateTime.of(
          gracePeriodExpiration, 
          TimeZone.getTimeZone("America/New_York").toZoneId());
      
      return LOCAL_DATE_TIME_FORMATTER.get().format(zdt);
    }
    throw new IllegalStateException("Unsupported billing grace period format: ["
        + billingGracePeriod
        + "] for building: ["
        + getNodePath()
        + "]. It should be 'Xd' or 'Xh', where X is an integer, 'd' denotes days and 'h' denotes hours");
  }

  public String getGracePeriodOrdinal() {
    if (_billingGracePeriodOrdinal == null) {
      getGracePeriodExpiration();
    }
    return _billingGracePeriodOrdinal;
  }
  
  public String getGracePeriodTimeUnit() {
    if (_billingGracePeriodTimeUnit == null) {
      getGracePeriodExpiration();
    }
    return _billingGracePeriodTimeUnit;
  }

  public boolean shouldGracePeriodExpirationWarningNotificationBeSent() {
    
    if (!getBuildingStatus().equals(BuildingStatus.PENDING_ACTIVATION)) {
      
      throw new IllegalStateException("shouldGracePeriodExpirationWarningNotificationBeSent() should only be invoked when the building is in the PENDING_ACTIVATION state. Building: "
          + getNodePath()
          + "] ");
    }
    
    if (buildingGracePeriodWarningNotificationId != null) {
      return false;
    }
    
    String normalizedGracePeriod = billingGracePeriod.trim().toLowerCase();
    if (normalizedGracePeriod.endsWith("d")) {

      normalizedGracePeriod = billingGracePeriod.replace("d","");
      Period gracePeriod = null;
      try {
        gracePeriod = Period.ofDays(Integer.parseInt(normalizedGracePeriod)); 
      } catch (Exception e) {
        throw new IllegalStateException("Unable to parse billing grace period: ["
            + billingGracePeriod
            + "].  It should be 'Xd', where X is an integer and 'd' denotes days");
      }
      
      long currentTimeMillis = getTimeKeeper().getCurrentTimeInMillis();
      long buildingStatusUpdatedAtMillis = getBuildingStatusUpdatedAt().getTime();
      
      long durationMillis = currentTimeMillis - buildingStatusUpdatedAtMillis;
      
      long durationDays = TimeUnit.MILLISECONDS.toDays(durationMillis);
      long gracePeriodDays = gracePeriod.getDays() - 2L;
      
      boolean shouldGracePeriodExpirationWarningNotificationBeSent = false;
      if (durationDays >= gracePeriodDays) {
        shouldGracePeriodExpirationWarningNotificationBeSent = true;
      }
      return shouldGracePeriodExpirationWarningNotificationBeSent;
      
    } else if (normalizedGracePeriod.endsWith("h")) {

      normalizedGracePeriod = billingGracePeriod.replace("h","");
      Duration gracePeriod = null;
      try {
        gracePeriod = Duration.ofHours(Integer.parseInt(normalizedGracePeriod));
      } catch (Exception e) {
        throw new IllegalStateException("Unable to parse billing grace period: ["
            + billingGracePeriod
            + "].  It should be 'Xh', where X is an integer and 'h' denotes hours");
      }
      
      long currentTimeMillis = getTimeKeeper().getCurrentTimeInMillis();
      long buildingStatusUpdatedAtMillis = this.getBuildingStatusUpdatedAt().getTime();
      
      long durationMillis = currentTimeMillis - buildingStatusUpdatedAtMillis;
      
      long durationHours = TimeUnit.MILLISECONDS.toHours(durationMillis);
      long gracePeriodHours = gracePeriod.toHours();
      if (gracePeriodHours > 2) {
        gracePeriodHours = gracePeriodHours - 2;
      }
      
      boolean shouldGracePeriodExpirationWarningNotificationBeSent = false;
      if (durationHours >= gracePeriodHours) {
        shouldGracePeriodExpirationWarningNotificationBeSent = true;
      }
      return shouldGracePeriodExpirationWarningNotificationBeSent;
    }
    throw new IllegalStateException("Unsupported billing grace period format: ["
        + billingGracePeriod
        + "] for building: ["
        + getNodePath()
        + "]. It should be 'Xd' or 'Xh', where X is an integer, 'd' denotes days and 'h' denotes hours");
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages, 
      boolean remediate) {

    if (ValidationMessage.hasPhaseOneIssueTypes(issueTypes)) {
      for (AbstractPointEntity point : getChildPoints()) {
        if (!point.getIsDeleted()) {
          point.validate(issueTypes, validationMessages, remediate);  
        }
      }
    }
    
    for (SubBuildingEntity subBuilding : childSubBuildings) {
      subBuilding.validate(issueTypes, validationMessages, remediate);  
    }    

    for (PlantEntity energyExchangePlant : childPlants) {
      energyExchangePlant.validate(issueTypes, validationMessages, remediate);  
    }    

    for (FloorEntity floor : childFloors) {
      floor.validate(issueTypes, validationMessages, remediate);  
    }    
    
    for (EquipmentEntity equipment : childEquipment) {
      equipment.validate(issueTypes, validationMessages, remediate);  
    }    
  }
  
  public AbstractNodeEntity getChildNodeByNameNullIfNotExists(String name) {
    
    AbstractNodeEntity childNode = getChildSubBuildingByNameNullIfNotExists(name);
    if (childNode == null) {

      childNode = getChildPlantByNameNullIfNotExists(name);
    }

    if (childNode == null) {

      childNode = getChildFloorByNameNullIfNotExists(name);
    }

    if (childNode == null) {

      childNode = getChildEquipmentByNameNullIfNotExists(name);
    }

    if (childNode == null) {

      childNode = getChildPointByNameNullIfNotExists(name);
    }
    
    return childNode;
  }
  
  public void mapToDtos(Map<String, Object> dtos) {
    
    PortfolioDtoMapper.mapNonPointNodeDto(this, dtos);
    
    for (SubBuildingEntity subBuilding: childSubBuildings) {
      if (!subBuilding.getIsDeleted()) {
        subBuilding.mapToDtos(dtos);  
      }
    }
    
    for (PlantEntity energyExchangePlant : childPlants) {
      if (!energyExchangePlant.getIsDeleted()) {
        energyExchangePlant.mapToDtos(dtos);  
      }
    }
    
    for (FloorEntity floor: childFloors) {
      if (!floor.getIsDeleted()) {
        floor.mapToDtos(dtos);  
      }
    }
    
    for (EquipmentEntity equipment: childEquipment) {
      if (!equipment.getIsDeleted()) {
        equipment.mapToDtos(dtos);  
      }
    }

    for (AbstractPointEntity point: getChildPoints()) {
      if (!point.getIsDeleted()) {
        point.mapToDtos(dtos);  
      }
    }

    for (ReportInstanceEntity reportInstance: reportInstances) {
      PortfolioDtoMapper.mapReportInstanceToDtos(reportInstance, dtos);  
    }    
  }
}
//@formatter:on