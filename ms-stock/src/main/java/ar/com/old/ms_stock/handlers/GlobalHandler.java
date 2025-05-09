package ar.com.old.ms_stock.handlers;

import ar.com.old.ms_stock.exceptions.LocationAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(LocationAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handlerConflictException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
