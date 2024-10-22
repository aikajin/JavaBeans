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
	public String index() {
		return "login_page";
	}

	@GetMapping("/register")
	public String getRegisterPage(Model model) {
		model.addAttribute("registerRequest", new User());;
		return "register_page";
	}
	@GetMapping("/register_page_admin")
	public String getRegisterPageAdmin(Model model) {
		model.addAttribute("registerRequest", new User());
		return "register_page_admin";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute User user, @RequestParam("file") MultipartFile tenancy, MultipartFile valid, Model model) throws IOException {
		Boolean checkUserEmail = userService.checkEmail(user.getEmail());
		Boolean checkUserPhone = userService.checkPhone(user.getContactnumber());
		if(checkUserEmail == null || checkUserPhone == null) {
			model.addAttribute("error", "Duplicate email/Contact Number");
			return "register_page";
		}
		else {
			User registeredUser = userService.registerUser(user.getName(), user.getPassword(), user.getEmail(), user.getContactnumber(),
					user.getBlock_num(), user.getLot_num(), user.getProperty_status(), tenancy, valid, "ROLE_USER");
			model.addAttribute("error", "Duplicate email/phone number");
			return registeredUser == null ? "register_page" : "redirect:/";
		}
	}
	@PostMapping("/register_page_admin")
	public String registerAdmin(@ModelAttribute User user, Model model) throws IOException {
		Boolean checkUser = userService.checkEmail(user.getEmail());
		if(checkUser == null) {
			model.addAttribute("error", "Duplicate email");
			return "register_page";
		}
		else {
			User adminRegister = userService.registerAdmin(user.getEmail(), user.getPassword(), "ROLE_ADMIN");
			//model.addAttribute("AdminRegister", new Admin());
			model.addAttribute("error", "Duplicate email");
			return adminRegister == null ? "redirect:/register_page_admin" : "redirect:/";
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
		//User currectUser = userService.findByEmail(currentEmail);
		if(authenticateUser == true) {
			//String currentEmail = user.getEmail();
			User currectUser = userService.findByEmail(currentEmail);
			if(currectUser.getRole().contains("ROLE_USER")){
				return "dashboard_user";
			}
			else {
				return "dashboard_admin";
			}
		}
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
	@GetMapping("/dash_admin")
	public String dashboardAdmin(Model model) {
		// Add attributes to the model if needed for profile management
		return "dashboard_admin";
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

	@GetMapping("/modifyrec_admin")
	public String modifyRecreationalArea(Model model) {
		// Add attributes to the model if needed for profile management
		return "modifyDelete_area";
	}
	@GetMapping("/useracc_admin")
	public String UserAccountsp(Model model) {
		// Add attributes to the model if needed for profile management
		return "UserAccounts_page";
	}


	@PostMapping("/forgotPassword_email")
	public String showForgotPasswordEmailPage() {
        return "forgotPassword_email"; 
    }
}
