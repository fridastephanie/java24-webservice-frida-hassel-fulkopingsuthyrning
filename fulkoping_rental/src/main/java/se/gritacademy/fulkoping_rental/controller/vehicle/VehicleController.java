package se.gritacademy.fulkoping_rental.controller.vehicle;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.gritacademy.fulkoping_rental.dto.vehicle.CreateVehicleDTO;
import se.gritacademy.fulkoping_rental.dto.vehicle.VehicleDTO;
import se.gritacademy.fulkoping_rental.model.vehicle.Car;
import se.gritacademy.fulkoping_rental.model.vehicle.Trailer;
import se.gritacademy.fulkoping_rental.model.vehicle.Truck;
import se.gritacademy.fulkoping_rental.model.vehicle.Vehicle;
import se.gritacademy.fulkoping_rental.service.vehicle.VehicleService;
import se.gritacademy.fulkoping_rental.mapper.VehicleMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * (USER/ADMIN) Fetches all vehicles, maps them to DTOs,
     * and returns the list.
     */
    @Operation(
            summary = "Get all vehicles",
            description = "Fetches all vehicles and returns them as DTOs. Requires UserKey.",
            tags = {"Vehicles"},
            security = @SecurityRequirement(name = "UserKey")
    )
    @GetMapping
    public List<VehicleDTO> getAllVehicles() {
        return vehicleService.getAllVehicles().stream()
                .map(VehicleMapper::toDTO)
                .toList();
    }

    /**
     * (USER/ADMIN) Filters all vehicles to only include cars,
     * maps them to DTOs, and returns the list.
     */
    @Operation(
            summary = "Get all cars",
            description = "Fetches all vehicles of type Car. Requires UserKey.",
            tags = {"Vehicles"},
            security = @SecurityRequirement(name = "UserKey")
    )
    @GetMapping("/cars")
    public List<VehicleDTO> getAllCars() {
        return vehicleService.getAllVehicles().stream()
                .filter(v -> v instanceof Car)
                .map(VehicleMapper::toDTO)
                .toList();
    }

    /**
     * (USER/ADMIN) Filters all vehicles to only include trailers,
     * maps them to DTOs, and returns the list.
     */
    @Operation(
            summary = "Get all trailers",
            description = "Fetches all vehicles of type Trailer. Requires UserKey.",
            tags = {"Vehicles"},
            security = @SecurityRequirement(name = "UserKey")
    )
    @GetMapping("/trailers")
    public List<VehicleDTO> getAllTrailers() {
        return vehicleService.getAllVehicles().stream()
                .filter(v -> v instanceof Trailer)
                .map(VehicleMapper::toDTO)
                .toList();
    }

    /**
     * (USER/ADMIN) Filters all vehicles to only include trucks,
     * maps them to DTOs, and returns the list.
     */
    @Operation(
            summary = "Get all trucks",
            description = "Fetches all vehicles of type Truck. Requires UserKey.",
            tags = {"Vehicles"},
            security = @SecurityRequirement(name = "UserKey")
    )
    @GetMapping("/trucks")
    public List<VehicleDTO> getAllTrucks() {
        return vehicleService.getAllVehicles().stream()
                .filter(v -> v instanceof Truck)
                .map(VehicleMapper::toDTO)
                .toList();
    }

    /**
     * (USER/ADMIN) Fetches a specific vehicle by ID,
     * maps it to DTO, and returns it.
     */
    @Operation(
            summary = "Get vehicle by ID",
            description = "Fetches a specific vehicle by its ID. Requires UserKey.",
            tags = {"Vehicles"},
            security = @SecurityRequirement(name = "UserKey")
    )
    @GetMapping("/{id}")
    public VehicleDTO getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return VehicleMapper.toDTO(vehicle);
    }

    /**
     * (ADMIN) Automatically validates the incoming DTO via @Valid,
     * creates a new vehicle entity, saves it to the database,
     * and returns the created vehicle as DTO with 201 status.
     */
    @Operation(
            summary = "Create a vehicle",
            description = "Creates a new vehicle based on the provided data. Requires AdminKey.",
            tags = {"Vehicles"},
            security = @SecurityRequirement(name = "AdminKey")
    )
    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody CreateVehicleDTO dto) {
        Vehicle vehicle = VehicleMapper.fromCreateDTO(dto);
        Vehicle saved = vehicleService.saveVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(VehicleMapper.toDTO(saved));
    }

    /**
     * (ADMIN) Validates allowed PATCH fields (only rented),
     * updates the vehicles rental status,
     * ends active rental if marking as available
     * and returns the updated vehicle as DTO.
     */
    @Operation(
            summary = "Update rent status",
            description = "Updates the 'rented' field of a vehicle by ID. Only 'rented' is allowed. Requires AdminKey.",
            tags = {"Vehicles"},
            security = @SecurityRequirement(name = "AdminKey")
    )
    @PatchMapping("/{id}/rent")
    public VehicleDTO updateRentStatus(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        validateAllowedFields(fields);
        boolean rented = extractRented(fields);
        Vehicle updatedVehicle = vehicleService.updateRentStatus(id, rented);
        return VehicleMapper.toDTO(updatedVehicle);
    }

    /**
     * (ADMIN) Deletes the vehicle by ID,
     * and returns 204 No Content on success.
     * Throws 404 Not Found if the vehicle does not exist.
     */
    @Operation(
            summary = "Delete vehicle",
            description = "Deletes a vehicle by its ID. Requires AdminKey.",
            tags = {"Vehicles"},
            security = @SecurityRequirement(name = "AdminKey")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper: Validates that the PATCH request body only contains allowed fields (rented).
     * Throws 400 if any other fields are present.
     */
    private void validateAllowedFields(Map<String, Object> fields) {
        Set<String> allowedFields = Set.of("rented");
        for (String key : fields.keySet()) {
            if (!allowedFields.contains(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field not allowed: " + key);
            }
        }
    }

    /**
     * Helper: Validates that the 'rented' field is present and is a boolean.
     * Throws 400 if 'rented' is missing or not a boolean
     */
    private boolean extractRented(Map<String, Object> fields) {
        if (!fields.containsKey("rented") || !(fields.get("rented") instanceof Boolean)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'rented' field is required and must be boolean");
        }
        return (Boolean) fields.get("rented");
    }
}