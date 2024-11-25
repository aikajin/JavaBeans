package com.accord.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.accord.Entity.Area;

public interface AreaRepository extends JpaRepository<Area, Long>{
    Optional<Area> findById(Long id);

    Optional<Area> findByName(String name);

    List<Area> findByAvailableTrue();

    List<Area> findByAvailableFalse();

    @Query("SELECT a.id AS areaId, a.name AS areaName, COALESCE(AVG(r.stars), 0) AS averageStars " +
            "FROM Area a LEFT JOIN a.ratings r " +
            "GROUP BY a.id")
    List<AreaWithRating> findAllAreasWithAverageRating();

    interface AreaWithRating {
        Long getAreaId();
        String getAreaName();
        Double getAverageStars();
    }
}
