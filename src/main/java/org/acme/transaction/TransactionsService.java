package org.acme.transaction;

import org.acme.api.dto.FiltroRicercaTransactionDTO;
import org.acme.exception.ServiceException;
import org.acme.transaction.dto.TransactionDTO;
import org.acme.transaction.dto.TransactionResponseDTO;

public interface TransactionsService {

    String createTransaction(TransactionDTO transactionDTO) throws ServiceException;

    TransactionResponseDTO ricerca(FiltroRicercaTransactionDTO filtroTransactionDTO) throws ServiceException;

    void aggiornaTransaction(String idTransaction, TransactionDTO transactionDTO) throws ServiceException;

    void cancella(String id) throws ServiceException;
}
