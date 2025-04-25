package ar.com.old.ms_products.exceptions;

public class WarehouseAlreadyExistException extends RuntimeException {
    public WarehouseAlreadyExistException(String message) {
        super(message);
    }
}
