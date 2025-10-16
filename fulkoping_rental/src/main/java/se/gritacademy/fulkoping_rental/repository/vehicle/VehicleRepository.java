package se.gritacademy.fulkoping_rental.repository.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import se.gritacademy.fulkoping_rental.model.vehicle.Vehicle;


public interface VehicleRepository extends JpaRepository<Vehicle, Long> {}
