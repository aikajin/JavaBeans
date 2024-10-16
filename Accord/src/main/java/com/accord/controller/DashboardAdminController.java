package com.accord.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.accord.Entity.User;
import com.accord.service.AdminService;

@Controller
//@RequestMapping("dashboard_admin")
public class DashboardAdminController {
	@Autowired
	private AdminService adminService;
	
	@GetMapping("dashboard_admin")
	public String homeAdmin() {
		return "dashboard_admin";
	}
	@RequestMapping("userlist")
	public String listUsers(Model model) {
		model.addAttribute("userlist", adminService.listAll());
		return "userlist";
	}
	
	@GetMapping("edit{id}")
	public String editUserForm(@PathVariable Long id, Model model) {
        User user = adminService.getUserById(id);
        model.addAttribute("userlist", user);
        return "edit";
	}
	
	@PostMapping("edit{id}")
	public String editUser(@PathVariable Long id, @ModelAttribute("user") User user){
		User existUser = adminService.getUserById(id);
		if(existUser != null) {
            existUser.setName(user.getName());
            existUser.setEmail(user.getEmail());
            adminService.saveUser(existUser);
		}
		return "redirect:/userlist";
	}
	
	@GetMapping("/delete")
	public String deleteUser(@PathVariable Long id) {
		adminService.deleteUserById(id);
		return "redirect:/userlist";
	}
}
