package ar.com.old.ms_stock.exceptions;

public class NegativeStockException extends RuntimeException {
    public NegativeStockException(String message) {
        super(message);
    }
}