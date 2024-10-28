package com.accord.controller;

import com.accord.Entity.User;
import com.accord.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

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

    // View user details
    // View user details
    /*@GetMapping("/view/{id}")
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
    }*/
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    

    
}
