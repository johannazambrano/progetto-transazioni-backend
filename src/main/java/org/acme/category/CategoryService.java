package org.acme.category;

import org.acme.category.dto.CategoryDTO;
import org.acme.exception.ServiceException;

import java.util.List;

public interface CategoryService {
    CategoryDTO findCategoryByCodice(String codice) throws ServiceException;

    CategoryDTO findById(String id) throws ServiceException;

    List<CategoryDTO> elenco() throws ServiceException;

    String crea(CategoryDTO profiloDTO) throws ServiceException;

    void aggiornaCategory(String id, CategoryDTO categoryDTO) throws ServiceException;

    void cancella(String id) throws ServiceException;
}
