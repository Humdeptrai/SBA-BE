package sum25.studentcode.backend.core.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sum25.studentcode.backend.core.response.BaseResponse;
import sum25.studentcode.backend.core.response.MetaInfo;
import sum25.studentcode.backend.core.util.MetaBuilder;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MetaBuilder metaBuilder;

    @Value("${app.version}")
    private String version = "1.0.0";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex, HttpServletRequest request) {
        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail(ex.getDetail());
        meta.setTraceId(ex.getCode());

        return ResponseEntity
                .status(ex.getStatus())
                .body(BaseResponse.fail(
                        ex.getMessage(),
                        ex.getStatus(),
                        meta,
                        UUID.randomUUID().toString(),
                        version
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            errors.put(err.getField(), err.getDefaultMessage());
        }

        log.warn("Validation error occurred: {}", errors);
        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail("Validation error");
        meta.setTraceId("VALIDATION_FAILED");

        return ResponseEntity
                .status(422)
                .body(BaseResponse.fail(
                        "Validation Error",
                        422,
                        meta,
                        UUID.randomUUID().toString(),
                        version
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpMethod(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail("Method not allowed: " + ex.getMethod());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(BaseResponse.fail(
                        "Method Not Allowed",
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        meta,
                        UUID.randomUUID().toString(),
                        version
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknown(Exception ex, HttpServletRequest request) {
        log.error("Unknown exception occurred", ex);
        MetaInfo meta = metaBuilder.fromRequest(request);
        meta.setDetail(ex.getMessage());

        return ResponseEntity
                .status(500)
                .body(BaseResponse.error(
                        "Internal Server Error",
                        500,
                        meta,
                        UUID.randomUUID().toString(),
                        version
                ));
    }
}
