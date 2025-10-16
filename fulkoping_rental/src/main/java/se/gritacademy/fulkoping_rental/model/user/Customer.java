package se.gritacademy.fulkoping_rental.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "customer")
public class Customer extends User {

    @NotBlank(message = "Phone number must not be blank")
    @Pattern(regexp = "\\+\\d{2}\\s?\\d{6,15}", message = "Phone number must be in the format +46xxxxxxx")
    private String phoneNumber;

    public Customer() {}

    public Customer(String firstName, String lastName, String email, String phoneNumber) {
        super(firstName, lastName, email);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}