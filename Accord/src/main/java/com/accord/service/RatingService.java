package com.accord.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Rating> findByAreaname(String areaname) {
        return ratingRepository.findFirstByAreaname(areaname);
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
        List<Rating> rating = ratingRepository.findFirstByAreaname(areaname);
        return rating.stream().mapToInt(Rating::getStars).average().orElse(0.0);
    }
}
