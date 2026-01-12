package org.acme.transaction.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.category.mapper.CategoryMapperImpl;
import org.acme.exception.MapperException;
import org.acme.transaction.dto.TransactionDTO;
import org.acme.transaction.entity.Transaction;
import org.acme.util.mapper.AbstractMapperComponent;
import org.bson.types.ObjectId;

@ApplicationScoped
public class TransactionMapperImpl extends AbstractMapperComponent<TransactionDTO, Transaction> {

    @Inject
    CategoryMapperImpl categoryMapper;

    @Override
    public TransactionDTO convertEntityToDto(Transaction entity) throws MapperException {
        if(entity != null){
            TransactionDTO dto = TransactionDTO.builder()
                    .title(entity.getTitle())
                    .amount(entity.getAmount())
                    .category(categoryMapper.convertEntityToDto(entity.getCategory()))
                    .date(entity.getDate())
                    .id(entity.getId().toHexString())
                    .build();
            return dto;
        }else{
            return null;
        }
    }

    @Override
    public Transaction convertDtoToEntity(TransactionDTO dto) throws MapperException {
        try {
            if (dto != null) {
                Transaction entity = new Transaction();
                if (dto.getId() != null) {
                    entity.setId(new ObjectId(dto.getId()));
                }
                entity.setTitle(dto.getTitle());
                entity.setAmount(dto.getAmount());
                entity.setCategory(categoryMapper.convertDtoToEntity(dto.getCategory()));
                entity.setDate(dto.getDate());
                return entity;
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new MapperException("Errore in mapper Transaction " + ex.getMessage());
        }
    }
}
