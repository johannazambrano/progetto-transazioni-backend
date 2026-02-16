package org.acme.layout.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.exception.MapperException;
import org.acme.layout.dto.LayoutDTO;
import org.acme.layout.entity.Layout;
import org.acme.layoutItem.mapper.LayoutItemMapperImpl;
import org.acme.util.mapper.AbstractMapperComponent;
import org.bson.types.ObjectId;

@ApplicationScoped
public class LayoutMapperImpl extends AbstractMapperComponent<LayoutDTO, Layout> {

    @Inject
    LayoutItemMapperImpl layoutItemMapper;

    @Override
    public LayoutDTO convertEntityToDto(Layout entity) throws MapperException {
        if (entity != null) {
            LayoutDTO dto = LayoutDTO.builder()
                    .id(entity.getId() != null ? entity.getId().toHexString() : null)
                    .layoutName(entity.getLayoutName())
                    .layoutItems(layoutItemMapper.convertEntityToDto(entity.getLayoutItems()))
                    .updatedAt(entity.getUpdatedAt())
                    .isDefault(entity.getIsDefault())
                    .build();
            return dto;
        } else {
            return null;
        }
    }

    @Override
    public Layout convertDtoToEntity(LayoutDTO dto) throws MapperException {
        try {
            if (dto != null) {
                Layout entity = new Layout();
                if (dto.getId() != null && !dto.getId().isEmpty()) {
                    entity.setId(new ObjectId(dto.getId()));
                }
                entity.setLayoutName(dto.getLayoutName());
                entity.setLayoutItems(layoutItemMapper.convertDtoToEntity(dto.getLayoutItems()));
                entity.setUpdatedAt(dto.getUpdatedAt());
                entity.setIsDefault(dto.getIsDefault());
                return entity;
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new MapperException("Errore in mapper Layout " + ex.getMessage());
        }
    }
}
