package com.supplyr.supplyr.organisationalUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyr.supplyr.controller.OrganisationalUnitController;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.ErrorDetails;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.service.OrganisationalUnitService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = OrganisationalUnitController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class OrganisationalUnitControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganisationalUnitService organisationalUnitService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PasswordEncoder passwordEncoder;


    private OrganisationalUnit it;
    private OrganisationalUnit finance;

    @BeforeEach
    public void setUp() {

        it = new OrganisationalUnit();
        it.setId(1L);
        it.setCredits(250);
        it.setName("IT");

        finance = new OrganisationalUnit();
        finance.setId(2L);
        finance.setName("Finance");
        finance.setCredits(300);
    }




    @Test
    public void createOrganisationalUnit() throws Exception {


        OrganisationalUnit organisationalUnitRequest = new OrganisationalUnit();
        organisationalUnitRequest.setName("IT");
        organisationalUnitRequest.setCredits(250);

        when(organisationalUnitService.createOrganisationalUnit(any(OrganisationalUnit.class))).thenReturn(it);

        MvcResult result = this.mockMvc.perform(post("/api/v1/organisational-unit")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"IT\",\n" +
                        "    \"credits\": 250\n" +
                        "}")
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        String responseAsString = result.getResponse().getContentAsString();
        OrganisationalUnit objectResponse = objectMapper.readValue(responseAsString, OrganisationalUnit.class);

        assertEquals("IT", objectResponse.getName());
    }

    @Test
    public void create_unit_that_already_exists() throws Exception {


        OrganisationalUnit organisationalUnitRequest = new OrganisationalUnit();
        organisationalUnitRequest.setName("IT");
        organisationalUnitRequest.setCredits(250);

        when(organisationalUnitService.createOrganisationalUnit(any(OrganisationalUnit.class))).thenThrow(new AlreadyExistsException("Organisational Unit IT already exists"));

        MvcResult result = mockMvc.perform(post("/api/v1/organisational-unit")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"IT\",\n" +
                        "    \"credits\": 250\n" +
                        "}")
        )

                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn();


        String responseAsString = result.getResponse().getContentAsString();
        ErrorDetails objectResponse = objectMapper.readValue(responseAsString, ErrorDetails.class);

        assertEquals(HttpStatus.CONFLICT, objectResponse.getStatus());
    }


    @Test
    public void create_unit_with_invalid_request() throws Exception {


        OrganisationalUnit organisationalUnitRequest = new OrganisationalUnit();
        organisationalUnitRequest.setName("IT");
        organisationalUnitRequest.setCredits(250);

        when(organisationalUnitService.createOrganisationalUnit(any(OrganisationalUnit.class)))
                .thenThrow(new BadRequestException("Invalid request"));

        MvcResult result = mockMvc.perform(post("/api/v1/organisational-unit")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"namee\": \"IT\",\n" +
                        "    \"credits\": 250\n" +
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
    public void get_list_of_organisational_units() throws Exception {

        OrganisationalUnit organisationalUnitRequest = new OrganisationalUnit();
        organisationalUnitRequest.setName("IT");
        organisationalUnitRequest.setCredits(250);

        List<OrganisationalUnit> organisationalUnits = new ArrayList<>();
        organisationalUnits.add(it);
        organisationalUnits.add(finance);

        when(organisationalUnitService.getOrganisationalUnits()).thenReturn(organisationalUnits);

        MvcResult result = mockMvc.perform(get("/api/v1/organisational-unit")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();

        OrganisationalUnit[] objectResponse = objectMapper.readValue(responseAsString, OrganisationalUnit[].class);


        assertEquals("IT", objectResponse[0].getName());
        assertEquals("Finance", objectResponse[1].getName());
    }

    @Test
    public void get_organisational_units_by_name() throws Exception {

        OrganisationalUnit organisationalUnitRequest = new OrganisationalUnit();
        organisationalUnitRequest.setName("IT");
        organisationalUnitRequest.setCredits(250);

        when(organisationalUnitService.getOrganisationalUnitByName("IT")).thenReturn(it);

        MvcResult result = mockMvc.perform(get("/api/v1/organisational-unit/IT")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();

        OrganisationalUnit objectResponse = objectMapper.readValue(responseAsString, OrganisationalUnit.class);

        assertEquals("IT", objectResponse.getName());
    }

    @Test
    public void get_non_existent_organisational_units_by_name() throws Exception {

        when(organisationalUnitService.getOrganisationalUnitByName("Management"))
                .thenThrow(new NotFoundException("Could not find Organisational Unit Management"));

        MvcResult result = mockMvc.perform(get("/api/v1/organisational-unit/Management")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();
        ErrorDetails objectResponse = objectMapper.readValue(responseAsString, ErrorDetails.class);

        assertEquals("Could not find Organisational Unit Management", objectResponse.getMessage());

    }
}
