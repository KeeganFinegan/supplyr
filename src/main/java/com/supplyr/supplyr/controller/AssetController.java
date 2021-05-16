package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.domain.Asset;
import com.supplyr.supplyr.domain.OrganisationalUnitAsset;
import com.supplyr.supplyr.domain.OrganisationalUnitAssetDto;
import com.supplyr.supplyr.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AssetController {

    @Autowired
    AssetService assetService;


    /**
     * REST endpoint to retrieve a list of all Assets
     */
    @GetMapping("/assets")
    public List<Asset> getAllAssets() {
        return assetService.getAllAssets();
    }


    /**
     * REST endpoint to add a new Asset type
     *
     * @param asset Asset to be added
     * @return Asset that was added
     */
    @PostMapping("/assets")
    public Asset addAssetType(@RequestBody Asset asset) {
        return assetService.addAssetType(asset);

    }

    /**
     * REST endpoint to update the quantity of an Asset held by an Organisational Unit
     *
     * @param assetObject Details of asset to be updated
     * @return Organisational Unit Asset that was updated
     */
    @PutMapping("/assets")
    public OrganisationalUnitAsset addOrganisationalUnitAsset(@RequestBody OrganisationalUnitAssetDto assetObject) {
        return assetService.addOrganisationalUnitAsset(assetObject);

    }
}
