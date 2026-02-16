package org.acme.layoutItem.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.exception.MapperException;
import org.acme.layoutItem.dto.LayoutItemDTO;
import org.acme.layoutItem.entity.LayoutItem;
import org.acme.util.mapper.AbstractMapperComponent;
import org.bson.types.ObjectId;

@ApplicationScoped
public class LayoutItemMapperImpl extends AbstractMapperComponent<LayoutItemDTO, LayoutItem> {

    @Override
    public LayoutItemDTO convertEntityToDto(LayoutItem entity) throws MapperException {
        if (entity != null) {
            LayoutItemDTO dto = LayoutItemDTO.builder()
                    .i(entity.getI())
                    .x(entity.getX())
                    .y(entity.getY())
                    .w(entity.getW())
                    .h(entity.getH())
                    .minW(entity.getMinW())
                    .maxW(entity.getMaxW())
                    .minH(entity.getMinH())
                    .maxH(entity.getMaxH())
                    .staticLayout(entity.getStaticLayout())
                    .build();
            return dto;
        } else {
            return null;
        }
    }

    @Override
    public LayoutItem convertDtoToEntity(LayoutItemDTO dto) throws MapperException {
        try {
            if (dto != null) {
                LayoutItem entity = new LayoutItem();
                // LayoutItem è embedded, quindi l'ID potrebbe non servire o essere generato diversamente,
                // ma se presente nel DTO lo mappiamo (anche se nel DTO non c'è ID esplicito tranne 'i' che sembra un identificativo logico)
                // entity.setId(new ObjectId(dto.getId())); // LayoutItemDTO non ha un campo 'id' ObjectId, ha 'i' stringa.
                
                entity.setI(dto.getI());
                entity.setX(dto.getX());
                entity.setY(dto.getY());
                entity.setW(dto.getW());
                entity.setH(dto.getH());
                entity.setMinW(dto.getMinW());
                entity.setMaxW(dto.getMaxW());
                entity.setMinH(dto.getMinH());
                entity.setMaxH(dto.getMaxH());
                entity.setStaticLayout(dto.getStaticLayout());
                return entity;
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new MapperException("Errore in mapper LayoutItem " + ex.getMessage());
        }
    }
}
