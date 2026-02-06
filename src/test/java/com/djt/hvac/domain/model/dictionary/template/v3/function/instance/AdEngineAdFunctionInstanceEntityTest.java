package com.djt.hvac.domain.model.dictionary.template.v3.function.instance;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.container.UnitsContainer;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionModuleEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionTemplateOutputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.AdFunctionTemplateInputPointGroupEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.DoubleAdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceOutputPointEntity;

public class AdEngineAdFunctionInstanceEntityTest extends AbstractResoluteDomainModelTest {

  @BeforeClass
  public static void beforeClass() throws Exception {
    AbstractResoluteDomainModelTest.beforeClass();
  }
  
  @Test
  public void evaluate() throws Exception {
    
    // STEP 1: ARRANGE
    UnitsContainer unitsContainer = dictionaryService.getUnitsContainer();
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    NodeTagTemplatesContainer pointTemplatesContainer = dictionaryService.getNodeTagTemplatesContainer();
    
    // Function module
    AdFunctionModuleEntity adFunctionModule = new AdFunctionModuleEntity(
        Integer.valueOf(31),
        "Zone_Temperature_Setpoint_Dead_Band_Is_Too_Low",
        "The zone temperature setpoint dead band is too low. A minimum difference of 5°F between cooling and heating setpoints is required by ASHRAE Standard 90.1 2016 - Section 6.4.3.1.2 Dead Band.",
        FunctionType.RULE);
        
    // Function module output
    adFunctionModule.addOutputPoint(new AdFunctionTemplateOutputPointEntity(
        Integer.valueOf(154),
        adFunctionModule,
        Integer.valueOf(1),
        "Anomaly Detected",
        DataType.BOOLEAN,
        unitsContainer.getUnit(1),
        "{\"trueText\":\"On\",\"falseText\":\"Off\"}",
        new HashSet<>()));
    
    // Function module input point group: EffPoints
    AdFunctionTemplateInputPointGroupEntity effInputPointGroup = new AdFunctionTemplateInputPointGroupEntity(
        Integer.valueOf(1),
        adFunctionModule,
        "EffPoints",
        Integer.valueOf(1),
        "(EffCoolSp - EffHeatSp) < DEADBAND");
    
    adFunctionModule.addInputPointGroup(effInputPointGroup);
    
    effInputPointGroup.addInputPoint(new DoubleAdFunctionTemplateInputPointEntity(
        Integer.valueOf(464),
        effInputPointGroup,
        "EffCoolSp",
        "Effective Cooling Setpoint",
        unitsContainer.getUnit("°F"),
        null,
        Boolean.FALSE,
        Integer.valueOf(1),
        tagsContainer.getTagsByTagNames("air, cooling, effective, sp, temp, zone")));    

    effInputPointGroup.addInputPoint(new DoubleAdFunctionTemplateInputPointEntity(
        Integer.valueOf(465),
        effInputPointGroup,
        "EffHeatSp",
        "Effective Heating Setpoint",
        unitsContainer.getUnit("°F"),
        null,
        Boolean.FALSE,
        Integer.valueOf(2),
        tagsContainer.getTagsByTagNames("air, effective, heating, sp, temp, zone")));    
    
    // Function module input point group: OccPoints
    AdFunctionTemplateInputPointGroupEntity occInputPointGroup = new AdFunctionTemplateInputPointGroupEntity(
        Integer.valueOf(2),
        adFunctionModule,
        "OccPoints",
        Integer.valueOf(2),
        "(OccCoolSp - OccHeatSp) < DEADBAND");
    adFunctionModule.addInputPointGroup(occInputPointGroup);

    occInputPointGroup.addInputPoint(new DoubleAdFunctionTemplateInputPointEntity(
        Integer.valueOf(462),
        occInputPointGroup,
        "OccCoolSp",
        "Occupied Cooling Setpoint",
        unitsContainer.getUnit("°F"),
        null,
        Boolean.FALSE,
        Integer.valueOf(1),
        tagsContainer.getTagsByTagNames("air, cooling, occ, sp, temp, zone")));    

    occInputPointGroup.addInputPoint(new DoubleAdFunctionTemplateInputPointEntity(
        Integer.valueOf(463),
        occInputPointGroup,
        "OccHeatSp",
        "Occupied Heating Setpoint",
        unitsContainer.getUnit("°F"),
        null,
        Boolean.FALSE,
        Integer.valueOf(2),
        tagsContainer.getTagsByTagNames("air, heating, occ, sp, temp, zone")));
    
    // Function module constants
    adFunctionModule.addInputConstant(new AdFunctionTemplateInputConstantEntity(
        Integer.valueOf(446),
        adFunctionModule,
        Integer.valueOf(1),
        "DEADBAND",
        "Minimum Temperature Dead Band Allowed",
        "5",
        DataType.NUMERIC,
        pointTemplatesContainer.getPointTemplateByName("EffCoolSp")));

    adFunctionModule.addInputConstant(new AdFunctionTemplateInputConstantEntity(
        Integer.valueOf(447),
        adFunctionModule,
        Integer.valueOf(2),
        "DELAY",
        "Delay Threshold",
        "15",
        DataType.NUMERIC,
        pointTemplatesContainer.getPointTemplateByName("EffCoolSp")));
    
    
    // Once everything has been constructed for the AD function template, call the method to "normalize" 
    // the fault expressions (i.e. replace point names with generic parameter names, such as P1, P2, ... PN
    // and input constant names with generic names, such as C1, C2, ... CN.
    adFunctionModule.normalizeFaultExpressionTemplates();
    
    
    // Function template (NOTE: The template is nothing more than an association relationship, but whose identity
    // is derived from a combination of the function module, energy exhange type and fault number.
    String nodeFilterExpression = null;
    AdFunctionTemplateEntity adFunctionTemplate = new AdFunctionTemplateEntity(
        Integer.valueOf(154),
        adFunctionModule,
        tagsContainer.getEnergyExchangeTypeByName("fcu"),
        "3.3.8.1",
        nodeFilterExpression);
    
    
    // Function instance: EffPoints (NOTE: Not the same thing as a node hierarchy function instance)
    // Here, there are "soft links" to the energy exchange node path and input point metric ids.
    // The assumption here is that the equipment type for the equipment with the given node path matches that
    // of the AD function template (in this case, fcu)
    AdEngineAdFunctionInstanceEntity adEngineAdFunctionInstanceEffPoints = new AdEngineAdFunctionInstanceEntity(
        adFunctionTemplate,
        "EffPoints",
        "McLaren/Bay Region/Test Equip Eff Points");
    
    adEngineAdFunctionInstanceEffPoints.addInputConstant(new AdEngineAdFunctionInstanceInputConstantEntity(
        adEngineAdFunctionInstanceEffPoints, 
        adFunctionTemplate.getAdFunction().getInputConstant("DELAY"),
        adFunctionTemplate.getAdFunction().getInputConstant("DELAY").getDefaultValue()));
    
    adEngineAdFunctionInstanceEffPoints.addInputConstant(new AdEngineAdFunctionInstanceInputConstantEntity(
        adEngineAdFunctionInstanceEffPoints, 
        adFunctionTemplate.getAdFunction().getInputConstant("DEADBAND"),
        adFunctionTemplate.getAdFunction().getInputConstant("DEADBAND").getDefaultValue()));
    
    adEngineAdFunctionInstanceEffPoints.addOutputPoint(new AdEngineAdFunctionInstanceOutputPointEntity(
        adEngineAdFunctionInstanceEffPoints, 
        adFunctionTemplate.getAdFunction().getOutputPoint(154),
        "McLaren/Bay Region/Test Equip Eff Points/Zone_Temperature_Setpoint_Dead_Band_Is_Too_Low/1"));
    
    AdEngineAdFunctionInstanceInputPointEntity EffCoolSp = new AdEngineAdFunctionInstanceInputPointEntity(
        adEngineAdFunctionInstanceEffPoints, 
        adFunctionTemplate.getAdFunction().getInputPointGroup("EffPoints").getInputPoint(464),
        "McLaren/Bay Region/Test Equip Eff Points/EffCoolSp",
        Integer.valueOf(1)); 
    adEngineAdFunctionInstanceEffPoints.addInputPoint(EffCoolSp);

    AdEngineAdFunctionInstanceInputPointEntity EffHeatSp = new AdEngineAdFunctionInstanceInputPointEntity(
        adEngineAdFunctionInstanceEffPoints, 
        adFunctionTemplate.getAdFunction().getInputPointGroup("EffPoints").getInputPoint(465),
        "McLaren/Bay Region/Test Equip Eff Points/EffHeatSp",
        Integer.valueOf(1)); 
    adEngineAdFunctionInstanceEffPoints.addInputPoint(EffHeatSp);
    
    
    
    
    // STEPS 2/3: ACT/ASSERT
    
    // OPTION 1: Evaluate the data, one timestamp at a time, passing in the data (data values from ad-rules)
    adEngineAdFunctionInstanceEffPoints.getInputConstant("DELAY").setValue("30");
    adEngineAdFunctionInstanceEffPoints.getInputConstant("DEADBAND").setValue("5");
    
    Long timestamp = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp("9/1/18 0:00");
    Map<String, String> instanceInputPoints = new LinkedHashMap<>();
    instanceInputPoints.put("EffCoolSp", "78");
    instanceInputPoints.put("EffHeatSp", "73");
    boolean result = adEngineAdFunctionInstanceEffPoints.evaluate(timestamp, instanceInputPoints);
    Assert.assertEquals("result is incorrect: " + result, Boolean.FALSE, Boolean.valueOf(result));

    timestamp = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp("9/1/18 0:15");
    instanceInputPoints.clear();
    instanceInputPoints.put("EffCoolSp", "78");
    instanceInputPoints.put("EffHeatSp", "73");
    result = adEngineAdFunctionInstanceEffPoints.evaluate(timestamp, instanceInputPoints);
    Assert.assertEquals("result is incorrect: " + result, Boolean.FALSE, Boolean.valueOf(result));

    timestamp = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp("9/1/18 0:30");
    instanceInputPoints.clear();
    instanceInputPoints.put("EffCoolSp", "78");
    instanceInputPoints.put("EffHeatSp", "74");
    result = adEngineAdFunctionInstanceEffPoints.evaluate(timestamp, instanceInputPoints);
    Assert.assertEquals("result is incorrect: " + result, Boolean.FALSE, Boolean.valueOf(result));

    timestamp = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp("9/1/18 0:45");
    instanceInputPoints.clear();
    instanceInputPoints.put("EffCoolSp", "78");
    instanceInputPoints.put("EffHeatSp", "74");
    result = adEngineAdFunctionInstanceEffPoints.evaluate(timestamp, instanceInputPoints);
    Assert.assertEquals("result is incorrect: " + result, Boolean.FALSE, Boolean.valueOf(result));

    timestamp = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp("9/1/18 1:00");
    instanceInputPoints.clear();
    instanceInputPoints.put("EffCoolSp", "78");
    instanceInputPoints.put("EffHeatSp", "75");
    result = adEngineAdFunctionInstanceEffPoints.evaluate(timestamp, instanceInputPoints);
    Assert.assertEquals("result is incorrect", Boolean.TRUE, Boolean.valueOf(result));

    
    // OPTION 2: Evaluate the data in chunks.  Here, we literally load the same CSV test data from ad-rules
    SortedMap<Long, Boolean> expectedResults = AdEngineAdFunctionInstanceTestDataReader.parseTestDataFile(adEngineAdFunctionInstanceEffPoints);
    SortedMap<Long, Boolean> actualResults = adEngineAdFunctionInstanceEffPoints.evaluate();
    Assert.assertEquals("results are incorrect", expectedResults, actualResults);
 }
}





// Function instance: OccPoints (NOTE: Not the same thing as a node hierarchy function instance)
/*
AdEngineAdFunctionInstanceEntity adEngineAdFunctionInstanceOccPoints = new AdEngineAdFunctionInstanceEntity(
    adFunctionTemplate,
    "OccPoints",
    "McLaren/Bay Region/Test Equip Occ Points");

adEngineAdFunctionInstanceOccPoints.addInputConstant(new AdEngineAdFunctionInstanceInputConstantEntity(
    adEngineAdFunctionInstanceOccPoints, 
    adFunctionTemplate.getAdFunction().getInputConstant("DELAY"),
    "30"));

adEngineAdFunctionInstanceOccPoints.addInputConstant(new AdEngineAdFunctionInstanceInputConstantEntity(
    adEngineAdFunctionInstanceOccPoints, 
    adFunctionTemplate.getAdFunction().getInputConstant("DEADBAND"),
    "5"));

adEngineAdFunctionInstanceOccPoints.addOutputPoint(new AdEngineAdFunctionInstanceOutputPointEntity(
    adEngineAdFunctionInstanceOccPoints, 
    adFunctionTemplate.getAdFunction().getOutputPoint(154),
    "McLaren/Bay Region/Test Equip Occ Points/Zone_Temperature_Setpoint_Dead_Band_Is_Too_Low/1"));

AdEngineAdFunctionInstanceInputPointEntity OccCoolSp = new AdEngineAdFunctionInstanceInputPointEntity(
    adEngineAdFunctionInstanceOccPoints, 
    adFunctionTemplate.getAdFunction().getInputPointGroup("OccPoints").getInputPoint(462),
    "McLaren/Bay Region/Test Equip Occ Points/OccCoolSp",
    Integer.valueOf(1)); 
adEngineAdFunctionInstanceOccPoints.addInputPoint(OccCoolSp);

AdEngineAdFunctionInstanceInputPointEntity OccHeatSp = new AdEngineAdFunctionInstanceInputPointEntity(
    adEngineAdFunctionInstanceOccPoints, 
    adFunctionTemplate.getAdFunction().getInputPointGroup("OccPoints").getInputPoint(463),
    "McLaren/Bay Region/Test Equip Occ Points/OccHeatSp",
    Integer.valueOf(1)); 
adEngineAdFunctionInstanceOccPoints.addInputPoint(OccHeatSp);
*/


/*
SortedMap<Long, Boolean> expectedResults = new TreeMap<>(); 
timestamp = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp("9/1/18 0:00");
EffCoolSp.addValue(timestamp, "78");
EffHeatSp.addValue(timestamp, "73");
expectedResults.put(timestamp, Boolean.FALSE);

timestamp = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp("9/1/18 0:15");
EffCoolSp.addValue(timestamp, "78");
EffHeatSp.addValue(timestamp, "73");
expectedResults.put(timestamp, Boolean.FALSE);

timestamp = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp("9/1/18 0:30");
EffCoolSp.addValue(timestamp, "78");
EffHeatSp.addValue(timestamp, "74");
expectedResults.put(timestamp, Boolean.FALSE);

timestamp = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp("9/1/18 0:45");
EffCoolSp.addValue(timestamp, "78");
EffHeatSp.addValue(timestamp, "74");
expectedResults.put(timestamp, Boolean.FALSE);

timestamp = AdEngineAdFunctionInstanceTestDataReader.parseDateTimeIntoTimestamp("9/1/18 1:00");
EffCoolSp.addValue(timestamp, "78");
EffHeatSp.addValue(timestamp, "75");
expectedResults.put(timestamp, Boolean.TRUE);
*/
