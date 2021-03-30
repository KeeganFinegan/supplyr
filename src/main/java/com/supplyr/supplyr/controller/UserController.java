package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.model.OrganisationalUnit;
import com.supplyr.supplyr.model.User;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import com.supplyr.supplyr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    OrganisationalUnitRepository organisationalUnitRepository;

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
    public User getUserById(@PathVariable Long userId) {
        Optional<User> optionalUser= userRepository.findById(userId);

        if(optionalUser.isPresent()) {
            return optionalUser.get();

        }else {
            throw new NotFoundException("Could not find user with id " + userId);
        }

    }

    /**
     * Create a new User
     */
    @PostMapping("/{organisationalUnit}")
    public User createUser(@PathVariable String organisationalUnit, @RequestBody User user){
        Optional<OrganisationalUnit> optUnit = organisationalUnitRepository.findByUnitName(organisationalUnit);
        Optional<User> optUser = userRepository.findByUsername(user.getUsername());
        if(optUnit.isPresent() && optUser.isEmpty()){

            user.setOrganisationalUnit(optUnit.get());
            return userRepository.save(user);

        } else if(optUnit.isPresent()){
            throw new AlreadyExistsException("User with username '" + user.getUsername() + "' already exists");

        }else{
            throw new NotFoundException("Could not find Organisational Unit " + organisationalUnit);
        }
    }

    /**
     * Update the details of User with a given Id
     */
    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User updatedUser){
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
    public String deleteStudent(@PathVariable Long userId){
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return "Deleted Successfully!";
                }).orElseThrow(() -> new NotFoundException("Student not found with id " + userId));
    }
}
