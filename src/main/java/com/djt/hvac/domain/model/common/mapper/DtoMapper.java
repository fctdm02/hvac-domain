package com.djt.hvac.domain.model.common.mapper;

/**
 * 
 * @author tommyers
 *
 * @param <A>
 * @param <E>
 * @param <D>
 */
public interface DtoMapper<A, E, D> {

    /**
     * 
     * @param entity
     * @return dto
     */
    D mapEntityToDto(E entity); 

    /**
     * 
     * @param aggregateRoot
     * @param dto
     * @return entity
     */
    E mapDtoToEntity(A aggregateRoot, D dto);
}