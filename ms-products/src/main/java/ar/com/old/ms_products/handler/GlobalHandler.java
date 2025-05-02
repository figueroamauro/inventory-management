package ar.com.old.ms_products.handler;

import ar.com.old.ms_products.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler({
            ExistingCategoryException.class,
            WarehouseAlreadyExistException.class,
            WarehouseAlreadyExistException.class,
            ProductAlreadyExistException.class})
    public ResponseEntity<Map<String, String>> handlerConflict(Exception e) {
        return buildResponseError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handlerBadRequest(Exception e) {
        return buildResponseError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            CategoryNotFoundException.class,
            WarehouseNotFoundException.class,
            ProductNotFoundException.class
    })
    public ResponseEntity<Map<String, String>> handlerNotFound(Exception e) {
        return buildResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConnectionFeignException.class)
    public ResponseEntity<Map<String, String>> handlerInternalError(Exception e) {
        return buildResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("error", errors.size() == 1 ? errors.get(0) : errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private static ResponseEntity<Map<String, String>> buildResponseError(Exception e, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(status).body(error);
    }
}
