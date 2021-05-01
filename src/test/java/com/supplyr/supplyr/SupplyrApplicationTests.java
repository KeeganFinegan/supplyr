package com.supplyr.supplyr;

import com.supplyr.supplyr.controller.UserController;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.User;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SupplyrApplicationTests {

    @Autowired
    UserController userController;

    @Autowired
    OrganisationalUnitRepository organisationalUnitRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }


    @Test
    void createUserTest() {
        // Given
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setName("Finance");
        organisationalUnit.setCredits(200);
        organisationalUnitRepository.save(organisationalUnit);

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("Keegan");
        expectedUser.setPassword("password");
        expectedUser.setOrganisationalUnit(organisationalUnit);
        expectedUser.setActive(true);
        expectedUser.setRoles("ROLE_USER");

        User user = new User();
        user.setUsername("Keegan");
        user.setPassword(passwordEncoder.encode("password"));

        User response = userController.createUser("Finance", user);
        System.out.println(expectedUser.toString());
        System.out.println(response.toString());
        assertEquals(expectedUser, response);


    }


}
