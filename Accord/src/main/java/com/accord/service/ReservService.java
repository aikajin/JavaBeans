package com.accord.service;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accord.Entity.Reservation;
import com.accord.repository.ReservRepository;

@Service
public class ReservService {
    @Autowired
    private ReservRepository reservRepository;
    
    public void addReservation(Reservation reservation, String areaname, String username, String useremail, LocalDate user_start_date,
                                    LocalDate user_end_date, LocalTime user_start_time, LocalTime user_end_time) {
        reservation.setAreaname(areaname);
        reservation.setUsername(username);
        reservation.setUseremail(useremail);
        reservation.setUser_start_date(user_start_date);
        reservation.setUser_end_date(user_end_date);
        reservation.setUser_start_time(user_start_time);
        reservation.setUser_end_time(user_end_time);
        reservRepository.save(reservation);
    }
    public Reservation bookReservation(Reservation reservation) {
        /*reservation.setAreaname(reservation.getAreaname());
        reservation.setUsername(reservation.getUsername());
        reservation.setUseremail(reservation.getUseremail());
        reservation.setUser_start_date(reservation.getUser_start_date());
        reservation.setUser_end_date(reservation.getUser_end_date());
        reservation.setUser_start_time(reservation.getUser_start_time());
        reservation.setUser_end_time(reservation.getUser_end_time());*/
        //reservRepository.save(reservation);
        //return book;
        return reservRepository.save(reservation);
    }

    public List<Reservation> listReservation() {
        return reservRepository.findAll();
    }

    public Reservation findReservationsByAreaName(String areaname) {
        return reservRepository.findByAreaname(areaname);
    }

    public Reservation findReservationsByUserEmail(String useremail) {
        return reservRepository.findByUseremail(useremail);
    }

    public Reservation findReservationById(Long id) {
        return reservRepository.findById(id).orElse(null);
    }
}