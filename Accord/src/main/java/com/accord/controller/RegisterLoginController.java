package com.accord.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.accord.Entity.User;
import com.accord.service.UserService;

@Controller
@RequestMapping("/")
public class RegisterLoginController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/")
	public String getLoginPage() {
		return "login_page";
	}

	@GetMapping("/register")
	public String getRegisterPage() {
		return "register_page";
	}
	@GetMapping("/register_page_admin")
	public String getRegisterPageAdmin() {
		return "register_page_admin";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute User user, @RequestParam("file") MultipartFile tenancy, MultipartFile valid, Model model) throws IOException {
		Boolean checkUserEmail = userService.checkEmail(user.getEmail());
		Boolean checkUserPhone = userService.checkPhone(user.getContactnumber());
		if(checkUserEmail == null) {
			model.addAttribute("error", "Duplicate Email"); //model.addAttribute("variablename", "variableMessage");
			return "register_page"; //Ilisda lang ang message
		}
		else if (checkUserPhone == null){
			model.addAttribute("error", "Duplicate Contact Number"); //model.addAttribute("variablename", "variableMessage");
			return "register_page";
		}
		else {
			userService.registerUser(user.getName(), user.getPassword(), user.getEmail(), user.getContactnumber(),
					user.getBlock_num(), user.getLot_num(), user.getProperty_status(), tenancy, valid, "ROLE_USER");
			return "redirect:/";
		}
	}
	@PostMapping("/register_page_admin")
	public String registerAdmin(@ModelAttribute User user, Model model) throws IOException {
		Boolean checkUser = userService.checkEmail(user.getEmail());
		if(checkUser == null) {
			model.addAttribute("error", "Duplicate email");
			return "register_page_admin";
		}
		else {
			userService.registerAdmin(user.getEmail(), user.getPassword(), "ROLE_ADMIN");
			return "redirect:/";
		}
	}

	@PostMapping("/login")
	public String login(@ModelAttribute User user, Model model) {
		//User authenticatedUser = userService.authenticate(user.getEmail(), user.getPassword());
		//User role = userSerivce.findByEmail(UserService.getcu)
		//Admin authenticatedAdmin = adminService.authenticate(admin.getEmail(), admin.getPassword());
		/*if(authenticatedUser != null) {
			model.addAttribute("userLogin", authenticatedUser.getEmail());
			return "dashboard_user";
		}
		else if(authenticatedAdmin != null) {
			model.addAttribute("adminLogin", authenticatedAdmin.getEmail());
			return "dashboard_admin";
		}
		else {
			model.addAttribute("error", "na error");
			return "login_page";
		}*/
		/*String currentEmail = user.getEmail();
		User userCurrent = userService.findByEmail(currentEmail);
		if(userCurrent.getRole().contains("ROLE_USER")) {
			return "dashboard_user";
		}
		else {
			return "dashboard_admin";
		}*/
		String currentEmail = user.getEmail();
		Boolean authenticateUser = userService.authenticateLogin(user.getEmail(), user.getPassword());
		if(authenticateUser == true) {
			User currectUser = userService.findByEmail(currentEmail);
			if(currectUser.getRole().contains("ROLE_USER")){
				return "dashboard_user";
			}
			else {
				return "dashboard_admin";
			}
		}
		model.addAttribute("error", "Invalid Input!");
		return "login_page";
	}

    @GetMapping("/dashboard_user")
    public String showDashboard() {
        return "dashboard_user";
    }
    
	@GetMapping("/dashboard_admin")
	public String allUsers(@ModelAttribute("form") User form, Model model) {
		List<User> users = userService.getAllUser();
		model.addAttribute("result", users);
		return "viewusers_page";
	}

	@GetMapping("/forgotPassword_page")
    public String showForgotPasswordPage() {
        return "forgotPassword_page";
    }
	
	@GetMapping("/manage-profile")
	public String manageProfile(Model model) {
		// Add attributes to the model if needed for profile management
		return "manage_profile";
	}

	@GetMapping("/recreational-areas-list")
	public String recreationalAreasList(Model model) {
		// Add attributes to the model if needed for profile management
		return "am_recreationalAreasList";
	}

	@GetMapping("/view-recreational-area")
	public String viewRecreationalArea(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recreational_area";
	}

	@GetMapping("/add-recreational-area")
	public String addRecreationalArea(Model model) {
		// Add attributes to the model if needed for profile management
		return "addNewRecreational_area";
	}

	@GetMapping("/modify-recreational-area")
	public String modifyRecreationalArea(Model model) {
		// Add attributes to the model if needed for profile management
		return "modifyDelete_area";
	}

	@PostMapping("/forgotPassword_email")
	public String showForgotPasswordEmailPage() {
        return "forgotPassword_email"; 
    }
}
