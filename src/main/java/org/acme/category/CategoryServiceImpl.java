package org.acme.category;

import com.mongodb.MongoWriteException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import lombok.extern.apachecommons.CommonsLog;
import org.acme.category.dto.CategoryDTO;
import org.acme.category.entity.Category;
import org.acme.category.mapper.CategoryMapperImpl;
import org.acme.exception.ServiceException;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@CommonsLog
public class CategoryServiceImpl implements CategoryService {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CategoryMapperImpl categoryMapper;

    @Override
    public CategoryDTO findCategoryByCodice(String codice) throws ServiceException {
        try{
            log.info("[CategoryServiceImpl.findCategoryByCodice] verifica esistenza categoria con codice" + codice);
            Optional<Category> category = categoryRepository.findByCodice(codice);
            if(category.isPresent()){
                log.info("[CategoryServiceImpl.findCategoryByCodice] trovata categoria con codice" + codice);
                return categoryMapper.convertEntityToDto(category.get());
            }
        }catch(Exception e){
            throw new ServiceException(e.getMessage());
        }
        return null;
    }

    @Override
    public CategoryDTO findById(String id) throws ServiceException{
        try{
            log.info("[CategoryServiceImpl.findById] verifica esistenza categoria con id" + id);
            Optional<Category> category = categoryRepository.findByIdOptional(new ObjectId(id));
            if(category.isPresent()){
                log.info("[CategoryServiceImpl.findById] trovata categoria con id" + id);
                return categoryMapper.convertEntityToDto(category.get());
            }
            throw new ServiceException("Categoria con id:" + id + " non trovata");
        }catch(Exception e){
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<CategoryDTO> elenco() throws ServiceException {
        try {
            log.info("[CategoryServiceImpl.elenco] Recupero delle categorie");
            List<Category> category = categoryRepository.listAll();
            log.info("[CategoryServiceImpl.elenco] Recuperate delle categorie");
            return categoryMapper.convertEntityToDto(category);
        }catch(Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    @Override
    public String crea(CategoryDTO categoryDTO) throws ServiceException {
        try {
            log.info("[CategoryServiceImpl.crea] Creazione del categoryDTO: " + categoryDTO);
            Category category = categoryMapper.convertDtoToEntity(categoryDTO);
            categoryRepository.persist(category);
            String idCategory = category.getId().toHexString();
            log.info("[CategoryServiceImpl.crea] ID categoria creata" + idCategory);
            return idCategory;
        }catch (MongoWriteException e) {
            // codice standard di errore (11000) per gli errori di chiave duplicata in MongoDB
            if (e.getCode() == 11000) {
                // Logga l'errore per il debug
                log.error("[CategoryServiceImpl.crea] Tentativo di inserire codice duplicato: " + categoryDTO.getCodice());

                // Lancia un'eccezione Service specifica per il conflitto di dati
                String msg = "Il codice '" + categoryDTO.getCodice() + "' esiste già.";
                throw new ServiceException(msg);
            }
            // Gestisci altre eccezioni Mongo, se necessario
            throw new ServiceException("Errore di scrittura su MongoDB: " + e.getMessage());
        } catch(Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    @Override
    public void aggiornaCategory(String id, CategoryDTO categoryDTO) throws ServiceException {
        try{
            log.info("[CategoryApi.aggiornaCategory] verifica esistenza Category con id: " + id);
            Optional<Category> category = categoryRepository.findByCodice(id);
            if(category.isPresent()){
                log.info("[CategoryApi.aggiornaCategory] trovata categoria con id: " + id);
                Category newCategory = categoryMapper.convertDtoToEntity(categoryDTO);
                newCategory.setId(category.get().getId());
                categoryRepository.update(newCategory);
                log.info("[CategoryApi.aggiornaCategory] aggiornata categoria con id: " + id);
                return;
            }
            throw new ServiceException("Categoria con id: " + id + " non trovata");
        }catch(MongoWriteException e){
        // codice standard di errore (11000) per gli errori di chiave duplicata in MongoDB
            if(e.getCode() == 11000) {
                log.error("[CategoryApi.aggiornaCategory] Tentativo di inserire codice duplicato: " + categoryDTO.getCodice());
            // lancia un'eccezione service specifica per il conflitto dei dati
                String msg = "Il codice '" + categoryDTO.getCodice() + "' esiste già.";
                throw new ServiceException(msg);
            }
        }catch(Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    @Override
    public void cancella(String id) throws ServiceException{
        try{
            log.info("[CategoryServiceImpl.cancella] verifica esistenza category con id: " + id);
            Optional<Category> category = categoryRepository.findByIdOptional(new ObjectId(id));
            if(category.isPresent()){
                log.info("[CategoryServiceImpl.cancella] trovata categoria con id: " + id);
                categoryRepository.delete(category.get());
                log.info("[CategoryServiceImpl.cancella] cancellata categoria con id: " + id);
            }else{
                throw new ServiceException("Categoria con id: " + id + " non trovata");
            }
        }catch(Exception ex){
            throw new ServiceException(ex.getMessage());
        }
    }

}
