package com.accord.controller;

import java.io.IOException;
import java.net.URI;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.accord.Entity.Reservation;
import com.accord.Entity.User;
import com.accord.service.AreaService;
import com.accord.service.ReservService;
import com.accord.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;



import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class RegisterLoginController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private AreaService areaService;

	@Autowired
	private ReservService reservService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
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

	// @PostMapping("/login")
	// public String login(@ModelAttribute User user, Model model) {
	// 	//User authenticatedUser = userService.authenticate(user.getEmail(), user.getPassword());
	// 	//User role = userSerivce.findByEmail(UserService.getcu)
	// 	//Admin authenticatedAdmin = adminService.authenticate(admin.getEmail(), admin.getPassword());
	// 	/*if(authenticatedUser != null) {
	// 		model.addAttribute("userLogin", authenticatedUser.getEmail());
	// 		return "dashboard_user";
	// 	}
	// 	else if(authenticatedAdmin != null) {
	// 		model.addAttribute("adminLogin", authenticatedAdmin.getEmail());
	// 		return "dashboard_admin";
	// 	}
	// 	else {
	// 		model.addAttribute("error", "na error");
	// 		return "login_page";
	// 	}*/
	// 	/*String currentEmail = user.getEmail();
	// 	User userCurrent = userService.findByEmail(currentEmail);
	// 	if(userCurrent.getRole().contains("ROLE_USER")) {
	// 		return "dashboard_user";
	// 	}
	// 	else {
	// 		return "dashboard_admin";
	// 	}*/
	// 	String currentEmail = user.getEmail();
	// 	Boolean authenticateUser = userService.authenticateLogin(user.getEmail(), user.getPassword());
	// 	//User currectUser = userService.findByEmail(currentEmail);
		
	// 	if (authenticateUser) {
	// 		// Use Optional to safely get the User object
	// 		Optional<User> optionalUser = userService.findByEmail(currentEmail);
	
	// 		if (optionalUser.isPresent()) {
	// 			User currectUser = optionalUser.get(); // Get the actual User object
	// 			if (currectUser.getRole().contains("ROLE_USER")) {
	// 				return "dashboard_user";
	// 			} else {
	// 				return "dashboard_admin";
	// 			}
	// 		} else {
	// 			model.addAttribute("error", "User not found");
	// 			return "login_page"; // Return to login page if user not found
	// 		}
	// 	}
	// 	return "login_page"; // Return to login page if authentication fails
	// }

// 	@PostMapping("/login")
// public String login(@ModelAttribute User user, Model model, HttpSession session) {
//     String currentEmail = user.getEmail();
//     Boolean authenticateUser = userService.authenticateLogin(user.getEmail(), user.getPassword());

//     if (authenticateUser) {
//         Optional<User> optionalUser = userService.findByEmail(currentEmail);
//         if (optionalUser.isPresent()) {
//             User currentUser = optionalUser.get();

//             // Store the user in the session for persistence across pages
//             session.setAttribute("loggedInUser", currentUser);

//             if (currentUser.getRole().contains("ROLE_USER")) {
//                 return "redirect:/dashboard_user";
//             } else {
//                 return "redirect:/dashboard_admin";
//             }
//         } else {
//             model.addAttribute("error", "User not found");
//             return "login_page";
//         }
//     }

//     model.addAttribute("error", "Invalid credentials");
//     return "login_page";
// }
// @PostMapping("/login")
// public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
//     User user = userService.findByEmail(email).orElse(null);
//     if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//         session.setAttribute("userId", user.getId()); // Store user ID instead of the whole user object
//         return ResponseEntity.ok("Login successful");
//     } else {
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//     }
// }

@PostMapping("/login")
public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
    // Fetch user by email first
    User user = userService.findByEmail(email).orElse(null);
    
    // Check if user exists and password matches
    if (user != null && passwordEncoder.matches(password, user.getPassword())) {
        // Store the user ID in the session for persistence
        session.setAttribute("userId", user.getId()); // Store user ID

        // Now retrieve the user by ID (optional, if you want to fetch the latest details)
        User currentUser = userService.findById(user.getId()).orElse(null);

        // Store the current user in the session for persistence across pages
        session.setAttribute("loggedInUser", currentUser);

        // Redirect based on user role
        if (currentUser.getRole().contains("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/dash_user")).build();
        } else {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/dash_admin")).build();
        }
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
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

	

	@GetMapping("/profile")
	public String manageProfile(Model model, HttpSession session, MultipartFile prof) throws IOException {
    Long userId = (Long) session.getAttribute("userId");
	User user = userService.findById(userId).orElse(null);
	//userService.updateUser(userId, currentUser, prof);
	//currentUser = userService.update2(currentUser, prof);
	model.addAttribute("user", user);
	return "manage_profile";
    /*if (userId != null) {
        User currentUser = userService.findById(userId).orElse(null);
        if (currentUser != null) {
			userService.updateUser(userId, currentUser, prof);
            model.addAttribute("user", currentUser);
            return "manage_profile"; // Load the user's profile details in the view
        } else {
            model.addAttribute("error", "User not found.");
            return "redirect:/"; // Redirect to home if user not found
        }
    }*/
    //return "redirect:/"; // Redirect to home if no userId in session
}
	
	// @PostMapping("/profile")
	// public String updateProfile(@ModelAttribute User user, @RequestParam("prof") MultipartFile prof, HttpSession session) throws IOException {
	// 	//TODO: process POST request
	// 	Long userId = (Long) session.getAttribute("userId");
	// 	//User user = userService.findById(userId).orElse(null);
	// 	userService.update2(userId, prof);
	// 	return "manage_profile";
	// }
	@PostMapping("/profile")
	public String updateProfile(@ModelAttribute User user, @RequestParam("prof") MultipartFile prof, HttpSession session, Model model) throws IOException {
    Long userId = (Long) session.getAttribute("userId");
    // Call the service to update user profile data
    userService.update2(userId, prof);

    // Fetch updated user data after saving changes
    User updatedUser = userService.findById(userId).orElse(null);

    // Add the updated user to the model to reflect changes
    model.addAttribute("user", updatedUser);

    return "manage_profile"; // Return the same view to reflect updated data
}

	

	@GetMapping("/mb-user")
	public String manageBookingsUser(Model model) {
		// Add attributes to the model if needed for profile management
		reservService.checkStatus();
		return "managebookingsUser";
	}

	@GetMapping("/mb-admin")
	public String manageBookingsAdmin(Model model) {
		// Add attributes to the model if needed for profile management
		reservService.checkStatus();
		List<Reservation> reservationList = reservService.listReservation();
		model.addAttribute("reservationList", reservationList);
		return "managebookingsAdmin";
	}

	@GetMapping("/analytics")
	public String facilityRatingAdmin(Model model) {
		// Add attributes to the model if needed for profile management
		return "facilityRating";
	}

	@GetMapping("/ratings")
	public String viewRatingsList(Model model) {
		// Add attributes to the model if needed for profile management
		return "viewRatingsList";
	}

	@GetMapping("/bookings/{id}")
	public String viewBookingsDetails(@PathVariable Long id, Model model) {
		// Add attributes to the model if needed for profile management
		Area area = areaService.getAreaById(id);
		//Reservation reservation = reservService.findReservationById(id);
		model.addAttribute("area", area);
		//model.addAttribute("reservation", reservation);
		return "view_recreational_area";
	}

	@GetMapping("/booking-area/{id}")
	public String viewBookingArea(@PathVariable Long id, Model model) {
		// Add attributes to the model if needed for profile management
		Area area = areaService.getAreaById(id);
		model.addAttribute("area", area);
		model.addAttribute("reservation", new Reservation());
		return "book_area";
	} 
	@GetMapping("/submit_book")
	public String addBooking(@ModelAttribute Reservation reservation) {
		// Add attributes to the model if needed for profile management
		reservService.bookReservation(reservation);
		return "redirect:/areas-user";
	}

	@GetMapping("/areas-admin")
	public String recreationalAreasList(Model model) {
		// Add attributes to the model if needed for profile management
		List<Area> areaList = areaService.getAllAreas();
		model.addAttribute("areaList", areaList);
		return "am_recreationalAreasList";
	}

	@GetMapping("/areas-user")
	public String recreationalAreasListUser(Model model) {
		// Add attributes to the model if needed for profile management
		model.addAttribute("areaList", areaService.getAllAvailableAreas());
		model.addAttribute("areaListFalse", areaService.getAllUnavailableAreas());
		return "am_recreationalAreasList_user";
	}



	

	@GetMapping("/view-recreational-area-user/{id}")
public String recreationalSwimmingPoolUser(@PathVariable("id") Long id, Model model) {
    // Add attributes to the model for profile management
    Area area = areaService.getAreaById(id);
    model.addAttribute("area", area);
    return "view_recreational_area_user";
}


	@GetMapping("/view-recreational-area/{id}")
	public String viewRecreationalArea(@PathVariable Long id, Model model) {
		// Add attributes to the model if needed for profile management
		Area area = areaService.getAreaById(id);
		model.addAttribute("area", area);
		return "view_recreational_area";
	}



	@GetMapping("/accounts")
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
	public String modifyRecreationalArea(@ModelAttribute("area") Area area, @PathVariable Long id, @RequestParam("fileCover") MultipartFile cover, @RequestParam("fileAdd") MultipartFile add,  @RequestParam("schedule-start-time") String startTime,    @RequestParam("schedule-end-time") String endTime, @RequestParam(value = "available", required = false) Boolean available, Model model) throws IOException {
	
	 Area areaEdit = areaService.getAreaById(id);


	 areaEdit.setStartTime(LocalTime.parse(startTime));
	 areaEdit.setEndTime(LocalTime.parse(endTime));
	 areaEdit.setAvailable(available != null && available);
	 areaEdit.setName(area.getName());
	 areaEdit.setGuidelines(area.getGuidelines());
	 areaService.updateArea(areaEdit, cover, add); 

	 Area updatedArea = areaService.getAreaById(id);
	 model.addAttribute("area", updatedArea);
 
	 return "redirect:/areas-admin"; 
 }

	@GetMapping("/deleteArea/{id}")
	public String deleteArea(@PathVariable Long id, Model model) {
		model.addAttribute("delete", "Are you sure you would like to delete this area?");
		areaService.deleteArea(id);

		return "redirect:/areas-admin";
	}

	/*@GetMapping("/deleteArea/{id}")
    public RedirectView deleteArea(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        areaService.deleteArea(id);
        redirectAttributes.addFlashAttribute("message", "Area deleted successfully");
        return new RedirectView("/recreational-areas-list", true);
    }*/
	
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
	
	return "forgotPassword_page"; 
}
@GetMapping("/forgotPassword_setPass")
public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
model.addAttribute("loginRequest", new User());
model.addAttribute("token", token); 
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
	return "redirect:/?resetSuccess=true"; 
} else {
	model.addAttribute("error", "An error occurred. Please try again.");
	return "forgotPassword_setPass"; 
}
}
}