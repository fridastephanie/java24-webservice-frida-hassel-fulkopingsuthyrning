package se.gritacademy.fulkoping_rental.controller.rental;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.gritacademy.fulkoping_rental.dto.rental.CreateRentalDTO;
import se.gritacademy.fulkoping_rental.dto.rental.RentalDTO;
import se.gritacademy.fulkoping_rental.mapper.RentalMapper;
import se.gritacademy.fulkoping_rental.model.rental.Rental;
import se.gritacademy.fulkoping_rental.service.rental.RentalService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rentals")
@SecurityRequirement(name = "AdminKey")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * (ADMIN) Fetches all rentals from the database,
     * maps each rental to a DTO, and returns the list.
     */
    @Operation(
            summary = "Get all rentals",
            description = "Fetches all rentals from the database and returns them as DTOs. Requires AdminKey.",
            tags = {"Rentals"}
    )
    @GetMapping
    public ResponseEntity<List<RentalDTO>> getAllRentals() {
        List<RentalDTO> list = rentalService.getAllRentals().stream()
                .map(RentalMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /**
     * (ADMIN) Fetches a specific rental by ID,
     * maps it to a DTO, and returns it.
     */
    @Operation(
            summary = "Get rental by ID",
            description = "Fetches a rental by its ID and returns it as a DTO. Requires AdminKey.",
            tags = {"Rentals"}
    )
    @GetMapping("/{id}")
    public RentalDTO getRentalById(@PathVariable Long id) {
        Rental rental = rentalService.getRental(id);
        return RentalMapper.toDTO(rental);
    }

    /**
     * (ADMIN) Fetches all rentals for a specific user,
     * maps them to DTOs, and returns the users rental history.
     */
    @Operation(
            summary = "Get user rental history",
            description = "Fetches all rentals for a specific user. Requires AdminKey.",
            tags = {"Rentals"}
    )
    @GetMapping("/history/users/{userId}")
    public List<RentalDTO> getUserHistory(@PathVariable Long userId) {
        return rentalService.getHistoryForUser(userId).stream()
                .map(RentalMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * (ADMIN) Fetches all rentals for a specific vehicle,
     * maps them to DTOs, and returns the vehicles rental history.
     */
    @Operation(
            summary = "Get vehicle rental history",
            description = "Fetches all rentals for a specific vehicle. Requires AdminKey.",
            tags = {"Rentals"}
    )
    @GetMapping("/history/vehicles/{vehicleId}")
    public List<RentalDTO> getVehicleHistory(@PathVariable Long vehicleId) {
        return rentalService.getHistoryForVehicle(vehicleId).stream()
                .map(RentalMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * (ADMIN) Automatically validates the incoming DTO via @Valid,
     * creates a new rental entity, saves it to the database,
     * and returns the created rental as DTO with 201 status.
     */
    @Operation(
            summary = "Create rental",
            description = "Creates a new rental from the provided DTO. Requires AdminKey.",
            tags = {"Rentals"}
    )
    @PostMapping
    public ResponseEntity<RentalDTO> createRental(@Valid @RequestBody CreateRentalDTO dto) {
        Rental rental = rentalService.createRental(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(RentalMapper.toDTO(rental));
    }

    /**
     * (ADMIN) Ensures the request body is empty,
     * marks the rental as returned via the service,
     * and returns the updated rental as DTO.
     */
    @Operation(
            summary = "Return rental",
            description = "Marks a rental as returned. Body must be empty. Requires AdminKey.",
            tags = {"Rentals"}
    )
    @PatchMapping("/{id}/return")
    public RentalDTO registerReturn(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        if (body != null && !body.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body should be empty when returning a rental.");
        }
        Rental returned = rentalService.returnRental(id);
        return RentalMapper.toDTO(returned);
    }

    /**
     * (ADMIN) Deletes the rental by ID,
     * and returns 204 No Content on success.
     */
    @Operation(
            summary = "Delete rental",
            description = "Deletes a rental by its ID. Requires AdminKey.",
            tags = {"Rentals"}
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}