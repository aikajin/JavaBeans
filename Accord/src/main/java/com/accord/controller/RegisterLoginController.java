
package com.accord.controller;

import java.io.IOException;
import java.net.URI;
import java.time.LocalTime;
import java.util.List;
import java.util.Base64;

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
import com.accord.repository.UserRepository;
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
	UserRepository repo;

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
public String showDashboard(Model m, HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    User currentUser = userService.findById(userId).orElse(null);
    if (currentUser != null) {
		if (currentUser.getProfile_picture() != null) {
			String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
			m.addAttribute("profilePictureBase64", base64Image);
		}
        m.addAttribute("user", currentUser); 
    }
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

	

// 	@GetMapping("/profile")
// 	public String manageProfile(Model model, HttpSession session, MultipartFile prof) throws IOException {
//     Long userId = (Long) session.getAttribute("userId");
// 	User user = userService.findById(userId).orElse(null);
// 	//userService.updateUser(userId, currentUser, prof);
// 	//currentUser = userService.update2(currentUser, prof);
// 	model.addAttribute("user", user);
// 	return "manage_profile";
   
// }
	@GetMapping("/profile")
	public String manageProfile(Model model, HttpSession session, MultipartFile prof) throws IOException {
    reservService.checkStatus();
		Long userId = (Long) session.getAttribute("userId");
		User user = userService.findById(userId).orElse(null);
		if (user != null) {
			if (user.getProfile_picture() != null) {
				String base64Image = Base64.getEncoder().encodeToString(user.getProfile_picture());
				model.addAttribute("profilePictureBase64", base64Image);
			}
			model.addAttribute("user", user); // Single user
		}
		return "manage_profile";
	}
	
	
	@PostMapping("/profile")
	public String updateProfile(@ModelAttribute User user, @RequestParam("prof") MultipartFile prof, HttpSession session, Model model) throws IOException {
    Long userId = (Long) session.getAttribute("userId");
    // Call the service to update user profile data
    userService.update2(userId, prof);

    // Fetch updated user data after saving changes
    User updatedUser = userService.findById(userId).orElse(null);

    // Add the updated user to the model to reflect changes
//     model.addAttribute("user", updatedUser);

    return "manage_profile"; // Return the same view to reflect updated data
}

	

	
	@GetMapping("/mb-user")
	public String manageBookingsUser(Model m, HttpSession session) {
	reservService.checkStatus();
    Long userId = (Long) session.getAttribute("userId");
    User currentUser = userService.findById(userId).orElse(null);
    if (currentUser != null) {
		if (currentUser.getProfile_picture() != null) {
			String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
			m.addAttribute("profilePictureBase64", base64Image);
		}
        m.addAttribute("user", currentUser); // Single user
    }
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
	public String showAreas(Model m, HttpSession session) {
	m.addAttribute("areaList", areaService.getAllAvailableAreas());
	m.addAttribute("areaListFalse", areaService.getAllUnavailableAreas());
    Long userId = (Long) session.getAttribute("userId");
    User currentUser = userService.findById(userId).orElse(null);
    if (currentUser != null) {
		if (currentUser.getProfile_picture() != null) {
			String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
			m.addAttribute("profilePictureBase64", base64Image);
		}
        m.addAttribute("user", currentUser); // Single user
    }
    return "am_recreationalAreasList_user";
}



	

	// @GetMapping("/view-recreational-area-user/{id}")
	// public String recreationalSwimmingPoolUser(@PathVariable("id") Long id, Model model) {
    // // Add attributes to the model for profile management
    // Area area = areaService.getAreaById(id);
    // model.addAttribute("area", area);
    // return "view_recreational_area_user";
	// }
	@GetMapping("/view-recreational-area-user/{id}")
	public String viewRecreationalAreasUser(@PathVariable("id") Long id, Model m, HttpSession session) {
	Area area = areaService.getAreaById(id);
	m.addAttribute("area", area);
    Long userId = (Long) session.getAttribute("userId");
    User currentUser = userService.findById(userId).orElse(null);
    if (currentUser != null) {
		if (currentUser.getProfile_picture() != null) {
			String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
			m.addAttribute("profilePictureBase64", base64Image);
		}
        m.addAttribute("user", currentUser); 
    }
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
	public String modifyRecreationalArea(@ModelAttribute("area") Area area, @PathVariable Long id, @RequestParam("fileCover") MultipartFile cover, @RequestParam("fileAdd") MultipartFile add,  @RequestParam("schedule-start-time") String startTime,    @RequestParam("schedule-end-time") String endTime, Model model) throws IOException {
	 // Retrieve the area to be edited
	 Area areaEdit = areaService.getAreaById(id);

	 // Update schedule fields
	 areaEdit.setStartTime(LocalTime.parse(startTime));
	 areaEdit.setEndTime(LocalTime.parse(endTime));
 
	 // Update other fields like Name, Guidelines, etc.
	 areaEdit.setName(area.getName());
	 areaEdit.setGuidelines(area.getGuidelines());

 
	 // Update cover and additional photos if files are uploaded
	 areaService.updateArea(areaEdit, cover, add); // Updates other properties, including photo fields
 
	 // Fetch the updated area and pass it to the model
	 Area updatedArea = areaService.getAreaById(id);
	 model.addAttribute("area", updatedArea);
 
	 return "redirect:/areas-admin"; // Redirect to the areas admin page
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