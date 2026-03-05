package org.acme.transaction;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.apachecommons.CommonsLog;
import org.acme.api.dto.FiltroRicercaTransactionDTO;
import org.acme.category.entity.Category;
import org.acme.transaction.entity.Transaction;

import java.time.LocalDate;
import java.util.regex.Pattern;

@ApplicationScoped
@CommonsLog
public class TransactionRepository implements PanacheMongoRepository<Transaction> {

    public void aggiornaCategoriaNelleTransactions(Category category) {
        mongoCollection().updateMany(
            Filters.eq("category._id", category.getId()),
            Updates.combine(
                Updates.set("category.descrizione", category.getDescrizione()),
                Updates.set("category.codice", category.getCodice()),
                Updates.set("category.budget", category.getBudget()),
                Updates.set("category.colore", category.getColore())
            )
        );
    }

    public PanacheQuery<Transaction> ricercaTransaction(FiltroRicercaTransactionDTO filtroTransactionDTO) {
        log.info("[TransactionRepository.ricercaTransaction] filtro: " + filtroTransactionDTO);

        String title = filtroTransactionDTO.getTitle();
        String category = filtroTransactionDTO.getCategory();
        LocalDate startDate = filtroTransactionDTO.getStartDate();
        LocalDate endDate = filtroTransactionDTO.getEndDate();

        StringBuilder query = new StringBuilder();
        Parameters params = new Parameters();

        if (title != null && !title.isBlank()) {
            query.append("title like :title");
            params.and("title", "(?i).*" + Pattern.quote(title) + ".*");
        }

        // Gestione range date
        if (startDate != null) {
            if (!query.isEmpty()) {
                query.append(" and ");
            }
            query.append("date >= :startDate");
            params.and("startDate", startDate);
        }

        if (endDate != null) {
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
            query.append("(category.descrizione like :category or category.codice like :category)");
            params.and("category", "(?i).*" + Pattern.quote(category) + ".*");
        }

        log.info("[TransactionRepository.ricercaTransaction] Query: " + query);

        if (!query.isEmpty()) {
            return find(query.toString(), params);
        } else {
            return findAll();
        }
    }
}
