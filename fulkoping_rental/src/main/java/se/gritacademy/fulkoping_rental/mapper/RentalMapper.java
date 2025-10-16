package se.gritacademy.fulkoping_rental.mapper;

import se.gritacademy.fulkoping_rental.dto.rental.RentalDTO;
import se.gritacademy.fulkoping_rental.model.rental.Rental;

public class RentalMapper {
    public static RentalDTO toDTO(Rental r) {
        RentalDTO dto = new RentalDTO();
        dto.setId(r.getId());
        dto.setUserId(r.getUser().getId());
        dto.setUserFirstName(r.getUser().getFirstName());
        dto.setUserLastName(r.getUser().getLastName());
        dto.setVehicleId(r.getVehicleId());
        dto.setVehicleRegistrationNumber(r.getVehicleRegistrationNumber());
        dto.setVehicleType(r.getVehicleType());
        dto.setStartDateTime(r.getStartDateTime());
        dto.setEndDateTime(r.getEndDateTime());
        return dto;
    }
}