package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.service.OrganisationalUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/organisational-unit")
public class OrganisationalUnitController {

    @Autowired
    OrganisationalUnitService organisationalUnitService;

    /**
     * Return a list of all Organisational Units
     *
     * @return List of all Organisational Units
     */
    @GetMapping
    public List<OrganisationalUnit> getOrganisationalUnits() {
        return organisationalUnitService.getOrganisationalUnits();
    }

    /**
     * Return an Organisational Unit with a given Id
     *
     * @param organisationalUnitId Id of Organisational Unit to be returned
     * @return Organisational Unit that was queried
     */
    @GetMapping("/{organisationalUnitId}")
    public OrganisationalUnit getOrganisationalUnitById(@PathVariable Long organisationalUnitId) {
        return organisationalUnitService.getOrganisationalUnitById(organisationalUnitId);
    }

    /**
     * Create a new Organisational Unit
     *
     * @param organisationalUnit Organisational Unit to be added
     * @return Organisational unit that was added
     */
    @PostMapping()
    public OrganisationalUnit createOrganisationalUnit(@RequestBody OrganisationalUnit organisationalUnit) {
        return organisationalUnitService.createOrganisationalUnit(organisationalUnit);
    }

    /**
     * Delete existing Organisational Unit
     *
     * @param organisationalUnitId Id of Organisational Unit to be deleted
     */
    @DeleteMapping
    public void deleteOrganisationalUnit(@PathVariable Long organisationalUnitId) {
        organisationalUnitService.deleteById(organisationalUnitId);
    }


}
