package com.supplyr.supplyr.organisationalUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyr.supplyr.controller.OrganisationalUnitController;
import com.supplyr.supplyr.domain.OrganisationalUnit;
import com.supplyr.supplyr.exception.AlreadyExistsException;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.ErrorDetails;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.jwt.JwtConfiguration;
import com.supplyr.supplyr.service.OrganisationalUnitService;
import com.supplyr.supplyr.service.SupplyrUserDetailsService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrganisationalUnitController.class)
public class OrganisationalUnitControllerWithSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    private OrganisationalUnitService organisationalUnitService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    SecretKey secretKey;

    @MockBean
    JwtConfiguration jwtConfiguration;

    @MockBean
    SupplyrUserDetailsService supplyrUserDetailsService;



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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
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

    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void createOrganisationalUnitAsUser() throws Exception {

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
                .andExpect(status().isForbidden())
                .andReturn();

    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
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

}