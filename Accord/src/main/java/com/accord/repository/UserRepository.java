package com.accord.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.accord.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndPassword(String email, String password);
    Optional<User> findFirstByEmail(String email);
    User findByEmail(String email);
    Optional<User> findFirstById(Long id);
    Optional<User> findByContactnumber(String contactnumber);
    List<User> findAll();
    Optional<User> findByResetToken(String token);
}




