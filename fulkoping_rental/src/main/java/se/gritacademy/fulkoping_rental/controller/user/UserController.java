package se.gritacademy.fulkoping_rental.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.gritacademy.fulkoping_rental.dto.user.CreateUserDTO;
import se.gritacademy.fulkoping_rental.dto.user.UserDTO;
import se.gritacademy.fulkoping_rental.model.user.Customer;
import se.gritacademy.fulkoping_rental.model.user.User;
import se.gritacademy.fulkoping_rental.service.user.UserService;
import se.gritacademy.fulkoping_rental.mapper.UserMapper;
import se.gritacademy.fulkoping_rental.service.user.UserValidator;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "AdminKey")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * (ADMIN) Fetches all users from the database,
     * maps them to DTOs, and returns the list.
     */
    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users. Requires AdminKey.",
            tags = {"Users"}
    )
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(UserMapper::toDTO)
                .toList();
        return ResponseEntity.ok(users);
    }

    /**
     * (ADMIN) Filters all users to include only customers,
     * maps them to DTOs, and returns the list.
     */
    @Operation(
            summary = "Get all customers",
            description = "Returns all users with customer role. Requires AdminKey.",
            tags = {"Users"}
    )
    @GetMapping("/customers")
    public ResponseEntity<List<UserDTO>> getAllCustomers() {
        List<UserDTO> customers = userService.getAllUsers().stream()
                .filter(u -> u instanceof Customer)
                .map(UserMapper::toDTO)
                .toList();
        return ResponseEntity.ok(customers);
    }

    /**
     * (ADMIN) Filters all users to include only admins,
     * maps them to DTOs, and returns the list.
     */
    @Operation(
            summary = "Get all admins",
            description = "Returns all users with admin role. Requires AdminKey.",
            tags = {"Users"}
    )
    @GetMapping("/admins")
    public ResponseEntity<List<UserDTO>> getAllAdmins() {
        List<UserDTO> admins = userService.getAllUsers().stream()
                .filter(u -> u.getClass().getSimpleName().equals("Admin"))
                .map(UserMapper::toDTO)
                .toList();
        return ResponseEntity.ok(admins);
    }

    /**
     * (ADMIN) Fetches a specific user by ID,
     * maps the user to a DTO, and returns it.
     */
    @Operation(
            summary = "Get user by ID",
            description = "Fetches a specific user by ID. Requires AdminKey.",
            tags = {"Users"}
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    /**
     * (ADMIN) Automatically validates the incoming DTO via @Valid,
     * creates a new user entity, saves it to the database,
     * and returns the created user as DTO with 201 status.
     */
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user from the provided DTO. Requires AdminKey.",
            tags = {"Users"}
    )
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO dto) {
        User user = UserMapper.fromCreateDTO(dto);
        User saved = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDTO(saved));
    }

    /**
     * (ADMIN) Fetches the user by ID,
     * validates the provided patch fields,
     * applies allowed updates, saves the user,
     * and returns the updated user as DTO.
     */
    @Operation(
            summary = "Update a user",
            description = "Applies allowed patch fields to a user. Requires AdminKey.",
            tags = {"Users"}
    )
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        User user = userService.getById(id);
        UserValidator validator = new UserValidator();
        validator.validatePatchFields(user, fields);
        applyPatchFields(user, fields);
        User saved = userService.saveUser(user);
        return ResponseEntity.ok(UserMapper.toDTO(saved));
    }

    /**
     * (ADMIN) Deletes the user by ID,
     * and returns 204 No Content on success.
     * Throws 404 Not Found if the user does not exist.
     */
    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by ID. Requires AdminKey.",
            tags = {"Users"}
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper: Applies the allowed PATCH fields (firstName, lastName, email, phoneNumber)
     * to the given user instance. Only updates fields that are valid for the user type.
     */
    private void applyPatchFields(User user, Map<String, Object> fields) {
        if (fields.containsKey("firstName")) user.setFirstName((String) fields.get("firstName"));
        if (fields.containsKey("lastName")) user.setLastName((String) fields.get("lastName"));
        if (fields.containsKey("email")) user.setEmail((String) fields.get("email"));
        if (fields.containsKey("phoneNumber") && user instanceof Customer c) {
            c.setPhoneNumber((String) fields.get("phoneNumber"));
        }
    }
}