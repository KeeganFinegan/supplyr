package com.supplyr.supplyr.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebConfig {


        /**
         * If the user refreshes the page while on a React route, the request will come here.
         * We need to tell it that there isn't any special page, just keep using React, by
         * forwarding it back to the root.
         */
        @RequestMapping({"/login", "/account", "/admin", "/home"})
        public String forward(HttpServletRequest httpServletRequest) {
            return "forward:/";
        }


}
