package com.example.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class MainController {
    
    @GetMapping("/index")
    public ModelAndView indexPage() {
        return new ModelAndView("index");
    }
    
}
