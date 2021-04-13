package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.AssetRepository;
import com.supplyr.supplyr.repository.OrganisationalUnitAssetRepository;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/organisational-unit")
public class OrganisationalUnitController {

    @Autowired
    OrganisationalUnitRepository organisationalUnitRepository;

    @Autowired
    OrganisationalUnitAssetRepository organisationalUnitAssetRepository;

    @Autowired
    AssetRepository assetRepository;

    /**
     * Return a list of all Organisational Units
     */
    @GetMapping
    public List<OrganisationalUnit> getOrganisationalUnits() {
        return organisationalUnitRepository.findAll();
    }

    /**
     * Return an Organisational Unit with a given Id
     */
    @GetMapping("/{organisationalUnitId}")
    public OrganisationalUnit getOrganisationalUnitById(@PathVariable Long organisationalUnitId) {
        Optional<OrganisationalUnit> optionalOrganisationalUnit = organisationalUnitRepository
                .findById(organisationalUnitId);

        if (optionalOrganisationalUnit.isPresent()) {
            return optionalOrganisationalUnit.get();
        } else {
            throw new NotFoundException("Could not find Organisational Unit with id " + organisationalUnitId);
        }
    }

    /**
     * Create a new Organisational Unit
     */
    @PostMapping()
    public OrganisationalUnit createOrganisationalUnit(@RequestBody OrganisationalUnit organisationalUnit) {
        Optional<OrganisationalUnit> optUnit = organisationalUnitRepository
                .findByUnitName(organisationalUnit.getName());

        if (optUnit.isPresent()) {
            throw new AlreadyExistsException("Organisational Unit " + organisationalUnit.getName()
                    + " already exists");
        } else {
            return organisationalUnitRepository.save(organisationalUnit);
        }
    }
}
