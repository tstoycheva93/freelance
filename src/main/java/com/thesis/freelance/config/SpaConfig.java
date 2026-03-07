package com.thesis.freelance.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpaConfig {

    @RequestMapping(value = "/**")
    public ModelAndView forward(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/api/") || path.startsWith("/actuator/") || path.contains(".")) {
            return new ModelAndView("forward:/error", HttpStatus.NOT_FOUND);
        }
        return new ModelAndView("forward:/index.html");
    }
}
