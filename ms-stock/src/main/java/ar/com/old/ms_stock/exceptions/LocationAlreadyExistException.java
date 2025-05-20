package ar.com.old.ms_stock.exceptions;

public class LocationAlreadyExistException extends RuntimeException {
    public LocationAlreadyExistException(String message) {
        super(message);
    }
}
