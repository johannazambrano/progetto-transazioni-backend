package org.acme.transaction.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.exception.MapperException;
import org.acme.transaction.dto.TransactionResponseDTO;
import org.acme.transaction.entity.TransactionResponse;
import org.acme.util.mapper.AbstractMapperComponent;

@ApplicationScoped
public class TransactionResponseMapperImpl extends AbstractMapperComponent<TransactionResponseDTO, TransactionResponse> {
    @Inject
    TransactionMapperImpl transactionMapper;

    @Override
    public TransactionResponseDTO convertEntityToDto(TransactionResponse entity) throws MapperException {
        if (entity != null) {
            TransactionResponseDTO dto = TransactionResponseDTO.builder()
                    .transactions(transactionMapper.convertEntityToDto(entity.getTransactions()))
                    .paginazione(entity.getPaginazione())
                    .build();
            return dto;
        } else {
            return null;
        }
    }

    @Override
    public TransactionResponse convertDtoToEntity(TransactionResponseDTO dto) throws MapperException {
        if (dto != null) {
            TransactionResponse entity = TransactionResponse.builder()
                    .transactions(transactionMapper.convertDtoToEntity(dto.getTransactions()))
                    .paginazione(dto.getPaginazione())
                    .build();
        }
        return null;
    }
}
