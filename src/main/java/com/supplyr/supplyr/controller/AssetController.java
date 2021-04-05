package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.model.*;
import com.supplyr.supplyr.repository.AssetRepository;
import com.supplyr.supplyr.repository.OrganisationalUnitAssetRepository;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class AssetController {

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    OrganisationalUnitRepository organisationalUnitRepository;

    @Autowired
    OrganisationalUnitAssetRepository organisationalUnitAssetRepository;

    /**
     * Return a list of all Assets
     */
    @GetMapping("/assets")
    public List<Asset> getAllAssets(){
        return assetRepository.findAll();
    }

    /**
     * Return Asset with a given Id
     */
    @GetMapping("/assets/{id}")
    public Asset getAssetById(@PathVariable Long id){

        if(assetRepository.existsById(id)){
            return assetRepository.findById(id).get();
        }else {
            throw new NotFoundException("Could not find asset with id " + id);
        }
    }

    /**
     * Add new Asset type
     */
    @PostMapping("/assets")
    public Asset addAssetType(@RequestBody Asset asset){
        Optional<Asset> optAsset = assetRepository.findByName(asset.getName());

        if (optAsset.isPresent()){
            throw new AlreadyExistsException("Asset " + asset.getName() + " already exists");

        }
        return assetRepository.save(asset);

    }

    /**
     * Update the quantity of an Asset held by an Organisational Unit
     */
    @PutMapping("/assets")
    public OrganisationalUnitAsset updateAsset(@RequestBody OrganisationalUnitAssetDto assetObject) {
        OrganisationalUnitAssetId organisationalUnitAssetId = new OrganisationalUnitAssetId(
                assetObject.getOrganisationalUnitId(), assetObject.getAssetId()
        );

        Optional<Asset> optionalAsset = assetRepository.findById(assetObject.getAssetId());

        Optional<OrganisationalUnit> optionalOrganisationalUnit = organisationalUnitRepository
                .findById(assetObject.getOrganisationalUnitId());

         if (optionalAsset.isPresent() && optionalOrganisationalUnit.isPresent()){
            OrganisationalUnitAsset organisationalUnitAsset = new OrganisationalUnitAsset(
                    organisationalUnitAssetId, optionalOrganisationalUnit.get(), optionalAsset.get(),
                    assetObject.getQuantity()
            );
            return organisationalUnitAssetRepository.save(organisationalUnitAsset);
        }
        throw new NotFoundException("No such asset or Organisational Unit exists");

    }
}
