package com.accord.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accord.Entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long>{
	Optional<Admin> findByEmailAndPassword(String email, String password);
	Optional<Admin> findFirstByEmail(String email);
}
