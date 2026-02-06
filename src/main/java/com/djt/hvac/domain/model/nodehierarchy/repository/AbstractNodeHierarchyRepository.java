//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.repository;

import static java.util.Objects.requireNonNull;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepository;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;

public abstract class AbstractNodeHierarchyRepository implements NodeHierarchyRepository {

  protected RawPointRepository rawPointRepository;
  protected CustomerRepository customerRepository;
  protected DictionaryRepository dictionaryRepository;

  public AbstractNodeHierarchyRepository(
      RawPointRepository rawPointRepository,
      CustomerRepository customerRepository,
      DictionaryRepository dictionaryRepository) {

    requireNonNull(rawPointRepository, "rawPointRepository cannot be null");
    this.rawPointRepository = rawPointRepository;
    
    requireNonNull(customerRepository, "customerRepository cannot be null");
    this.customerRepository = customerRepository;
    
    if (dictionaryRepository != null) {
      this.dictionaryRepository = dictionaryRepository;
    } else {
      this.dictionaryRepository = new DictionaryRepositoryFileSystemImpl();
    }
  }
  
  @Override
  public PortfolioEntity loadPortfolio(int customerId) throws EntityDoesNotExistException {

    return loadPortfolio(
        LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(Boolean.TRUE)
        .build()); 
  }
}
//@formatter:on