package com.accord.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accord.Entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByEmailAndPassword(String email,  String password);
	Optional<User> findFirstByEmail(String email);
	Optional<User> findFirstById(Long id);
	Optional<User> findByContactnumber(String contactnumber);
	List<User> findAll();
}
