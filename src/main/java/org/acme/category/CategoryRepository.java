package org.acme.category;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.api.dto.FiltroRicercaCategoryDTO;
import org.acme.category.entity.Category;

import java.util.Optional;

@ApplicationScoped
@MongoEntity(collection = "category")
public class CategoryRepository implements PanacheMongoRepository<Category> {
    public Optional<Category> findByCodice(String codice) {
        return find("codice", codice).firstResultOptional();
    }

    public PanacheQuery<Category> ricercaCategoria(FiltroRicercaCategoryDTO filtro) {
        if (filtro == null || (filtro.getDescrizione() == null || filtro.getDescrizione().isBlank())) {
            return findAll();
        }
        return find("descrizione like ?1", "%" + filtro.getDescrizione() + "%");
    }
}
