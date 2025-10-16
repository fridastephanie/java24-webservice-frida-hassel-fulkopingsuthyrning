package se.gritacademy.fulkoping_rental.dto.vehicle;

public class VehicleDTO {
    private Long id;
    private String type;
    private String registrationNumber;
    private String brand;
    private String model;
    private boolean isRented;
    // Only Car
    private Integer seatCount;
    // Only Trailer
    private Integer maxWeight;
    // Only Truck
    private String drivingLicenseLevel;

    public VehicleDTO(Long id, String type, String registrationNumber, String brand, String model,
                      boolean isRented, Integer seatCount, Integer maxWeight, String drivingLicenseLevel) {
        this.id = id;
        this.type = type;
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.model = model;
        this.isRented = isRented;
        this.seatCount = seatCount;
        this.maxWeight = maxWeight;
        this.drivingLicenseLevel = drivingLicenseLevel;
    }

    public VehicleDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
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
}