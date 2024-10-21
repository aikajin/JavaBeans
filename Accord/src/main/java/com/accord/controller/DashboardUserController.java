package com.accord.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.accord.Entity.User;
import com.accord.repository.UserRepository;
import com.accord.service.UserService;

@Controller
@RequestMapping("/dashboard_user")
public class DashboardUserController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@ModelAttribute
    public void getUserDetails(Principal principal, Model model) {
        if(principal != null) {
            String email = principal.getName();
            User userDetails = userService.findByEmail(email);
            model.addAttribute("user", userDetails);
        }
    }
    @GetMapping("/dashboard_user")
    public String showDashboard(Principal principal, Model model) {
        String username = principal.getName();
        User user = userService.findByEmail(username);
        model.addAttribute("user", user);
        return "dashboard_user";
    }
    
    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user) {
        userService.updateUser(user);
        return "redirect:/dashboard";
    }
}
