package se.gritacademy.fulkoping_rental.mapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.gritacademy.fulkoping_rental.dto.vehicle.CreateVehicleDTO;
import se.gritacademy.fulkoping_rental.dto.vehicle.VehicleDTO;
import se.gritacademy.fulkoping_rental.model.vehicle.*;

public class VehicleMapper {

    public static VehicleDTO toDTO(Vehicle vehicle) {
        if (vehicle instanceof Car car) {
            return new VehicleDTO(
                    car.getId(),
                    "Car",
                    car.getRegistrationNumber(),
                    car.getBrand(),
                    car.getModel(),
                    car.isRented(),
                    car.getSeatCount(),
                    null,
                    null
            );
        } else if (vehicle instanceof Trailer trailer) {
            return new VehicleDTO(
                    trailer.getId(),
                    "Trailer",
                    trailer.getRegistrationNumber(),
                    trailer.getBrand(),
                    trailer.getModel(),
                    trailer.isRented(),
                    null,
                    trailer.getMaxWeight(),
                    null
            );
        } else if (vehicle instanceof Truck truck) {
            return new VehicleDTO(
                    truck.getId(),
                    "Truck",
                    truck.getRegistrationNumber(),
                    truck.getBrand(),
                    truck.getModel(),
                    truck.isRented(),
                    null,
                    null,
                    truck.getDrivingLicenseLevel()
            );
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown vehicle type");
        }
    }

    public static Vehicle fromCreateDTO(CreateVehicleDTO dto) {
        return switch (dto.getType().toLowerCase()) {
            case "car" -> new Car(dto.getRegistrationNumber(), dto.getBrand(), dto.getModel(), dto.isRented(), dto.getSeatCount());
            case "trailer" -> new Trailer(dto.getRegistrationNumber(), dto.getBrand(), dto.getModel(), dto.isRented(), dto.getMaxWeight());
            case "truck" -> new Truck(dto.getRegistrationNumber(), dto.getBrand(), dto.getModel(), dto.isRented(), dto.getDrivingLicenseLevel());
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown type: " + dto.getType());
        };
    }
}