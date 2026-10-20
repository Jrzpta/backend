package cl.oursour.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
 
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
 
@RestControllerAdvice
public class GlobalExceptionHandler {
 
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex) {
        return build(ex.getStatus(), ex.getMessage(), null);
    }
 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(f -> details.put(f.getField(), f.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Datos inválidos", details);
    }
 
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "El cuerpo de la petición es inválido", null);
    }
 
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Parámetro inválido: " + ex.getName(), null);
    }
 
    private ResponseEntity<ApiError> build(HttpStatus status, String message, Map<String, String> details) {
        ApiError body = new ApiError(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), message, details);
        return ResponseEntity.status(status).body(body);
    }
}

