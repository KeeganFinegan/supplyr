package com.supplyr.supplyr.service;

import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.SupplyrUserDetails;
import com.supplyr.supplyr.domain.User;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import com.supplyr.supplyr.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplyrUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;


    private final OrganisationalUnitRepository organisationalUnitRepository;


    private final PasswordEncoder passwordEncoder;


    private final TradeService tradeService;

    public SupplyrUserDetailsService(UserRepository userRepository, OrganisationalUnitRepository organisationalUnitRepository, PasswordEncoder passwordEncoder, TradeService tradeService) {
        this.userRepository = userRepository;
        this.organisationalUnitRepository = organisationalUnitRepository;
        this.passwordEncoder = passwordEncoder;
        this.tradeService = tradeService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        user.orElseThrow(() -> new UsernameNotFoundException("Could not find user with username " + username));

        return user.map(SupplyrUserDetails::new).get();

    }

    /**
     * Add new user into the database
     *
     * @param organisationalUnit Organisational Unit that the user is to be a member of
     * @param user               User to be registered
     * @return User that was registered
     * @throws AlreadyExistsException If the user already exists
     * @throws NotFoundException      If the Organisational Unit doesn't exist
     */
    public User registerNewUser(String organisationalUnit, User user) {
        Optional<OrganisationalUnit> optUnit = organisationalUnitRepository.findByUnitName(organisationalUnit);
        Optional<User> optUser = userRepository.findByUsername(user.getUsername());
        if (optUnit.isPresent() && optUser.isEmpty()) {

            user.setOrganisationalUnit(optUnit.get());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(true);
            user.setRoles("ROLE_USER");
            return userRepository.save(user);

        } else if (optUnit.isPresent()) {
            throw new AlreadyExistsException("User with username '" + user.getUsername() + "' already exists");

        } else {
            throw new NotFoundException("Could not find Organisational Unit " + organisationalUnit);
        }
    }

    /**
     * Add new admin user into the database
     *
     * @param user User to be registered
     * @return User that was registered
     * @throws AlreadyExistsException When admin already exists
     * @throws NotFoundException      When Supplyr Admin unit does not exist
     */
    public User registerNewAdmin(User user) {

        Optional<OrganisationalUnit> optUnit = organisationalUnitRepository.findByUnitName("Supplyr Admin");
        Optional<User> optUser = userRepository.findByUsername(user.getUsername());
        if (optUnit.isPresent() && optUser.isEmpty()) {

            user.setOrganisationalUnit(optUnit.get());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(true);
            user.setRoles("ROLE_ADMIN");
            return userRepository.save(user);

        } else if (optUnit.isPresent()) {
            throw new AlreadyExistsException("User with username '" + user.getUsername() + "' already exists");

        } else {
            throw new NotFoundException("Could not find Organisational Unit Supplyr Admin");
        }

    }

    /**
     * Update the details of User with a given Id in the database
     *
     * @param updatedUserPassword New password
     * @return Updated User
     * @throws NotFoundException When the user does not exist
     */
    public User updateUserPassword(User updatedUserPassword, String username) {

        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(updatedUserPassword.getPassword()));
                    return userRepository.save(user);
                }).orElseThrow(() -> new NotFoundException("Could not find student " + updatedUserPassword.getUsername()));
    }

    /**
     * Delete a User with a given id from the database
     *
     * @param user User to be deleted
     * @throws NotFoundException When the user does not exist
     */
    public void deleteUser(User user) {
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            userRepository.deleteById(optionalUser.get().getId());
        } else {
            throw new NotFoundException(String.format("Could not find user %s", user.getUsername()));
        }
    }


    /**
     * Return User details by username
     *
     * @param username username of User to be retrieved
     * @return User details of user
     * @throws NotFoundException When the user does not exist
     */
    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        user.orElseThrow(() -> new NotFoundException("Could not find user " + username));

        return user.get();
    }

    /**
     * Return a List of all Users
     *
     * @return List of all users
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
