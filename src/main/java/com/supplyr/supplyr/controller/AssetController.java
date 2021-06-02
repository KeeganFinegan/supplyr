package com.supplyr.supplyr.controller;

import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.service.AssetService;
import com.supplyr.supplyr.service.OfferService;
import com.supplyr.supplyr.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AssetController {


    private final AssetService assetService;
    private final TradeService tradeService;
    private final OfferService offerService;


    @Autowired
    public AssetController(AssetService assetService, TradeService tradeService, OfferService offerService) {
        this.assetService = assetService;
        this.tradeService = tradeService;
        this.offerService = offerService;
    }


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

    @GetMapping("/assets/{asset}/trades")
    public List<Trade> getAssetTrades(@PathVariable String asset) {
        return tradeService.getAssetTrades(asset);
    }

    @GetMapping("/assets/{asset}/offer-info")
    public LowestAskHighestBidDto getLowestAsk(@PathVariable String asset) {

        List<Double> lowestAskHighestBid = offerService.getLowestAskAndHighestBid(asset);
        LowestAskHighestBidDto lowestAskHighestBidDto = new LowestAskHighestBidDto();
        lowestAskHighestBidDto.setLowestAsk(lowestAskHighestBid.get(0));
        lowestAskHighestBidDto.setHighestBid(lowestAskHighestBid.get(1));
        return lowestAskHighestBidDto;


    }
}
