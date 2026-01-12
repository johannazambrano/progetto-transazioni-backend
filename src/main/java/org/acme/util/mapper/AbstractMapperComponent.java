package org.acme.util.mapper;

import org.acme.exception.MapperException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s.g.
 */
public abstract class AbstractMapperComponent<T, E> implements MapperComponent<T, E> {

    @Override
    public List<T> convertEntityToDto(List<E> entityList) throws MapperException {
        List<T> dtoList = new ArrayList<>();
        if (entityList != null) {
            for (E entity : entityList) {
                T dto = convertEntityToDto(entity);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public List<E> convertDtoToEntity(List<T> dtoList) throws MapperException {
        List<E> entityList = new ArrayList<>();
        if (dtoList != null) {
            for (T dto : dtoList) {
                E entity = convertDtoToEntity(dto);
                entityList.add(entity);
            }
        }
        return entityList;
    }
}
