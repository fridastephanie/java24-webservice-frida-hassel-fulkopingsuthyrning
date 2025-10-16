package se.gritacademy.fulkoping_rental.service.rental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se.gritacademy.fulkoping_rental.dto.rental.CreateRentalDTO;
import se.gritacademy.fulkoping_rental.model.rental.Rental;
import se.gritacademy.fulkoping_rental.model.user.User;
import se.gritacademy.fulkoping_rental.model.vehicle.Vehicle;
import se.gritacademy.fulkoping_rental.repository.rental.RentalRepository;
import se.gritacademy.fulkoping_rental.repository.user.UserRepository;
import se.gritacademy.fulkoping_rental.repository.vehicle.VehicleRepository;

import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class RentalService {

    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalRepository,
                         UserRepository userRepository,
                         VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Creates a new rental for a user and vehicle.
     * Checks if vehicle is available, marks it as rented, saves the rental, and returns it.
     */
    public Rental createRental(CreateRentalDTO dto) {
        User user = getUser(dto.getUserId());
        Vehicle vehicle = getVehicle(dto.getVehicleId());
        if (vehicle.isRented()) {
            logger.warn("Attempted to create rental for vehicle {} that is already rented", vehicle.getRegistrationNumber());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Vehicle is already rented: " + vehicle.getRegistrationNumber());
        }
        markVehicleRented(vehicle, true);
        Rental rental = buildNewRental(user, vehicle);
        Rental savedRental = rentalRepository.save(rental);
        logger.info("Created new rental (id={}) for userId={} and vehicleId={}", savedRental.getId(), user.getId(), vehicle.getId());
        return savedRental;
    }

    /**
     * Registers the return of a rental.
     * Sets end date, marks vehicle as available, and saves the updated rental.
     */
    public Rental returnRental(Long rentalId) {
        Rental rental = getRental(rentalId);
        if (rental.getEndDateTime() != null) {
            logger.warn("Attempted to return rental (id={}) that is already returned", rentalId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rental already returned");
        }
        rental.setEndDateTime(OffsetDateTime.now());
        Vehicle vehicle = getVehicle(rental.getVehicleId());
        markVehicleRented(vehicle, false);
        Rental updatedRental = rentalRepository.save(rental);
        logger.info("Rental (id={}) returned for vehicleId={} by userId={}", updatedRental.getId(), vehicle.getId(), rental.getUser().getId());
        return updatedRental;
    }

    /**
     * Deletes a rental.
     * Marks vehicle as available if rental is still active and removes rental from repository.
     * Marked as @Transactional to ensure both vehicle update and rental deletion happen atomically.
     */
    @Transactional
    public void deleteRental(Long rentalId) {
        Rental rental = getRental(rentalId);
        if (rental.getEndDateTime() == null) {
            Vehicle vehicle = getVehicle(rental.getVehicleId());
            markVehicleRented(vehicle, false);
        }
        rentalRepository.deleteById(rentalId);
        logger.info("Deleted rental (id={}) for vehicleId={} and userId={}", rental.getId(), rental.getVehicleId(), rental.getUser().getId());
    }

    /**
     * Deletes all finished rentals for a given user.
     * A finished rental is defined as having a non-null endDateTime.
     * Marked as @Transactional to ensure all deletions happen atomically.
     * If no finished rentals exist, the method does nothing.
     */
    @Transactional
    public void deleteFinishedRentalsByUser(Long userId) {
        List<Rental> finishedRentals = rentalRepository.findByUserIdAndEndDateTimeIsNotNull(userId);
        rentalRepository.deleteAll(finishedRentals);
        logger.info("Deleted {} finished rentals for userId={}", finishedRentals.size(), userId);
    }

    /**
     * Deletes all finished rentals for a given vehicle.
     * A finished rental is defined as having a non-null endDateTime.
     * Marked as @Transactional to ensure all deletions happen atomically.
     * If no finished rentals exist, the method does nothing.
     */
    @Transactional
    public void deleteFinishedRentalsByVehicle(Long vehicleId) {
        List<Rental> finishedRentals = rentalRepository.findByVehicleIdAndEndDateTimeIsNotNull(vehicleId);
        rentalRepository.deleteAll(finishedRentals);
        logger.info("Deleted {} finished rentals for vehicleId={}", finishedRentals.size(), vehicleId);
    }

    /**
     * Returns all rentals for a specific user.
     * Throws if user not found.
     */
    public List<Rental> getHistoryForUser(Long userId) {
        getUser(userId);
        return rentalRepository.findByUserId(userId);
    }

    /**
     * Returns all rentals for a specific vehicle.
     * Throws if vehicle not found.
     */
    public List<Rental> getHistoryForVehicle(Long vehicleId) {
        getVehicle(vehicleId);
        return rentalRepository.findByVehicleId(vehicleId);
    }

    /**
     * Returns all rentals in the system.
     */
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    /**
     * Checks if a vehicle has any active rental.
     */
    public boolean vehicleHasActiveRental(Long vehicleId) {
        return rentalRepository.findByVehicleId(vehicleId)
                .stream().anyMatch(r -> r.getEndDateTime() == null);
    }

    /**
     * Checks if a user has any active rental.
     */
    public boolean userHasActiveRental(Long userId) {
        return rentalRepository.findByUserId(userId)
                .stream().anyMatch(r -> r.getEndDateTime() == null);
    }

    /**
     * Fetches rental or throws 404 if not found.
     */
    public Rental getRental(Long rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rental not found"));
    }

    /**
     * Saves a rental entity to the database.
     */
    public void saveRental(Rental rental) {
        rentalRepository.save(rental);
    }

    /**
     * Helper: Fetches user or throws 404 if not found.
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    /**
     * Helper: Fetches vehicle or throws 404 if not found.
     */
    private Vehicle getVehicle(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Vehicle not found"));
    }

    /**
     * Helper: Marks a vehicle as rented or available and saves it.
     */
    private void markVehicleRented(Vehicle vehicle, boolean rented) {
        vehicle.setRented(rented);
        vehicleRepository.save(vehicle);
        logger.info("Vehicle {} rental status updated to {}", vehicle.getRegistrationNumber(), rented ? "rented" : "available");
    }

    /**
     * Helper: Builds a new Rental entity from user and vehicle info.
     */
    private Rental buildNewRental(User user, Vehicle vehicle) {
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setVehicleId(vehicle.getId());
        rental.setVehicleRegistrationNumber(vehicle.getRegistrationNumber());
        rental.setVehicleType(vehicle.getClass().getSimpleName());
        rental.setStartDateTime(OffsetDateTime.now());
        return rental;
    }
}