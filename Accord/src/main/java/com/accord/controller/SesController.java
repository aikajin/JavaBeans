package com.accord.controller;

import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.accord.Entity.Rating;
import com.accord.Entity.Reservation;
import com.accord.Entity.User;
import com.accord.repository.UserRepository;
import com.accord.service.RatingService;
import com.accord.service.ReservService;
import com.accord.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class SesController {



    @Autowired
	private UserService userService;
    @Autowired
    UserRepository repo;
    @Autowired
    RatingService ratingService;
    @Autowired
    ReservService reservService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    // @GetMapping("/getUserdeets")
    // public String getUserdeets(Model m){
    //     m.addAttribute("error",repo.findAll());
    //     return "error_page";

    // }
    @GetMapping("/getUserdeets")
    public String showDashboard(Model m, HttpSession session) {
        m.addAttribute("error",repo.findAll());
    Long userId = (Long) session.getAttribute("userId");
    User currentUser = userService.findById(userId).orElse(null);
    if (currentUser != null) {
		if (currentUser.getProfile_picture() != null) {
			String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
			m.addAttribute("profilePictureBase64", base64Image);
		}
        m.addAttribute("user", currentUser); 
    }
    return "error_page";
}
@PostMapping("/updateName")
public ResponseEntity<String> updateName(@RequestParam Long id, @RequestParam String name) {
    if (id == null || id <= 0) {
        return ResponseEntity.badRequest().body("Invalid user ID.");
    }
    
    if (name == null || name.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Name cannot be empty.");
    }
    
    try {
        User user = userService.findById(id).orElse(null);
        List<Rating> rating = ratingService.listByUser(user);
        List<Reservation> reservations = reservService.findAllByUser(user);
        userService.updatename(id, name);
        ratingService.updateAllName(rating, name);
        reservService.updateAllName(reservations, name);
        return ResponseEntity.ok("Name updated successfully.");
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
    }
}
@PostMapping("/updateEmail")
public ResponseEntity<String> updateEmail(@RequestParam Long id, @RequestParam String email) {
    if (id == null || id <= 0) {
        return ResponseEntity.badRequest().body("Invalid user ID.");
    }

    if (email == null || email.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Email cannot be empty.");
    }

    try {
        System.out.println("Updating email for user ID: " + id); // Log the id
        User user = userService.findById(id).orElse(null);
        List<Rating> rating = ratingService.listByUser(user);
        List<Reservation> reservations = reservService.findAllByUser(user);
        userService.updateemail(id, email);
        ratingService.updateAllEmail(rating, email);
        reservService.updateAllEmail(reservations, email);
        return ResponseEntity.ok("Email updated successfully.");
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
    }
}
@PostMapping("/updatePassword")
public ResponseEntity<String> updatePassword(
    @RequestParam Long id, 
    @RequestParam String currentPassword, 
    @RequestParam String newPassword) {

    if (id == null || id <= 0) {
        return ResponseEntity.badRequest().body("Invalid user ID.");
    }

    if (currentPassword == null || currentPassword.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Current password cannot be empty.");
    }

    if (newPassword == null || newPassword.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("New password cannot be empty.");
    }

    try {
        User user = userService.findById(id).orElse(null);
        if (!userService.checkPassword(user, currentPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current password is incorrect.");
        }

        userService.updatePassword(id, newPassword);
        return ResponseEntity.ok("Password updated successfully.");
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
    }
}




    
}
