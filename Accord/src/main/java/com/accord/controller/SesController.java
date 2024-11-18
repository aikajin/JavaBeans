package com.accord.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.accord.repository.UserRepository;

@Controller
public class SesController {


    @Autowired
    UserRepository repo;
    @GetMapping("/getUserdeets")
    public String getUserdeets(Model m){
        m.addAttribute("error",repo.findAll());
        return "error_page";

    }
    
}
