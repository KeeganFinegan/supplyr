package com.supplyr.supplyr.asset;

import com.supplyr.supplyr.domain.Asset;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.OrganisationalUnitAsset;
import com.supplyr.supplyr.domain.OrganisationalUnitAssetDto;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.AssetRepository;
import com.supplyr.supplyr.repository.OrganisationalUnitAssetRepository;
import com.supplyr.supplyr.repository.OrganisationalUnitRepository;
import com.supplyr.supplyr.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AssetServiceTests {

    @InjectMocks
    AssetService assetService;

    @Mock
    AssetRepository assetRepository;

    @Mock
    OrganisationalUnitRepository organisationalUnitRepository;

    @Mock
    OrganisationalUnitAssetRepository organisationalUnitAssetRepository;

    private OrganisationalUnit it;
    private Asset cpuHours;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        it = new OrganisationalUnit();
        it.setId(1L);
        it.setCredits(10);
        it.setName("IT");


        cpuHours = new Asset();
        cpuHours.setAssetId(1L);
        cpuHours.setName("CPU Hours");

    }

    @Test
    public void get_asset_by_name_success() {
        cpuHours.setAssetId(1L);
        when(assetRepository.findByName(cpuHours.getName())).thenReturn(java.util.Optional.ofNullable(cpuHours));

        Asset asset = assetService.getAssetByName("CPU Hours");

        assertEquals("CPU Hours", asset.getName());
        assertEquals(1L, asset.getAssetId());


    }

    @Test
    public void get_asset_by_name_non_existent() {
        when(assetRepository.findByName(cpuHours.getName())).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> {
            assetService.getAssetByName("NOT EXISTANT ASSET");
        });


    }

    @Test
    public void add_asset_type_success() {
        when(assetRepository.findByName(cpuHours.getName())).thenReturn(Optional.empty());
        when(assetRepository.save(cpuHours)).thenReturn(cpuHours);

        Asset asset = assetService.addAssetType(cpuHours);

        assertEquals("CPU Hours", asset.getName());
        assertEquals(1L, asset.getAssetId());


    }

    @Test
    public void add_asset_type_already_exists() {
        when(assetRepository.findByName(cpuHours.getName())).thenReturn(Optional.of(cpuHours));
        when(assetRepository.save(cpuHours)).thenReturn(cpuHours);

        assertThrows(AlreadyExistsException.class, () -> {
            assetService.addAssetType(cpuHours);
        });


    }

    @Test
    public void update_unit_asset_success() {

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setOrganisationalUnit(it);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setQuantity(10);

        OrganisationalUnitAssetDto updateRequest = new OrganisationalUnitAssetDto();
        updateRequest.setAssetName("CPU Hours");
        updateRequest.setQuantity(20);
        updateRequest.setOrganisationalUnitName("IT");


        OrganisationalUnitAsset updatedItCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setOrganisationalUnit(it);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setQuantity(30);

        cpuHours.setAssetId(1L);
        when(assetRepository.findByName(cpuHours.getName())).thenReturn(Optional.of(cpuHours));
        when(assetRepository.findById(1L)).thenReturn(Optional.of(cpuHours));
        when(organisationalUnitRepository.findByUnitName("IT")).thenReturn(Optional.of(it));
        when(organisationalUnitRepository.findById((1L))).thenReturn(Optional.of(it));
        when(organisationalUnitAssetRepository.findByOrganisationalUnitAndAsset(it, cpuHours)).thenReturn(Optional.of(itCpuHours));
        when(organisationalUnitAssetRepository.save(updatedItCpuHours)).thenReturn(updatedItCpuHours);


        assertDoesNotThrow(() -> {
            assetService.updateOrganisationalUnitAsset(updateRequest);
        });


    }

    @Test
    public void update_unit_asset_negative_quantity() {

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setOrganisationalUnit(it);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setQuantity(10);

        OrganisationalUnitAssetDto updateRequest = new OrganisationalUnitAssetDto();
        updateRequest.setAssetName("CPU Hours");
        updateRequest.setQuantity(-100);
        updateRequest.setOrganisationalUnitName("IT");


        OrganisationalUnitAsset updatedItCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setOrganisationalUnit(it);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setQuantity(20);

        cpuHours.setAssetId(1L);
        when(assetRepository.findByName(cpuHours.getName())).thenReturn(Optional.of(cpuHours));
        when(assetRepository.findById(1L)).thenReturn(Optional.of(cpuHours));
        when(organisationalUnitRepository.findByUnitName("IT")).thenReturn(Optional.of(it));
        when(organisationalUnitRepository.findById((1L))).thenReturn(Optional.of(it));
        when(organisationalUnitAssetRepository.findByOrganisationalUnitAndAsset(it, cpuHours)).thenReturn(Optional.of(itCpuHours));
        when(organisationalUnitAssetRepository.save(updatedItCpuHours)).thenReturn(updatedItCpuHours);


        assertThrows(BadRequestException.class, () -> {
            assetService.updateOrganisationalUnitAsset(updateRequest);
        }, "An asset cannot have a negative quantity");


    }

    @Test
    public void add_organisational_unit_asset() {

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setOrganisationalUnit(it);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setQuantity(10);

        OrganisationalUnitAssetDto assetRequest = new OrganisationalUnitAssetDto();
        assetRequest.setAssetName("CPU Hours");
        assetRequest.setQuantity(10);
        assetRequest.setOrganisationalUnitName("IT");

        when(organisationalUnitRepository.findByUnitName("IT")).thenReturn(Optional.of(it));
        when(organisationalUnitRepository.findById((1L))).thenReturn(Optional.of(it));
        when(assetRepository.findByName(cpuHours.getName())).thenReturn(Optional.of(cpuHours));
        when(assetRepository.findById(1L)).thenReturn(Optional.of(cpuHours));
        when(organisationalUnitAssetRepository.findByOrganisationalUnitAndAsset(it, cpuHours)).thenReturn(Optional.of(itCpuHours));
        when(organisationalUnitAssetRepository.save(any())).thenReturn(itCpuHours);

        OrganisationalUnitAsset organisationalUnitAsset = assetService.addOrganisationalUnitAsset(assetRequest);

        assertEquals("CPU Hours", organisationalUnitAsset.getAsset().getName());
        assertEquals(10, organisationalUnitAsset.getQuantity());
        assertEquals("IT", organisationalUnitAsset.getOrganisationalUnit().getName());


    }

    @Test
    public void add_organisational_unit_asset_negative_credits() {

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setOrganisationalUnit(it);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setQuantity(10);

        OrganisationalUnitAssetDto assetRequest = new OrganisationalUnitAssetDto();
        assetRequest.setAssetName("CPU Hours");
        assetRequest.setQuantity(-10);
        assetRequest.setOrganisationalUnitName("IT");

        when(organisationalUnitRepository.findByUnitName("IT")).thenReturn(Optional.of(it));
        when(organisationalUnitRepository.findById((1L))).thenReturn(Optional.of(it));
        when(assetRepository.findByName(cpuHours.getName())).thenReturn(Optional.of(cpuHours));
        when(assetRepository.findById(1L)).thenReturn(Optional.of(cpuHours));
        when(organisationalUnitAssetRepository.findByOrganisationalUnitAndAsset(it, cpuHours)).thenReturn(Optional.of(itCpuHours));
        when(organisationalUnitAssetRepository.save(any())).thenReturn(itCpuHours);


        assertThrows(BadRequestException.class, () -> {
            assetService.addOrganisationalUnitAsset(assetRequest);
        }, "An asset cannot have a negative quantity");


    }


}
