package org.acme.transaction;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.apachecommons.CommonsLog;
import org.acme.api.dto.FiltroRicercaTransactionDTO;
import org.acme.api.dto.PaginazioneDTO;
import org.acme.exception.ServiceException;
import org.acme.transaction.entity.Transaction;
import org.acme.transaction.entity.TransactionResponse;

@ApplicationScoped
@CommonsLog
public class TransactionRepository implements PanacheMongoRepository<Transaction> {

    public TransactionResponse ricercaTransaction(FiltroRicercaTransactionDTO filtroTransactionDTO) throws ServiceException {
        log.info("[TransactionRepository.ricercaTransaction] filtro: " + filtroTransactionDTO);

        String title = filtroTransactionDTO.getTitle();
        Double amount = filtroTransactionDTO.getAmount();
        String category = filtroTransactionDTO.getCategory();
        String startDate = filtroTransactionDTO.getStartDate();
        String endDate = filtroTransactionDTO.getEndDate();
        
        // Validazione delle date
        if (startDate != null && !startDate.isBlank() && endDate != null && !endDate.isBlank()) {
            if (startDate.compareTo(endDate) > 0) {
                throw new ServiceException("La data di inizio non può essere successiva alla data di fine.");
            }
        }
        
        TransactionResponse transactionResponse = new TransactionResponse();

        StringBuilder query = new StringBuilder();
        Parameters params = new Parameters();

        if (title != null && !title.isBlank()) {
            query.append("title like :title");
            params.and("title", "(?i).*" + title + ".*");
        }
        
        // Gestione range date
        if (startDate != null && !startDate.isBlank()) {
            if (!query.isEmpty()) {
                query.append(" and ");
            }
            query.append("date >= :startDate");
            params.and("startDate", startDate);
        }

        if (endDate != null && !endDate.isBlank()) {
            if (!query.isEmpty()) {
                query.append(" and ");
            }
            query.append("date <= :endDate");
            params.and("endDate", endDate);
        }

        if (category != null && !category.isBlank()) {
            if (!query.isEmpty()) {
                query.append(" and ");
            }
            // Corretto da 'categories.descrizione' a 'category.descrizione'
            // Inoltre cerco anche per codice per essere più robusto
            query.append("(category.descrizione like :category or category.codice like :category)");
            params.and("category", "(?i).*" + category + ".*");
        }


        log.info("[TransactionRepository.ricercaTransaction] Query: " + query);

        //Paginazione
        PaginazioneDTO paginazioneDTO;
        if (filtroTransactionDTO.getPaginazione() == null) {
            // Se nel filtro la paginazione non è settata la imposto di default
            PaginazioneDTO paginazioneDefault = new PaginazioneDTO();
            paginazioneDefault.setNumeroPagina(0);
            paginazioneDefault.setNumeroElementiPerPagina(10);
            paginazioneDTO = paginazioneDefault;
        } else {
            paginazioneDTO = filtroTransactionDTO.getPaginazione();
        }

        log.info("[TransactionRepository.ricercaTransaction] Paginazione: " + paginazioneDTO);

        PanacheQuery<Transaction> panacheQuery;
        if (!query.isEmpty()) {
            panacheQuery = find(query.toString(), params);
        } else {
            panacheQuery = findAll();
        }
        
        // Applica la paginazione alla query
        panacheQuery.page(paginazioneDTO.getNumeroPagina(), paginazioneDTO.getNumeroElementiPerPagina());

        paginazioneDTO.setNumeroRisTotali(panacheQuery.count());
        paginazioneDTO.setNumeroPagTotali((int) Math.ceil((double) paginazioneDTO.getNumeroRisTotali() / paginazioneDTO.getNumeroElementiPerPagina()));

        TransactionResponse transactionsFiltrate = new TransactionResponse();
        transactionsFiltrate.setTransactions(panacheQuery.list());
        transactionsFiltrate.setPaginazione(paginazioneDTO);

        log.info("[TransactionRepository.ricercaTransaction] transactionsFiltrate: " + transactionsFiltrate);

        return transactionsFiltrate;
    }
}
