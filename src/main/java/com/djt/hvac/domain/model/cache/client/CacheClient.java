//@formatter:off
package com.djt.hvac.domain.model.cache.client;

import java.sql.Timestamp;

import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;

/**
 * 
 * Simple interface for loading/storing byte arrays in a "cache", keyed by a byte array (limitation of Redis/Lettuce impl)
 * 
 * @author tmyers
 *
 */
public interface CacheClient {
  
  Integer ONE_HOUR_TIME_TO_LIVE = 3600;
  Integer ONE_DAY_TIME_TO_LIVE = 86400;
  Integer ONE_WEEK_TIME_TO_LIVE = 86400 * 7;
  
  String KEY_PREFIX = "node_hierarchy_repository_";
  String PORTFOLIO_NODE_UPDATED_AT_KEY_PREFIX = "portfolio_updated_at_";
  
  /**
   * 
   * @param loadPortfolioOptions The options for loading the portfolio (use case dependent)
   * @param repositoryUpdatedAt The timestamp (assumed to be newly loaded from the DB), that has the latest timestamp of the portfolio
   * @return The portfolio corresponding to the load portfolio options, if present
   */
  PortfolioEntity loadPortfolio(LoadPortfolioOptions loadPortfolioOptions, Timestamp repositoryUpdatedAt);
  
  /**
   * 
   * @param loadPortfolioOptions The options for loading the portfolio (use case dependent)
   * @param portfolio The portfolio to set
   */
  void storePortfolio(LoadPortfolioOptions loadPortfolioOptions, PortfolioEntity portfolio);
  
  /**
   * 
   * @param loadPortfolioOptions The options for loading the portfolio (use case dependent)
   * @return The generated key for the given loadPortfolioOptions
   */
  String generateLoadPortfolioCacheKey(LoadPortfolioOptions loadPortfolioOptions);

  /**
   * 
   * @param key The key
   * @return The value
   */
  byte[] get(String key);
  
  /**
   * 
   * @param key The key
   * @param value The value
   * @param timeToLiveInSeconds The number of seconds that the entry is to be valid/stored in the cache
   */
  void set(String key, byte[] value, Integer timeToLiveInSeconds);

  /**
   * 
   * @param key The cache key to use
   */
  void removeCacheEntry(String key);

  /**
   * 
   * @param customerId The owning customerId
   * @return The number of removed cache entries
   */
  int removeAllCacheEntriesForCustomer(Integer customerId);
  
  /**
   * 
   * @param keyPrefix The cache prefix to use
   * 
   * @return The number of removed cache entries
   */
  int removeAllCacheEntries(String keyPrefix);
  
  /**
   * 
   * @return The number of removed cache entries
   */
  int removeAllCacheEntries();
}
//@formatter:on