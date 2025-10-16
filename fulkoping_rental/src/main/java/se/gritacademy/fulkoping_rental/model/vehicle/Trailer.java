package se.gritacademy.fulkoping_rental.model.vehicle;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "trailer")
public class Trailer extends Vehicle {

    @Min(value = 1, message = "Max weight must be positive")
    @Max(value = 750, message = "Max weight cannot be more than 750kg")
    @NotNull(message = "Max weight must not be null")
    private Integer maxWeight;

    public Trailer(String registrationNumber, String brand, String model, boolean isRented, Integer maxWeight) {
        super(registrationNumber, brand, model, isRented);
        this.maxWeight = maxWeight;
    }

    public Trailer() {
    }

    public Integer getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Integer maxWeight) {
        this.maxWeight = maxWeight;
    }
}