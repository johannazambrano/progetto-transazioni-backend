package org.acme.exception;

public class BadRequestException extends RuntimeException{
    private final String codice = "400_BAD_REQUEST";

    public BadRequestException(String message) {
        super(message);
    }

    public String getCodice(){
        return codice;
    }
}
