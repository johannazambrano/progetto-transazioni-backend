package org.acme.transaction;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.acme.api.dto.FiltroRicercaTransactionDTO;
import org.acme.category.entity.Category;
import org.acme.transaction.entity.Transaction;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@ApplicationScoped
@Slf4j
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

        Document filter = new Document();

        // Text search per title (usa text index, no full collection scan)
        if (title != null && !title.isBlank()) {
            filter.append("$text", new Document("$search", title));
        }

        // Gestione range date
        if (startDate != null || endDate != null) {
            Document dateFilter = new Document();
            if (startDate != null) dateFilter.append("$gte", startDate);
            if (endDate != null) dateFilter.append("$lte", endDate);
            filter.append("date", dateFilter);
        }

        // Category search (regex case-insensitive su sub-document)
        if (category != null && !category.isBlank()) {
            String escaped = Pattern.quote(category);
            Document regex = new Document("$regex", escaped).append("$options", "i");
            filter.append("$or", List.of(
                    new Document("category.descrizione", regex),
                    new Document("category.codice", regex)
            ));
        }

        log.info("[TransactionRepository.ricercaTransaction] Query: " + filter);

        if (filter.isEmpty()) {
            return findAll();
        }
        return find(filter);
    }
}
