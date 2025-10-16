package se.gritacademy.fulkoping_rental.model.vehicle;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "car")
public class Car extends Vehicle {

    @Min(value = 1, message = "Car must have at least 1 seat")
    @Max(value = 9, message = "Car cannot have more than 9 seat")
    @NotNull(message = "Seat count must not be null")
    private Integer seatCount;

    public Car(String registrationNumber, String brand, String model, boolean isRented, Integer seatCount) {
        super(registrationNumber, brand, model, isRented);
        this.seatCount = seatCount;
    }

    public Car() {
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }
}
