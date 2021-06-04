package com.supplyr.supplyr.offer;

import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.UnauthorizedException;
import com.supplyr.supplyr.repository.OfferRepository;
import com.supplyr.supplyr.repository.UserRepository;
import com.supplyr.supplyr.service.*;
import com.supplyr.supplyr.utility.BeanUtility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class OfferServiceTests {

    @InjectMocks
    private OfferService offerService;

    @Mock
    private AssetService assetService;


    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OrganisationalUnitService organisationalUnitService;

    @Mock
    private OrganisationalUnitAssetService organisationalUnitAssetService;


    @Mock
    TradeService tradeService;

    @Mock
    SecurityContextService securityContextService;

    @Mock
    UserRepository userRepository;

    private OrganisationalUnit it;
    private OrganisationalUnit finance;
    private Asset cpuHours;
    private User user;
    private final MockedStatic<BeanUtility> beanUtilityMockedStatic = Mockito.mockStatic(BeanUtility.class);


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mocking Bean Utility class in order to access static method - getBean
        beanUtilityMockedStatic.when(() -> {
            BeanUtility.getBean(OfferService.class);
        }).thenReturn(offerService);
        beanUtilityMockedStatic.when(() -> {
            BeanUtility.getBean(TradeService.class);
        }).thenReturn(tradeService);
        beanUtilityMockedStatic.when(() -> {
            BeanUtility.getBean(AssetService.class);
        }).thenReturn(assetService);
        beanUtilityMockedStatic.when(() -> {
            BeanUtility.getBean(OrganisationalUnitService.class);
        }).thenReturn(organisationalUnitService);
        beanUtilityMockedStatic.when(() -> {
            BeanUtility.getBean(OrganisationalUnitAssetService.class);
        }).thenReturn(organisationalUnitAssetService);


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

    @AfterEach
    public void closeStatic() {
        beanUtilityMockedStatic.close();

    }

    @Test
    // Add valid buy Offer
    public void addValidBuyOffer() {

        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setQuantity(10);
        buyOffer.setPrice(1);
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setAsset("CPU Hours");

        Offer offer = new Offer();
        offer.setQuantity(buyOffer.getQuantity());
        offer.setPrice(buyOffer.getPrice());
        offer.setOrganisationalUnit(it);
        offer.setAsset(cpuHours);

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(100);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(it);

        when(assetService.getAssetByName(any())).thenReturn(cpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName(any())).thenReturn(it);
        when(organisationalUnitAssetService.getOrganisationalUnitAsset(any(), any())).thenReturn(itCpuHours);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(securityContextService.getCurrentUser()).thenReturn(user.getUsername());
        when(offerRepository.save(any())).thenReturn(offer);


        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);

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

        when(organisationalUnitService.getOrganisationalUnitByName(buyOffer.getOrganisationalUnit()))
                .thenReturn(finance);

        when(assetService.getAssetByName(buyOffer.getAsset())).thenReturn(cpuHours);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        when(securityContextService.getCurrentUser()).thenReturn(user.getUsername());


        assertThrows(UnauthorizedException.class, () -> {
            offerService.addBuyOffer(buyOffer);
        });

    }

    @Test
    // Add buy offer when out by 1 credit
    public void addBuyOfferInsufficientCreditsBoundaryCase() {

        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setQuantity(11);
        buyOffer.setPrice(1);
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setAsset("CPU Hours");

        Offer offer = new Offer();
        offer.setQuantity(buyOffer.getQuantity());
        offer.setPrice(buyOffer.getPrice());
        offer.setOrganisationalUnit(it);
        offer.setAsset(cpuHours);

        when(assetService.getAssetByName(any())).thenReturn(cpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName(any())).thenReturn(it);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(securityContextService.getCurrentUser()).thenReturn(user.getUsername());
        when(offerRepository.save(any())).thenReturn(offer);

        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);


        assertThrows(BadRequestException.class, () -> {
            offerService.addBuyOffer(buyOffer);
        });


    }

    @Test
    // Add buy offer when out by 0.01 credits
    public void addBuyOfferInsufficientCreditsExceptionalCase() {

        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setQuantity(10.001);
        buyOffer.setPrice(1);
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setAsset("CPU Hours");

        Offer offer = new Offer();
        offer.setQuantity(buyOffer.getQuantity());
        offer.setPrice(buyOffer.getPrice());
        offer.setOrganisationalUnit(it);
        offer.setAsset(cpuHours);

        when(assetService.getAssetByName(any())).thenReturn(cpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName(any())).thenReturn(it);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(securityContextService.getCurrentUser()).thenReturn(user.getUsername());
        when(offerRepository.save(any())).thenReturn(offer);

        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);


        assertThrows(BadRequestException.class, () -> {
            offerService.addBuyOffer(buyOffer);
        });

    }

    @Test
    // Add buy offer with negative price
    public void addBuyOfferWithNegativePrice() {

        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setQuantity(10);
        buyOffer.setPrice(-5);
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setAsset("CPU Hours");

        Offer offer = new Offer();
        offer.setQuantity(buyOffer.getQuantity());
        offer.setPrice(buyOffer.getPrice());
        offer.setOrganisationalUnit(it);
        offer.setAsset(cpuHours);

        when(assetService.getAssetByName(any())).thenReturn(cpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName(any())).thenReturn(it);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(securityContextService.getCurrentUser()).thenReturn(user.getUsername());
        when(offerRepository.save(any())).thenReturn(offer);

        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);


        assertThrows(BadRequestException.class, () -> {
            offerService.addBuyOffer(buyOffer);
        });

    }


    @Test
    // Add sell offer when out by 1 asset quantity
    public void addSellOfferInsufficientAssetsBoundaryCase() {

        OfferRequest sellOffer = new OfferRequest();
        sellOffer.setQuantity(10);
        sellOffer.setPrice(1);
        sellOffer.setOrganisationalUnit("IT");
        sellOffer.setAsset("CPU Hours");

        Offer offer = new Offer();
        offer.setQuantity(sellOffer.getQuantity());
        offer.setPrice(sellOffer.getPrice());
        offer.setOrganisationalUnit(it);
        offer.setAsset(cpuHours);

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(9);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(it);

        when(assetService.getAssetByName(any())).thenReturn(cpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName(any())).thenReturn(it);
        when(organisationalUnitAssetService.getOrganisationalUnitAsset(any(), any())).thenReturn(itCpuHours);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(securityContextService.getCurrentUser()).thenReturn(user.getUsername());
        when(offerRepository.save(any())).thenReturn(offer);

        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);

        assertThrows(BadRequestException.class, () -> {
            offerService.addSellOffer(sellOffer);
        });


    }

    @Test
    // Add buy offer when out by 0.01 credits
    public void addSellOfferInsufficientAssetsExceptionalCase() {

        OfferRequest sellOffer = new OfferRequest();
        sellOffer.setQuantity(10);
        sellOffer.setPrice(1);
        sellOffer.setOrganisationalUnit("IT");
        sellOffer.setAsset("CPU Hours");

        Offer offer = new Offer();
        offer.setQuantity(sellOffer.getQuantity());
        offer.setPrice(sellOffer.getPrice());
        offer.setOrganisationalUnit(it);
        offer.setAsset(cpuHours);

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(9.99);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(it);

        when(assetService.getAssetByName(any())).thenReturn(cpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName(any())).thenReturn(it);
        when(organisationalUnitAssetService.getOrganisationalUnitAsset(any(), any())).thenReturn(itCpuHours);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(securityContextService.getCurrentUser()).thenReturn(user.getUsername());
        when(offerRepository.save(any())).thenReturn(offer);

        user.setUsername("Keegan");
        user.setOrganisationalUnit(it);

        assertThrows(BadRequestException.class, () -> {
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

    @Test
    public void getLowestAskHighestBid() {

        Offer buyOffer1 = new Offer();
        buyOffer1.setQuantity(10);
        buyOffer1.setPrice(20);
        buyOffer1.setType(OfferType.BUY);
        buyOffer1.setOrganisationalUnit(it);
        buyOffer1.setAsset(cpuHours);
        buyOffer1.setId(1L);

        Offer buyOffer2 = new Offer();
        buyOffer2.setQuantity(10);
        buyOffer2.setPrice(19);
        buyOffer2.setType(OfferType.BUY);
        buyOffer2.setOrganisationalUnit(it);
        buyOffer2.setAsset(cpuHours);
        buyOffer2.setId(1L);

        Offer buyOffer3 = new Offer();
        buyOffer3.setQuantity(10);
        buyOffer3.setPrice(14);
        buyOffer3.setType(OfferType.BUY);
        buyOffer3.setOrganisationalUnit(it);
        buyOffer3.setAsset(cpuHours);
        buyOffer3.setId(1L);

        Offer sellOffer1 = new Offer();
        sellOffer1.setQuantity(10);
        sellOffer1.setPrice(20);
        sellOffer1.setType(OfferType.SELL);
        sellOffer1.setOrganisationalUnit(it);
        sellOffer1.setAsset(cpuHours);
        sellOffer1.setId(1L);

        Offer sellOffer2 = new Offer();
        sellOffer2.setQuantity(10);
        sellOffer2.setPrice(19);
        sellOffer2.setType(OfferType.SELL);
        sellOffer2.setOrganisationalUnit(it);
        sellOffer1.setAsset(cpuHours);
        sellOffer2.setId(1L);

        List<Offer> offerList = new ArrayList<>();
        offerList.add(buyOffer1);
        offerList.add(sellOffer2);
        offerList.add(buyOffer3);
        offerList.add(buyOffer3);
        offerList.add(sellOffer1);
        offerList.add(buyOffer2);

        when(offerRepository.findOffersByAsset_Name("CPU Hours")).thenReturn(Optional.of(offerList));

        List<Double> lowestAskHighestBid = offerService.getLowestAskAndHighestBid("CPU Hours");

        assertEquals(19, lowestAskHighestBid.get(0));
        assertEquals(20, lowestAskHighestBid.get(1));


    }


}

