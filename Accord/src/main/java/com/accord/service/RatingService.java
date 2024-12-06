package com.accord.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.accord.Entity.User;
import com.accord.Entity.Area;
import com.accord.Entity.Rating;
import com.accord.repository.RatingRepository;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    public void createRating(User user, Area area, String feedback, int stars) {
        Rating rating = new Rating();
        LocalDate date = LocalDate.now();
        rating.setUseremail(user.getEmail());
        rating.setUsername(user.getName());
        rating.setAreaname(area.getName());
        rating.setFeedback(feedback);
        rating.setStars(stars);
        rating.setRatingDate(date);
        rating.setUser(user);
        rating.setArea(area);
        ratingRepository.save(rating);
    }

    public Rating findByUserAndArea(User user, Area area) {
        return ratingRepository.findByUserAndArea(user, area);
    }

    public void saveUpdateRating(Rating rating) {
        LocalDate date = LocalDate.now();
        rating.setRatingDate(date);
        ratingRepository.save(rating);
    }

    public Rating findByUseremail(String useremail) {
        return ratingRepository.findFirstByUseremail(useremail);
    }

    public Rating findFirstByAreaname(String areaname) {
        return ratingRepository.findFirstByAreaname(areaname);
    }

    public Rating returnRatingUseremailAndAreaname(String useremail, String areaname) {
        return ratingRepository.findByUseremailAndAreaname(useremail, areaname);
    }

    public void updateAllAreaname(List<Rating> rating, String areaname) {
        rating.forEach(rA -> rA.setAreaname(areaname));
        ratingRepository.saveAll(rating);
    }

    public void updateAllEmail(List<Rating> rating, String email) {
        rating.forEach(rA -> rA.setUseremail(email));
        ratingRepository.saveAll(rating);
    }

    public void updateAllName(List<Rating> rating, String name) {
        rating.forEach(rA -> rA.setUsername(name));
        ratingRepository.saveAll(rating);
    }

    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    public List<Rating> listByUseremail(String useremail) {
        return ratingRepository.findByUseremail(useremail);
    }

    public List<Rating> listByAreaname(String areaname) {
        return ratingRepository.findByAreaname(areaname);
    }

    public List<Rating> listByUsername(String username) {
        return ratingRepository.findByUsername(username);
    }

    public List<Rating> listByUser(User user) {
        return ratingRepository.findAllByUser(user);
    }
    
}
