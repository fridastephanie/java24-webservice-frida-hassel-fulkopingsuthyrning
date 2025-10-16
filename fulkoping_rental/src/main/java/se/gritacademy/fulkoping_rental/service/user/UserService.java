package se.gritacademy.fulkoping_rental.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se.gritacademy.fulkoping_rental.model.user.User;
import se.gritacademy.fulkoping_rental.repository.user.UserRepository;
import se.gritacademy.fulkoping_rental.service.rental.RentalService;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RentalService rentalService;

    public UserService(UserRepository userRepository, RentalService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    /**
     * Saves a new user or updates an existing user in the database.
     */
    public User saveUser(User user) {
        User savedUser = userRepository.save(user);
        logger.info("Saved user (id={}) with name={} {} and email={}", savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());
        return savedUser;
    }

    /**
     * Fetches and returns all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Fetches a user by ID or throws 404 if not found.
     */
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with id " + id));
    }

    /**
     * Deletes a user if they have no active rentals; otherwise throws 400.
     * Before deleting the user, all finished rentals associated with the user are also deleted.
     */
    public void deleteUser(Long id) {
        if (rentalService.userHasActiveRental(id)) {
            logger.warn("Attempted to delete user (id={}) with active rentals", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete user with active rentals");
        }
        rentalService.deleteFinishedRentalsByUser(id);
        userRepository.deleteById(id);
        logger.info("Deleted user (id={})", id);
    }
}