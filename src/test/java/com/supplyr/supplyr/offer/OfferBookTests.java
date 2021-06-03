package com.supplyr.supplyr.offer;


import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.repository.OfferRepository;
import com.supplyr.supplyr.repository.UserRepository;
import com.supplyr.supplyr.service.*;
import com.supplyr.supplyr.utility.BeanUtility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;

public class OfferBookTests {


    private OrganisationalUnit it;
    private OrganisationalUnit finance;
    private Asset cpuHours;
    private final MockedStatic<BeanUtility> beanUtilityMockedStatic = Mockito.mockStatic(BeanUtility.class);

    @Mock
    private AssetService assetService;

    @Mock
    private OrganisationalUnitService organisationalUnitService;

    @Mock
    private OrganisationalUnitAssetService organisationalUnitAssetService;

    @Mock
    OfferService offerService;


    @Mock
    TradeService tradeService;




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


    }


    @AfterEach
    public void closeStatic(){
        beanUtilityMockedStatic.close();

    }

    @Test
    public void successfully_add_buy_offer(){

        Offer buyOffer = new Offer();
        buyOffer.setType(OfferType.BUY);
        buyOffer.setQuantity(10);
        buyOffer.setPrice(5);
        buyOffer.setOrganisationalUnit(it);
        buyOffer.setAsset(cpuHours);
        buyOffer.setId(1L);
        buyOffer.setTimestamp(LocalDateTime.now());


        OfferBook offerBook = new OfferBook(1L);
        offerBook.addOfferHelper(buyOffer);


        assertEquals("CPU Hours",offerBook.getBuyOffers().peek().getAsset().getName());
        assertEquals(OfferType.BUY,offerBook.getBuyOffers().peek().getType());
        assertEquals(10,offerBook.getBuyOffers().peek().getQuantity());
        assertEquals(5,offerBook.getBuyOffers().peek().getPrice());
        assertEquals("IT",offerBook.getBuyOffers().peek().getOrganisationalUnit().getName());


    }

    @Test
    public void successfully_add_sell_offer(){

        Offer sellOffer = new Offer();
        sellOffer.setType(OfferType.SELL);
        sellOffer.setQuantity(10);
        sellOffer.setPrice(5);
        sellOffer.setOrganisationalUnit(it);
        sellOffer.setAsset(cpuHours);
        sellOffer.setId(2L);
        sellOffer.setTimestamp(LocalDateTime.now());

        OfferBook offerBook = new OfferBook(1L);
        offerBook.addOfferHelper(sellOffer);

        assertEquals("CPU Hours",offerBook.getSellOffers().peek().getAsset().getName());
        assertEquals(OfferType.SELL,offerBook.getSellOffers().peek().getType());
        assertEquals(10,offerBook.getSellOffers().peek().getQuantity());
        assertEquals(5,offerBook.getSellOffers().peek().getPrice());
        assertEquals("IT",offerBook.getSellOffers().peek().getOrganisationalUnit().getName());


    }

    @Test
    public void match_sell_offer_in_book(){

        Offer buyOffer = new Offer();
        buyOffer.setType(OfferType.BUY);
        buyOffer.setQuantity(10);
        buyOffer.setPrice(5);
        buyOffer.setId(1L);
        buyOffer.setOrganisationalUnit(finance);
        buyOffer.setAsset(cpuHours);
        buyOffer.setTimestamp(LocalDateTime.of(2021, Month.JANUARY,4,10,1));

        Offer sellOffer = new Offer();
        sellOffer.setType(OfferType.SELL);
        sellOffer.setId(2L);
        sellOffer.setQuantity(10);
        sellOffer.setPrice(5);
        sellOffer.setOrganisationalUnit(it);
        sellOffer.setAsset(cpuHours);
        sellOffer.setTimestamp(LocalDateTime.now());

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(100);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(finance);

        OrganisationalUnitAsset financeCpuHours = new OrganisationalUnitAsset();
        financeCpuHours.setQuantity(100);
        financeCpuHours.setAsset(cpuHours);
        financeCpuHours.setOrganisationalUnit(it);

        when(organisationalUnitAssetService.getOrganisationalUnitAsset(it,cpuHours)).thenReturn(itCpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName("Finance")).thenReturn(finance);

        OfferBook offerBook = new OfferBook(1L);
        offerBook.addOfferHelper(sellOffer);
        offerBook.addOfferHelper(buyOffer);


        // Check buy offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(1L).getAsset().getName());
        assertEquals(OfferType.BUY,offerBook.getFilledOffers().get(1L).getType());

        // Check sell offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(2L).getAsset().getName());
        assertEquals(OfferType.SELL,offerBook.getFilledOffers().get(2L).getType());




    }

    @Test
    public void match_buy_offer_in_book(){

        Offer buyOffer = new Offer();
        buyOffer.setType(OfferType.BUY);
        buyOffer.setQuantity(10);
        buyOffer.setPrice(5);
        buyOffer.setId(1L);
        buyOffer.setOrganisationalUnit(finance);
        buyOffer.setAsset(cpuHours);
        buyOffer.setTimestamp(LocalDateTime.of(2021, Month.JANUARY,4,10,1));

        Offer sellOffer = new Offer();
        sellOffer.setType(OfferType.SELL);
        sellOffer.setId(2L);
        sellOffer.setQuantity(10);
        sellOffer.setPrice(5);
        sellOffer.setOrganisationalUnit(it);
        sellOffer.setAsset(cpuHours);
        sellOffer.setTimestamp(LocalDateTime.now());

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(100);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(finance);

        OrganisationalUnitAsset financeCpuHours = new OrganisationalUnitAsset();
        financeCpuHours.setQuantity(100);
        financeCpuHours.setAsset(cpuHours);
        financeCpuHours.setOrganisationalUnit(it);

        when(organisationalUnitAssetService.getOrganisationalUnitAsset(it,cpuHours)).thenReturn(itCpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName("Finance")).thenReturn(finance);

        OfferBook offerBook = new OfferBook(1L);

        offerBook.addOfferHelper(buyOffer);
        offerBook.addOfferHelper(sellOffer);


        // Check buy offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(1L).getAsset().getName());
        assertEquals(OfferType.BUY,offerBook.getFilledOffers().get(1L).getType());

        // Check sell offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(2L).getAsset().getName());
        assertEquals(OfferType.SELL,offerBook.getFilledOffers().get(2L).getType());


    }

    @Test
    public void fill_partial_buy_order(){

        Offer buyOffer = new Offer();
        buyOffer.setType(OfferType.BUY);
        buyOffer.setQuantity(30);
        buyOffer.setPrice(5);
        buyOffer.setId(1L);
        buyOffer.setOrganisationalUnit(finance);
        buyOffer.setAsset(cpuHours);
        buyOffer.setTimestamp(LocalDateTime.of(2021, Month.JANUARY,4,10,1));

        Offer sellOffer = new Offer();
        sellOffer.setType(OfferType.SELL);
        sellOffer.setId(2L);
        sellOffer.setQuantity(10);
        sellOffer.setPrice(5);
        sellOffer.setOrganisationalUnit(it);
        sellOffer.setAsset(cpuHours);
        sellOffer.setTimestamp(LocalDateTime.now());

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(100);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(finance);

        OrganisationalUnitAsset financeCpuHours = new OrganisationalUnitAsset();
        financeCpuHours.setQuantity(100);
        financeCpuHours.setAsset(cpuHours);
        financeCpuHours.setOrganisationalUnit(it);

        when(organisationalUnitAssetService.getOrganisationalUnitAsset(it,cpuHours)).thenReturn(itCpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName("Finance")).thenReturn(finance);

        OfferBook offerBook = new OfferBook(1L);

        offerBook.addOfferHelper(buyOffer);
        offerBook.addOfferHelper(sellOffer);


        // Check buy offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(1L).getAsset().getName());
        assertEquals(OfferType.BUY,offerBook.getFilledOffers().get(1L).getType());
        assertEquals(10,offerBook.getFilledOffers().get(1L).getQuantity());


        // Check sell offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(2L).getAsset().getName());
        assertEquals(OfferType.SELL,offerBook.getFilledOffers().get(2L).getType());
        assertEquals(10,offerBook.getFilledOffers().get(2L).getQuantity());


        // Check partial offer is still in offers queue
        assertEquals(20,offerBook.getBuyOffers().peek().getQuantity());
        assertEquals(OfferType.BUY,offerBook.getBuyOffers().peek().getType());


    }

    @Test
    public void fill_partial_sell_order(){

        Offer buyOffer = new Offer();
        buyOffer.setType(OfferType.BUY);
        buyOffer.setQuantity(10);
        buyOffer.setPrice(5);
        buyOffer.setId(1L);
        buyOffer.setOrganisationalUnit(finance);
        buyOffer.setAsset(cpuHours);
        buyOffer.setTimestamp(LocalDateTime.of(2021, Month.JANUARY,4,10,1));

        Offer sellOffer = new Offer();
        sellOffer.setType(OfferType.SELL);
        sellOffer.setId(2L);
        sellOffer.setQuantity(30);
        sellOffer.setPrice(5);
        sellOffer.setOrganisationalUnit(it);
        sellOffer.setAsset(cpuHours);
        sellOffer.setTimestamp(LocalDateTime.now());

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(100);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(finance);

        OrganisationalUnitAsset financeCpuHours = new OrganisationalUnitAsset();
        financeCpuHours.setQuantity(100);
        financeCpuHours.setAsset(cpuHours);
        financeCpuHours.setOrganisationalUnit(it);

        when(organisationalUnitAssetService.getOrganisationalUnitAsset(it,cpuHours)).thenReturn(itCpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName("Finance")).thenReturn(finance);

        OfferBook offerBook = new OfferBook(1L);

        offerBook.addOfferHelper(buyOffer);
        offerBook.addOfferHelper(sellOffer);


        // Check buy offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(1L).getAsset().getName());
        assertEquals(OfferType.BUY,offerBook.getFilledOffers().get(1L).getType());
        assertEquals(10,offerBook.getFilledOffers().get(1L).getQuantity());


        // Check sell offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(2L).getAsset().getName());
        assertEquals(OfferType.SELL,offerBook.getFilledOffers().get(2L).getType());
        assertEquals(10,offerBook.getFilledOffers().get(2L).getQuantity());


        // Check partial offer is still in offers queue
        assertEquals(20,offerBook.getSellOffers().peek().getQuantity());
        assertEquals(OfferType.SELL,offerBook.getSellOffers().peek().getType());

    }

    @Test
    public void dont_match_orders_from_same_unit(){

        Offer buyOffer = new Offer();
        buyOffer.setType(OfferType.BUY);
        buyOffer.setQuantity(10);
        buyOffer.setPrice(5);
        buyOffer.setId(1L);
        buyOffer.setOrganisationalUnit(it);
        buyOffer.setAsset(cpuHours);
        buyOffer.setTimestamp(LocalDateTime.of(2021, Month.JANUARY,4,10,1));

        Offer sellOffer = new Offer();
        sellOffer.setType(OfferType.SELL);
        sellOffer.setId(2L);
        sellOffer.setQuantity(10);
        sellOffer.setPrice(5);
        sellOffer.setOrganisationalUnit(it);
        sellOffer.setAsset(cpuHours);
        sellOffer.setTimestamp(LocalDateTime.now());

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(100);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(finance);

        OrganisationalUnitAsset financeCpuHours = new OrganisationalUnitAsset();
        financeCpuHours.setQuantity(100);
        financeCpuHours.setAsset(cpuHours);
        financeCpuHours.setOrganisationalUnit(it);

        when(organisationalUnitAssetService.getOrganisationalUnitAsset(it,cpuHours)).thenReturn(itCpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName("IT")).thenReturn(it);

        OfferBook offerBook = new OfferBook(1L);

        offerBook.addOfferHelper(buyOffer);
        offerBook.addOfferHelper(sellOffer);

        assertEquals("CPU Hours",offerBook.getSellOffers().peek().getAsset().getName());
        assertEquals(OfferType.SELL,offerBook.getSellOffers().peek().getType());
        assertEquals(10,offerBook.getSellOffers().peek().getQuantity());
        assertEquals(5,offerBook.getSellOffers().peek().getPrice());
        assertEquals("IT",offerBook.getSellOffers().peek().getOrganisationalUnit().getName());

        assertEquals("CPU Hours",offerBook.getBuyOffers().peek().getAsset().getName());
        assertEquals(OfferType.BUY,offerBook.getBuyOffers().peek().getType());
        assertEquals(10,offerBook.getBuyOffers().peek().getQuantity());
        assertEquals(5,offerBook.getBuyOffers().peek().getPrice());
        assertEquals("IT",offerBook.getBuyOffers().peek().getOrganisationalUnit().getName());

    }



    @Test
    public void match_orders_with_lower_sell_price(){

        Offer buyOffer = new Offer();
        buyOffer.setType(OfferType.BUY);
        buyOffer.setQuantity(10);
        buyOffer.setPrice(10);
        buyOffer.setId(1L);
        buyOffer.setOrganisationalUnit(finance);
        buyOffer.setAsset(cpuHours);
        buyOffer.setTimestamp(LocalDateTime.of(2021, Month.JANUARY,4,10,1));

        Offer sellOffer = new Offer();
        sellOffer.setType(OfferType.SELL);
        sellOffer.setId(2L);
        sellOffer.setQuantity(10);
        sellOffer.setPrice(9);
        sellOffer.setOrganisationalUnit(it);
        sellOffer.setAsset(cpuHours);
        sellOffer.setTimestamp(LocalDateTime.now());

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(100);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(finance);

        OrganisationalUnitAsset financeCpuHours = new OrganisationalUnitAsset();
        financeCpuHours.setQuantity(100);
        financeCpuHours.setAsset(cpuHours);
        financeCpuHours.setOrganisationalUnit(it);

        when(organisationalUnitAssetService.getOrganisationalUnitAsset(it,cpuHours)).thenReturn(itCpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName("Finance")).thenReturn(finance);

        OfferBook offerBook = new OfferBook(1L);

        offerBook.addOfferHelper(buyOffer);
        offerBook.addOfferHelper(sellOffer);


        // Check buy offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(1L).getAsset().getName());
        assertEquals(OfferType.BUY,offerBook.getFilledOffers().get(1L).getType());
        assertEquals(10,offerBook.getFilledOffers().get(1L).getPrice());



        // Check sell offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(2L).getAsset().getName());
        assertEquals(OfferType.SELL,offerBook.getFilledOffers().get(2L).getType());
        assertEquals(10,offerBook.getFilledOffers().get(2L).getPrice());

    }

    @Test
    public void complete_partially_filled_sell_order(){

        Offer buyOffer = new Offer();
        buyOffer.setType(OfferType.BUY);
        buyOffer.setQuantity(10);
        buyOffer.setPrice(5);
        buyOffer.setId(1L);
        buyOffer.setOrganisationalUnit(finance);
        buyOffer.setAsset(cpuHours);
        buyOffer.setTimestamp(LocalDateTime.of(2021, Month.JANUARY,4,10,1));

        Offer sellOffer = new Offer();
        sellOffer.setType(OfferType.SELL);
        sellOffer.setId(2L);
        sellOffer.setQuantity(30);
        sellOffer.setPrice(5);
        sellOffer.setOrganisationalUnit(it);
        sellOffer.setAsset(cpuHours);
        sellOffer.setTimestamp(LocalDateTime.now());

        Offer buyOffer2 = new Offer();
        buyOffer2.setType(OfferType.BUY);
        buyOffer2.setQuantity(10);
        buyOffer2.setPrice(5);
        buyOffer2.setId(3L);
        buyOffer2.setOrganisationalUnit(finance);
        buyOffer2.setAsset(cpuHours);
        buyOffer2.setTimestamp(LocalDateTime.now());

        OrganisationalUnitAsset itCpuHours = new OrganisationalUnitAsset();
        itCpuHours.setQuantity(500);
        itCpuHours.setAsset(cpuHours);
        itCpuHours.setOrganisationalUnit(finance);

        OrganisationalUnitAsset financeCpuHours = new OrganisationalUnitAsset();
        financeCpuHours.setQuantity(500);
        financeCpuHours.setAsset(cpuHours);
        financeCpuHours.setOrganisationalUnit(it);

        when(organisationalUnitAssetService.getOrganisationalUnitAsset(it,cpuHours)).thenReturn(itCpuHours);
        when(organisationalUnitService.getOrganisationalUnitByName("Finance")).thenReturn(finance);

        OfferBook offerBook = new OfferBook(1L);

        offerBook.addOfferHelper(buyOffer);
        offerBook.addOfferHelper(sellOffer);

        // Check buy offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(1L).getAsset().getName());
        assertEquals(OfferType.BUY,offerBook.getFilledOffers().get(1L).getType());
        assertEquals(10,offerBook.getFilledOffers().get(1L).getQuantity());


        // Check sell offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(2L).getAsset().getName());
        assertEquals(OfferType.SELL,offerBook.getFilledOffers().get(2L).getType());
        assertEquals(10,offerBook.getFilledOffers().get(2L).getQuantity());


        // Check partial offer is still in offers queue
        assertEquals(20,offerBook.getSellOffers().peek().getQuantity());
        assertEquals(OfferType.SELL,offerBook.getSellOffers().peek().getType());

        // Add buy offer to fill partially filled sell offer
        offerBook.addOfferHelper(buyOffer2);

        // Check sell offer is filled correctly
        assertEquals("CPU Hours",offerBook.getFilledOffers().get(2L).getAsset().getName());
        assertEquals(OfferType.SELL,offerBook.getFilledOffers().get(2L).getType());
        assertEquals(20,offerBook.getFilledOffers().get(2L).getQuantity());

    }



}
