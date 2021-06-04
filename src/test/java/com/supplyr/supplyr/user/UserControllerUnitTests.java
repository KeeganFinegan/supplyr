package com.supplyr.supplyr.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyr.supplyr.controller.UserController;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.User;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.security.ApplicationUserRole;
import com.supplyr.supplyr.service.SupplyrUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTests {

    private UserController userController;

    @Mock
    private SupplyrUserDetailsService supplyrUserDetailsService;

    private OrganisationalUnit it;
    private OrganisationalUnit finance;
    private User bob;
    private User james;
    private User[] users;
    private List<User> userList;

    @BeforeEach
    public void setUp() {

        userController = new UserController(supplyrUserDetailsService);

        it = new OrganisationalUnit();
        it.setId(1L);
        it.setCredits(250);
        it.setName("IT");

        finance = new OrganisationalUnit();
        finance.setId(2L);
        finance.setName("Finance");
        finance.setCredits(300);

        bob = new User();
        bob.setId(1L);
        bob.setUsername("Bob");
        bob.setRoles("ROLE_USER");
        bob.setOrganisationalUnit(it);

        james = new User();
        james.setId(2L);
        james.setUsername("James");
        james.setRoles("ROLE_USER");
        james.setOrganisationalUnit(finance);

        users = new User[]{bob, james};

        userList = Arrays.asList(users);

    }

    @Test
    public void getUserByUserName() throws Exception {

        when(supplyrUserDetailsService.getUserByUsername("Bob")).thenReturn(bob);

        User returnedUser = userController.getUserObjectByUsername("Bob");
        assertEquals("Bob", returnedUser.getUsername());
    }

    @Test
    public void getAllUsers() throws Exception {


        when(supplyrUserDetailsService.getUsers()).thenReturn(userList);

        List<User> returnedUserList = userController.getUsers();

        assertEquals("Bob", returnedUserList.get(0).getUsername());
        assertEquals("James", returnedUserList.get(1).getUsername());
    }

    @Test()
    public void getNonExistentUser() throws Exception {

        when(supplyrUserDetailsService.getUserByUsername("Donald")).thenThrow(new NotFoundException("Could not find user " + "Bob"));


        assertThrows(NotFoundException.class, () -> {
            userController.getUserObjectByUsername("Donald");
        });
    }

    @Test
    public void addNewUser() throws Exception {

        User userRequest = new User();
        userRequest.setUsername("Keegan");
        userRequest.setPassword("password");

        User userRegistered = new User();
        userRegistered.setUsername("Keegan");
        userRegistered.setPassword("password");
        userRegistered.setActive(true);
        userRegistered.setOrganisationalUnit(it);
        userRegistered.setRoles("ROLE_USER");

        when(supplyrUserDetailsService
                .registerNewUser("IT",userRequest))
                .thenReturn(userRegistered);


        User addedUser = userController.createUser("IT",userRequest);
        assertEquals("Keegan", addedUser.getUsername());
        assertEquals("ROLE_USER", addedUser.getRoles());


    }

    @Test
    public void addNewUserWithoutPassword() throws Exception {

        User userRequest = new User();
        userRequest.setUsername("Keegan");


        assertThrows(BadRequestException.class, () -> {
            userController.createUser("IT",userRequest);
        });


    }




    @Test
    public void addNewAdmin()  {
        User userRequest = new User();
        userRequest.setUsername("Keegan");
        userRequest.setPassword("password");

        User userRegistered = new User();
        userRegistered.setUsername("Keegan");
        userRegistered.setPassword("password");
        userRegistered.setActive(true);
        userRegistered.setOrganisationalUnit(it);
        userRegistered.setRoles("ROLE_ADMIN");


        when(supplyrUserDetailsService
                .registerNewAdmin(userRequest))
                .thenReturn(userRegistered);

        User addedUser = userController.createAdmin(userRequest);
        assertEquals("Keegan", addedUser.getUsername());
        assertEquals("ROLE_ADMIN", addedUser.getRoles());

    }

    @Test
    public void addNewAdminWithoutPassword() {

        User userRequest = new User();
        userRequest.setUsername("Keegan");


        assertThrows(BadRequestException.class, () -> {
            userController.createAdmin(userRequest);
        });


    }

    @Test
    public void updateUserPassword() {

        User updatedUser = new User();
        updatedUser.setPassword("newPassword");

        bob.setPassword("newPassword");

        when(supplyrUserDetailsService.updateUserPassword(any(User.class), eq("Bob"))).thenReturn(bob);

        assertDoesNotThrow(() -> {
            userController.updateUserPassword(updatedUser,"Bob");
        });





    }

    @Test
    public void updateUserPasswordWithoutPassword() {

        User updatedUser = new User();
        updatedUser.setUsername("Bob");


        assertThrows(BadRequestException.class, () -> {
            userController.updateUserPassword(updatedUser,"Bob");
        });

    }


}
