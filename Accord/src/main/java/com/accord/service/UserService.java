package com.accord.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accord.Entity.User;
import com.accord.notification.EmailNotification;
import com.accord.repository.EmailNotificationRepository;
import com.accord.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private EmailNotificationRepository emailNotificationRepository; // Injecting the EmailNotificationRepository

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String name, String password, String email, String contactnumber, int block_num, int lot_num, String property_status,
    		 MultipartFile tenancy, MultipartFile valid) {
        if(email != null && password != null) {
            if(userRepository.findFirstByEmail(email).isPresent()) {
                System.out.println("Duplicate email");
                return null;
                }
            if(userRepository.findByContactnumber(contactnumber).isPresent()) {
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
        try {
			user.setTenancy_name(tenancy.getOriginalFilename());
			user.setTenancy_type(tenancy.getContentType());
			user.setTenancy_document(tenancy.getBytes());
			user.setId_name(valid.getOriginalFilename());
			user.setId_type(valid.getContentType());
			user.setId_document(valid.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        User registeredUser = userRepository.save(user);
        return registeredUser;
    }
    public User authenticate(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password).orElse(null);
    }
	public List<User> getAllUser(){
		return userRepository.findAll();
	}
}
