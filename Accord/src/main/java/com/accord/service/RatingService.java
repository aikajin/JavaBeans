package com.accord.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.accord.Entity.Area;
import com.accord.Entity.Rating;
import com.accord.repository.RatingRepository;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    public void createRating(Rating rating) {
        ratingRepository.save(rating);
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
    public List<Rating> findByAreaname(String areaname) {
        return ratingRepository.findAll().stream().filter(rating -> areaname.equalsIgnoreCase(rating.getAreaname())).collect(Collectors.toList());
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
