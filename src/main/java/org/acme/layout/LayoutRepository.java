package org.acme.layout;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.apachecommons.CommonsLog;
import org.acme.layout.entity.Layout;

@ApplicationScoped
@CommonsLog
public class LayoutRepository implements PanacheMongoRepository<Layout> {

    public Layout findByLayoutName(String layoutName) {
        return find("layoutName", layoutName).firstResult();
    }

    public Layout findByFilters(String layoutName, Boolean isDefault) {
        log.info("[LayoutRepository.findByFilters] Ricerca layout con layoutName: " + layoutName + ", isDefault: " + isDefault);

        StringBuilder query = new StringBuilder();
        Parameters params = new Parameters();

        if (layoutName != null) {
            query.append("layoutName = :layoutName");
            params.and("layoutName", layoutName);
        }

        if (isDefault != null) {
            if (query.length() > 0) {
                query.append(" AND ");
            }
            query.append("isDefault = :isDefault");
            params.and("isDefault", isDefault);
        }

        log.info("[LayoutRepository.findByFilters] Query: " + query);

        if (query.length() > 0) {
            return find(query.toString(), params).firstResult();
        }

        return null;
    }
}
