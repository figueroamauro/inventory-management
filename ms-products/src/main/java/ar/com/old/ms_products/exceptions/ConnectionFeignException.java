package ar.com.old.ms_products.exceptions;

public class ConnectionFeignException extends RuntimeException {

    public ConnectionFeignException(String message) {
        super(message);
    }
}
