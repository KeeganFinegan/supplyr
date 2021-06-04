package com.supplyr.supplyr.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyr.supplyr.controller.UserController;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.User;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.service.SupplyrUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplyrUserDetailsService supplyrUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrganisationalUnit it;
    private OrganisationalUnit finance;
    private User bob;
    private User james;
    private User[] users;
    private List<User> userList;

    @BeforeEach
    public void setUp() {

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

        MvcResult result = mockMvc.perform(get("/api/v1/users/Bob"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        verify(supplyrUserDetailsService).getUserByUsername("Bob");
        String responseAsString = result.getResponse().getContentAsString();

        String expectedJsonResponse = "{\"id\":1,\"username\":\"Bob\",\"organisationalUnit\":{\"id\":1,\"name" +
                "\":\"IT\",\"credits\":250.0,\"organisationalUnitAssets\":null,\"offers\":null},\"active\"" +
                ":false,\"roles\":\"ROLE_USER\"}";

        assertEquals(expectedJsonResponse, responseAsString);
    }

    @Test
    public void getAllUsers() throws Exception {


        when(supplyrUserDetailsService.getUsers()).thenReturn(userList);

        MvcResult result = mockMvc.perform(get("/api/v1/users/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        verify(supplyrUserDetailsService).getUsers();

        String responseAsString = result.getResponse().getContentAsString();

        String expectedJsonResponse = "[{\"id\":1,\"username\":\"Bob\",\"organisationalUnit\":{\"id\":1,\"name\":\"I" +
                "T\",\"credits\":250.0,\"organisationalUnitAssets\":null,\"offers\":null},\"active\":false,\"roles" +
                "\":\"ROLE_USER\"},{\"id\":2,\"username\":\"James\",\"organisationalUnit\":{\"id\":2,\"name\":\"" +
                "Finance\",\"credits\":300.0,\"organisationalUnitAssets\":null,\"offers\":null},\"active\":false,\"" +
                "roles\":\"ROLE_USER\"}]";

        assertEquals(expectedJsonResponse, responseAsString);
    }

    @Test()
    public void getNonExistentUser() throws Exception {

        when(supplyrUserDetailsService.getUserByUsername("Bob")).thenThrow(new NotFoundException("Could not find user " + "Bob"));

        MvcResult result = mockMvc.perform(get("/api/v1/users/Bob"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        verify(supplyrUserDetailsService).getUserByUsername("Bob");

        String responseAsString = result.getResponse().getContentAsString();

        String expectedResponse = "{\"status\":\"NOT_FOUND\",\"error\":\"NotFoundException\",\"message\":\"Could not find user Bob\"}";

        assertEquals(responseAsString, expectedResponse);
    }

    @Test
    public void addNewUser() throws Exception {

        User userRequest = new User();
        userRequest.setUsername("Bob");
        userRequest.setPassword("password");

        User userRegistered = new User();
        userRegistered.setUsername("Bob");
        userRegistered.setPassword("password");
        userRegistered.setActive(true);
        userRegistered.setOrganisationalUnit(it);
        userRegistered.setRoles("ROLE_USER");

        when(supplyrUserDetailsService.registerNewUser(eq("IT"), any(User.class))).thenReturn(userRegistered);

        MvcResult result = mockMvc.perform(post("/api/v1/users/IT")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"username\": \"Bob\",\n" +
                        "    \"password\": \"p\"\n" +
                        "}")
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();

        User objectResponse = objectMapper.readValue(responseAsString, User.class);


        assertEquals("Bob", objectResponse.getUsername());
        assertEquals("IT", objectResponse.getOrganisationalUnit().getName());
        assertEquals("ROLE_USER", objectResponse.getRoles());
    }


    @Test
    public void addNewAdmin() throws Exception {

        User userRequest = new User();
        userRequest.setUsername("Bob");
        userRequest.setPassword("password");

        User userRegistered = new User();
        userRegistered.setUsername("Bob");
        userRegistered.setPassword("password");
        userRegistered.setActive(true);
        userRegistered.setOrganisationalUnit(it);
        userRegistered.setRoles("ROLE_ADMIN");

        when(supplyrUserDetailsService.registerNewAdmin(any(User.class))).thenReturn(userRegistered);

        MvcResult result = mockMvc.perform(post("/api/v1/users/admin")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"username\": \"Bob\",\n" +
                        "    \"password\": \"p\"\n" +
                        "}")
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();

        User objectResponse = objectMapper.readValue(responseAsString, User.class);

        assertEquals("Bob", objectResponse.getUsername());
        assertEquals("IT", objectResponse.getOrganisationalUnit().getName());
        assertEquals("ROLE_ADMIN", objectResponse.getRoles());
    }

    @Test
    public void updateUserPassword() throws Exception {

        User updatedUser = new User();
        updatedUser.setPassword("newPassword");

        bob.setPassword("newPassword");

        when(supplyrUserDetailsService.updateUserPassword(any(User.class), eq("Bob"))).thenReturn(bob);


        MvcResult result = mockMvc.perform(put("/api/v1/users/Bob")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"password\": \"newPassword\"\n" +
                        "}")

        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        String responseAsString = result.getResponse().getContentAsString();

        String expectedJsonResponse = "{\"id\":1,\"username\":\"Bob\",\"organisationalUnit\":{\"id\":1,\"name" +
                "\":\"IT\",\"credits\":250.0,\"organisationalUnitAssets\":null,\"offers\":null},\"active\"" +
                ":false,\"roles\":\"ROLE_USER\"}";

        assertEquals(expectedJsonResponse, responseAsString);
    }


}
