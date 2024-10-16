package com.accord.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.accord.Entity.Admin;
import com.accord.Entity.User;
import com.accord.repository.AdminRepository;
import com.accord.repository.UserRepository;

@Service
public class AdminService {
	@Autowired
	private final AdminRepository adminRepository;
	@Autowired
	private final UserRepository userRepository;
	
	public AdminService(AdminRepository adminRepository, UserRepository userRepository) {
		this.adminRepository = adminRepository;
		this.userRepository = userRepository;
	}
	
	public Boolean checkEmail(String email, String password) {
		if(email != null && password != null) {
			if(adminRepository.findFirstByEmail(email).isPresent()) {
				return null;
			}
			else {
				return true;
			}
		}
		return true;
	}
	
	public Admin authenticate(String email, String password) {
		return adminRepository.findByEmailAndPassword(email, password).orElse(null);
	}
	
	public List<User> listAll(){
		return userRepository.findAll(Sort.by("id").ascending());
	}
	
	public User getUserById(Long id) {
		return userRepository.findById(id).orElse(null);
	}
	
	public void saveUser(User user) {
		userRepository.save(user);
	}
	
	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
	}
}
