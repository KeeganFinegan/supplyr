package com.supplyr.supplyr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    /**
     * Allows routing to be performed on the client side by React. Attempts to reach
     * these specified routes will forward the user to the root where React routes from
     *
     * @return pushes browser to index root
     */
    @RequestMapping({"/login", "/account", "/admin", "/home"})
    public String forward(HttpServletRequest httpServletRequest) {
        return "forward:/";
    }

}
