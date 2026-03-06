package org.acme.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
public class MongoIndexConfig {

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String databaseName;

    void onStart(@Observes StartupEvent ev) {
        log.info("[MongoIndexConfig] Creazione indici MongoDB...");
        MongoDatabase db = mongoClient.getDatabase(databaseName);

        // CATEGORY
        db.getCollection("CATEGORY")
                .createIndex(Indexes.ascending("codice"), new IndexOptions().name("idx_category_codice"));

        // TRANSACTION
        db.getCollection("TRANSACTION")
                .createIndex(Indexes.ascending("date"), new IndexOptions().name("idx_transaction_date"));
        try {
            db.getCollection("TRANSACTION").dropIndex("idx_transaction_title");
        } catch (Exception ignored) {
            // L'indice potrebbe non esistere al primo avvio
        }
        db.getCollection("TRANSACTION")
                .createIndex(Indexes.text("title"), new IndexOptions().name("idx_transaction_title"));
        db.getCollection("TRANSACTION")
                .createIndex(Indexes.ascending("category.descrizione", "category.codice"),
                        new IndexOptions().name("idx_transaction_category"));

        // LAYOUT
        db.getCollection("LAYOUT")
                .createIndex(Indexes.ascending("layoutName"), new IndexOptions().name("idx_layout_layoutName"));
        db.getCollection("LAYOUT")
                .createIndex(Indexes.ascending("isDefault"), new IndexOptions().name("idx_layout_isDefault"));

        log.info("[MongoIndexConfig] Indici MongoDB creati.");
    }
}
