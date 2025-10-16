package se.gritacademy.fulkoping_rental.service.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.gritacademy.fulkoping_rental.dto.user.UpdateUserDTO;
import se.gritacademy.fulkoping_rental.model.user.Customer;
import se.gritacademy.fulkoping_rental.model.user.User;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserValidator {

    private final Validator validator;

    public UserValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * Validates the fields in a patch request for a User
     * Throws BAD_REQUEST if invalid or not allowed.
     */
    public void validatePatchFields(User user, Map<String, Object> fields) {
        if (fields == null || fields.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot be empty");
        }
        checkAllowedFields(user, fields);
        validateStringFields(fields);
        UpdateUserDTO tempDto = buildTempDto(fields);
        validateDtoConstraints(tempDto);
    }

    /**
     * Helper: Ensures that only allowed fields are present in the patch request
     * Throws BAD_REQUEST if not allowed.
     */
    private void checkAllowedFields(User user, Map<String, Object> fields) {
        Set<String> allowedFields = new HashSet<>(List.of("firstName", "lastName", "email"));
        if (user instanceof Customer) allowedFields.add("phoneNumber");
        for (String key : fields.keySet()) {
            if (!allowedFields.contains(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field not allowed: " + key);
            }
        }
    }

    /**
     * Helper: Validates that all provided fields are strings.
     * Throws BAD_REQUEST if any field is not a string.
     */
    private void validateStringFields(Map<String, Object> fields) {
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Object value = entry.getValue();
            if (!(value instanceof String)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, entry.getKey() + " must be a string");
            }
        }
    }

    /**
     * Helper: Builds a temporary DTO from the request fields.
     */
    private UpdateUserDTO buildTempDto(Map<String, Object> fields) {
        UpdateUserDTO dto = new UpdateUserDTO();
        Map<String, java.util.function.BiConsumer<UpdateUserDTO, String>> setters = Map.of(
                "firstName", UpdateUserDTO::setFirstName,
                "lastName", UpdateUserDTO::setLastName,
                "email", UpdateUserDTO::setEmail,
                "phoneNumber", UpdateUserDTO::setPhoneNumber
        );
        for (Map.Entry<String, java.util.function.BiConsumer<UpdateUserDTO, String>> entry : setters.entrySet()) {
            String key = entry.getKey();
            if (fields.containsKey(key)) {
                entry.getValue().accept(dto, (String) fields.get(key));
            }
        }
        return dto;
    }

    /**
     * Helper: Validates the DTO fields against constraints
     * Throws BAD_REQUEST if not allowed.
     */
    private void validateDtoConstraints(UpdateUserDTO dto) {
        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Invalid fields");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }
}