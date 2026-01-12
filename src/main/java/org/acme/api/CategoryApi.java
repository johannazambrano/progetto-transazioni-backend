package org.acme.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.apachecommons.CommonsLog;
import org.acme.category.CategoryService;
import org.acme.category.dto.CategoryDTO;
import org.acme.category.dto.CategoryResponse;
import org.acme.exception.ApplicationException;
import org.acme.exception.ServiceException;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.net.URI;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CommonsLog
public class CategoryApi {

    @Inject
    CategoryService categoryService;

    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Lista categorie recuperata con successo"),
                    @APIResponse(
                            responseCode = "500",
                            description = "Internal Server error")
            })
    @Operation(
            summary = "Endpoint per la lista delle categorie",
            description = "Lista delle categorie")
    @GET
    @Path("/")
    public Response elencoCategories() throws ApplicationException {
        try{
            log.info("[CategoryApi.elencoCategories] Recupero dell'elenco delle categorie");
            CategoryResponse categories = new CategoryResponse();
            categories.setCategories(categoryService.elenco());
            log.info("[CategoryApi.elencoCategories] Recuperato l'elenco delle categorie");
            return Response.ok(categories).build();
        }catch (ServiceException e){
            throw new ApplicationException(e);
        }
    }

    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "500",
                            description = "Internal Server Error"),
                    @APIResponse(
                            responseCode = "200",
                            description = "Categoria creata con successo"
                    )
            }
    )
    @Operation(
            summary = "Endpointper la creazione di una categoria",
            description = "Crea una nuova categoria sul sistema")
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response creaCategory(CategoryDTO categoryDTO) throws ApplicationException {
        try{
            String idCategory = categoryService.crea(categoryDTO);
            log.info("[CategoryApi.creaCategory] Crea Categoria" + idCategory);
            return Response.created(URI.create("/categories/" + idCategory)).build();
        }catch(Exception ex){
            throw new ApplicationException(ex);
        }

    }

    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "500",
                            description = "Internal Server Error"),
                    @APIResponse(
                            responseCode = "200",
                            description = "Category aggiornata con successo"
                    )
            }
    )
    @Operation(summary = "Endpoint per l'aggiornamento di una category",
            description = "Aggiorna una category sul sistema dato un id")
    @PUT
    @Path("/{id}")
    public Response aggiornaCategory(@PathParam("id") String id, CategoryDTO categoryDTO) throws ApplicationException {
        try{
            log.info("[CategoryApi.aggiornaCategory] Aggiorna Categoria con id: " + id);
            //Controlla che esista il profilo con quell'id. Se esiste aggiorna altrimenti lancia eccezione
            categoryService.aggiornaCategory(id, categoryDTO);
            log.info("[CategoryApi.aggiornaCategory] Aggiornata category con id: " + id);
            return Response.noContent().build();
        }catch(Exception ex){
            throw new ApplicationException(ex);
        }
    }

    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "500",
                            description = "Internal Service Error"),
                    @APIResponse(
                            responseCode = "204",
                            description = "Category cancellata con successo"
                    )
            }
    )
    @Operation(summary = "Endpoint per la cancellazione di una category",
            description = "Cancella una category sul sistema dato un id")
    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") String id) throws ApplicationException {
        try{
            log.info("[CategoryApi.deleteCategory] Cancella Categoria con id: " + id);
            categoryService.cancella(id);
            log.info("[CategoryApi.deleteCategory] Cancellata Categoria con id: " + id);
            return Response.noContent().build();
        }catch(Exception ex){
            throw new ApplicationException(ex);
        }
    }
}
