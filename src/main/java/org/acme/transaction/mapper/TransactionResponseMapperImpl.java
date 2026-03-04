package org.acme.transaction.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.api.dto.PaginazioneDTO;
import org.acme.exception.MapperException;
import org.acme.transaction.dto.TransactionResponseDTO;
import org.acme.transaction.entity.TransactionResponse;
import org.acme.util.entity.Paginazione;
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
                    .paginazione(convertPaginazioneToDto(entity.getPaginazione()))
                    .build();
            return dto;
        } else {
            return null;
        }
    }

    @Override
    public TransactionResponse convertDtoToEntity(TransactionResponseDTO dto) throws MapperException {
        if (dto != null) {
            return TransactionResponse.builder()
                    .transactions(transactionMapper.convertDtoToEntity(dto.getTransactions()))
                    .paginazione(convertDtoToPaginazione(dto.getPaginazione()))
                    .build();
        }
        return null;
    }

    private PaginazioneDTO convertPaginazioneToDto(Paginazione paginazione) {
        if (paginazione == null) {
            return null;
        }
        return PaginazioneDTO.builder()
                .numeroPagina(paginazione.getNumeroPagina())
                .risultatiPagina(paginazione.getRisultatiPagina())
                .numeroPagTotali(paginazione.getNumeroPagTotali())
                .numeroRisTotali(paginazione.getNumeroRisTotali())
                .numeroElementiPerPagina(paginazione.getNumeroElementiPerPagina())
                .build();
    }

    private Paginazione convertDtoToPaginazione(PaginazioneDTO dto) {
        if (dto == null) {
            return null;
        }
        return Paginazione.builder()
                .numeroPagina(dto.getNumeroPagina())
                .risultatiPagina(dto.getRisultatiPagina())
                .numeroPagTotali(dto.getNumeroPagTotali())
                .numeroRisTotali(dto.getNumeroRisTotali())
                .numeroElementiPerPagina(dto.getNumeroElementiPerPagina())
                .build();
    }
}
