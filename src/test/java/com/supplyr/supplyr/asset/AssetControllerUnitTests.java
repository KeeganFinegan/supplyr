package com.supplyr.supplyr.asset;


import com.supplyr.supplyr.controller.AssetController;
import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.service.AssetService;
import com.supplyr.supplyr.service.OfferService;
import com.supplyr.supplyr.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssetControllerUnitTests {

    private AssetController assetController;

    @Mock
    private TradeService tradeService;

    @Mock
    private AssetService assetService;

    @Mock
    private OfferService offerService;

    private OrganisationalUnit it;
    private Asset cpuHours;
    private Asset softwareLicense;

    @BeforeEach
    public void setUp() {


        assetController = new AssetController(assetService, tradeService, offerService);
        it = new OrganisationalUnit();
        it.setId(1L);
        it.setCredits(250);
        it.setName("IT");

        cpuHours = new Asset();
        cpuHours.setAssetId(1L);
        cpuHours.setName("CPU Hours");

        softwareLicense = new Asset();
        softwareLicense.setAssetId(2L);
        softwareLicense.setName("Software License");
    }

    @Test
    public void create_new_asset_type() throws Exception {


        when(assetService.addAssetType(any(Asset.class))).thenReturn(cpuHours);

        Asset newAssetType = assetController.addAssetType(cpuHours);

        assertEquals("CPU Hours", newAssetType.getName());

    }


    @Test
    public void get_all_assets() throws Exception {

        List<Asset> assetList = new ArrayList<>();
        assetList.add(softwareLicense);
        assetList.add(cpuHours);

        when(assetService.getAllAssets()).thenReturn(assetList);

        List<Asset> returnedAssetList = assetController.getAllAssets();

        assertEquals("Software License", returnedAssetList.get(0).getName());
        assertEquals("CPU Hours", returnedAssetList.get(1).getName());

    }

    @Test
    public void allocate_asset_to_organisational_unit() throws Exception {

        OrganisationalUnitAsset addedUnitAssest = new OrganisationalUnitAsset();
        addedUnitAssest.setQuantity(10);
        addedUnitAssest.setAsset(cpuHours);
        addedUnitAssest.setOrganisationalUnit(it);

        OrganisationalUnitAssetDto request = new OrganisationalUnitAssetDto();
        request.setOrganisationalUnitName("IT");
        request.setQuantity(10);
        request.setAssetName("CPU Hours");

        when(assetService.addOrganisationalUnitAsset(request)).thenReturn(addedUnitAssest);

        OrganisationalUnitAsset addedUnitAsset = assetController.addOrganisationalUnitAsset(request);

        assertEquals("CPU Hours", addedUnitAsset.getAsset().getName());
        assertEquals("IT", addedUnitAsset.getOrganisationalUnit().getName());
        assertEquals(10, addedUnitAsset.getQuantity());

    }

    @Test
    public void get_asset_trades() throws Exception {

        Trade trade1 = new Trade();
        trade1.setAsset(cpuHours);
        trade1.setOrganisationalUnit(it);
        trade1.setPrice(10);
        trade1.setQuantity(2);

        Trade trade2 = new Trade();
        trade2.setAsset(cpuHours);
        trade2.setOrganisationalUnit(it);
        trade2.setPrice(20);
        trade2.setQuantity(2);

        List<Trade> tradeList = new ArrayList<>();
        tradeList.add(trade1);
        tradeList.add(trade2);

        when(tradeService.getAssetTrades("CPU Hours")).thenReturn(tradeList);

        List<Trade> returnedTrades = assetController.getAssetTrades("CPU Hours");


        assertEquals("CPU Hours", returnedTrades.get(0).getAsset().getName());
        assertEquals("CPU Hours", returnedTrades.get(1).getAsset().getName());
        assertEquals("IT", returnedTrades.get(1).getOrganisationalUnit().getName());
        assertEquals("IT", returnedTrades.get(0).getOrganisationalUnit().getName());

    }

    @Test
    public void get_lowest_ask_highest_bid() throws Exception {

        List<Double> lowestAskHighestBid = new ArrayList<>();
        lowestAskHighestBid.add(19.0);
        lowestAskHighestBid.add(20.5);

        when(offerService.getLowestAskAndHighestBid("CPU Hours")).thenReturn(lowestAskHighestBid);

        LowestAskHighestBidDto returnedValues = assetController.getLowestAskHighestBid("CPU Hours");


        assertEquals(19.0, returnedValues.getLowestAsk());
        assertEquals(20.5, returnedValues.getHighestBid());

    }

}
