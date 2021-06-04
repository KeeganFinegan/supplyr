package com.supplyr.supplyr.service;

import com.supplyr.supplyr.domain.Asset;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.OrganisationalUnitAsset;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.OrganisationalUnitAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganisationalUnitAssetService {

    private final OrganisationalUnitAssetRepository organisationalUnitAssetRepository;

    @Autowired
    public OrganisationalUnitAssetService(OrganisationalUnitAssetRepository organisationalUnitAssetRepository) {
        this.organisationalUnitAssetRepository = organisationalUnitAssetRepository;
    }


    /**
     * Get an OrganisationalUnitAsset object from the database
     *
     * @param organisationalUnit Organisational Unit to be retrieved
     * @param asset              Asset to be retrieved
     * @return OrganisationalUnitAsset
     * @throws NotFoundException when the OrganisationalUnitAsset does not exist
     */
    public OrganisationalUnitAsset getOrganisationalUnitAsset(OrganisationalUnit organisationalUnit, Asset asset) {
        Optional<OrganisationalUnitAsset> optionalOrganisationalUnitAsset = organisationalUnitAssetRepository
                .findByOrganisationalUnitAndAsset(organisationalUnit, asset);

        if (optionalOrganisationalUnitAsset.isPresent()) {
            return optionalOrganisationalUnitAsset.get();
        } else {
            throw new NotFoundException(String.format("Organisational Unit %s does not possess %s",
                    organisationalUnit.getName(), asset.getName()));
        }


    }
}
