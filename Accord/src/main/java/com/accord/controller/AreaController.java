package com.accord.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.accord.service.AreaService;

public class AreaController {
     private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    /*@GetMapping("/delete/{id}")
    public String deleteArea(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        areaService.deleteArea(id);  // This method in AreaService should handle area deletion by ID
        redirectAttributes.addFlashAttribute("message", "Area deleted successfully!");
        return "redirect:/areas";  // Redirects back to the areas listing page
    }*/
}
