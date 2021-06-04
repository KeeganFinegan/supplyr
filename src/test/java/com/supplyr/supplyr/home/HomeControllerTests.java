package com.supplyr.supplyr.home;

import com.supplyr.supplyr.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = HomeController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void redirects_to_index_route_login() throws Exception {
        MvcResult result = mockMvc.perform(get("/login")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void redirects_to_index_route_home() throws Exception {
        MvcResult result = mockMvc.perform(get("/home")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void redirects_to_index_route_account() throws Exception {
        MvcResult result = mockMvc.perform(get("/account")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void redirects_to_index_route_admin() throws Exception {
        MvcResult result = mockMvc.perform(get("/admin")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void returns_404_for_non_specified_route() throws Exception {
        MvcResult result = mockMvc.perform(get("/non-specified")
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }


}
