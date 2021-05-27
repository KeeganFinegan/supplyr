package com.supplyr.supplyr.offer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.ErrorDetails;
import com.supplyr.supplyr.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public class OfferControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OfferService offerService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrganisationalUnit it;
    private Asset cpuHours;

    @BeforeEach
    public void setUp() {

        it = new OrganisationalUnit();
        it.setId(1L);
        it.setCredits(10);
        it.setName("IT");

        cpuHours = new Asset();
        cpuHours.setAssetId(1L);
        cpuHours.setName("CPU Hours");
    }


    @Test
    public void addBuyOffer() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();
        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setAsset("CPU Hours");
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setPrice(1);
        buyOffer.setQuantity(5);

        Offer approvedOffer = new Offer();
        approvedOffer.setPrice(1);
        approvedOffer.setQuantity(5);
        approvedOffer.setAsset(cpuHours);
        approvedOffer.setId(1L);
        approvedOffer.setOrganisationalUnit(it);
        approvedOffer.setFulfilled(false);
        approvedOffer.setTimestamp(timestamp);
        approvedOffer.setType(OfferType.BUY);

        when(offerService.addBuyOffer(any(OfferRequest.class))).thenReturn(approvedOffer);

        MvcResult result = mockMvc.perform(post("/api/v1/offers/buy")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"organisationalUnit\": \"IT\",\n" +
                        "    \"asset\": \"CPU Hours\",\n" +
                        "    \"quantity\": 5,\n" +
                        "    \"price\": 1\n" +
                        "}")
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();

        Offer objectResponse = objectMapper.readValue(responseAsString, Offer.class);

        assertEquals(approvedOffer.getAsset().getName(), objectResponse.getAsset().getName());
        assertEquals(approvedOffer.getPrice(), objectResponse.getPrice());
        assertEquals(approvedOffer.getOrganisationalUnit().getName(), objectResponse.getOrganisationalUnit().getName());
        assertEquals(approvedOffer.getQuantity(), objectResponse.getQuantity());
        assertEquals(OfferType.BUY, objectResponse.getType());


    }

    @Test
    public void addBuyOfferWithNotEnoughCredits() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();
        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setAsset("CPU Hours");
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setPrice(1);
        buyOffer.setQuantity(5);

        Offer approvedOffer = new Offer();
        approvedOffer.setPrice(1);
        approvedOffer.setQuantity(11);
        approvedOffer.setAsset(cpuHours);
        approvedOffer.setId(1L);
        approvedOffer.setOrganisationalUnit(it);
        approvedOffer.setFulfilled(false);
        approvedOffer.setTimestamp(timestamp);
        approvedOffer.setType(OfferType.BUY);

        when(offerService.addBuyOffer(any(OfferRequest.class))).thenThrow(new BadRequestException("Insufficient funds to complete BUY offer"));

        MvcResult result = mockMvc.perform(post("/api/v1/offers/buy")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"organisationalUnit\": \"IT\",\n" +
                        "    \"asset\": \"CPU Hours\",\n" +
                        "    \"quantity\": 11,\n" +
                        "    \"price\": 1\n" +
                        "}")
        )

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();

        ErrorDetails objectResponse = objectMapper.readValue(responseAsString, ErrorDetails.class);

        assertEquals(HttpStatus.BAD_REQUEST, objectResponse.getStatus());
        assertEquals("Insufficient funds to complete BUY offer", objectResponse.getMessage());


    }

    @Test
    public void addSellOfferWithNotEnoughAssets() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();
        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setAsset("CPU Hours");
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setPrice(1);
        buyOffer.setQuantity(20);

        Offer approvedOffer = new Offer();
        approvedOffer.setPrice(1);
        approvedOffer.setQuantity(11);
        approvedOffer.setAsset(cpuHours);
        approvedOffer.setId(1L);
        approvedOffer.setOrganisationalUnit(it);
        approvedOffer.setFulfilled(false);
        approvedOffer.setTimestamp(timestamp);
        approvedOffer.setType(OfferType.SELL);

        when(offerService.addSellOffer(any(OfferRequest.class))).thenThrow(new BadRequestException("Not enough CPU Hours to complete the offer request"));

        MvcResult result = mockMvc.perform(post("/api/v1/offers/sell")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"organisationalUnit\": \"IT\",\n" +
                        "    \"asset\": \"CPU Hours\",\n" +
                        "    \"quantity\": 11,\n" +
                        "    \"price\": 1\n" +
                        "}")
        )

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();

        ErrorDetails objectResponse = objectMapper.readValue(responseAsString, ErrorDetails.class);

        assertEquals(HttpStatus.BAD_REQUEST, objectResponse.getStatus());
        assertEquals("Not enough CPU Hours to complete the offer request", objectResponse.getMessage());


    }

    @Test
    public void addSellOffer() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();
        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setAsset("CPU Hours");
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setPrice(1);
        buyOffer.setQuantity(5);

        Offer approvedOffer = new Offer();
        approvedOffer.setPrice(1);
        approvedOffer.setQuantity(5);
        approvedOffer.setAsset(cpuHours);
        approvedOffer.setId(1L);
        approvedOffer.setOrganisationalUnit(it);
        approvedOffer.setFulfilled(false);
        approvedOffer.setTimestamp(timestamp);
        approvedOffer.setType(OfferType.SELL);

        when(offerService.addSellOffer(any(OfferRequest.class))).thenReturn(approvedOffer);

        MvcResult result = mockMvc.perform(post("/api/v1/offers/sell")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"organisationalUnit\": \"IT\",\n" +
                        "    \"asset\": \"CPU Hours\",\n" +
                        "    \"quantity\": 5,\n" +
                        "    \"price\": 1\n" +
                        "}")
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        Offer objectResponse = objectMapper.readValue(responseAsString, Offer.class);
        assertEquals(approvedOffer.getAsset().getName(), objectResponse.getAsset().getName());
        assertEquals(approvedOffer.getPrice(), objectResponse.getPrice());
        assertEquals(approvedOffer.getOrganisationalUnit().getName(), objectResponse.getOrganisationalUnit().getName());
        assertEquals(approvedOffer.getQuantity(), objectResponse.getQuantity());
        assertEquals(OfferType.SELL, objectResponse.getType());
    }

    @Test
    public void deleteOffer() throws Exception {

        doNothing().when(offerService).deleteOfferById(1L);

        mockMvc.perform(post("/api/v1/offers/delete/1")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

    }


}
