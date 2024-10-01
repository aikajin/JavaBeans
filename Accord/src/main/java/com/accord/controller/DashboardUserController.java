package com.accord.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.accord.repository.UserRepository;
import com.accord.service.UserService;

@Controller
@RequestMapping("/Dashboard")
public class DashboardUserController {
	@Autowired
	private UserService userSerivce;
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/Dashboard")
	public String dashboardHome() {
		return "dashboard_user";
	}
}
