package org.acme.category.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.category.dto.CategoryDTO;
import org.acme.category.entity.Category;
import org.acme.exception.MapperException;
import org.acme.util.mapper.AbstractMapperComponent;
import org.bson.types.ObjectId;

@ApplicationScoped
public class CategoryMapperImpl extends AbstractMapperComponent<CategoryDTO, Category> {

    @Override
    public CategoryDTO convertEntityToDto(Category entity) throws MapperException {
        if(entity != null){
            CategoryDTO dto = CategoryDTO.builder()
                    .descrizione(entity.getDescrizione())
                    .codice(entity.getCodice())
                    .budget(entity.getBudget())
                    .colore(entity.getColore())
                    .id(entity.getId().toHexString())
                    .build();
            return dto;
        }else{
            return null;
        }
    }

    @Override
    public Category convertDtoToEntity(CategoryDTO dto) throws MapperException {
        try{
            if(dto != null){
                Category entity = new Category();

                if(dto.getId() != null){
                    entity.setId(new ObjectId(dto.getId()));
                }

                entity.setDescrizione(dto.getDescrizione());
                entity.setCodice(dto.getCodice());
                entity.setBudget(dto.getBudget());
                entity.setColore(dto.getColore());

                return entity;
            }else{
                return null;
            }
        }catch(Exception e){
            throw new MapperException("Errore in mapper Category" + e.getMessage());
        }
    }
}
