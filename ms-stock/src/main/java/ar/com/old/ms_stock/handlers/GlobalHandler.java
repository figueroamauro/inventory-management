package ar.com.old.ms_stock.handlers;

import ar.com.old.ms_stock.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler({LocationAlreadyExistException.class,
            LocationConflictException.class,
            ProductConflictException.class,
            NegativeStockException.class,
            LocationInUseException.class})
    public ResponseEntity<Map<String, String>> handlerConflictException(Exception e) {
        return buildResponseError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlerNotFoundException(Exception e) {
        return buildResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handlerBadRequestException(Exception e) {
        return buildResponseError(e, HttpStatus.BAD_REQUEST);
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
