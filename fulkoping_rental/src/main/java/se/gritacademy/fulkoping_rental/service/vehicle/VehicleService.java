package se.gritacademy.fulkoping_rental.service.vehicle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.gritacademy.fulkoping_rental.model.rental.Rental;
import se.gritacademy.fulkoping_rental.model.vehicle.Vehicle;
import se.gritacademy.fulkoping_rental.repository.vehicle.VehicleRepository;
import se.gritacademy.fulkoping_rental.service.rental.RentalService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository vehicleRepository;
    private final RentalService rentalService;

    public VehicleService(VehicleRepository vehicleRepository, RentalService rentalService) {
        this.vehicleRepository = vehicleRepository;
        this.rentalService = rentalService;
    }

    /**
     * Saves a new vehicle or updates an existing vehicle in the database.
     */
    public Vehicle saveVehicle(Vehicle vehicle) {
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        logger.info("Saved vehicle (id={}) with registration number={} and type={}",
                savedVehicle.getId(), savedVehicle.getRegistrationNumber(), savedVehicle.getClass().getSimpleName());
        return savedVehicle;
    }

    /**
     * Fetches and returns all vehicles.
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Fetches a vehicle by ID or throws 404 if not found.
     */
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Vehicle not found with id " + id));
    }

    /**
     * Updates the rented status of a vehicle.
     * Ends active rental if marking as available.
     */
    public Vehicle updateRentStatus(Long id, boolean rented) {
        Vehicle vehicle = getVehicleById(id);
        if (!rented && rentalService.vehicleHasActiveRental(id)) {
            endActiveRental(id);
        }
        vehicle.setRented(rented);
        Vehicle updated = vehicleRepository.save(vehicle);
        logger.info("Vehicle (id={}) rental status updated to {}", updated.getId(), rented ? "rented" : "available");
        return updated;
    }

    /**
     * Deletes a vehicle if it has no active rentals; otherwise throws 400.
     * Before deleting the vehicle, all finished rentals associated with the vehicle are also deleted.
     */
    public void deleteVehicle(Long id) {
        if (rentalService.vehicleHasActiveRental(id)) {
            logger.warn("Attempted to delete vehicle (id={}) with active rentals", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete vehicle with active rentals");
        }
        rentalService.deleteFinishedRentalsByVehicle(id);
        vehicleRepository.deleteById(id);
        logger.info("Deleted vehicle (id={})", id);
    }

    /**
     * Helper: Ends active rental for a vehicle if it exists.
     */
    private void endActiveRental(Long vehicleId) {
        getActiveRental(vehicleId).ifPresent(this::finishRental);
    }

    /**
     * Helper: Fetches active rental for a vehicle.
     */
    private Optional<Rental> getActiveRental(Long vehicleId) {
        return rentalService.getHistoryForVehicle(vehicleId).stream()
                .filter(r -> r.getEndDateTime() == null)
                .findFirst();
    }

    /**
     * Helper: Finishes a rental by setting endDateTime and saving it.
     */
    private void finishRental(Rental rental) {
        rental.setEndDateTime(OffsetDateTime.now());
        rentalService.saveRental(rental);
        logger.info("Active rental (id={}) ended automatically due to PATCH on vehicle {}", rental.getId(), rental.getVehicleId());
    }
}