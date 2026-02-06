package com.djt.hvac.domain.model.rawpoint.repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.OutOfBandCustomerEntity;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.rawpoint.dto.RawPointDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class RawPointRepositoryFileSystemImpl implements RawPointRepository {

  //@formatter:off
  private static Integer MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextPersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }
  
  private static boolean USE_PRETTY_PRINT = false;
  public static boolean getPrettyPrint() {
    return USE_PRETTY_PRINT;
  }
  public static void setPrettyPrint(boolean prettyPrint) {
    USE_PRETTY_PRINT = prettyPrint;
  }

  private String basePath;

  public RawPointRepositoryFileSystemImpl() {
    this(null);
  }

  public RawPointRepositoryFileSystemImpl(String basePath) {
    super();
    if (basePath != null) {
      this.basePath = basePath;
    } else {
      this.basePath = System.getProperty("user.home") + "/";      
    }
  }
  
  public String basePath() {
    return basePath;
  }
  
  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  @Override
  public List<RawPointEntity> loadRawPoints(
      Integer customerId,
      List<Integer> rawPointIds) {
    
    Set<Integer> set = new HashSet<Integer>();
    set.addAll(rawPointIds);
    
    OutOfBandCustomerEntity parentCustomer = OutOfBandCustomerEntity.buildCustomerStubForPortfolio(customerId);
    
    List<RawPointEntity> entityList = new ArrayList<>();
    List<RawPointDto> dtoList = loadRawPointDtoList(customerId);
    for (RawPointDto dto: dtoList) {
      
      if (set.contains(dto.getId())) {

        entityList.add(RawPointEntity
            .Mapper
            .getInstance()
            .mapDtoToEntity(parentCustomer, dto));
      }
    }
    return entityList;
  }
  
  @Override
  public List<RawPointEntity> loadRawPoints(AbstractCustomerEntity parentCustomer) {
    
    return loadRawPoints(parentCustomer, true, true, true);
  }
  
  @Override
  public List<RawPointEntity> loadRawPoints(
      AbstractCustomerEntity parentCustomer, 
      boolean loadUnmappedOnly,
      boolean loadIgnored,
      boolean loadDeleted) {

    List<RawPointEntity> entityList = new ArrayList<>();
    List<RawPointDto> dtoList = loadRawPointDtoList(parentCustomer.getPersistentIdentity());
    for (RawPointDto dto: dtoList) {
      
      entityList.add(RawPointEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(parentCustomer, dto));
    }
    return entityList;
  }
  
  @Override
  public void storeRawPoints(int customerId, Collection<RawPointEntity> rawPoints) {

    List<RawPointDto> dtoList = new ArrayList<>();
    for (RawPointEntity rawPoint: rawPoints) {

      Integer id = rawPoint.getPersistentIdentity();
      if (id == null) {
        rawPoint.setPersistentIdentity(getNextPersistentIdentityValue());  
      }
      dtoList.add(RawPointEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(rawPoint));
    }
    storeRawPointDtoList(customerId, dtoList);
  }
  
  @Override
  public List<Integer> ignoreRawPoints(AbstractCustomerEntity parentCustomer, List<Integer> rawPointIds) {
    
    Set<Integer> set = new HashSet<>();
    set.addAll(rawPointIds);
    List<RawPointEntity> rawPoints = loadRawPoints(parentCustomer, true, true, true);

    List<RawPointDto> dtoList = new ArrayList<>();
    for (RawPointEntity rawPoint: rawPoints) {
      
      if (set.contains(rawPoint.getPersistentIdentity())) {
        rawPoint.setIgnored(true);
      }
      
      dtoList.add(RawPointEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(rawPoint));
    }
    storeRawPointDtoList(parentCustomer.getPersistentIdentity(), dtoList);
    return rawPointIds;
  }

  @Override
  public List<Integer> unignoreRawPoints(AbstractCustomerEntity parentCustomer, List<Integer> rawPointIds) {
    
    Set<Integer> set = new HashSet<>();
    set.addAll(rawPointIds);
    List<RawPointEntity> rawPoints = loadRawPoints(parentCustomer, true, true, true);

    List<RawPointDto> dtoList = new ArrayList<>();
    for (RawPointEntity rawPoint: rawPoints) {
      
      if (set.contains(rawPoint.getPersistentIdentity())) {
        rawPoint.setIgnored(false);
      }
      
      dtoList.add(RawPointEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(rawPoint));
    }
    storeRawPointDtoList(parentCustomer.getPersistentIdentity(), dtoList);
    return rawPointIds;
  }
  
  private List<RawPointDto> loadRawPointDtoList(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_RawPoints.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<RawPointDto> dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<RawPointDto>>() {});
          return dtoList;
  
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new ArrayList<>();
  }
  
  private void storeRawPointDtoList(int customerId, List<RawPointDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_RawPoints.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, dtoList);

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
  //@formatter:off
}
