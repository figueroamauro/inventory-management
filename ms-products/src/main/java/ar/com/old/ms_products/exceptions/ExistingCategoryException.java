package ar.com.old.ms_products.exceptions;

public class ExistingCategoryException extends RuntimeException {
    public ExistingCategoryException(String message) {
        super(message);
    }
}
