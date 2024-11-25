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

    public void createRating(Rating rating) {
        LocalDate date = LocalDate.now();
        rating.setRatingDate(date);
        ratingRepository.save(rating);
    }

    public Rating findByAreaAndUser(User user, Area area) {
        return ratingRepository.findByUserAndArea(user, area);
    }

    public void saveUpdateRating(User user, Area area, String feedback, int stars) {
        LocalDate date = LocalDate.now();
        Rating checkRating = findByAreaAndUser(user, area);
        if(checkRating == null) {
            checkRating = new Rating();
            checkRating.setUser(user);
            checkRating.setArea(area);
        }
        checkRating.setAreaname(area.getName());
        checkRating.setUsername(user.getName());
        checkRating.setUseremail(user.getEmail());
        checkRating.setFeedback(feedback);
        checkRating.setStars(stars);
        checkRating.setRatingDate(date);
        ratingRepository.save(checkRating);
    }
    public Rating findByUseremail(String useremail) {
        return ratingRepository.findFirstByUseremail(useremail);
    }

    public Rating findFirstByAreaname(String areaname) {
        return ratingRepository.findFirstByAreaname(areaname);
    }

    public int findByUseremailAndAreaname(String useremail, String areaname) {
        Rating rating = ratingRepository.findByUseremailAndAreaname(useremail, areaname);
        if(rating == null || rating.getAreaname() == null) {
            return 1;
        }
        return 2;
    }

    public Rating returnRatingUseremailAndAreaname(String useremail, String areaname) {
        return ratingRepository.findByUseremailAndAreaname(useremail, areaname);
    }

    public void updateAllAreaname(List<Rating> rating, String areaname) {
        rating.forEach(rA -> rA.setAreaname(areaname));
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

    public double averageStars() {
        List<Rating> rating = ratingRepository.findAll();
        return rating.stream().mapToInt(Rating::getStars).average().orElse(0.0);
    }

    public double averageStarsAreaname(String areaname) {
        List<Rating> rating = ratingRepository.findByAreaname(areaname);
        return rating.stream().mapToInt(Rating::getStars).average().orElse(0.0);
    }
}
