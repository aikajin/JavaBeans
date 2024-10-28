package com.accord.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.accord.service.AreaService;
import com.accord.service.UserService;
import com.accord.Entity.Area;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
//@RequestMapping("/dash_admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private AreaService areaService;

    /*@GetMapping("/add-area")
    public String addArea() {
        return "/addNewRecreational_area";
    }
    
    @PostMapping("/add-area")
    public String addArea(Area area) {
        areaService.createArea(area);
        return "/addNewRecreational_area";
    }*/
    
}
