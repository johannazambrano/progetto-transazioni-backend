package org.acme.layout.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.exception.MapperException;
import org.acme.layout.dto.FiltroDTO;
import org.acme.layout.entity.Filtro;
import org.acme.util.mapper.AbstractMapperComponent;

@ApplicationScoped
public class FiltroMapperImpl extends AbstractMapperComponent<FiltroDTO, Filtro> {
    @Override
    public FiltroDTO convertEntityToDto(Filtro entity) throws MapperException {
        if (entity != null) {
            FiltroDTO dto = FiltroDTO.builder()
                    .layoutName(entity.getLayoutName())
                    .isDefault(entity.getIsDefault())
                    .build();
            return dto;
        }
        return null;
    }

    @Override
    public Filtro convertDtoToEntity(FiltroDTO dto) throws MapperException {
        if (dto != null) {
            Filtro entity = new Filtro();
            entity.setLayoutName(dto.getLayoutName());
            entity.setIsDefault(dto.getIsDefault());
            return entity;
        }
        return null;
    }
}
