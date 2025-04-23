package ar.com.old.ms_products.handler;

import ar.com.old.ms_products.exceptions.CategoryNotFoundException;
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
        Map<String, String> error = new HashMap<>();

        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handlerBadRequest(Exception e) {
        Map<String, String> error = new HashMap<>();

        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlerNotFound(Exception e) {
        Map<String, String> error = new HashMap<>();

        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
