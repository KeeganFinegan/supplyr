package com.supplyr.supplyr.organisationalUnit;

import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import com.supplyr.supplyr.service.OrganisationalUnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


public class OrganisationalUnitServiceTests {

    @InjectMocks
    OrganisationalUnitService organisationalUnitService;

    @Mock
    OrganisationalUnitRepository organisationalUnitRepository;

    private OrganisationalUnit it;
    private OrganisationalUnit finance;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        it = new OrganisationalUnit();
        it.setId(1L);
        it.setCredits(10);
        it.setName("IT");

        finance = new OrganisationalUnit();
        finance.setId(2L);
        finance.setCredits(50);
        finance.setName("Finance");

    }

    @Test
    public void get_organisational_units() {
        OrganisationalUnit admin = new OrganisationalUnit();
        admin.setName("Supplyr Admin");
        List<OrganisationalUnit> organisationalUnits = new ArrayList<>();
        organisationalUnits.add(admin);
        organisationalUnits.add(it);
        organisationalUnits.add(finance);
        when(organisationalUnitRepository.findByUnitName("Supplyr Admin")).thenReturn(Optional.of(admin));
        when(organisationalUnitRepository.findAll()).thenReturn(organisationalUnits);
        List<OrganisationalUnit> units = organisationalUnitService.getOrganisationalUnits();

        assertEquals(units.get(0).getName(), "IT");


    }


    @Test
    public void get_organisational_unit_by_name_success() {

        when(organisationalUnitRepository.findByUnitName("IT")).thenReturn(Optional.of(it));
        OrganisationalUnit organisationalUnit = organisationalUnitService.getOrganisationalUnitByName("IT");
        assertEquals(organisationalUnit.getName(), "IT");


    }

    @Test
    public void get_organisational_unit_by_name_non_existent() {

        when(organisationalUnitRepository.findByUnitName("NON EXISTENT")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            organisationalUnitService.getOrganisationalUnitByName("NON EXISTENT");

        }, "Could not find Organisational Unit NON EXISTENT");

    }

    @Test
    public void create_organisational_unit_success() {

        OrganisationalUnit request = new OrganisationalUnit();
        request.setName("IT");
        request.setCredits(10);

        when(organisationalUnitRepository.findByUnitName(request.getName())).thenReturn(Optional.empty());
        when(organisationalUnitRepository.save(request)).thenReturn(it);
        OrganisationalUnit organisationalUnit = organisationalUnitService.createOrganisationalUnit(request);

        assertEquals(organisationalUnit.getName(), "IT");
        assertEquals(organisationalUnit.getCredits(), 10);


    }

    @Test
    public void create_organisational_unit_already_exists() {

        OrganisationalUnit request = new OrganisationalUnit();
        request.setName("IT");
        request.setCredits(10);

        when(organisationalUnitRepository.findByUnitName(request.getName())).thenReturn(Optional.of(it));

        assertThrows(AlreadyExistsException.class, () -> {
            organisationalUnitService.createOrganisationalUnit(request);
        }, "Organisational Unit IT already exists");


    }

    @Test
    public void create_organisational_unit_negative_credits() {

        OrganisationalUnit request = new OrganisationalUnit();
        request.setName("IT");
        request.setCredits(-10);

        when(organisationalUnitRepository.findByUnitName(request.getName())).thenReturn(Optional.of(it));

        assertThrows(BadRequestException.class, () -> {
            organisationalUnitService.createOrganisationalUnit(request);
        }, "A unit cannot have negative credits");


    }

    @Test
    public void create_organisational_unit_credits_success() {

        OrganisationalUnit request = new OrganisationalUnit();
        request.setName("IT");
        request.setCredits(10);

        when(organisationalUnitRepository.findById(1L)).thenReturn(Optional.of(it));

        assertDoesNotThrow(() -> {
            organisationalUnitService.updateOrganisationalUnitCredits(1L, 20);
        });
    }

    @Test
    public void create_organisational_unit_credits_negative_credits() {

        OrganisationalUnit request = new OrganisationalUnit();
        request.setName("IT");
        request.setCredits(10);

        when(organisationalUnitRepository.findById(1L)).thenReturn(Optional.of(it));

        assertThrows(BadRequestException.class, () -> {
            organisationalUnitService.updateOrganisationalUnitCredits(1L, -100);
        }, "A unit cannot have negative credits");

    }


}
