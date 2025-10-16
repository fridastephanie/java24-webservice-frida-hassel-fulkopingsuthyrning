package se.gritacademy.fulkoping_rental.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Validation Errors (Spring Validation / @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail pd = createProblemDetail(HttpStatus.BAD_REQUEST, "Validation Error", "Invalid fields in request");
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", fe.getDefaultMessage()
                ))
                .toList();
        pd.setProperty("errors", errors);
        return pd;
    }

    /**
     * Constraint violations (like @Min, @NotNull directly on params)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail pd = createProblemDetail(HttpStatus.BAD_REQUEST, "Validation Error", "Invalid field values");
        var errors = ex.getConstraintViolations().stream()
                .map(cv -> Map.of(
                        "field", cv.getPropertyPath().toString(),
                        "message", cv.getMessage()
                ))
                .toList();
        pd.setProperty("errors", errors);
        return pd;
    }

    /**
     * ResponseStatusException (manual throws)
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        return createProblemDetail(status, status.getReasonPhrase(), ex.getReason());
    }

    /**
     * Malformed JSON or incorrect data types in request bodies (like PATCH/POST)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String detail = "Malformed JSON or invalid data type";
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidFormat) {
            detail = String.format(
                    "Invalid value '%s' for field '%s'. Expected type: %s",
                    invalidFormat.getValue(),
                    invalidFormat.getPath().stream().map(p -> p.getFieldName()).reduce((a,b) -> a+"."+b).orElse("?"),
                    invalidFormat.getTargetType().getSimpleName()
            );
        } else if (cause instanceof MismatchedInputException mismatch) {
            detail = "Mismatched input: " + mismatch.getOriginalMessage();
        }
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Invalid Request Body", detail);
    }

    /**
     * Unique constraint violations (duplicate email, registrationNumber, employeeNumber)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail pd = createProblemDetail(HttpStatus.CONFLICT, "Conflict", "Database constraint violated");
        String message = ex.getMostSpecificCause().getMessage();
        if (message != null && message.contains("Duplicate entry")) {
            String field = "Field";
            if (message.contains("UKob8kqyqqgmefl0aco34akdtpe")) field = "email";
            else if (message.contains("admin.UKnqu8w0fjd1yujs0klm1vboyt4")) field = "employeeNumber";
            else if (message.contains("vehicle.UK6fo0502tpr111m29vqj0bhpa4")) field = "registrationNumber";

            pd = createProblemDetail(HttpStatus.CONFLICT, "Conflict", field + " already exists");
        }
        return pd;
    }

    /**
     * Fallback for unexpected errors
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    /**
     * Helper: Creates a ProblemDetail object with the given HTTP status, title, and detail message.
     */
    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        return pd;
    }
}