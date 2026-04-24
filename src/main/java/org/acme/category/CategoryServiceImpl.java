package org.acme.category;

import com.mongodb.MongoWriteException;
import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import org.acme.api.dto.FiltroRicercaCategoryDTO;
import org.acme.api.dto.PaginazioneDTO;
import org.acme.category.dto.CategoryDTO;
import org.acme.category.dto.CategoryResponseDTO;
import org.acme.category.entity.Category;
import org.acme.category.mapper.CategoryMapperImpl;
import org.acme.exception.BadRequestException;
import org.acme.exception.NotFoundException;
import org.acme.exception.ServiceException;
import org.acme.transaction.TransactionRepository;
import org.acme.util.entity.Paginazione;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CategoryMapperImpl categoryMapper;

    @Inject
    TransactionRepository transactionRepository;

    @Override
    public CategoryDTO findCategoryByCodice(String codice) throws ServiceException {
        try{
            log.info("[CategoryServiceImpl.findCategoryByCodice] verifica esistenza categoria con codice" + codice);
            Optional<Category> category = categoryRepository.findByCodice(codice);
            if(category.isPresent()){
                log.info("[CategoryServiceImpl.findCategoryByCodice] trovata categoria con codice" + codice);
                return categoryMapper.convertEntityToDto(category.get());
            }
            throw new NotFoundException("Categoria con codice: " + codice + " non trovata");
        }catch(NotFoundException nfe){
            throw nfe;
        }catch(Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
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
            throw new NotFoundException("Categoria con id:" + id + " non trovata");
        }catch(NotFoundException nfe){
            throw nfe;
        }catch(IllegalArgumentException iae){
            throw new BadRequestException("ID non valido: " + id);
        }catch(Exception e) {
            throw new ServiceException(e.getMessage(), e);
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
            throw new ServiceException(ex.getMessage(), ex);
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
            throw new ServiceException("Errore di scrittura su MongoDB: " + e.getMessage(), e);
        } catch(Exception ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    public void aggiornaCategory(String id, CategoryDTO categoryDTO) throws ServiceException {
        try{
            log.info("[CategoryServiceImpl.aggiornaCategory] verifica esistenza Category con id: " + id);
            Optional<Category> category = categoryRepository.findByIdOptional(new ObjectId(id));
            if(category.isPresent()){
                log.info("[CategoryServiceImpl.aggiornaCategory] trovata categoria con id: " + id);
                Category newCategory = categoryMapper.convertDtoToEntity(categoryDTO);
                newCategory.setId(category.get().getId());
                categoryRepository.update(newCategory);
                transactionRepository.aggiornaCategoriaNelleTransactions(newCategory);
                log.info("[CategoryServiceImpl.aggiornaCategory] aggiornata categoria con id: " + id);
                return;
            }
            throw new NotFoundException("Categoria con id: " + id + " non trovata");
        }catch(NotFoundException nfe){
            throw nfe;
        }catch(IllegalArgumentException iae){
            throw new BadRequestException("ID non valido: " + id);
        }catch(MongoWriteException e){
        // codice standard di errore (11000) per gli errori di chiave duplicata in MongoDB
            if(e.getCode() == 11000) {
                log.error("[CategoryServiceImpl.aggiornaCategory] Tentativo di inserire codice duplicato: " + categoryDTO.getCodice());
            // lancia un'eccezione service specifica per il conflitto dei dati
                String msg = "Il codice '" + categoryDTO.getCodice() + "' esiste già.";
                throw new ServiceException(msg);
            }else {
                throw new ServiceException(e.getMessage(), e);
            }
        }catch(Exception ex) {
            throw new ServiceException(ex.getMessage(), ex);
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
                throw new NotFoundException("Categoria con id: " + id + " non trovata");
            }
        }catch(NotFoundException nfe){
            throw nfe;
        }catch(IllegalArgumentException iae){
            throw new BadRequestException("ID non valido: " + id);
        }catch(Exception ex){
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    public CategoryResponseDTO elencoConPaginazione(FiltroRicercaCategoryDTO filtro) throws ServiceException {
        try {
            log.info("[CategoryServiceImpl.elencoConPaginazione] Ricerca categorie con filtro: " + filtro);

            // Paginazione default
            Paginazione paginazione;
            if (filtro.getPaginazione() == null) {
                paginazione = new Paginazione();
                paginazione.setNumeroPagina(0);
                paginazione.setNumeroElementiPerPagina(10);
            } else {
                paginazione = Paginazione.builder()
                        .numeroPagina(filtro.getPaginazione().getNumeroPagina())
                        .numeroElementiPerPagina(filtro.getPaginazione().getNumeroElementiPerPagina())
                        .build();
            }

            // Query dal repository
            PanacheQuery<Category> panacheQuery = categoryRepository.ricercaCategoria(filtro);

            // Count prima della paginazione
            long totalCount = panacheQuery.count();
            paginazione.setNumeroRisTotali(totalCount);
            paginazione.setNumeroPagTotali((int) Math.ceil((double) totalCount / paginazione.getNumeroElementiPerPagina()));

            // Assemblaggio risposta
            CategoryResponseDTO response = new CategoryResponseDTO();
            if (totalCount > 0) {
                panacheQuery.page(paginazione.getNumeroPagina(), paginazione.getNumeroElementiPerPagina());
                response.setCategories(categoryMapper.convertEntityToDto(panacheQuery.list()));
            } else {
                response.setCategories(java.util.List.of());
            }

            // Converti Paginazione entity a DTO
            PaginazioneDTO paginazioneDTO = PaginazioneDTO.builder()
                    .numeroPagina(paginazione.getNumeroPagina())
                    .risultatiPagina(paginazione.getRisultatiPagina())
                    .numeroPagTotali(paginazione.getNumeroPagTotali())
                    .numeroRisTotali(paginazione.getNumeroRisTotali())
                    .numeroElementiPerPagina(paginazione.getNumeroElementiPerPagina())
                    .build();
            response.setPaginazione(paginazioneDTO);

            log.info("[CategoryServiceImpl.elencoConPaginazione] Risultato ricerca: " + response);
            return response;
        } catch (Exception ex) {
            log.error("[CategoryServiceImpl.elencoConPaginazione] Errore durante la ricerca delle categorie", ex);
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

}
