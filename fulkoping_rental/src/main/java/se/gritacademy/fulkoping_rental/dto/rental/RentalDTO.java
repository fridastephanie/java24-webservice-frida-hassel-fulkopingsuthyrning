package se.gritacademy.fulkoping_rental.dto.rental;

import java.time.OffsetDateTime;

public class RentalDTO {
    private Long id;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private Long vehicleId;
    private String VehicleRegistrationNumber;
    private String VehicleType;
    private OffsetDateTime startDateTime;
    private OffsetDateTime endDateTime;

    public RentalDTO() {}

    public RentalDTO(Long id, Long userId, String userFirstName, String userLastName,
                     Long vehicleId, String VehicleRegistrationNumber, String VehicleType,
                     OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
        this.id = id;
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.vehicleId = vehicleId;
        this.VehicleRegistrationNumber = VehicleRegistrationNumber;
        this.VehicleType = VehicleType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleRegistrationNumber() {
        return VehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        VehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public OffsetDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(OffsetDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public OffsetDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(OffsetDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}