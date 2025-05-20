package ar.com.old.ms_stock.exceptions;

public class ProductConflictException extends RuntimeException {
    public ProductConflictException(String message) {
        super(message);
    }
}
