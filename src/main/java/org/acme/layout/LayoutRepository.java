package org.acme.layout;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.apachecommons.CommonsLog;
import org.acme.layout.entity.Layout;

@ApplicationScoped
@CommonsLog
public class LayoutRepository implements PanacheMongoRepository<Layout> {

    // Qui puoi aggiungere metodi di ricerca custom se necessario
    // Esempio: findByLayoutName(String name)
    
    public Layout findByLayoutName(String layoutName) {
        return find("layoutName", layoutName).firstResult();
    }
    
    public Layout findDefaultLayout() {
        return find("isDefault", true).firstResult();
    }
}
