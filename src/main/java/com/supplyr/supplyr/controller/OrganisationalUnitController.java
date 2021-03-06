package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.service.OrganisationalUnitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/organisational-unit")
public class OrganisationalUnitController {


    private final OrganisationalUnitService organisationalUnitService;

    public OrganisationalUnitController(OrganisationalUnitService organisationalUnitService) {
        this.organisationalUnitService = organisationalUnitService;
    }


    /**
     * REST endpoint to retrieve a list of all Organisational Units
     *
     * @return List of all Organisational Units
     */
    @GetMapping
    public List<OrganisationalUnit> getOrganisationalUnits() {
        return organisationalUnitService.getOrganisationalUnits();
    }

    /**
     * REST endpoint to retrieve an Organisational Unit with a given Id
     *
     * @param organisationalUnitName Organisational Unit to be returned
     * @return Organisational Unit that was queried
     */
    @GetMapping("/{organisationalUnitName}")
    public OrganisationalUnit getOrganisationalUnitByName(@PathVariable String organisationalUnitName) {
        return organisationalUnitService.getOrganisationalUnitByName(organisationalUnitName);
    }

    /**
     * REST endpoint to create a new Organisational Unit
     *
     * @param organisationalUnit Organisational Unit to be added
     * @return Organisational unit that was added
     */
    @PostMapping()
    public OrganisationalUnit createOrganisationalUnit(@RequestBody OrganisationalUnit organisationalUnit) {
        return organisationalUnitService.createOrganisationalUnit(organisationalUnit);
    }

}
