package com.accord.controller;
import com.accord.Entity.User;
import com.accord.service.UserService;
import java.io.IOException;
import java.util.List;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class ForgotPasswordController {

    @Autowired
    private UserService userService;
    @PostMapping("/forgotPassword")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        // Look for the user by email
        Optional<User> userOptional = userService.findByEmail(email); // This returns Optional<User>
        
        if (userOptional.isPresent()) {
            // If the user exists, send a password reset email
            User user = userOptional.get(); // Extract the User from Optional
            userService.sendPasswordResetEmail(user);
    
            // Add a success message to the model
            model.addAttribute("message", "Password reset email has been sent to " + email);
            return "forgotPassword_email"; // Show a confirmation page
        } else {
            // If the user does not exist, show an error message
            model.addAttribute("error", "No account found with that email address.");
            return "forgotPassword_page"; // Stay on the forgot password page
        }
    }
}

 