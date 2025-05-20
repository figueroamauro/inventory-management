package ar.com.old.ms_stock.exceptions;

public class LocationConflictException extends RuntimeException {
    public LocationConflictException(String message) {
        super(message);
    }
}
