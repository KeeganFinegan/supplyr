package com.supplyr.supplyr.user;

import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.User;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import com.supplyr.supplyr.repository.UserRepository;
import com.supplyr.supplyr.service.SupplyrUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTests {
    @InjectMocks
    SupplyrUserDetailsService supplyrUserDetailsService;

    @Mock
    OrganisationalUnitRepository organisationalUnitRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void successfullyNewUserToTheDatabase() {

        User user = new User("Bob", "password");
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setName("IT");
        organisationalUnit.setCredits(250);
        organisationalUnit.setOrganisationalUnitAssets(null);
        organisationalUnit.setOffers(null);
        organisationalUnit.setId(1L);

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("Bob");
        expectedUser.setOrganisationalUnit(organisationalUnit);
        expectedUser.setActive(true);
        expectedUser.setRoles("ROLE_USER");

        Optional<OrganisationalUnit> optionalOrganisationalUnit = Optional.of(organisationalUnit);
        Optional<User> optionalUser = Optional.empty();

        when(passwordEncoder.encode(any())).thenReturn("wkfdjbawfwr8234r8!@@#IQP3E3ERN");
        when(organisationalUnitRepository.findByUnitName(any())).thenReturn(optionalOrganisationalUnit);
        when(userRepository.findByUsername(any())).thenReturn(optionalUser);
        when(userRepository.save(any())).thenReturn(expectedUser);

        assertEquals(expectedUser, supplyrUserDetailsService.registerNewUser("IT", user));

    }

    @Test
    public void successfullyNewAdminToTheDatabase() {

        User user = new User("Bob", "password");
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setName("IT");
        organisationalUnit.setCredits(250);
        organisationalUnit.setOrganisationalUnitAssets(null);
        organisationalUnit.setOffers(null);
        organisationalUnit.setId(1L);

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("Bob");
        expectedUser.setOrganisationalUnit(organisationalUnit);
        expectedUser.setActive(true);
        expectedUser.setRoles("ROLE_ADMIN");

        Optional<OrganisationalUnit> optionalOrganisationalUnit = Optional.of(organisationalUnit);
        Optional<User> optionalUser = Optional.empty();

        when(passwordEncoder.encode(any())).thenReturn("wkfdjbawfwr8234r8!@@#IQP3E3ERN");
        when(organisationalUnitRepository.findByUnitName(any())).thenReturn(optionalOrganisationalUnit);
        when(userRepository.findByUsername(any())).thenReturn(optionalUser);
        when(userRepository.save(any())).thenReturn(expectedUser);

        assertEquals(expectedUser, supplyrUserDetailsService.registerNewAdmin(user));

    }

    @Test
    public void addExistingUserToTheDatabase() {

        User user = new User("Bob", "password");
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setName("IT");
        organisationalUnit.setCredits(250);
        organisationalUnit.setOrganisationalUnitAssets(null);
        organisationalUnit.setOffers(null);
        organisationalUnit.setId(1L);

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("Bob");
        expectedUser.setOrganisationalUnit(organisationalUnit);
        expectedUser.setActive(true);
        expectedUser.setRoles("ROLE_USER");

        Optional<OrganisationalUnit> optionalOrganisationalUnit = Optional.of(organisationalUnit);
        Optional<User> optionalUser = Optional.of(expectedUser);

        when(organisationalUnitRepository.findByUnitName(any())).thenReturn(optionalOrganisationalUnit);
        when(userRepository.findByUsername(any())).thenReturn(optionalUser);

        assertThrows(AlreadyExistsException.class, () -> {
            supplyrUserDetailsService.registerNewUser("IT", user);
        });

    }

    @Test
    public void addUserToNonExistentUnit() {

        User user = new User("Bob", "password");
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setName("IT");
        organisationalUnit.setCredits(250);
        organisationalUnit.setOrganisationalUnitAssets(null);
        organisationalUnit.setOffers(null);
        organisationalUnit.setId(1L);

        Optional<OrganisationalUnit> optionalOrganisationalUnit = Optional.empty();

        when(passwordEncoder.encode(any())).thenReturn("wkfdjbawfwr8234r8!@@#IQP3E3ERN");
        when(organisationalUnitRepository.findByUnitName(any())).thenReturn(optionalOrganisationalUnit);

        assertThrows(NotFoundException.class, () -> {
            supplyrUserDetailsService.registerNewUser("IT", user);
        });

    }

    @Test
    public void updateUserPassword() {

        User user = new User("Bob", "newPassword");
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setName("IT");
        organisationalUnit.setCredits(250);
        organisationalUnit.setOrganisationalUnitAssets(null);
        organisationalUnit.setOffers(null);
        organisationalUnit.setId(1L);

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("John");
        expectedUser.setPassword("wkfdjbawfwr8234r8!@@#IQP3E3ERN");
        expectedUser.setOrganisationalUnit(organisationalUnit);
        expectedUser.setActive(true);
        expectedUser.setRoles("ROLE_USER");


        Optional<User> optionalUser = Optional.of(user);

        when(passwordEncoder.encode(any())).thenReturn("wkfdjbawfwr8234r8!@@#IQP3E3ERN");
        when(userRepository.findByUsername(any())).thenReturn(optionalUser);
        when(userRepository.save(any())).thenReturn(expectedUser);

        assertEquals(expectedUser, supplyrUserDetailsService.updateUserPassword(user, "Bob"));


    }

    @Test
    public void updateNonExistentUser() {

        User user = new User("Bob", "newPassword");
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setName("IT");
        organisationalUnit.setCredits(250);
        organisationalUnit.setOrganisationalUnitAssets(null);
        organisationalUnit.setOffers(null);
        organisationalUnit.setId(1L);

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("Bob");
        expectedUser.setPassword("wkfdjbawfwr8234r8!@@#IQP3E3ERN");
        expectedUser.setOrganisationalUnit(organisationalUnit);
        expectedUser.setActive(true);
        expectedUser.setRoles("ROLE_USER");

        Optional<User> optionalUser = Optional.empty();

        when(passwordEncoder.encode(any())).thenReturn("wkfdjbawfwr8234r8!@@#IQP3E3ERN");
        when(userRepository.findByUsername(any())).thenReturn(optionalUser);

        assertThrows(NotFoundException.class, () -> {
            supplyrUserDetailsService.updateUserPassword(user, "Bob");
        });

    }
}
