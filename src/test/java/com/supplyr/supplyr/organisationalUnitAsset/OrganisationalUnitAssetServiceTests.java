package com.supplyr.supplyr.organisationalUnitAsset;


import com.supplyr.supplyr.domain.Asset;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.domain.OrganisationalUnitAsset;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.OrganisationalUnitAssetRepository;
import com.supplyr.supplyr.service.OrganisationalUnitAssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class OrganisationalUnitAssetServiceTests {

    @InjectMocks
    OrganisationalUnitAssetService organisationalUnitAssetService;

    @Mock
    OrganisationalUnitAssetRepository organisationalUnitAssetRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void getOrganisationalUnitAssetSuccess() {

        Asset cpuHours = new Asset();
        cpuHours.setAssetId(1L);
        cpuHours.setName("CPU Hours");


        OrganisationalUnit it = new OrganisationalUnit();
        it.setCredits(100);
        it.setName("IT");

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(100);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(it);

        when(organisationalUnitAssetRepository
                .findByOrganisationalUnitAndAsset(it, cpuHours)).thenReturn(Optional.of(itCpuHours));

        OrganisationalUnitAsset returnedUnitAsset = organisationalUnitAssetService
                .getOrganisationalUnitAsset(it, cpuHours);

        assertEquals("CPU Hours", returnedUnitAsset.getAsset().getName());
        assertEquals("IT", returnedUnitAsset.getOrganisationalUnit().getName());

    }

    @Test
    public void getOrganisationalUnitAssetNonExistent() {

        Asset cpuHours = new Asset();
        cpuHours.setAssetId(1L);
        cpuHours.setName("CPU Hours");

        OrganisationalUnit it = new OrganisationalUnit();
        it.setCredits(100);
        it.setName("IT");


        when(organisationalUnitAssetRepository
                .findByOrganisationalUnitAndAsset(it, cpuHours))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            organisationalUnitAssetService.getOrganisationalUnitAsset(it, cpuHours);
        });


    }


}
