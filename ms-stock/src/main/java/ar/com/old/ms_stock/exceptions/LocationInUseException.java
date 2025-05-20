package ar.com.old.ms_stock.exceptions;

public class LocationInUseException extends RuntimeException {
    public LocationInUseException(String message) {
        super(message);
    }
}
