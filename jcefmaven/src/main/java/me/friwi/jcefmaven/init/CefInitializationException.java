package me.friwi.jcefmaven.init;

public class CefInitializationException extends Exception {
    public CefInitializationException(String message) {
        super(message);
    }

    public CefInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
