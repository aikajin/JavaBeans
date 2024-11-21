package com.accord.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accord.Entity.Reservation;
import java.util.List;
import java.time.LocalDate;

public interface ReservRepository extends JpaRepository<Reservation, Long>{
    Optional<Reservation> findReservationById(Long id);

    Reservation findByAreaname(String areaname);

    List<Reservation> findByStatusIn(List<String> statuses);
    
    List<Reservation> findByUseremail(String useremail);

    List<Reservation> findByUseremailAndStatusIn(String useremail, List<String> statuses);

    Long countByUseremailAndStatusIn(String useremail, List<String> statuses);
}
