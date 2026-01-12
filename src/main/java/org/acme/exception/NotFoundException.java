package org.acme.exception;

public class NotFoundException extends RuntimeException{
    private final String codice = "404_NOT_FOUND";

    public NotFoundException(String message) {
        super(message);
    }

    public String getCodice(){
        return codice;
    }
}
