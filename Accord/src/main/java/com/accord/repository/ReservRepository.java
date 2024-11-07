package com.accord.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accord.Entity.Reservation;
import java.util.List;
import java.time.LocalDate;

public interface ReservRepository extends JpaRepository<Reservation, Long>{
    Optional<Reservation> findReservationById(Long id);
    Reservation findByAreaname(String areaname);
    Reservation findByUseremail(String useremail);
}
