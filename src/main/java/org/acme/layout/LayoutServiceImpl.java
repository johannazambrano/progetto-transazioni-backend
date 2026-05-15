package org.acme.layout;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.exception.BadRequestException;
import org.acme.exception.MapperException;
import org.acme.exception.NotFoundException;
import org.acme.exception.ServiceException;
import org.acme.layout.dto.FiltroDTO;
import org.acme.layout.dto.LayoutDTO;
import org.acme.layout.entity.Layout;
import org.acme.layout.mapper.LayoutMapperImpl;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
@Slf4j
public class LayoutServiceImpl implements LayoutService {

    @Inject
    LayoutRepository layoutRepository;

    @Inject
    LayoutMapperImpl layoutMapper;

    @Override
    public LayoutDTO findLayoutByName(String name) throws ServiceException {
        try {
            log.info("[LayoutServiceImpl.findLayoutByName] Ricerca layout per nome: " + name);
            Layout layout = layoutRepository.findByLayoutName(name);
            if (layout == null) {
                throw new NotFoundException("Layout con nome " + name + " non trovato.");
            }
            return layoutMapper.convertEntityToDto(layout);
        } catch (NotFoundException nfe) {
            throw nfe;
        } catch (Exception e) {
            log.error("[LayoutServiceImpl.findLayoutByName] Errore durante la ricerca del layout per nome ", e);
            throw new ServiceException("Errore durante la ricerca del layout per nome: " + e.getMessage(), e);
        }
    }

    @Override
    public LayoutDTO findLayoutById(String id) throws ServiceException {
        try {
            log.info("[LayoutServiceImpl.findLayoutById] Ricerca layout per ID: " + id);
            Layout layout = layoutRepository.findById(new ObjectId(id));
            if (layout == null) {
                throw new NotFoundException("Layout con ID " + id + " non trovato.");
            }
            return layoutMapper.convertEntityToDto(layout);
        } catch (NotFoundException nfe) {
            throw nfe;
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException("ID non valido: " + id);
        } catch (Exception e) {
            log.error("[LayoutServiceImpl.findLayoutById] Errore durante la ricerca del layout per ID ", e);
            throw new ServiceException("Errore durante la ricerca del layout per ID: " + e.getMessage(), e);
        }
    }

    @Override
    public String createLayout(LayoutDTO layoutDTO) throws ServiceException {
        try {
            log.info("[LayoutServiceImpl.createLayout] Creazione nuovo layout");
            Layout layout = layoutMapper.convertDtoToEntity(layoutDTO);
            layoutRepository.persist(layout);
            return layout.getId().toHexString();
        } catch (Exception e) {
            log.error("[LayoutServiceImpl.createLayout] Errore durante la creazione del layout ", e);
            throw new ServiceException("Errore durante la creazione del layout: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateLayout(String id, LayoutDTO layoutDTO) throws ServiceException {
        try {
            log.info("[LayoutServiceImpl.updateLayout] Aggiornamento layout con id: " + id);
            Layout existingLayout = layoutRepository.findById(new ObjectId(id));
            if (existingLayout == null) {
                throw new NotFoundException("Layout con id " + id + " non trovato.");
            }
            Layout updatedLayout = layoutMapper.convertDtoToEntity(layoutDTO);
            // Assicura che l'ID non venga cambiato
            updatedLayout.setId(existingLayout.getId());
            layoutRepository.update(updatedLayout);
        } catch (NotFoundException nfe) {
            throw nfe;
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException("ID non valido: " + id);
        } catch (Exception e) {
            log.error("[LayoutServiceImpl.updateLayout] Errore durante l'aggiornamento del layout ", e);
            throw new ServiceException("Errore durante l'aggiornamento del layout: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteLayout(String id) throws ServiceException {
        try {
            log.info("[LayoutServiceImpl.deleteLayout] Cancellazione layout con id: " + id);
            boolean deleted = layoutRepository.deleteById(new ObjectId(id));
            if (!deleted) {
                throw new NotFoundException("Layout con id " + id + " non trovato.");
            }
        } catch (NotFoundException nfe) {
            throw nfe;
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException("ID non valido: " + id);
        } catch (Exception e) {
            log.error("[LayoutServiceImpl.deleteLayout] Errore durante la cancellazione del layout ", e);
            throw new ServiceException("Errore durante la cancellazione del layout: " + e.getMessage(), e);
        }
    }

    @Override
    public List<LayoutDTO> getAllLayouts() throws ServiceException {
        try {
            log.info("[LayoutServiceImpl.getAllLayouts] Recupero di tutti i layout");
            List<Layout> layouts = layoutRepository.listAll();
            return layoutMapper.convertEntityToDto(layouts);
        } catch (Exception e) {
            log.error("[LayoutServiceImpl.getAllLayouts] Errore durante il recupero di tutti i layout ", e);
            throw new ServiceException("Errore durante il recupero di tutti i layout: " + e.getMessage(), e);
        }
    }

    @Override
    public LayoutDTO findByFiltro(FiltroDTO filtroDto) throws ServiceException {
        try {
            log.info("[LayoutServiceImpl.findByFiltro] Ricerca layout di default");
            Layout layout = layoutRepository.findByFilters(filtroDto.getLayoutName(), filtroDto.getIsDefault());
            if (layout != null) {
                return layoutMapper.convertEntityToDto(layout);
            }
            log.info("[LayoutServiceImpl.findByFiltro] Layout di default non trovato");
            throw new NotFoundException("Layout con nome: " + filtroDto.getLayoutName() + " non trovato");
        } catch(NotFoundException nfe){
            throw nfe;
        }catch (Exception e) {
            log.error("[LayoutServiceImpl.findByFiltro] Errore durante la ricerca del layout di default ", e);
            throw new ServiceException("[LayoutServiceImpl.findByFiltro] Errore durante la ricerca del layout di default: " + e.getMessage(), e);
        }
    }
}
