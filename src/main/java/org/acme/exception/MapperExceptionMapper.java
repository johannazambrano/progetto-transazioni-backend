package org.acme.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MapperExceptionMapper implements ExceptionMapper<MapperException> {

    @Override
    public Response toResponse(MapperException exception) {
        ErrorResponse error = new ErrorResponse("MAPPER_ERROR", exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
