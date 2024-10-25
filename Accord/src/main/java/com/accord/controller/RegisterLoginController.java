package com.accord.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
		return "test";
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
		
		if (authenticateUser) {
			// Use Optional to safely get the User object
			Optional<User> optionalUser = userService.findByEmail(currentEmail);
	
			if (optionalUser.isPresent()) {
				User currectUser = optionalUser.get(); // Get the actual User object
				if (currectUser.getRole().contains("ROLE_USER")) {
					return "dashboard_user";
				} else {
					return "dashboard_admin";
				}
			} else {
				model.addAttribute("error", "User not found");
				return "login_page"; // Return to login page if user not found
			}
		}
		return "login_page"; // Return to login page if authentication fails
	}

    @GetMapping("/dash_user")
    public String showDashboard() {
        return "dashboard_user";
    }
	@GetMapping("/dash_admin")
    public String showDashboardAdmin() {
        return "dashboard_admin";
    }
    
	@GetMapping("/dashboard_admin")
	public String allUsers(@ModelAttribute("form") User form, Model model) {
		List<User> users = userService.getAllUser();
		model.addAttribute("result", users);
		return "viewusers_page";
	}

	@GetMapping("/forgotPassword_page")
    public String showForgotPasswordPage(Model model) {
        // Add "resetRequest" to the model for form binding
        model.addAttribute("resetRequest", new User());
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

	@GetMapping("/view-recre-area-basketball")
	public String viewRecreAreaBasketball(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recre_area_basketball";
	}

	@GetMapping("/view-recre-area-clubhouse")
	public String viewRecreAreaClubhouse(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recre_area_clubhouse";
	}

	@GetMapping("/view-recre-area-coveredcourt")
	public String viewRecreAreaCoveredCourt(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recre_area_coveredcourt";
	}

	@GetMapping("/view-recre-area-hall")
	public String viewRecreAreaHall(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recre_area_hall";
	}

	@GetMapping("/view-recre-area-tenniscourt")
	public String viewRecreAreaTennisCourt(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recre_area_tenniscourt";
	}

	@GetMapping("/useracc_admin")
	public String UserAccountsAdmin(Model model) {
		// Add attributes to the model if needed for profile management
		return "UserAccounts_page";
	}

	@GetMapping("/add_area")
	public String addRecreationalArea(Model model) {
		// Add attributes to the model if needed for profile management
		return "addNewRecreational_area";
	}

	@GetMapping("/modifyrec_admin")
	public String modifyRecreationalArea(Model model) {
		return "modifyDelete_area";
	}

	// @GetMapping("/details")
	// public String UserAcc_VIewDetails(Model model) {
	// 	return "UserAccounts_viewDetails";
	// }


	@PostMapping("/forgotPassword_email")
public String processForgotPassword(@ModelAttribute("resetRequest") User resetRequest, Model model) {
    // Retrieve the User object based on the email
    User user = userService.findByEmail(resetRequest.getEmail()).orElse(null);
    
    if (user != null) {
        // Now pass the User object to sendPasswordResetEmail
        boolean isEmailSent = userService.sendPasswordResetEmail(user);
        
        if (isEmailSent) {
            model.addAttribute("message", "Password reset email sent successfully.");
            return "forgotPassword_email";
        } else {
            model.addAttribute("error", "Failed to send password reset email. Please try again.");
        }
    } else {
        model.addAttribute("error", "No user found with the provided email.");
    }
    
    return "forgotPassword_page"; // Redirect back to the form in case of an error
}
@GetMapping("/forgotPassword_setPass")
public String showResetPasswordPage(Model model) {
    // Add any required model attributes for the form, if needed
    model.addAttribute("loginRequest", new User()); // Assuming User is needed for form binding
    return "forgotPassword_setPass"; // This must match the name of your HTML file without ".html"
}
@PostMapping("/resetpassword")
public String handlePasswordReset(
    @RequestParam("token") String token, 
    @RequestParam("password") String newPassword,
    Model model) {
    
    // Validate the token again and reset the password
    boolean isPasswordReset = userService.resetPassword(token, newPassword);
    
    if (isPasswordReset) {
        model.addAttribute("message", "Password has been successfully reset.");
        return "login_page"; // Redirect to login page after success
    } else {
        model.addAttribute("error", "Failed to reset password. The token might be invalid or expired.");
        return "forgotPassword_setPass"; // Stay on the form if there's an error
    }
}
}