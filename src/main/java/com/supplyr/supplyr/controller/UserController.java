package com.supplyr.supplyr.controller;


import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.model.OrganisationalUnit;
import com.supplyr.supplyr.model.User;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import com.supplyr.supplyr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping()
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public User getUserById(@PathVariable Long userId) {
        Optional<User> optionalUser= userRepository.findById(userId);

        if(optionalUser.isPresent()) {
            return optionalUser.get();
        }else {
            throw new NotFoundException("User with id " + userId + " does not exist");
        }

    }

    @PostMapping("/{organisationalUnit}")
    public ResponseEntity<String> createUser(@PathVariable String organisationalUnit, @RequestBody User user){
            Optional<OrganisationalUnit> optUnit = organisationalUnitRepository.findByUnitName(organisationalUnit);
            Optional<User> optUser = userRepository.findByUsername(user.getUsername());
            if(optUnit.isPresent() && optUser.isEmpty()){
                user.setOrganisationalUnit(optUnit.get());
                userRepository.save(user);
                return new ResponseEntity<>(
                        "New user created!",
                        HttpStatus.OK
                );
            } else if(optUnit.isPresent()){
                return new ResponseEntity<>(
                        "Username " + user.getUsername() + " already exists!",
                        HttpStatus.BAD_REQUEST
                );
            }else{
                return new ResponseEntity<>(
                        "Organisational Unit '" + organisationalUnit + "' does not exist!",
                        HttpStatus.BAD_REQUEST
                );
            }
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User updatedUser){
        return userRepository.findById(userId)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setPassword(updatedUser.getPassword());
                    user.setOrganisationalUnit(updatedUser.getOrganisationalUnit());
                    return userRepository.save(user);
                }).orElseThrow(() -> new NotFoundException("Student not found with id " + userId));

    }

    @DeleteMapping("/{userId}")
    public String deleteStudent(@PathVariable Long userId){
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return "Deleted Successfully!";
                }).orElseThrow(() -> new NotFoundException("Student not found with id " + userId));
    }






}
