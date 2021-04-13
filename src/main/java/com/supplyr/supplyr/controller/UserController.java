package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.domain.User;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import com.supplyr.supplyr.repository.UserRepository;
import com.supplyr.supplyr.service.SupplyrUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    OrganisationalUnitRepository organisationalUnitRepository;

    @Autowired
    SupplyrUserDetailsService supplyrUserDetailsService;

    /**
     * Return a List of all Users
     */
    @GetMapping()
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Return a User with a given Id
     */
    @GetMapping("/{userId}")
    @ResponseBody
    public UserDetails getUserById(@PathVariable String username) {
        return supplyrUserDetailsService.loadUserByUsername(username);

    }

    /**
     * Create a new User
     */
    @PostMapping("/{organisationalUnit}")
    public User createUser(@PathVariable String organisationalUnit, @RequestBody User user) {
        return supplyrUserDetailsService.registerNewUser(organisationalUnit, user);

    }

    /**
     * Update the details of User with a given Id
     */
    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setPassword(updatedUser.getPassword());
                    user.setOrganisationalUnit(updatedUser.getOrganisationalUnit());
                    return userRepository.save(user);
                }).orElseThrow(() -> new NotFoundException("Could not find student with id " + userId));
    }

    /**
     * Delete a User with a given Id
     */
    @DeleteMapping("/{userId}")
    public String deleteStudent(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return "Deleted Successfully!";
                }).orElseThrow(() -> new NotFoundException("Student not found with id " + userId));
    }
}
