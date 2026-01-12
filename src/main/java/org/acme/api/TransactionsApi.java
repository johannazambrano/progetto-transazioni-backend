package org.acme.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.apachecommons.CommonsLog;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.api.dto.FiltroRicercaTransactionDTO;
import org.acme.exception.ApplicationException;
import org.acme.transaction.TransactionsService;
import org.acme.transaction.dto.TransactionDTO;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.net.URI;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CommonsLog
public class TransactionsApi {

    @Inject
    TransactionsService transactionsService;

    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Transaction recuperata con successo"),
                    @APIResponse(
                            responseCode = "500",
                            description = "Internal Server error")
            })
    @Operation(
            summary = "Endpoint per la ricerca delle transactions",
            description = "Cerca transactions per title, category")
    @POST
    @Path("/ricerca")
    public Response ricercaTransactions(FiltroRicercaTransactionDTO filtroRicercaTransactionDTO) throws ApplicationException {
        try{
            log.info("[TransactionsApi.listaTransactions] Ricerca transaction con filtro: " + filtroRicercaTransactionDTO);
            return Response.ok(transactionsService.ricerca(filtroRicercaTransactionDTO)).build();
        }catch(Exception ex){
            throw new ApplicationException(ex);
        }

    }

    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "500",
                            description = "Internal Server error"),
                    @APIResponse(
                            responseCode = "201",
                            description = "Transaction creatoa con successo")})
    @Operation(
            summary = "Endpoint per la creazione di una transaction",
            description = "Crea una nuova transaction sul sistema")
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response creaTransaction(TransactionDTO transactionDTO) throws ApplicationException {
        try{
            String idTransaction = transactionsService.createTransaction(transactionDTO);
            log.info("[TransactionsApi.creaTransaction] creaTransaction: " + idTransaction);
            return Response.created(URI.create("/transactions/" + idTransaction)).build();
        }catch (Exception ex) {
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
                            description = "Transaction aggiornata con successo"
                    )
            }
    )
    @Operation(summary = "Endpoint per l'aggiornamento di una transaction",
            description = "Aggiorna una transaction sul sistema dato un id")
    @PUT
    @Path("/{id}")
    public Response aggiornaTransaction(@PathParam("id") String id,TransactionDTO transactionDTO) throws ApplicationException {
        try{
            log.info("[TransactionsApi.aggiornaTransaction] Aggiorna transaction con id:" + id);
            // controlla se esiste la transazione altrimenti lancia eccezzione
            transactionsService.aggiornaTransaction(id, transactionDTO);
            log.info("[TransactionsApi.aggiornaTransaction] Aggiornata transaction con id:" + id);
            return Response.noContent().build();
        }catch(Exception ex){
            throw new ApplicationException(ex);
        }
    }

    @Operation(summary = "Endpoint per la cancellazione di una transaction",
            description = "Cancella una transaction sul sistema dato un id")
    @DELETE
    @Path("/{id}")
    public Response deleteTransaction(@PathParam("id") String id) throws ApplicationException {
        try{
            log.info("[TransactionApi.deleteTransaction] Cancella transaction con id" + id);
            transactionsService.cancella(id);
            log.info("[TransactionApi.deleteTransaction] Cancellata transaction con id" + id);
            return Response.noContent().build();
        }catch (Exception e){
            throw new ApplicationException(e);
        }
    }

}
