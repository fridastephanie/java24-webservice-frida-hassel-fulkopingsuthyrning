package se.gritacademy.fulkoping_rental.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "admin")
public class Admin extends User {

    @NotBlank(message = "Employee number must not be blank")
    @Column(unique = true)
    private String employeeNumber;

    public Admin() {}

    public Admin(String firstName, String lastName, String email, String employeeNumber) {
        super(firstName, lastName, email);
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}