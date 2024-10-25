package com.accord.service;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accord.Entity.User;
import com.accord.repository.EmailNotificationRepository;
import com.accord.repository.UserRepository;
import com.accord.util.AppConstant;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private EmailNotificationRepository emailNotificationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JavaMailSender mailSender;  // Injecting JavaMailSender for sending email

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ================== CRUD METHODS ==================

    /**
     * Create a new user and save to the database
     * @param user - User entity to be created
     * @return - The saved user entity
     */
    public User createUser(User user) {
        return userRepository.save(user);  // Save the user object to the repository
    }

    /**
     * Read/Get a user by their ID
     * @param id - User ID
     * @return - Optional User entity (returns empty if not found)
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);  // Find user by ID from the repository
    }

    /**
     * Get all users from the database
     * @return - List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();  // Retrieve all users from the repository
    }

    /**
     * Update an existing user by their ID
     * @param id - User ID
     * @param updatedUser - User object with updated fields
     * @return - The updated user entity or null if not found
     */
    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            // Update the fields of the existing user with new values
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setContactnumber(updatedUser.getContactnumber());
            // Add other fields that need to be updated...
            return userRepository.save(existingUser);  // Save updated user to the repository
        } else {
            return null;  // Return null if user not found
        }
    }

    /**
     * Delete a user by their ID
     * @param id - User ID
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);  // Delete the user by ID from the repository
    }

    // ================== END OF CRUD METHODS ==================

    // Other service methods you already have
    public boolean sendPasswordResetEmail(User user) {
        String token = UUID.randomUUID().toString();  // Generate unique token
        String resetLink = "http://localhost:8086/forgotPassword_setPass?token=" + token;

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

            return true;  // Return true if email is sent successfully
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;  // Return false if email sending failed
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));  // Update password
            userRepository.save(user);  // Save the updated user
            return true;  // Password reset successful
        }

        return false;  // Token is invalid or user not found
    }

    public User registerUser(String name, String password, String email, String contactnumber, int block_num, int lot_num, String property_status,
            MultipartFile tenancy, MultipartFile valid, String role) {
        if (email != null && password != null) {
            if (userRepository.findFirstByEmail(email).isPresent()) {
                return null;
            }
            if (userRepository.findByContactnumber(contactnumber).isPresent()) {
                return null;
            }
        }

        User user = new User();
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setContactnumber(contactnumber);
        user.setBlock_num(block_num);
        user.setLot_num(lot_num);
        user.setProperty_status(property_status);
        user.setConfirmation_email(null);  // Email confirmation pending
        user.setConfirmation_account(null);  // Admin approval pending

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
        user.setRole(role);
        User registeredUser = userRepository.save(user);

        // Send an email to the admin for approval
        sendApprovalRequestToAdmin(registeredUser);

        return registeredUser;
    }

    public User registerAdmin(String email, String password, String role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setConfirmation_email(true);
        user.setConfirmation_account(true);
        user.setRole(role);
        return userRepository.save(user);
    }

    public Boolean authenticateLogin(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    public Boolean checkPhone(String contactnumber) {
        return !userRepository.findByContactnumber(contactnumber).isPresent();
    }

    public Boolean checkEmail(String email) {
        return !userRepository.findFirstByEmail(email).isPresent();
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    private void sendApprovalRequestToAdmin(User user) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);  // true indicates multipart

            helper.setFrom("extrahamham@gmail.com");  // Your app's email
            helper.setTo("klayam12x@gmail.com");  // Admin's email
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
        }
    }






    public void increaseFailedAttempt(User user) {
        int attempt = user.getFailedAttempt() + 1;
        user.setFailedAttempt(attempt);
        userRepository.save(user);
    }

    public void userAccountLock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    public boolean unlockAccountTimeExpired(User user) {

        long lockTime = user.getLockTime().getTime();
        long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if (unLockTime < currentTime) {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    public void resetAttempt(int userId) {

    }

    public void updateUserResetToken(String email, String resetToken) {
        User findByEmail = userRepository.findByEmail(email);
        findByEmail.setResetToken(resetToken);
        userRepository.save(findByEmail);
    }

    public Optional<User> getUserByToken(String token) {
        return userRepository.findByResetToken(token);
    }
}
