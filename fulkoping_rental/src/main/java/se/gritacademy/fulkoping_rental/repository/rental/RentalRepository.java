package se.gritacademy.fulkoping_rental.repository.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import se.gritacademy.fulkoping_rental.model.rental.Rental;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByUserId(Long userId);
    List<Rental> findByVehicleId(Long vehicleId);
    List<Rental> findByUserIdAndEndDateTimeIsNotNull(Long userId);
    List<Rental> findByVehicleIdAndEndDateTimeIsNotNull(Long vehicleId);
}