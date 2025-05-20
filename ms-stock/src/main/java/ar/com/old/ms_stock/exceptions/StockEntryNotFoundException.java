package ar.com.old.ms_stock.exceptions;

public class StockEntryNotFoundException extends RuntimeException {
    public StockEntryNotFoundException(String message) {
        super(message);
    }
}
