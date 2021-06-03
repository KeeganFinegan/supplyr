package com.supplyr.supplyr.service;

import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class OrganisationalUnitService {

    private final OrganisationalUnitRepository organisationalUnitRepository;

    @Autowired
    public OrganisationalUnitService(OrganisationalUnitRepository organisationalUnitRepository) {
        this.organisationalUnitRepository = organisationalUnitRepository;
    }

    /**
     * Retrieve all Organisational Units from database
     *
     * @return List of all Organisational Units
     */
    public List<OrganisationalUnit> getOrganisationalUnits() {
        Optional<OrganisationalUnit> adminUnit = organisationalUnitRepository.findByUnitName("Supplyr Admin");
        if (adminUnit.isPresent()) {
            List<OrganisationalUnit> units = organisationalUnitRepository.findAll();
            units.remove(adminUnit.get());
            return units;
        }
        throw new BadRequestException("An Error has occurred");

    }

    /**
     * Retrieve a given Organisational Units from database
     *
     * @param organisationalUnitName Name of Organisational Unit to be retrieved
     * @return Queried Organisational Unit
     */
    public OrganisationalUnit getOrganisationalUnitByName(String organisationalUnitName) {

            Optional<OrganisationalUnit> optionalOrganisationalUnit = organisationalUnitRepository
                    .findByUnitName(organisationalUnitName);

            if (optionalOrganisationalUnit.isPresent()) {
                return optionalOrganisationalUnit.get();
            } else {
                throw new NotFoundException("Could not find Organisational Unit " + organisationalUnitName);
            }

    }

    /**
     * Insert new Organisational Unit into database
     *
     * @param organisationalUnit Organisational Unit to be inserted
     * @return Organisational Unit that was inserted
     */
    public OrganisationalUnit createOrganisationalUnit(OrganisationalUnit organisationalUnit) {

        Optional<OrganisationalUnit> optUnit = organisationalUnitRepository
                .findByUnitName(organisationalUnit.getName());

        if (optUnit.isPresent()) {
            throw new AlreadyExistsException("Organisational Unit " + organisationalUnit.getName()
                    + " already exists");
        } else {

            return organisationalUnitRepository.save(organisationalUnit);
        }

    }


    public void updateOrganisationalUnitCredits(Long organisationalUnitId, double creditAmount) {
        organisationalUnitRepository
                .findById(organisationalUnitId).map(organisationalUnit -> {
                    if ((organisationalUnit.getCredits() + creditAmount) < 0){
                        throw new BadRequestException("A unit cannot have negative credits");
                    }
            organisationalUnit.setCredits(organisationalUnit.getCredits() + creditAmount);
            return organisationalUnit;
        }).orElseThrow(() -> new NotFoundException(String.format("Could not find Organisational Unit with id %d "
                , organisationalUnitId)));

    }

    /**
     * Delete Organisational Unit from database
     *
     * @param organisationalUnitId Id of Organisational Unit to be deleted
     */
    public void deleteById(Long organisationalUnitId) {

        if (organisationalUnitRepository.existsById(organisationalUnitId)) {
            organisationalUnitRepository.deleteById(organisationalUnitId);
        } else {
            throw new NotFoundException(String.format("Could not find Organisational Unit with id %d ",
                    organisationalUnitId));
        }
    }


}
