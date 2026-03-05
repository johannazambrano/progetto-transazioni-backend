package org.acme.transaction;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.apachecommons.CommonsLog;
import org.acme.api.dto.FiltroRicercaTransactionDTO;
import org.acme.exception.NotFoundException;
import org.acme.exception.ServiceException;
import org.acme.transaction.dto.TransactionDTO;
import org.acme.transaction.dto.TransactionResponseDTO;
import org.acme.transaction.entity.Transaction;
import org.acme.transaction.entity.TransactionResponse;
import org.acme.transaction.mapper.TransactionMapperImpl;
import org.acme.transaction.mapper.TransactionResponseMapperImpl;
import org.acme.util.entity.Paginazione;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
@CommonsLog
public class TransactionsServiceImpl implements TransactionsService{

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    TransactionMapperImpl transactionMapper;

    @Inject
    TransactionResponseMapperImpl transactionResponseMapper;

    @Override
    public String createTransaction(TransactionDTO transactionDTO) throws ServiceException {
        try {
            log.info("[TransactionServiceImpl.crea] creazione transactionDTO in ingresso:" + transactionDTO);
            Transaction transaction = transactionMapper.convertDtoToEntity(transactionDTO);
            transactionRepository.persist(transaction);
            log.info("[TransactionServiceImpl.crea] creata transaction:" + transaction);
            String transactionId = transaction.getId().toHexString();

            log.info("[TransactionServiceImpl.crea] ID transaction creato:" + transactionId);
            return transactionId;
        } catch (Exception ex) {
            log.error("[TransactionServiceImpl.crea] Errore durante la creazione della transaction", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public TransactionResponseDTO ricerca(FiltroRicercaTransactionDTO filtroTransactionDTO) throws ServiceException {
        try {
            log.info("[TransactionServiceImpl.ricerca] Ricerca transactions con filtro:" + filtroTransactionDTO);

            // Validazione delle date
            LocalDate startDate = filtroTransactionDTO.getStartDate();
            LocalDate endDate = filtroTransactionDTO.getEndDate();
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new ServiceException("La data di inizio non può essere successiva alla data di fine.");
            }

            // Paginazione default
            Paginazione paginazione;
            if (filtroTransactionDTO.getPaginazione() == null) {
                paginazione = new Paginazione();
                paginazione.setNumeroPagina(0);
                paginazione.setNumeroElementiPerPagina(10);
            } else {
                paginazione = Paginazione.builder()
                        .numeroPagina(filtroTransactionDTO.getPaginazione().getNumeroPagina())
                        .numeroElementiPerPagina(filtroTransactionDTO.getPaginazione().getNumeroElementiPerPagina())
                        .build();
            }

            // Query dal repository
            PanacheQuery<Transaction> panacheQuery = transactionRepository.ricercaTransaction(filtroTransactionDTO);

            // Applica la paginazione alla query
            panacheQuery.page(paginazione.getNumeroPagina(), paginazione.getNumeroElementiPerPagina());

            // Calcolo metadati paginazione
            paginazione.setNumeroRisTotali(panacheQuery.count());
            paginazione.setNumeroPagTotali((int) Math.ceil((double) paginazione.getNumeroRisTotali() / paginazione.getNumeroElementiPerPagina()));

            // Assemblaggio risposta
            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.setTransactions(panacheQuery.list());
            transactionResponse.setPaginazione(paginazione);

            log.info("[TransactionServiceImpl.ricerca] Risultato ricerca:" + transactionResponse);
            return transactionResponseMapper.convertEntityToDto(transactionResponse);
        } catch (ServiceException se) {
            throw se;
        } catch (Exception ex) {
            log.error("[TransactionServiceImpl.ricerca] Errore durante la ricerca delle transactions", ex);
            throw new ServiceException(ex);
        }
    }

    @Override
    public void aggiornaTransaction(String id, TransactionDTO transactionDTO) throws ServiceException {
        try{
            log.info("[TransactionServiceImpl.aggiornaTransaction] verifica esistenza transaction con id" + id);
            Optional<Transaction> transaction = transactionRepository.findByIdOptional(new ObjectId(id));
            if (transaction.isPresent()){
                log.info("[TransactionServiceImpl.aggiornaTransaction] trovata transaction con id" + id);
                Transaction newTransaction = transactionMapper.convertDtoToEntity(transactionDTO);
                newTransaction.setId(transaction.get().getId());
                transactionRepository.update(newTransaction);
                log.info("[TransactionServiceImpl.aggiornaTransaction] aggiornata transaction con id" + id);
                return;
            }
            throw new NotFoundException("Transaction con id:" + id + " non trovato!");
        }catch(NotFoundException nfe){
            throw nfe;
        }catch(Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void cancella(String id) throws ServiceException{
        try{
            log.info("[TransactionServiceImpl.cancella] verifica esistenza transaction con id" + id);
            Optional<Transaction> transaction = transactionRepository.findByIdOptional(new ObjectId(id));
            if(transaction.isPresent()){
                log.info("[TransactionServiceImpl.cancella] trovata transaction con id" + id);
                transactionRepository.delete(transaction.get());
                log.info("[TransactionServiceImpl.cancella] cancellata transaction con id" + id);
            }else{
                throw new NotFoundException("Transaction con id:" + id + " non trovato!");
            }
        }catch(NotFoundException nfe){
            throw nfe;
        }catch(Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
