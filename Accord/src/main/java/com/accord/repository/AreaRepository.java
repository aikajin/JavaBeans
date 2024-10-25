package com.accord.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accord.Entity.Area;

public interface AreaRepository extends JpaRepository<Area, Long>{
    Optional<Area> findById(Long id);
    List<Area> findAll();
    Area findFirstByName(String name);
}
