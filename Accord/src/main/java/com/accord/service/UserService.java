package com.accord.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accord.Entity.User;
import com.accord.repository.EmailNotificationRepository;
import com.accord.repository.UserRepository;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;  // To generate a unique token

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private EmailNotificationRepository emailNotificationRepository;

    @Autowired
    private JavaMailSender mailSender;  // Injecting JavaMailSender for sending email

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public boolean sendPasswordResetEmail(User user) {
        String token = UUID.randomUUID().toString();  // Generate unique token
        String resetLink = "http://127.0.0.1:5500/Accord/src/main/resources/templates/forgotPassword_setPass.html" + token;
    
        MimeMessage message = mailSender.createMimeMessage();
    
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("extrahamham@gmail.com");
            helper.setTo(user.getEmail());
            helper.setSubject("Password Reset Request");
    
            String emailContent = "<p>Hi " + user.getName() + ",</p>" +
                                  "<p>To reset your password, click the link below:</p>" +
                                  "<a href=\"" + resetLink + "\">Reset Password</a>" +
                                  "<p>If you didn't request a password reset, please ignore this email.</p>";
            
            helper.setText(emailContent, true);
    
            // Send email
            mailSender.send(message);
    
            return true; // Return true if email is sent successfully
        } catch (MessagingException e) {
            e.printStackTrace();
            return false; // Return false if email sending failed
        }
    }



    public Optional<User> findByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    public User registerUser(String name, String password, String email, String contactnumber, int block_num, int lot_num, String property_status,
             MultipartFile tenancy, MultipartFile valid) {
        if (email != null && password != null) {
            if (userRepository.findFirstByEmail(email).isPresent()) {
                System.out.println("Duplicate email");
                return null;
            }
            if (userRepository.findByContactnumber(contactnumber).isPresent()) {
                System.out.println("Duplicate phone number");
                return null;
            }
        }
        
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setContactnumber(contactnumber);
        user.setBlock_num(block_num);
        user.setLot_num(lot_num);
        user.setProperty_status(property_status);
        user.setConfirmation_email(false);  // Email confirmation pending
        user.setConfirmation_account(false);  // Admin approval pending

        try {
            user.setTenancy_name(tenancy.getOriginalFilename());
            user.setTenancy_type(tenancy.getContentType());
            user.setTenancy_document(tenancy.getBytes());
            user.setId_name(valid.getOriginalFilename());
            user.setId_type(valid.getContentType());
            user.setId_document(valid.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        User registeredUser = userRepository.save(user);
        
        // Send an email to the admin for approval
        sendApprovalRequestToAdmin(registeredUser);

        return registeredUser;
    }

    public User authenticate(String email, String password) {
        // Only allow login if the account is approved by the admin
        return userRepository.findByEmailAndPassword(email, password)
                .filter(User::getConfirmation_account)  // Check if account is approved
                .orElse(null);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    private void sendApprovalRequestToAdmin(User user) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            // Use MimeMessageHelper to handle multipart emails (attachments)
            MimeMessageHelper helper = new MimeMessageHelper(message, true);  // true indicates multipart

            helper.setFrom("extrahamham@gmail.com");  // Your app's email
            helper.setTo("kyledowiromero@gmail.com");  // Admin's email
            helper.setSubject("Approval Request for New User Registration");

            // Email content with user details
            helper.setText("A new user has registered with the following details:\n\n" +
                    "Name: " + user.getName() + "\n" +
                    "Email: " + user.getEmail() + "\n" +
                    "Contact: " + user.getContactnumber() + "\n" +
                    "Block Number: " + user.getBlock_num() + "\n" +
                    "Lot Number: " + user.getLot_num() + "\n" +
                    "Property Status: " + user.getProperty_status() + "\n\n" +
                    "Please approve or reject this registration in the admin panel.");

            // Add the tenancy agreement as an attachment
            if (user.getTenancy_name() != null) {
                helper.addAttachment(user.getTenancy_name(), new ByteArrayResource(user.getTenancy_document()));
            }

            // Add the valid ID as an attachment
            if (user.getId_name() != null) {
                helper.addAttachment(user.getId_name(), new ByteArrayResource(user.getId_document()));
            }

            // Send the email
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle exception if sending fails
        }
    }
}
