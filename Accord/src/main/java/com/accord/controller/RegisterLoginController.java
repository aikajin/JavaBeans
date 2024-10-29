package com.accord.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.accord.Entity.Area;
import com.accord.Entity.User;
import com.accord.service.AreaService;
import com.accord.service.UserService;

@Controller
@RequestMapping("/")
public class RegisterLoginController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private AreaService areaService;
	
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
		List<Area> areaList = areaService.getAllAreas();
		model.addAttribute("areaList", areaList);
		return "am_recreationalAreasList";
	}

	@GetMapping("/recreationalAreasList-user")
	public String recreationalAreasListUser(Model model) {
		// Add attributes to the model if needed for profile management
		List<Area> areaList = areaService.getAllAreas();
		model.addAttribute("areaList", areaList);
		return "am_recreationalAreasList_user";
	}

	@GetMapping("/view-recre-area-basketball-user")
	public String recreationalBasketballUser(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recre_area_basketball_user";
	}

	@GetMapping("/view-recre-area-clubhouse-user")
	public String recreationalClubhouseUser(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recre_area_clubhouse_user";
	}

	@GetMapping("/view-recre-area-coveredcourt-user")
	public String recreationalCoveredCourteUser(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recre_area_coveredcourt_user";
	}

	@GetMapping("/view-recre-area-hall-user")
	public String recreationalHallUser(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recre_area_hall_user";
	}

	@GetMapping("/view-recre-area-tenniscourt-user")
	public String recreationalTennisCourtUser(Model model) {
		// Add attributes to the model if needed for profile management
		return "view_recre_area_tenniscourt_user";
	}

	@GetMapping("/view-recreational-area-user")
	public String recreationalSwmmingPoolUser(Model model) {
		// Add attributes to the model if needed for profile management
		List<Area> areaList = areaService.getAllAreas();
		model.addAttribute("areaList", areaList);
		return "view_recreational_area_user";
	}

	@GetMapping("/view-recreational-area/{id}")
	public String viewRecreationalArea(@PathVariable Long id, Model model) {
		// Add attributes to the model if needed for profile management
		Area area = areaService.getAreaById(id);
		model.addAttribute("area", area);
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
		model.addAttribute("area", new Area());
		//areaService.createArea(area);
		return "addNewRecreational_area";
	}
	
	@PostMapping("/add-area")
    public String addArea(Area area, @RequestParam("fileCover") MultipartFile cover, @RequestParam("fileAdd") MultipartFile add) {
        areaService.createArea(area, cover, add);
        return "view_recreational_area";
    }

	@GetMapping("/modifyrec_admin/{id}")
	public String showModify(@PathVariable Long id, Model model) {
		Area area = areaService.getAreaById(id);
		model.addAttribute("area", area);
		return "modifyDelete_area";
	}
	
	@PostMapping("/modifyrec_admin/{id}")
	public String modifyRecreationalArea(@ModelAttribute("area") Area area, @PathVariable Long id, @RequestParam("fileCover") MultipartFile cover, @RequestParam("fileAdd") MultipartFile add, Model model) throws IOException {
		//Area areaEdit = areaService.getAreaById(id);
		if(add.isEmpty()) {
			areaService.createArea(area, cover, add);
		}
		else {
			areaService.createArea2(area, cover);
		}
		model.addAttribute("area", area);
		return "am_recreationalAreasList";
	}

	/*@GetMapping("/deleteArea/{id}")
	public String deleteArea(@PathVariable Long id, Model model) {
		model.addAttribute("delete", "Are you sure you would like to delete this area?");
		areaService.deleteArea(id);
		
		return "am_recreationalAreasList";
	}*/

	@GetMapping("/deleteArea/{id}")
    public RedirectView deleteArea(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        areaService.deleteArea(id);
        redirectAttributes.addFlashAttribute("message", "Area deleted successfully");
        return new RedirectView("/recreational-areas-list", true);
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
public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
model.addAttribute("loginRequest", new User());
model.addAttribute("token", token); // Add token to the model
return "forgotPassword_setPass";
}
@PostMapping("/forgotPassword_setPass")
public String setPassword(@RequestParam("token") String token,
					  @RequestParam("newPassword") String newPassword,
					  @RequestParam("confirmPassword") String confirmPassword,
					  Model model) {
if (!newPassword.equals(confirmPassword)) {
	model.addAttribute("error", "Passwords do not match.");
	return "forgotPassword_setPass"; 
}

boolean success = userService.resetPassword(token, newPassword);
if (success) {
	return "redirect:/?resetSuccess=true"; // Redirect to login page on success
} else {
	model.addAttribute("error", "An error occurred. Please try again.");
	return "forgotPassword_setPass"; 
}
}
}