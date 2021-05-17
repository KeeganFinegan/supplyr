package com.supplyr.supplyr.service;

import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import com.supplyr.supplyr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplyrUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrganisationalUnitRepository organisationalUnitRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TradeService tradeService;

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
     * @param updatedUser Updated details of User
     * @return Updated User
     */
    public User updateUserPassword(User updatedUser, String username) {

        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    return userRepository.save(user);
                }).orElseThrow(() -> new NotFoundException("Could not find student " + updatedUser.getUsername()));
    }

    /**
     * Delete a User with a given id from the database
     *
     * @param user User to be deleted
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
     */
    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        user.orElseThrow(() -> new NotFoundException("Could not find user " + username));

        return user.get();
    }

    /**
     * Return a List of all Users
     * @return List of all users
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
