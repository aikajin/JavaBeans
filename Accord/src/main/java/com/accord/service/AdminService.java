package com.accord.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accord.Entity.Admin;
import com.accord.repository.AdminRepository;

@Service
public class AdminService {
	@Autowired
	private final AdminRepository adminRepository;
	
	public AdminService(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
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
}
