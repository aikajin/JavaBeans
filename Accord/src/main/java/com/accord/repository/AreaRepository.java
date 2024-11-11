package com.accord.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accord.Entity.Area;

public interface AreaRepository extends JpaRepository<Area, Long>{
    Optional<Area> findById(Long id);

    Area findByName(String name);

    List<Area> findByAvailableTrue();

    List<Area> findByAvailableFalse();

    //List<Area> getAllAreas();
}
