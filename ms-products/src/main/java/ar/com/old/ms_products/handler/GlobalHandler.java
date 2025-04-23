package ar.com.old.ms_products.handler;

import ar.com.old.ms_products.exceptions.CategoryNotFoundException;
import ar.com.old.ms_products.exceptions.ConnectionFeignException;
import ar.com.old.ms_products.exceptions.ExistingCategoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(ExistingCategoryException.class)
    public ResponseEntity<Map<String, String>> handlerConflict(Exception e) {
        return buildResponseError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handlerBadRequest(Exception e) {
        return buildResponseError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlerNotFound(Exception e) {
        return buildResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConnectionFeignException.class)
    public ResponseEntity<Map<String, String>> handlerInternalError(Exception e) {
        return buildResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ResponseEntity<Map<String, String>> buildResponseError(Exception e, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(status).body(error);
    }
}
