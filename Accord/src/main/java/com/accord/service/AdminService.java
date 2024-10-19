package com.accord.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.accord.Entity.Admin;
import com.accord.repository.AdminRepository;

@Service
public class AdminService {
	@Autowired
	private final AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
	public AdminService(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}
	
	public Admin registerAdmin(String email, String password) {
		if(checkEmail(email) == null) {
				return null;
		}
		Admin admin = new Admin();
		admin.setEmail(email);
		admin.setPassword(passwordEncoder.encode(password));
		Admin registeredAdmin = adminRepository.save(admin);
		return registeredAdmin;
	}
	public Boolean checkEmail(String email) {
		if(email != null) {
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
}
