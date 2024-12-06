package com.accord.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accord.Entity.User;
import com.accord.Entity.Area;
import com.accord.Entity.Rating;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long>{
    Rating findByUserAndArea(User user, Area area);

    Rating findFirstByUseremail(String useremail);

    Rating findFirstByAreaname(String areaname);

    Rating findByUseremailAndAreaname(String useremail, String areaname);

    List<Rating> findByAreaname(String areaname);

    List<Rating> findByUseremail(String useremail);

    List<Rating> findByUsername(String username);

    List<Rating> findAllByUser(User user);

    Long countByAreaname(String areaname);
    
}
