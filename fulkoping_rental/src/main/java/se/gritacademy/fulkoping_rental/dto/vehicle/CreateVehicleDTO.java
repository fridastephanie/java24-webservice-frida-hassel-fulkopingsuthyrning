package se.gritacademy.fulkoping_rental.dto.vehicle;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

public class CreateVehicleDTO {

    @NotBlank(message = "Vehicle type must not be blank (Car, Truck, Trailer)")
    private String type;

    @NotBlank(message = "Registration number must not be blank")
    @Column(unique = true)
    private String registrationNumber;

    @NotBlank(message = "Brand must not be blank")
    private String brand;

    @NotBlank(message = "Model must not be blank")
    private String model;

    // Only Car
    @Min(value = 1, message = "Car must have at least 1 seat")
    @Max(value = 9, message = "Car cannot have more than 9 seats")
    private Integer seatCount;

    // Only Trailer
    @Min(value = 1, message = "Max weight must be positive")
    @Max(value = 750, message = "Max weight cannot exceed 750kg")
    private Integer maxWeight;

    // Only Truck
    private String drivingLicenseLevel;

    @NotNull(message = "Rented status must not be null")
    private Boolean isRented;

    public CreateVehicleDTO() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public Integer getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Integer maxWeight) {
        this.maxWeight = maxWeight;
    }

    public String getDrivingLicenseLevel() {
        return drivingLicenseLevel;
    }

    public void setDrivingLicenseLevel(String drivingLicenseLevel) {
        this.drivingLicenseLevel = drivingLicenseLevel;
    }

    public Boolean isRented() {
        return isRented;
    }

    public void setRented(Boolean rented) {
        isRented = rented;
    }
}