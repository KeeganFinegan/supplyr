package com.supplyr.supplyr.organisationalUnit;

import com.supplyr.supplyr.controller.OrganisationalUnitController;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.service.OrganisationalUnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganisationalUnitControllerUnitTests {

    @Mock
    private OrganisationalUnitService organisationalUnitService;

    @Mock
    PasswordEncoder passwordEncoder;

    private OrganisationalUnitController organisationalUnitController;
    private OrganisationalUnit it;
    private OrganisationalUnit finance;

    @BeforeEach
    public void setUp() {

        organisationalUnitController = new OrganisationalUnitController(organisationalUnitService);

        it = new OrganisationalUnit();
        it.setId(1L);
        it.setCredits(250);
        it.setName("IT");

        finance = new OrganisationalUnit();
        finance.setId(2L);
        finance.setName("Finance");
        finance.setCredits(300);
    }


    @Test
    public void createOrganisationalUnit() throws Exception {


        OrganisationalUnit organisationalUnitRequest = new OrganisationalUnit();
        organisationalUnitRequest.setName("IT");
        organisationalUnitRequest.setCredits(250);

        when(organisationalUnitService.createOrganisationalUnit(any(OrganisationalUnit.class))).thenReturn(it);

        OrganisationalUnit organisationalUnit = organisationalUnitController
                .createOrganisationalUnit(organisationalUnitRequest);

        assertEquals("IT", organisationalUnit.getName());
        assertEquals(250, organisationalUnit.getCredits());
    }

    @Test
    public void create_unit_that_already_exists() throws Exception {


        OrganisationalUnit organisationalUnitRequest = new OrganisationalUnit();
        organisationalUnitRequest.setName("IT");
        organisationalUnitRequest.setCredits(250);

        when(organisationalUnitService.createOrganisationalUnit(any(OrganisationalUnit.class))).thenThrow(new AlreadyExistsException("Organisational Unit IT already exists"));


        assertThrows(AlreadyExistsException.class, () -> {
            organisationalUnitController
                    .createOrganisationalUnit(organisationalUnitRequest);
        });
    }


    @Test
    public void create_unit_with_invalid_request() throws Exception {


        OrganisationalUnit organisationalUnitRequest = new OrganisationalUnit();
        organisationalUnitRequest.setName("IT");
        organisationalUnitRequest.setCredits(250);

        when(organisationalUnitService.createOrganisationalUnit(any(OrganisationalUnit.class)))
                .thenThrow(new BadRequestException("Invalid request"));


        assertThrows(BadRequestException.class, () -> {
            organisationalUnitController
                    .createOrganisationalUnit(organisationalUnitRequest);
        });
    }

    @Test
    public void get_list_of_organisational_units() throws Exception {


        List<OrganisationalUnit> organisationalUnits = new ArrayList<>();
        organisationalUnits.add(it);
        organisationalUnits.add(finance);

        when(organisationalUnitService.getOrganisationalUnits()).thenReturn(organisationalUnits);

        List<OrganisationalUnit> organisationalUnitsList = organisationalUnitController
                .getOrganisationalUnits();

        assertEquals(organisationalUnitsList.get(0).getName(), organisationalUnits.get(0).getName());
        assertEquals(organisationalUnitsList.get(1).getName(), organisationalUnits.get(1).getName());


    }

    @Test
    public void get_organisational_units_by_name() throws Exception {


        when(organisationalUnitService.getOrganisationalUnitByName("IT")).thenReturn(it);

        OrganisationalUnit organisationalUnit = organisationalUnitController
                .getOrganisationalUnitByName("IT");

        assertEquals("IT", organisationalUnit.getName());
    }

    @Test
    public void get_non_existent_organisational_units_by_name() throws Exception {

        when(organisationalUnitService.getOrganisationalUnitByName("Management"))
                .thenThrow(new NotFoundException("Could not find Organisational Unit Management"));

        assertThrows(NotFoundException.class, () -> {
            organisationalUnitController.getOrganisationalUnitByName("Management");
        });

    }
}
