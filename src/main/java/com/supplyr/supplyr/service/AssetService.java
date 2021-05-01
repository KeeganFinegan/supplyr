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

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    /**
     * Retrieve Asset with a given id from the database
     *
     * @param assetId Id of Asset to be retrieved from database
     * @return Asset that was queried
     * @throws NotFoundException When asset does not exists
     */
    public Asset getAssetById(Long assetId) {
        if (assetRepository.existsById(assetId)) {
            return assetRepository.findById(assetId).get();
        } else {
            throw new NotFoundException("Could not find asset with id " + assetId);
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

        }

        return assetRepository.save(asset);
    }

    /**
     * Update the quantity of an Asset held by an Organisational Unit in the database
     *
     * @param assetObject Details of asset to be updated
     * @return Organisational Unit Asset that was updated in the database
     */
    public void updateOrganisationalUnitAsset(OrganisationalUnitAssetDto assetObject) {

        Optional<Asset> optionalAsset = assetRepository.findById(assetObject.getAssetId());

        Optional<OrganisationalUnit> optionalOrganisationalUnit = organisationalUnitRepository
                .findById(assetObject.getOrganisationalUnitId());

        if (optionalAsset.isPresent() && optionalOrganisationalUnit.isPresent()) {

            Optional<OrganisationalUnitAsset> optionalOrganisationalUnitAsset = organisationalUnitAssetRepository
                    .findByOrganisationalUnitAndAsset(optionalOrganisationalUnit.get(), optionalAsset.get());

            if (optionalOrganisationalUnitAsset.isPresent()) {
                double oldQuantity = optionalOrganisationalUnitAsset.get().getQuantity();
                double newQuantity = oldQuantity + assetObject.getQuantity();
                OrganisationalUnitAsset organisationalUnitAsset = optionalOrganisationalUnitAsset.get();

                organisationalUnitAsset.setQuantity(newQuantity);
                organisationalUnitAssetRepository.save(organisationalUnitAsset);


            } else {
                throw new NotFoundException("No such asset or OrganisationalUnitAsset exists");
            }


        } else {
            throw new NotFoundException("No such asset or Organisational Unit exists");
        }

    }

    public OrganisationalUnitAsset addOrganisationalUnitAsset(OrganisationalUnitAssetDto assetObject) {
        OrganisationalUnitAssetId organisationalUnitAssetId = new OrganisationalUnitAssetId(
                assetObject.getOrganisationalUnitId(), assetObject.getAssetId()
        );

        Optional<Asset> optionalAsset = assetRepository.findById(assetObject.getAssetId());

        Optional<OrganisationalUnit> optionalOrganisationalUnit = organisationalUnitRepository
                .findById(assetObject.getOrganisationalUnitId());

        if (optionalAsset.isPresent() && optionalOrganisationalUnit.isPresent()) {
            OrganisationalUnitAsset organisationalUnitAsset = new OrganisationalUnitAsset(
                    organisationalUnitAssetId, optionalOrganisationalUnit.get(), optionalAsset.get(),
                    assetObject.getQuantity()
            );
            return organisationalUnitAssetRepository.save(organisationalUnitAsset);
        }
        throw new NotFoundException("No such asset or Organisational Unit exists");

    }


}
