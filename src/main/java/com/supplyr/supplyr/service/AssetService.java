package com.supplyr.supplyr.service;

import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.AssetRepository;
import com.supplyr.supplyr.repository.OrganisationalUnitAssetRepository;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssetService {

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    OrganisationalUnitRepository organisationalUnitRepository;

    @Autowired
    OrganisationalUnitAssetRepository organisationalUnitAssetRepository;

    /**
     * Retrieve a list of all Assets
     *
     * @return List of all assets
     */
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    /**
     * Retrieve Asset with a given id from the database
     *
     * @param assetName Asset to be retrieved from database
     * @return Asset that was queried
     * @throws NotFoundException When asset does not exists
     */
    public Asset getAssetByName(String assetName) {
        Optional<Asset> optionalAsset = assetRepository.findByName(assetName);
        if (optionalAsset.isPresent()) {
            return optionalAsset.get();
        } else {
            throw new NotFoundException("Could not find asset " + assetName);
        }

    }

    /**
     * Add new Asset type to the database
     *
     * @param asset Asset to be added into the database
     * @return Asset that was added to the database
     */
    public Asset addAssetType(Asset asset) {


        Optional<Asset> optAsset = assetRepository.findByName(asset.getName());

        if (optAsset.isPresent()) {
            throw new AlreadyExistsException("Asset " + asset.getName() + " already exists");

        } else {
            return assetRepository.save(asset);
        }


    }

    /**
     * Update the quantity of an Asset held by an Organisational Unit in the database
     *
     * @param assetObject Details of asset to be updated
     */
    public void updateOrganisationalUnitAsset(OrganisationalUnitAssetDto assetObject) {
        Optional<Asset> optionalAsset = getAssetFromDatabase(assetObject);
        Optional<OrganisationalUnit> optionalOrganisationalUnit = getOrganisationalUnitFromDatabase(assetObject);

        if (optionalAsset.isPresent() && optionalOrganisationalUnit.isPresent()) {

            Optional<OrganisationalUnitAsset> optionalOrganisationalUnitAsset = organisationalUnitAssetRepository
                    .findByOrganisationalUnitAndAsset(optionalOrganisationalUnit.get(), optionalAsset.get());

            if (optionalOrganisationalUnitAsset.isPresent()) {


            } else {
                addOrganisationalUnitAsset(assetObject);

            }

            if (optionalOrganisationalUnitAsset.isPresent()) {
                double oldQuantity = optionalOrganisationalUnitAsset.get().getQuantity();
                double newQuantity = oldQuantity + assetObject.getQuantity();
                OrganisationalUnitAsset organisationalUnitAsset = optionalOrganisationalUnitAsset.get();

                organisationalUnitAsset.setQuantity(newQuantity);
                organisationalUnitAssetRepository.save(organisationalUnitAsset);

            }


        } else {
            throw new NotFoundException("No such asset or Organisational Unit exists");
        }

    }

    /**
     * Add a new instance of an Asset held by an Organisational Unit
     *
     * @param assetObject Details of asset to be added
     */
    public OrganisationalUnitAsset addOrganisationalUnitAsset(OrganisationalUnitAssetDto assetObject) {

        Optional<Asset> optionalAsset = getAssetFromDatabase(assetObject);
        Optional<OrganisationalUnit> optionalOrganisationalUnit = getOrganisationalUnitFromDatabase(assetObject);

        if (optionalAsset.isPresent() && optionalOrganisationalUnit.isPresent()) {
            OrganisationalUnitAssetId organisationalUnitAssetId = new OrganisationalUnitAssetId(
                    optionalOrganisationalUnit.get().getId(), optionalAsset.get().getAssetId()
            );
            OrganisationalUnitAsset organisationalUnitAsset = new OrganisationalUnitAsset(
                    organisationalUnitAssetId, optionalOrganisationalUnit.get(), optionalAsset.get(),
                    assetObject.getQuantity()
            );
            return organisationalUnitAssetRepository.save(organisationalUnitAsset);
        }
        throw new NotFoundException("No such asset or Organisational Unit exists");

    }


    private Optional<Asset> getAssetFromDatabase(OrganisationalUnitAssetDto assetObject) {
        Optional<Asset> optionalAsset;

        // If ID exists, retrieve by ID
        if (assetObject.getAssetId() != null) {
            optionalAsset = assetRepository.findById(assetObject.getAssetId());
            // If ID is null, retrieve by name
        } else {
            optionalAsset = assetRepository.findByName(assetObject.getAssetName());

        }

        return optionalAsset;

    }

    private Optional<OrganisationalUnit> getOrganisationalUnitFromDatabase(OrganisationalUnitAssetDto assetObject) {
        Optional<OrganisationalUnit> optionalOrganisationalUnit;
        // If ID exists, retrieve by ID
        if (assetObject.getOrganisationalUnitId() != null) {
            optionalOrganisationalUnit = organisationalUnitRepository
                    .findById(assetObject.getOrganisationalUnitId());
            // If ID is null, retrieve by name
        } else {
            optionalOrganisationalUnit = organisationalUnitRepository
                    .findByUnitName(assetObject.getOrganisationalUnitName());

        }

        return optionalOrganisationalUnit;

    }


}
