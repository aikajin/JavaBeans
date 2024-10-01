package com.accord.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.accord.Entity.Admin;
import com.accord.Entity.User;
import com.accord.service.AdminService;
import com.accord.service.UserService;

@Controller
@RequestMapping("/")
public class RegisterLoginController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private AdminService adminService;
	
	@GetMapping("/")
	public String index() {
		return "login_page";
	}
	
	@GetMapping("/register")
	public String getRegisterPage(Model model) {
		model.addAttribute("registerRequest", new User());
		model.addAttribute("registerRequest", new Admin());
		return "register_page";
	}
	
	@GetMapping("/login")
	public String getLoginPage(Model model) {
		model.addAttribute("loginRequest", new User());
		model.addAttribute("loginRequest", new Admin());
		return "login_page";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute User user, @RequestParam("file") MultipartFile tenancy, MultipartFile valid, Admin admin, Model model) throws IOException {
		Boolean checkAdmin = adminService.checkEmail(admin.getEmail(), admin.getPassword());
		if(checkAdmin == null) {
			return "duplicate_page";
		}
		else {
			User registeredUser = userService.registerUser(user.getName(), user.getPassword(), user.getEmail(), user.getContactnumber(),
					user.getBlock_num(), user.getLot_num(), user.getProperty_status(), tenancy, valid);
			return registeredUser == null ? "error_page" : "redirect:/login";
		}
	}
	
	@PostMapping("/login")
	public String login(@ModelAttribute User user, Admin admin, Model model) {
		System.out.println("login request" + user);
		User authenticatedUser = userService.authenticate(user.getEmail(), user.getPassword());
		Admin authenticatedAdmin = adminService.authenticate(admin.getEmail(), admin.getPassword());
		if(authenticatedUser != null) {
			model.addAttribute("userLogin", authenticatedUser.getEmail());
			return "dashboard_user";
		}
		else if(authenticatedAdmin != null) {
			model.addAttribute("adminLogin", authenticatedAdmin.getEmail());
			return "dashboard_admin";
		}
		else {
			return "error_page";
		}
	}
	
	@GetMapping("/dashboard_admin")
	public String allUsers(@ModelAttribute("form") User form, Model model) {
		List<User> users = userService.getAllUser();
		model.addAttribute("result", users);
		return "viewusers_page";
	}
}