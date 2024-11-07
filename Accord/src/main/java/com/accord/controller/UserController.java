package com.accord.controller;

import com.accord.Entity.User;
import com.accord.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.accord.config.SecurityConfig;
import com.accord.repository.UserRepository;

import jakarta.servlet.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.Map;
import java.io.File;

import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    

    // Serve HTML view for the user accounts page
    @GetMapping
    public String userAccountsPage(Model model) {
        return "UserAccounts_page";  // Ensure this matches your actual Thymeleaf template file name
    }

    // List all users as JSON (for JavaScript/AJAX requests)
    @GetMapping("/list")
    @ResponseBody
    public List<User> listUsersJson() {
        return userService.getAllUsers();
    }

    // Show create user form
    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user-form";
    }

    // Save new user
    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        userService.createUser(user);
        redirectAttributes.addFlashAttribute("message", "User created successfully!");
        return "redirect:/users";
    }

    // Show update user form
    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "user-form";
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/users";
        }
    }

    // Update user
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            redirectAttributes.addFlashAttribute("message", "User updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found!");
        }
        return "redirect:/users";
    }

    // Delete user
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        return "redirect:/users";
    }

    

     // Delete area
     /*  @GetMapping("/delete/{id}")
     public String deleteArea(@PathVariable Long id, RedirectAttributes redirectAttributes) {
         userService.deleteArea(id);
         redirectAttributes.addFlashAttribute("message", "Area deleted successfully!");
         return "redirect:/area";
     }*/

    // View user details
    // View user details
    @GetMapping("/view/{id}")
    public String viewUserDetails(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
    
            // Format file size manually
            long fileSize = user.getTenancy_document().length;
            String formattedSize = formatBytes(fileSize);
            model.addAttribute("formattedSize", formattedSize);
            
            return "UserAccounts_viewDetails";
        } else {
            throw new RuntimeException("User not found");
        }
    }

    //
//     @PostMapping("/manage-profile")
// public ResponseEntity<?> updateProfile(@RequestParam String name, 
//                                         @RequestParam(required = false) MultipartFile profilePicture,
//                                         @RequestParam(required = false) String password) {
//     String currentEmail = UserService.getCurrentEmail();
//     Optional<User> optionalUser = userService.findByEmail(currentEmail);
    
//     if (optionalUser.isPresent()) {
//         User user = optionalUser.get();
//         user.setName(name);

//         // Handle profile picture upload
//         if (profilePicture != null && !profilePicture.isEmpty()) {
//             try {
//                 user.setTenancy_name(profilePicture.getOriginalFilename());
//                 user.setTenancy_document(profilePicture.getBytes());
//             } catch (IOException e) {
//                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture.");
//             }
//         }

//         // Handle password update
//         if (password != null && !password.isEmpty()) {
//             user.setPassword(passwordEncoder.encode(password));
//         }

//         userService.updateUser(user.getId(), user);  // Assuming you have an update method
//         return ResponseEntity.ok("Profile updated successfully.");
//     } else {
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
//     }
// }
@GetMapping("/manage_profile")
public String manageProfile(Model model, HttpSession session) {
    // Retrieve the user ID from the session
    Long userId = (Long) session.getAttribute("userId"); // Ensure you have this in the session

    if (userId != null) {
        // Fetch user by ID using UserService
        Optional<User> optionalUser = userService.findById(userId); // Assuming findById returns Optional<User>
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
        } else {
            model.addAttribute("error", "User not found");
        }
    } else {
        model.addAttribute("error", "User not found");
    }
    return "manage_profile"; // Return the Thymeleaf template for profile management
}

// @PostMapping("/manage-profile")
// public ResponseEntity<?> updateProfile(@RequestParam String name, 
//                                        @RequestParam(required = false) MultipartFile profilePicture,
//                                        @RequestParam(required = false) String password,
//                                        HttpSession session) {
//     Long userId = (Long) session.getAttribute("userId");
//     if (userId == null) {
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
//     }

//     Optional<User> optionalUser = userService.findById(userId);
//     if (optionalUser.isPresent()) {
//         User loggedInUser = optionalUser.get();
        
//         // Update name
//         loggedInUser.setName(name);
//         logger.info("Updating name for user: {}", loggedInUser.getEmail());

//         // Handle profile picture upload
//         if (profilePicture != null && !profilePicture.isEmpty()) {
//             // Validate file type and size
//             if (!profilePicture.getContentType().startsWith("image/")) {
//                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File must be an image.");
//             }
//             if (profilePicture.getSize() > 2 * 1024 * 1024) {
//                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds limit.");
//             }
//             try {
//                 loggedInUser.setTenancy_name(profilePicture.getOriginalFilename());
//                 loggedInUser.setTenancy_document(profilePicture.getBytes());
//             } catch (IOException e) {
//                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture.");
//             }
//         }

//         // Handle password update
//         if (password != null && !password.isEmpty()) {
//             loggedInUser.setPassword(passwordEncoder.encode(password));
//             logger.info("Password updated for user: {}", loggedInUser.getEmail());
//         }

//         userService.updateUser(loggedInUser.getId(), loggedInUser);
//         logger.info("Profile updated successfully for user: {}", loggedInUser.getEmail());
//         return ResponseEntity.ok("Profile updated successfully.");
//     } else {
//         logger.warn("User not found in session.");
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
//     }
// }

@PostMapping("/manage-profile")
public ResponseEntity<?> updateProfile(@RequestParam String name,
                                       @RequestParam("file") MultipartFile profilePicture,
                                       @RequestParam(required = false) String password,
                                       HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
    }

    Optional<User> optionalUser = userService.findById(userId);
    if (optionalUser.isPresent()) {
        User loggedInUser = optionalUser.get();

        // Update fields
        loggedInUser.setName(name);

        // Handle profile picture upload
        if (profilePicture != null && !profilePicture.isEmpty()) {
            // Validate file type and size
            // Your validation code...
            saveProfilePicture(profilePicture);
            /*try {
                //loggedInUser.setProfile_name(profilePicture.getOriginalFilename());
                //loggedInUser.setProfile_picture(profilePicture.getBytes());
                // Save URL if applicable
                //String profilePictureUrl = saveProfilePicture(profilePicture);
                saveProfilePicture(profilePicture);
                //loggedInUser.setProfilePictureUrl(profilePictureUrl);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture.");
            }*/
        }

        // Handle password update
        if (password != null && !password.isEmpty()) {
            loggedInUser.setPassword(passwordEncoder.encode(password));
        }

        // Attempt to update user and check result
        User updatedUser = userService.updateUser(loggedInUser.getId(), loggedInUser);
        if (updatedUser != null) {
            return ResponseEntity.ok("Profile updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }
}





// @PostMapping("/updateProfile")
// public ResponseEntity<Map<String, Object>> updateProfile(@RequestParam("fullName") String fullName,
//                                                           @RequestParam("email") String email,
//                                                           @RequestParam("password") String password,
//                                                           @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
//                                                           Principal principal) {
//     String userEmail = principal.getName(); // Get logged-in user's email
//    User user = userService.findByEmail(userEmail).orElse(null);
   
//    if (user == null) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "User not found"));
//    }
   
//    // Rest of the code...

//     user.setName(fullName);
//     user.setEmail(email);
//     // Hash the password if it is not empty
//     if (!password.isEmpty()) {
//         user.setPassword(passwordEncoder.encode(password));
//     }

//     if (profilePicture != null && !profilePicture.isEmpty()) {
//         // Save the profile picture and get the URL
//         String profilePictureUrl = saveProfilePicture(profilePicture);
//         user.setProfilePictureUrl(profilePictureUrl);
//     }

//     userService.save(user);
    
//     Map<String, Object> response = new HashMap<>();
//     response.put("success", true);
//     response.put("profilePictureUrl", user.getProfilePictureUrl()); // Return the new picture URL

//     return ResponseEntity.ok(response);
// }

private void saveProfilePicture(MultipartFile file) {
    /*try {
        String uploadDir = "/path/to/uploads"; // Define your upload directory
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File uploadFile = new File(uploadDir, fileName);
        file.transferTo(uploadFile);
        return "/uploads/" + fileName; // Adjust the URL based on your application’s context
    } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException("Could not save profile picture: " + e.getMessage());
    }*/
    User user = new User();
    try {
        user.setProfile_name(file.getOriginalFilename());
        user.setProfile_type(file.getContentType());
        user.setProfile_picture(file.getBytes());
        //return userRepository.save(user).toString();
        userRepository.save(user);
    } catch(IOException e) {
        e.printStackTrace();
    }
    //return userRepository.save(user).toString();
}



    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    

    
}
