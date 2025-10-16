package se.gritacademy.fulkoping_rental.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import se.gritacademy.fulkoping_rental.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {}