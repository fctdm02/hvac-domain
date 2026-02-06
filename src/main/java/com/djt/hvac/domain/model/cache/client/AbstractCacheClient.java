//@formatter:off
package com.djt.hvac.domain.model.cache.client;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.djt.hvac.domain.model.cache.kryo.KryoSerialize;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;

public abstract class AbstractCacheClient implements CacheClient {
  
  @Override
  public PortfolioEntity loadPortfolio(
      LoadPortfolioOptions loadPortfolioOptions, 
      Timestamp tsRepositoryUpdatedAt) {
    
    PortfolioEntity portfolio = null;
    
    if (tsRepositoryUpdatedAt != null) {

      // See if the cached portfolio is stale by comparing its timestamp to the repository (assumed to be the master of record).
      // Store timestamp in separate key/value to avoid loading the entire graph (when stale).
      LocalDateTime repositoryUpdatedAt = tsRepositoryUpdatedAt.toLocalDateTime().truncatedTo(ChronoUnit.SECONDS);
      LocalDateTime cacheUpdatedAt = loadPortfolioNodeUpdatedAtFromCache(loadPortfolioOptions);
      
      if (cacheUpdatedAt == null || cacheUpdatedAt.equals(repositoryUpdatedAt)) {
      
        byte[] bytes = get(generateLoadPortfolioCacheKey(loadPortfolioOptions));
        if (bytes != null) {
        
          portfolio = KryoSerialize.getInstance().decode(bytes, PortfolioEntity.class);
        }
      }
    }
    
    return portfolio;
  }
  
  @Override
  public void storePortfolio(
      LoadPortfolioOptions loadPortfolioOptions,
      PortfolioEntity portfolio) {

    // Store the portfolio using the loadPortfolioOptions as the key.
    set(generateLoadPortfolioCacheKey(
        loadPortfolioOptions), 
        KryoSerialize
            .getInstance()
            .encode(portfolio),
       loadPortfolioOptions.getTimeToLiveInSeconds());
    
    // Store the portfolio timestamp to a separate key for use with "getPortfolio()" above.
    storePortfolioNodeUpdatedAtToCache(
        loadPortfolioOptions,
        portfolio.getUpdatedAt(),
        loadPortfolioOptions.getTimeToLiveInSeconds());
  }
  
  @Override
  public String generateLoadPortfolioCacheKey(LoadPortfolioOptions loadPortfolioOptions) {
   
    return "portfolio_" + loadPortfolioOptions.toString();
  }
  
  @Override
  public int removeAllCacheEntriesForCustomer(Integer customerId) {
    
    return removeAllCacheEntries("customerId=" + customerId.toString());
  }
  
  private LocalDateTime loadPortfolioNodeUpdatedAtFromCache(LoadPortfolioOptions loadPortfolioOptions) {
    
    try {
      
      LocalDateTime value = null;
      byte[] bytes = get(PORTFOLIO_NODE_UPDATED_AT_KEY_PREFIX + loadPortfolioOptions.toString());
      if (bytes != null) {
        value = AbstractEntity.parseTimestamp(new String(bytes)).toLocalDateTime();
      }
      return value;
    } catch (Exception e) {
      throw new RuntimeException("Error loading portfolio updated at timestamp from cache for: " + loadPortfolioOptions, e);
    }     
  }

  private void storePortfolioNodeUpdatedAtToCache(
      LoadPortfolioOptions loadPortfolioOptions,
      Timestamp updatedAt,
      Integer timeToLiveInSeconds) {
    
    try {

      set(
          PORTFOLIO_NODE_UPDATED_AT_KEY_PREFIX + loadPortfolioOptions.toString(),
          updatedAt.toLocalDateTime().truncatedTo(ChronoUnit.SECONDS).toString().getBytes(),
          timeToLiveInSeconds);
      
    } catch (Exception e) {
      throw new RuntimeException("Error storing portfolio updated at timestamp to cache for: " + loadPortfolioOptions, e);
    }     
  }  
}
//@formatter:on