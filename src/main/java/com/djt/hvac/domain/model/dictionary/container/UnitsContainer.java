package com.djt.hvac.domain.model.dictionary.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.dto.IdNameDto;
import com.djt.hvac.domain.model.dictionary.enums.AggregatorType;

public class UnitsContainer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(UnitsContainer.class);

  private static Map<String, Integer> NIAGARA_UNIT_MAPPINGS = new HashMap<>();
  static {
    NIAGARA_UNIT_MAPPINGS.put("", 1);
    NIAGARA_UNIT_MAPPINGS.put(" ", 1);
    NIAGARA_UNIT_MAPPINGS.put("ampere",2);
    NIAGARA_UNIT_MAPPINGS.put("btu",3);
    NIAGARA_UNIT_MAPPINGS.put("btu per pound", 4);
    NIAGARA_UNIT_MAPPINGS.put("btus per hour", 5);
    NIAGARA_UNIT_MAPPINGS.put("btus per pound",4);
    NIAGARA_UNIT_MAPPINGS.put("btus per pound dry air",6);
    NIAGARA_UNIT_MAPPINGS.put("celsius",7);
    NIAGARA_UNIT_MAPPINGS.put("cubic feet per minute",8);
    NIAGARA_UNIT_MAPPINGS.put("cubic foot",9);
    NIAGARA_UNIT_MAPPINGS.put("day",10);
    NIAGARA_UNIT_MAPPINGS.put("degrees angular",11);
    NIAGARA_UNIT_MAPPINGS.put("dollar",12);
    NIAGARA_UNIT_MAPPINGS.put("fahrenheit",13);
    NIAGARA_UNIT_MAPPINGS.put("fahrenheit degrees differential",14);
    NIAGARA_UNIT_MAPPINGS.put("gallon",15);
    NIAGARA_UNIT_MAPPINGS.put("gallons per minute",16);
    NIAGARA_UNIT_MAPPINGS.put("hertz",18);
    NIAGARA_UNIT_MAPPINGS.put("hour",19);
    NIAGARA_UNIT_MAPPINGS.put("hundred cubic feet",20);
    NIAGARA_UNIT_MAPPINGS.put("hundred cubic feet per minute",21);
    NIAGARA_UNIT_MAPPINGS.put("inch",22);
    NIAGARA_UNIT_MAPPINGS.put("inches of mercury",23);
    NIAGARA_UNIT_MAPPINGS.put("inches of water",24);
    NIAGARA_UNIT_MAPPINGS.put("inches of water differential",25);
    NIAGARA_UNIT_MAPPINGS.put("kilobtu",26);
    NIAGARA_UNIT_MAPPINGS.put("kilobtus per hour",27);
    NIAGARA_UNIT_MAPPINGS.put("kilobtus per square foot",28);
    NIAGARA_UNIT_MAPPINGS.put("kilovolt ampere",29);
    NIAGARA_UNIT_MAPPINGS.put("kilovolt ampere hour",30);
    NIAGARA_UNIT_MAPPINGS.put("kilovolt ampere reactive",31);
    NIAGARA_UNIT_MAPPINGS.put("kilovolt ampere reactive hour",32);
    NIAGARA_UNIT_MAPPINGS.put("kilowatt",33);
    NIAGARA_UNIT_MAPPINGS.put("kilowatt hour",34);
    NIAGARA_UNIT_MAPPINGS.put("kilowatt hours per square foot",35);
    NIAGARA_UNIT_MAPPINGS.put("megabtu",36);
    NIAGARA_UNIT_MAPPINGS.put("megabtus per hour",37);
    NIAGARA_UNIT_MAPPINGS.put("megawatt hour",38);
    NIAGARA_UNIT_MAPPINGS.put("metric ton",39);
    NIAGARA_UNIT_MAPPINGS.put("milliampere",40);
    NIAGARA_UNIT_MAPPINGS.put("minute",41);
    NIAGARA_UNIT_MAPPINGS.put("parts per million",42);
    NIAGARA_UNIT_MAPPINGS.put("percent",43);
    NIAGARA_UNIT_MAPPINGS.put("percent relative humidity",44);
    NIAGARA_UNIT_MAPPINGS.put("pound",45);
    NIAGARA_UNIT_MAPPINGS.put("pounds per hour",46);
    NIAGARA_UNIT_MAPPINGS.put("pounds per square inch",47);
    NIAGARA_UNIT_MAPPINGS.put("pounds per square inch differential",48);
    NIAGARA_UNIT_MAPPINGS.put("power factor",49);
    NIAGARA_UNIT_MAPPINGS.put("revolutions per minute",50);
    NIAGARA_UNIT_MAPPINGS.put("second",51);
    NIAGARA_UNIT_MAPPINGS.put("square foot",52);
    NIAGARA_UNIT_MAPPINGS.put("square inch",53);
    NIAGARA_UNIT_MAPPINGS.put("tons refrigeration",54);
    NIAGARA_UNIT_MAPPINGS.put("tons refrigeration hour",55);
    NIAGARA_UNIT_MAPPINGS.put("volt",56);
    NIAGARA_UNIT_MAPPINGS.put("volt ampere",57);
    NIAGARA_UNIT_MAPPINGS.put("volt ampere hour",58);
    NIAGARA_UNIT_MAPPINGS.put("volt ampere reactive",59);
    NIAGARA_UNIT_MAPPINGS.put("volt ampere reactive hour",60);
    NIAGARA_UNIT_MAPPINGS.put("watt",61);
    NIAGARA_UNIT_MAPPINGS.put("a",2);
    NIAGARA_UNIT_MAPPINGS.put("btu/lb",4);
    NIAGARA_UNIT_MAPPINGS.put("btu/h",5);
    NIAGARA_UNIT_MAPPINGS.put("btu/lb_dry",6);
    NIAGARA_UNIT_MAPPINGS.put("°c",7);
    NIAGARA_UNIT_MAPPINGS.put("cfm",8);
    NIAGARA_UNIT_MAPPINGS.put("cf",9);
    NIAGARA_UNIT_MAPPINGS.put("deg",11);
    NIAGARA_UNIT_MAPPINGS.put("$",12);
    NIAGARA_UNIT_MAPPINGS.put("°f",13);
    NIAGARA_UNIT_MAPPINGS.put("?°f",14);
    NIAGARA_UNIT_MAPPINGS.put("gal",15);
    NIAGARA_UNIT_MAPPINGS.put("gpm",16);
    NIAGARA_UNIT_MAPPINGS.put("hz",18);
    NIAGARA_UNIT_MAPPINGS.put("hr",19);
    NIAGARA_UNIT_MAPPINGS.put("ccf",20);
    NIAGARA_UNIT_MAPPINGS.put("ccf/min",21);
    NIAGARA_UNIT_MAPPINGS.put("in",22);
    NIAGARA_UNIT_MAPPINGS.put("inhg",23);
    NIAGARA_UNIT_MAPPINGS.put("inh2o",24);
    NIAGARA_UNIT_MAPPINGS.put("?inh2o",25);
    NIAGARA_UNIT_MAPPINGS.put("kbtu",26);
    NIAGARA_UNIT_MAPPINGS.put("kbtu/h",27);
    NIAGARA_UNIT_MAPPINGS.put("kbtu/ft2",28);
    NIAGARA_UNIT_MAPPINGS.put("kva",29);
    NIAGARA_UNIT_MAPPINGS.put("kvah",30);
    NIAGARA_UNIT_MAPPINGS.put("kvar",31);
    NIAGARA_UNIT_MAPPINGS.put("kvarh",32);
    NIAGARA_UNIT_MAPPINGS.put("kw",33);
    NIAGARA_UNIT_MAPPINGS.put("kwh",34);
    NIAGARA_UNIT_MAPPINGS.put("kwh/ft2",35);
    NIAGARA_UNIT_MAPPINGS.put("mmbtu",36);
    NIAGARA_UNIT_MAPPINGS.put("mmbtu/h",37);
    NIAGARA_UNIT_MAPPINGS.put("mwh",38);
    NIAGARA_UNIT_MAPPINGS.put("ton",39);
    NIAGARA_UNIT_MAPPINGS.put("ma",40);
    NIAGARA_UNIT_MAPPINGS.put("min",41);
    NIAGARA_UNIT_MAPPINGS.put("ppm",42);
    NIAGARA_UNIT_MAPPINGS.put("%",43);
    NIAGARA_UNIT_MAPPINGS.put("%rh",44);
    NIAGARA_UNIT_MAPPINGS.put("lb",45);
    NIAGARA_UNIT_MAPPINGS.put("lb/h",46);
    NIAGARA_UNIT_MAPPINGS.put("psi",47);
    NIAGARA_UNIT_MAPPINGS.put("?psi",48);
    NIAGARA_UNIT_MAPPINGS.put("pf",49);
    NIAGARA_UNIT_MAPPINGS.put("rpm",50);
    NIAGARA_UNIT_MAPPINGS.put("s",51);
    NIAGARA_UNIT_MAPPINGS.put("ft2",52);
    NIAGARA_UNIT_MAPPINGS.put("in2",53);
    NIAGARA_UNIT_MAPPINGS.put("tonref",54);
    NIAGARA_UNIT_MAPPINGS.put("tonrefh",55);
    NIAGARA_UNIT_MAPPINGS.put("v",56);
    NIAGARA_UNIT_MAPPINGS.put("va",57);
    NIAGARA_UNIT_MAPPINGS.put("vah",58);
    NIAGARA_UNIT_MAPPINGS.put("var",59);
    NIAGARA_UNIT_MAPPINGS.put("varh",60);
    NIAGARA_UNIT_MAPPINGS.put("w",61);
    NIAGARA_UNIT_MAPPINGS.put("angular",63);
    NIAGARA_UNIT_MAPPINGS.put("cu. ft.",65);
    NIAGARA_UNIT_MAPPINGS.put("mcf",67);
    NIAGARA_UNIT_MAPPINGS.put("footcandle",78);
  }

  private final Map<Integer, UnitEntity> units;
  
  public UnitsContainer() {
    super();
    units = new HashMap<>();
  }

  public UnitsContainer(Map<Integer, UnitEntity> units) {
    super();
    this.units = units;
  }
  
  public Set<UnitEntity> getUnits() {
    
    Set<UnitEntity> set = new TreeSet<>();
    set.addAll(units.values());
    return set;
  }
  
  public UnitEntity getUnit(Integer unitId) {
    
    UnitEntity unit = getUnitNullIfNotExists(unitId);
    if (unit == null) {
      throw new IllegalStateException("Unit with id: ["
          + unitId
          + "] does not exist");
    }
    return unit;
  }

  public UnitEntity getUnitNullIfNotExists(Integer unitId) {

    if (unitId == null) {
      throw new IllegalStateException("unitId must be specified.");
    }
    return units.get(unitId);
  }

  public UnitEntity getUnit(String name) throws EntityDoesNotExistException {
    
    UnitEntity unit = getUnitNullIfNotExists(name);
    if (unit != null) {
      return unit;
    }
    throw new EntityDoesNotExistException("Unit with name: ["
        + name
        + "] not found.");
  }  
  
  public UnitEntity getUnitNullIfNotExists(String name) {
    
    if (name == null) {
      throw new IllegalArgumentException("'name' must be specified.");
    }
    
    for (UnitEntity unit: units.values()) {
      if (unit.getName().equals(name)) {
        return unit;
      }
    }
    if (name.trim().isEmpty()) {
      return units.get(Integer.valueOf(1));
    }
    return null;
  }
  
  public void addUnit(UnitEntity unit) {
    
    units.put(unit.getPersistentIdentity(), unit);
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("UnitsContainer [units=")
        .append(units)
        .append("]")
        .toString();
  }

  public static UnitsContainer mapFromDtos(List<IdNameDto> dtoList) {
    
    Map<Integer, UnitEntity> map = new HashMap<>(); 
    Iterator<IdNameDto> iterator = dtoList.iterator();
    while (iterator.hasNext()) {
      
      IdNameDto idNameDto = iterator.next();
      Integer id = idNameDto.getId();
      
      Integer aggregatorId = idNameDto.getAggregator_id();
      AggregatorType aggregatorType = null;
      if (aggregatorId != null && aggregatorId.intValue() > 0) {
        aggregatorType = AggregatorType.get(aggregatorId);
      }
      
      map.put(id, new UnitEntity(id, idNameDto.getName(), aggregatorType));
    }
    return new UnitsContainer(map);
  }
  
  public static List<IdNameDto> mapToDtos(UnitsContainer unitsContainer) {
    
    List<IdNameDto> dtos = new ArrayList<>();
    Iterator<UnitEntity> iterator = unitsContainer.getUnits().iterator();
    while (iterator.hasNext()) {
      
      UnitEntity entity = iterator.next();
      dtos.add(IdNameDto
          .builder()
          .withId(entity.getPersistentIdentity())
          .withName(entity.getName())
          .withAggregator_id(entity.getAggregatorType().getId())
          .build());
    }
    return dtos;
  }  
  
  public UnitEntity getUnitFromNiagaraUnitType(String unitType) {
    
    if (unitType == null) {
      unitType = "";
    }
    
    Integer unitId = NIAGARA_UNIT_MAPPINGS.get(unitType.trim().toLowerCase());
    UnitEntity unit = null;
    if (unitId != null) {
      unit = getUnitNullIfNotExists(unitId);
    }
    
    if (unit == null) {
      LOGGER.warn("No niagara unit type to resolute unit mapping found for: [{}], using default.",
          unitType);
      unit = getUnit(Integer.valueOf(1));
    }
    return unit;
  }
}