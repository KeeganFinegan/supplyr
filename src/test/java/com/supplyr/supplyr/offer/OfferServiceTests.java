package com.supplyr.supplyr.offer;


import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.UnauthorizedException;
import com.supplyr.supplyr.repository.*;
import com.supplyr.supplyr.service.OfferService;
import com.supplyr.supplyr.startup.StartupApplicationListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest()
public class OfferServiceTests {

    @Autowired
    OfferService offerService;

    @MockBean
    AssetRepository assetRepository;

    @MockBean
    OrganisationalUnitRepository organisationalUnitRepository;

    @MockBean
    OfferRepository offerRepository;

    @MockBean
    StartupApplicationListener startupApplicationListener;

    @MockBean
    OrganisationalUnitAssetRepository organisationalUnitAssetRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    SecurityContext securityContext;

    @MockBean
    Authentication authentication;


    private OrganisationalUnit it;
    private OrganisationalUnitAsset itAssets;
    private OrganisationalUnit finance;
    private Asset cpuHours;
    private User user;


    @BeforeEach
    public void setUp() {

        it = new OrganisationalUnit();
        it.setId(1L);
        it.setCredits(10);
        it.setName("IT");


        finance = new OrganisationalUnit();
        finance.setId(2L);
        finance.setName("Finance");
        finance.setCredits(10);

        cpuHours = new Asset();
        cpuHours.setAssetId(1L);
        cpuHours.setName("CPU Hours");

        user = new User();


    }


    @Test
    // Add valid buy Offer
    public void addValidBuyOffer() {

        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setQuantity(10);
        buyOffer.setPrice(1);
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setAsset("CPU Hours");

        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);

        when(organisationalUnitRepository.findByUnitName(buyOffer.getOrganisationalUnit())).thenReturn(Optional.of(it));

        when(assetRepository.findByName(buyOffer.getAsset())).thenReturn(Optional.of(cpuHours));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        securityContext.setAuthentication(authentication);
        when(authentication.getName()).thenReturn("Keegan");
        SecurityContextHolder.setContext(securityContext);

        Offer acceptedBuyOffer = offerService.addBuyOffer(buyOffer);

        assertEquals(buyOffer.getAsset(), acceptedBuyOffer.getAsset().getName());

    }

    @Test
    // Add buy Offer with unauthorized user
    public void addBuyOfferUnauthorizedUser() {

        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setQuantity(10);
        buyOffer.setPrice(1);
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setAsset("CPU Hours");

        user.setUsername("Keegan");
        user.setOrganisationalUnit(finance);

        when(organisationalUnitRepository.findByUnitName(buyOffer.getOrganisationalUnit())).thenReturn(Optional.of(finance));

        when(assetRepository.findByName(buyOffer.getAsset())).thenReturn(Optional.of(cpuHours));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        securityContext.setAuthentication(authentication);
        when(authentication.getName()).thenReturn("Keegan");
        SecurityContextHolder.setContext(securityContext);


        assertThrows(UnauthorizedException.class, () -> {
            offerService.addBuyOffer(buyOffer);
        });

    }

    @Test
    // Add buy offer when out by 1 credit
    public void addBuyOfferInsufficientCreditsBoundaryCase() {

        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);

        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setQuantity(11);
        buyOffer.setPrice(1);
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setAsset("CPU Hours");

        when(organisationalUnitRepository.findByUnitName(buyOffer.getOrganisationalUnit())).thenReturn(Optional.of(it));

        when(assetRepository.findByName(buyOffer.getAsset())).thenReturn(Optional.of(cpuHours));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));


        when(securityContext.getAuthentication()).thenReturn(authentication);
        securityContext.setAuthentication(authentication);
        when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);


        assertThrows(BadRequestException.class, () -> {
            offerService.addBuyOffer(buyOffer);
        });


    }

    @Test
    // Add buy offer when out by 0.01 credits
    public void addBuyOfferInsufficientCreditsExceptionalCase() {

        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);

        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setQuantity(10.01);
        buyOffer.setPrice(1);
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setAsset("CPU Hours");

        when(organisationalUnitRepository.findByUnitName(buyOffer.getOrganisationalUnit())).thenReturn(Optional.of(it));

        when(assetRepository.findByName(buyOffer.getAsset())).thenReturn(Optional.of(cpuHours));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));


        when(securityContext.getAuthentication()).thenReturn(authentication);
        securityContext.setAuthentication(authentication);
        when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

        assertThrows(BadRequestException.class, () -> {
            offerService.addBuyOffer(buyOffer);
        });

    }

    @Test
    // Add sell offer when out by 1 asset quantity
    public void addSellOfferInsufficientAssetsBoundaryCase() {

        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);

        itAssets = new OrganisationalUnitAsset();
        itAssets.setAsset(cpuHours);
        itAssets.setQuantity(10);

        OfferRequest sellOffer = new OfferRequest();
        sellOffer.setQuantity(11);
        sellOffer.setPrice(1);
        sellOffer.setOrganisationalUnit("IT");
        sellOffer.setAsset("CPU Hours");

        when(organisationalUnitRepository.findByUnitName(sellOffer.getOrganisationalUnit())).thenReturn(Optional.of(it));

        when(assetRepository.findByName(sellOffer.getAsset())).thenReturn(Optional.of(cpuHours));

        when(organisationalUnitAssetRepository
                .findByOrganisationalUnitAndAsset(it, cpuHours)).thenReturn(Optional.of(itAssets));

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));


        when(securityContext.getAuthentication()).thenReturn(authentication);
        securityContext.setAuthentication(authentication);
        when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

        assertThrows(BadRequestException.class, () -> {
            offerService.addSellOffer(sellOffer);
        });

    }

    @Test
    // Add buy offer when out by 0.01 credits
    public void addSellOfferInsufficientAssetsExceptionalCase() {

        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);

        itAssets = new OrganisationalUnitAsset();
        itAssets.setAsset(cpuHours);
        itAssets.setQuantity(10);

        OfferRequest sellOffer = new OfferRequest();
        sellOffer.setQuantity(10.01);
        sellOffer.setPrice(1);
        sellOffer.setOrganisationalUnit("IT");
        sellOffer.setAsset("CPU Hours");

        when(organisationalUnitRepository
                .findByUnitName(sellOffer.getOrganisationalUnit())).thenReturn(Optional.of(it));

        when(assetRepository.findByName(sellOffer.getAsset())).thenReturn(Optional.of(cpuHours));

        when(organisationalUnitAssetRepository
                .findByOrganisationalUnitAndAsset(it, cpuHours)).thenReturn(Optional.of(itAssets));

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        securityContext.setAuthentication(authentication);
        when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

        assertThrows(BadRequestException.class, () -> {
            offerService.addSellOffer(sellOffer);
        });

    }

    @Test
    // Add sell Offer with unauthorized user
    public void addSellOfferUnauthorizedUser() {

        OfferRequest sellOffer = new OfferRequest();
        sellOffer.setQuantity(10);
        sellOffer.setPrice(1);
        sellOffer.setOrganisationalUnit("IT");
        sellOffer.setAsset("CPU Hours");

        user.setUsername("Keegan");
        user.setOrganisationalUnit(finance);

        when(organisationalUnitRepository.findByUnitName(sellOffer.getOrganisationalUnit())).thenReturn(Optional.of(finance));

        when(assetRepository.findByName(sellOffer.getAsset())).thenReturn(Optional.of(cpuHours));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        securityContext.setAuthentication(authentication);
        when(authentication.getName()).thenReturn("Keegan");
        SecurityContextHolder.setContext(securityContext);


        assertThrows(UnauthorizedException.class, () -> {
            offerService.addSellOffer(sellOffer);
        });
    }

    @Test
    // Successfully update an offer quantity
    public void updateOffer() {

        Offer existingOffer = new Offer();
        existingOffer.setQuantity(10);
        existingOffer.setPrice(1);
        existingOffer.setOrganisationalUnit(it);
        existingOffer.setAsset(cpuHours);

        Offer adjustedOffer = new Offer();
        adjustedOffer.setQuantity(5);
        adjustedOffer.setPrice(1);
        adjustedOffer.setOrganisationalUnit(it);
        adjustedOffer.setAsset(cpuHours);

        when(offerRepository.findById(any())).thenReturn(Optional.of(existingOffer));
        when(offerRepository.save(any())).thenReturn(adjustedOffer);
        Offer result = offerService.updateOffer(1L, 5);
        assertEquals(5, result.getQuantity());

    }

    @Test
    // Successfully delete an offer
    public void deleteOfferById() {

        Offer existingOffer = new Offer();
        existingOffer.setQuantity(10);
        existingOffer.setPrice(1);
        existingOffer.setOrganisationalUnit(it);
        existingOffer.setAsset(cpuHours);
        existingOffer.setId(1L);

        when(offerRepository.existsById(1L)).thenReturn(true);
        when(offerRepository.findById(1L)).thenReturn(Optional.of(existingOffer));

        assertDoesNotThrow(() -> {
            offerService.deleteOfferById(1L);
        });

    }


}
