package org.acme.util.mapper;

import org.acme.exception.MapperException;
import java.util.List;

public interface MapperComponent<T, E> {

    T convertEntityToDto(E entity) throws MapperException;

    E convertDtoToEntity(T dto) throws MapperException;

    List<T> convertEntityToDto(List<E> entityList) throws MapperException;

    List<E> convertDtoToEntity(List<T> dtoList) throws MapperException;

}
