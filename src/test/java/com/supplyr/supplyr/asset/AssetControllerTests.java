package com.supplyr.supplyr.asset;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyr.supplyr.controller.AssetController;
import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.ErrorDetails;
import com.supplyr.supplyr.service.AssetService;
import com.supplyr.supplyr.service.OfferService;
import com.supplyr.supplyr.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AssetController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class AssetControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeService tradeService;

    @MockBean
    private AssetService assetService;

    @MockBean
    private OfferService offerService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrganisationalUnit it;
    private Asset cpuHours;
    private Asset softwareLicense;

    @BeforeEach
    public void setUp() {

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

        MvcResult result = mockMvc.perform(post("/api/v1/assets")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"CPU Hours\"\n" +
                        "}")
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        String responseAsString = result.getResponse().getContentAsString();
        Asset objectResponse = objectMapper.readValue(responseAsString, Asset.class);

        assertEquals("CPU Hours", objectResponse.getName());

    }

    @Test
    public void create_new_asset_type_invalid_request_body() throws Exception {


        when(assetService.addAssetType(any(Asset.class))).thenThrow(new BadRequestException("Invalid request"));

        MvcResult result = mockMvc.perform(post("/api/v1/assets")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"namfe\": \"CPU Hours\"\n" +
                        "}")
        )

                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();


        String responseAsString = result.getResponse().getContentAsString();
        ErrorDetails objectResponse = objectMapper.readValue(responseAsString, ErrorDetails.class);

        assertEquals(HttpStatus.BAD_REQUEST, objectResponse.getStatus());

    }


    @Test
    public void get_all_assets() throws Exception {

        List<Asset> assetList = new ArrayList<>();
        assetList.add(softwareLicense);
        assetList.add(cpuHours);

        when(assetService.getAllAssets()).thenReturn(assetList);

        MvcResult result = mockMvc.perform(get("/api/v1/assets")

        )

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        String responseAsString = result.getResponse().getContentAsString();
        Asset[] objectResponse = objectMapper.readValue(responseAsString, Asset[].class);

        assertEquals("Software License", objectResponse[0].getName());
        assertEquals("CPU Hours", objectResponse[1].getName());

    }

    @Test
    public void allocate_asset_to_organisational_unit() throws Exception {

        OrganisationalUnitAsset request = new OrganisationalUnitAsset();
        request.setQuantity(10);
        request.setAsset(cpuHours);
        request.setOrganisationalUnit(it);
        when(assetService.addOrganisationalUnitAsset(any(OrganisationalUnitAssetDto.class))).thenReturn(request);

        MvcResult result = mockMvc.perform(put("/api/v1/assets")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"organisationalUnitName\": \"IT\",\n" +
                        "    \"assetName\": \"CPU Hours\",\n" +
                        "    \"quantity\": 10\n" +
                        "}")

        )

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        String responseAsString = result.getResponse().getContentAsString();
        OrganisationalUnitAsset objectResponse = objectMapper.readValue(responseAsString, OrganisationalUnitAsset.class);

        assertEquals("CPU Hours", objectResponse.getAsset().getName());

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

        when(tradeService.getAssetTrades("IT")).thenReturn(tradeList);
        MvcResult result = mockMvc.perform(get("/api/v1/assets/IT/trades"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        Trade[] objectResponse = objectMapper.readValue(responseAsString, Trade[].class);

        assertEquals("CPU Hours",objectResponse[0].getAsset().getName());
        assertEquals(10,objectResponse[0].getPrice());
    }

    @Test
    public void get_lowest_ask_highest_bid() throws Exception {

        List<Double> lowestAskHighestBid = new ArrayList<>();
        lowestAskHighestBid.add(19.0);
        lowestAskHighestBid.add(20.5);

        when(offerService.getLowestAskAndHighestBid("CPU Hours")).thenReturn(lowestAskHighestBid);
        MvcResult result = mockMvc.perform(get("/api/v1/assets/CPU Hours/offer-info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        LowestAskHighestBidDto objectResponse = objectMapper.readValue(responseAsString, LowestAskHighestBidDto.class);

        assertEquals(19.0,objectResponse.getLowestAsk());
        assertEquals(20.5,objectResponse.getHighestBid());

    }

}
