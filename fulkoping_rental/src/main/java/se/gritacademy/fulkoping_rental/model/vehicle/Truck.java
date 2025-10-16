package se.gritacademy.fulkoping_rental.model.vehicle;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "truck")
public class Truck extends Vehicle {

    @NotBlank(message = "Driving license level must not be blank")
    private String drivingLicenseLevel;

    public Truck(String registrationNumber, String brand, String model, boolean isRented, String drivingLicenseLevel) {
        super(registrationNumber, brand, model, isRented);
        this.drivingLicenseLevel = drivingLicenseLevel;
    }

    public Truck() {
    }

    public String getDrivingLicenseLevel() {
        return drivingLicenseLevel;
    }

    public void setDrivingLicenseLevel(String drivingLicenseLevel) {
        this.drivingLicenseLevel = drivingLicenseLevel;
    }
}