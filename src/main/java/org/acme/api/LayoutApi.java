package org.acme.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.apachecommons.CommonsLog;
import org.acme.exception.ApplicationException;
import org.acme.exception.ServiceException;
import org.acme.layout.LayoutService;
import org.acme.layout.dto.LayoutDTO;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.net.URI;
import java.util.List;

@Path("/layouts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CommonsLog
public class LayoutApi {

    @Inject
    LayoutService layoutService;

    @GET
    @Path("/")
    @Operation(summary = "Recupera tutti i layout", description = "Restituisce una lista di tutti i layout disponibili")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Lista layout recuperata con successo"),
            @APIResponse(responseCode = "500", description = "Errore interno del server")
    })
    public Response getAllLayouts() throws ApplicationException {
        try {
            List<LayoutDTO> layouts = layoutService.getAllLayouts();
            log.info("[LayoutApi.getAllLayouts] recuperata lista layout con successo");
            return Response.ok(layouts).build();
        } catch (ServiceException e) {
            throw new ApplicationException(e);
        }
    }

    @GET
    @Path("/all")
    @Operation(summary = "Recupera tutti i layout (alias)", description = "Restituisce una lista di tutti i layout disponibili")
    public Response getAllLayoutsAlias() throws ApplicationException {
        return getAllLayouts();
    }

    @GET
    @Path("/default")
    @Operation(summary = "Recupera il layout di default", description = "Restituisce il layout impostato come default")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Layout di default trovato"),
            @APIResponse(responseCode = "404", description = "Nessun layout di default trovato"),
            @APIResponse(responseCode = "500", description = "Errore interno del server")
    })
    public Response getDefaultLayout() throws ApplicationException {
        try {
            log.info("[LayoutApi.getDefaultLayout] recupero del layout di default");
            LayoutDTO layout = layoutService.findDefaultLayout();
            log.info("[LayoutApi.getDefaultLayout] recuperato layout di default con successo");
            return Response.ok(layout).build();
        } catch (ServiceException e) {
            throw new ApplicationException(e);
        }
    }

    @POST
    @Path("/reset")
    @Operation(summary = "Resetta al layout di default", description = "Restituisce il layout di default per l'utente")
    public Response resetLayout() throws ApplicationException {
        return getDefaultLayout();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Recupera un layout per ID", description = "Restituisce il dettaglio di un layout specifico")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Layout trovato"),
            @APIResponse(responseCode = "404", description = "Layout non trovato"),
            @APIResponse(responseCode = "500", description = "Errore interno del server")
    })
    public Response getLayoutById(@PathParam("id") String id) throws ApplicationException {
        try {
            log.info("[LayoutApi.getLayoutById] Id ricevuto: " + id);
            if ("default".equals(id)) {
                return getDefaultLayout();
            }
            LayoutDTO layout = layoutService.findLayoutById(id);
            return Response.ok(layout).build();
        } catch (ServiceException e) {
            throw new ApplicationException(e);
        }
    }

    @POST
    @Path("/")
    @Operation(summary = "Crea un nuovo layout", description = "Crea un nuovo layout e restituisce l'ID")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Layout creato con successo"),
            @APIResponse(responseCode = "500", description = "Errore interno del server")
    })
    public Response createLayout(LayoutDTO layoutDTO) throws ApplicationException {
        try {
            log.info("[LayoutApi.createLayout] crea layout con DTO: " + layoutDTO);
            String id = layoutService.createLayout(layoutDTO);
            log.info("[LayoutApi.createLayout] creato layout con id: " + id);
            return Response.created(URI.create("/layouts/" + id)).build();
        } catch (ServiceException e) {
            throw new ApplicationException(e);
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Aggiorna un layout esistente", description = "Aggiorna i dati di un layout esistente")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Layout aggiornato con successo"),
            @APIResponse(responseCode = "404", description = "Layout non trovato"),
            @APIResponse(responseCode = "500", description = "Errore interno del server")
    })
    public Response updateLayout(@PathParam("id") String id, LayoutDTO layoutDTO) throws ApplicationException {
        try {
            log.info("[LayoutApi.updateLayout] aggiorna layout con DTO: " + layoutDTO + " e id: " + id);
            layoutService.updateLayout(id, layoutDTO);
            log.info("[LayoutApi.updateLayout] aggiornato layout con successo con id: " + id);
            return Response.noContent().build();
        } catch (ServiceException e) {
            throw new ApplicationException(e);
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Cancella un layout", description = "Rimuove un layout dal sistema")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Layout cancellato con successo"),
            @APIResponse(responseCode = "404", description = "Layout non trovato"),
            @APIResponse(responseCode = "500", description = "Errore interno del server")
    })
    public Response deleteLayout(@PathParam("id") String id) throws ApplicationException {
        try {
            log.info("[LayoutApi.deleyeLayout] elimina layout con id: " + id);
            layoutService.deleteLayout(id);
            log.info("[LayoutApi.deleteLayout] eliminato layout con successo con id: " + id);
            return Response.noContent().build();
        } catch (ServiceException e) {
            throw new ApplicationException(e);
        }
    }
}
