package org.acme.exception;

/**
 * Classe che implementa le eccezioni generiche dell'applicazione.
 */
public class ApplicationException extends Exception {

    /**
     * Costruttore.
     *
     * @param cause causa dell'eccezione
     */
    public ApplicationException(final Throwable cause) {
        super(cause);
    }

    /**
     * Costruttore.
     *
     * @param message messaggio dell'eccezione
     * @param cause   causa dell'eccezione
     */
    public ApplicationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Costruttore.
     *
     * @param message messaggio dell'eccezione
     */
    public ApplicationException(final String message) {
        super(message);
    }

    /**
     * Costruttore.
     */
    public ApplicationException() {
    }

}