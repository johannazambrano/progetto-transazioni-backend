package org.acme.category;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.category.entity.Category;

import java.util.Optional;

@ApplicationScoped
public class CategoryRepository implements PanacheMongoRepository<Category> {
    public Optional<Category> findByCodice(String codice) {
        return find("codice", codice).firstResultOptional();
    }
}
