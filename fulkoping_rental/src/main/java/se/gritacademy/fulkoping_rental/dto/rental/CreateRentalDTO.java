package se.gritacademy.fulkoping_rental.dto.rental;

import jakarta.validation.constraints.NotNull;


public class CreateRentalDTO {

    @NotNull
    private Long userId;

    @NotNull
    private Long vehicleId;

    public CreateRentalDTO() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
}