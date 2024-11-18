package com.accord.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accord.Entity.Reservation;
import com.accord.repository.ReservRepository;

@Service
public class ReservService {
    @Autowired
    private ReservRepository reservRepository;

    public Reservation bookReservation(Reservation reservation) {
        reservation.setStatus("NOT STARTED");
        return reservRepository.save(reservation);
    }

    public List<Reservation> listReservation() {
        checkStatus();
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

    public void checkStatus() {
        LocalDateTime dateTime = LocalDateTime.now();
        List<Reservation> r = reservRepository.findAll();
        r.forEach(rA -> {
            if(rA.getStatus() == "CANCELLED") {
                rA.setStatus("CANCELLED");
            }
            else if(dateTime.isAfter(LocalDateTime.of(rA.getUser_start_date(), rA.getUser_end_time()))) {
                rA.setStatus("COMPLETED");
            }
            else if((dateTime.isBefore(LocalDateTime.of(rA.getUser_start_date(), rA.getUser_end_time()))) &&
                    dateTime.isAfter(LocalDateTime.of(rA.getUser_start_date(), rA.getUser_start_time()))) {
                rA.setStatus("STARTED");
            }
            else {
                rA.setStatus("NOT STARTED");
            }
        });
        reservRepository.saveAll(r);
    }

}