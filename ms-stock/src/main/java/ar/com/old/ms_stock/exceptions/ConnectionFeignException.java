package ar.com.old.ms_stock.exceptions;

public class ConnectionFeignException extends RuntimeException {

    public ConnectionFeignException(String message) {
        super(message);
    }
}
