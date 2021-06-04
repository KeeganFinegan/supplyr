package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.domain.User;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.service.SupplyrUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final SupplyrUserDetailsService supplyrUserDetailsService;

    @Autowired
    public UserController(SupplyrUserDetailsService supplyrUserDetailsService) {

        this.supplyrUserDetailsService = supplyrUserDetailsService;
    }

    /**
     * Return a List of all Users
     *
     * @return List of all users
     */
    @GetMapping()
    @ResponseBody
    public List<User> getUsers() {
        return supplyrUserDetailsService.getUsers();
    }


    @GetMapping("/{username}")
    @ResponseBody
    @ExceptionHandler(UsernameNotFoundException.class)
    public User getUserObjectByUsername(@PathVariable String username) {

        return supplyrUserDetailsService.getUserByUsername(username);
    }

    /**
     * REST endpoint to register a new User
     *
     * @param organisationalUnit Organisational Unit that the user is to be a member of
     * @param user               User to be registered
     * @return User that was registered
     */
    @PostMapping("/{organisationalUnit}")
    public User createUser(@PathVariable String organisationalUnit, @RequestBody User user) {

        if (user.getPassword() != null) {
            return supplyrUserDetailsService.registerNewUser(organisationalUnit, user);
        }
        throw new BadRequestException("You must provide a password when creating a new user");


    }

    /**
     * REST endpoint to register a new admin User
     *
     * @param user User to be registered
     * @return User that was registered
     */
    @PostMapping("/admin")
    public User createAdmin(@RequestBody User user) {
        if (user.getPassword() != null) {
            return supplyrUserDetailsService.registerNewAdmin(user);
        }
        throw new BadRequestException("You must provide a password when creating a new admin");

    }

    /**
     * REST endpoint to update the details of User with a given Id
     *
     * @param updatedUser Updated details of User
     * @return Updated User
     */
    @PutMapping("/{username}")
    public User updateUserPassword(@RequestBody User updatedUser, @PathVariable String username) {
        if (updatedUser.getPassword() != null) {
            return supplyrUserDetailsService.updateUserPassword(updatedUser, username);
        }
        throw new BadRequestException("You must provide a password when creating a new admin");
    }

}
