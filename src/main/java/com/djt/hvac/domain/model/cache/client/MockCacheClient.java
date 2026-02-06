//@formatter:off
package com.djt.hvac.domain.model.cache.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.AbstractEntity;

public class MockCacheClient extends AbstractCacheClient {
  
  private static final MockCacheClient INSTANCE = new MockCacheClient();
  public static final MockCacheClient getInstance() {
    return INSTANCE;
  }
  
  private Map<String, byte[]> cacheMap = new TreeMap<>();
  private Map<String, Long> timeToLiveMillisMap = new HashMap<>();
  private Map<String, Long> timeOfSetMillisMap = new HashMap<>();
 
  private MockCacheClient() {
    super();
  }
  
  @Override
  public byte[] get(String key) {
    
    try {
      byte[] value = cacheMap.get(key);
      Long timeToLiveMillis = timeToLiveMillisMap.get(key);
      Long timeOfSetMillis = timeOfSetMillisMap.get(key);
      if (timeToLiveMillis != null 
          && timeOfSetMillis != null
          && ((AbstractEntity.getTimeKeeper().getCurrentTimeInMillis() - timeOfSetMillis)) - timeToLiveMillis > 0) {
        
        value = null;
      }
      return value;
    } catch (Exception e) {
      throw new RuntimeException("Error loading value for key: ["
          + key
          + "], error: ["
          + e.getMessage()
          + "]", e);
    }
  }

  @Override
  public void set(String key, byte[] value, Integer timeToLiveInSeconds) {
    
    try {
      cacheMap.put(key, value);
      timeToLiveMillisMap.put(key, Long.valueOf(timeToLiveInSeconds*1000));
      timeOfSetMillisMap.put(key, AbstractEntity.getTimeKeeper().getCurrentTimeInMillis());
    } catch (Exception e) {
      throw new RuntimeException("Error storing value for key: ["
          + key
          + "], error: ["
          + e.getMessage()
          + "]", e);
    }
  }
  
  @Override
  public void removeCacheEntry(String key) {
    
    cacheMap.remove(key);
    timeToLiveMillisMap.remove(key);
    timeOfSetMillisMap.remove(key);
  }

  @Override
  public int removeAllCacheEntries(String keyPrefix) {
    
    try {
      List<String> victims = new ArrayList<>();
      for (String key: cacheMap.keySet()) {
        if (key.contains(keyPrefix)) {
          victims.add(key);
        }
      }
      for (String key: victims) {
        cacheMap.remove(key);
        timeToLiveMillisMap.remove(key);
        timeOfSetMillisMap.remove(key);
      }
      return victims.size();
    } catch (Exception e) {
      throw new RuntimeException("Error removing cache entries, error: ["
          + e.getMessage()
          + "]", e);
    }
  }
  
  @Override
  public int removeAllCacheEntries() {

    int size = cacheMap.size();
    
    cacheMap.clear();
    timeToLiveMillisMap.clear();
    timeOfSetMillisMap.clear();
    
    return size;
  }  
}
//@formatter:on