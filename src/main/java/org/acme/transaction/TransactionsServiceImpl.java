package org.acme.transaction;

import com.mongodb.MongoWriteException;
import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;
import lombok.extern.apachecommons.CommonsLog;
import org.acme.api.dto.FiltroRicercaTransactionDTO;
import org.acme.category.CategoryRepository;
import org.acme.exception.ServiceException;
import org.acme.transaction.dto.TransactionDTO;
import org.acme.transaction.dto.TransactionResponseDTO;
import org.acme.transaction.entity.Transaction;
import org.acme.transaction.entity.TransactionResponse;
import org.acme.transaction.mapper.TransactionMapperImpl;
import org.acme.transaction.mapper.TransactionResponseMapperImpl;
import org.bson.types.ObjectId;

import java.util.Optional;

@Model
@CommonsLog
public class TransactionsServiceImpl implements TransactionsService{

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    TransactionMapperImpl transactionMapper;

    @Inject
    TransactionResponseMapperImpl transactionResponseMapper;

    @Inject
    CategoryRepository categoryRepository;

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
            TransactionResponse transaction = transactionRepository.ricercaTransaction(filtroTransactionDTO);
            log.info("[TransactionServiceImpl.ricerca] Transaction con filtro:" + transaction);
            return transactionResponseMapper.convertEntityToDto(transaction);
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
            throw new ServiceException("Transaction con id:" + id + " non trovato!");
        }catch(Exception ex) {
            throw new ServiceException(ex);
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
                throw new ServiceException("Transaction con id:" + id + " non trovato!");
            }
        }catch(Exception ex) {
            throw new ServiceException(ex);
        }
    }
}
