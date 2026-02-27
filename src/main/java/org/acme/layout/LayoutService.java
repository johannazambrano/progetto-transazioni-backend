package org.acme.layout;

import org.acme.exception.MapperException;
import org.acme.exception.ServiceException;
import org.acme.layout.dto.FiltroDTO;
import org.acme.layout.dto.LayoutDTO;
import java.util.List;

public interface LayoutService {
    LayoutDTO findLayoutByName(String name) throws ServiceException;
    LayoutDTO findLayoutById(String id) throws ServiceException;
//    LayoutDTO findDefaultLayout() throws ServiceException;
    String createLayout(LayoutDTO layoutDTO) throws ServiceException;
    void updateLayout(String id, LayoutDTO layoutDTO) throws ServiceException;
    void deleteLayout(String id) throws ServiceException;
    List<LayoutDTO> getAllLayouts() throws ServiceException;
    LayoutDTO findByFiltro(FiltroDTO filtroDto) throws ServiceException;
}
