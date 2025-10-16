package se.gritacademy.fulkoping_rental.mapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.gritacademy.fulkoping_rental.dto.user.CreateUserDTO;
import se.gritacademy.fulkoping_rental.dto.user.UserDTO;
import se.gritacademy.fulkoping_rental.model.user.Admin;
import se.gritacademy.fulkoping_rental.model.user.Customer;
import se.gritacademy.fulkoping_rental.model.user.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user instanceof Admin admin) {
            return new UserDTO(
                    admin.getId(),
                    "Admin",
                    admin.getFirstName(),
                    admin.getLastName(),
                    admin.getEmail(),
                    null,
                    admin.getEmployeeNumber()
            );
        } else if (user instanceof Customer customer) {
            return new UserDTO(
                    customer.getId(),
                    "Customer",
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    customer.getPhoneNumber(),
                    null
            );
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown user type");
        }
    }

    public static User fromCreateDTO(CreateUserDTO dto) {
        return switch (dto.getType().toLowerCase()) {
            case "admin" -> new Admin(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getEmployeeNumber());
            case "customer" -> new Customer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getPhoneNumber());
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown type: " + dto.getType());
        };
    }
}