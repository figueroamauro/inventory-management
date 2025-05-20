package ar.com.old.ms_stock.exceptions;

public class LocationNotFoundException extends RuntimeException {

    public LocationNotFoundException(String message) {
        super(message);
    }
}
