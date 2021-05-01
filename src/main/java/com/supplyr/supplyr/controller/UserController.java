package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.domain.User;
import com.supplyr.supplyr.exception.UnauthorizedException;
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
//    @GetMapping("/{username}")
//    @ResponseBody
//    public UserDetails getUserDetailsByUsername(@PathVariable String username) {
//        return supplyrUserDetailsService.loadUserByUsername(username);
//
//    }

    @GetMapping("/{username}")
    @ResponseBody
    public User getUseObjectByUsername(@PathVariable String username) {
        return supplyrUserDetailsService.getUserByUsername(username);

    }

    /**
     * Register a new User
     *
     * @param organisationalUnit Organisational Unit that the user is to be a member of
     * @param user               User to be registered
     * @return User that was registered
     */
    @PostMapping("/{organisationalUnit}")
    public User createUser(@PathVariable String organisationalUnit, @RequestBody User user) {

        return supplyrUserDetailsService.registerNewUser(organisationalUnit, user);



    }

    /**
     * Register a new admin User
     *
     * @param user               User to be registered
     * @return User that was registered
     */
    @PostMapping("/admin")
    public User createAdmin( @RequestBody User user) {
        return supplyrUserDetailsService.registerNewAdmin(user);

    }

    /**
     * Update the details of User with a given Id
     *
     * @param userId      Id of User to be updated
     * @param updatedUser Updated details of User
     * @return Updated User
     */
    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        return supplyrUserDetailsService.updateUser(userId, updatedUser);
    }

    /**
     * Delete a User with a given Id
     *
     * @param userId Id of User to be deleted
     */
    @DeleteMapping("/{userId}")
    public void deleteStudent(@PathVariable Long userId) {
        supplyrUserDetailsService.deleteStudent(userId);
    }
}
