package com.accord.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accord.Entity.Reservation;
import java.util.List;
import java.time.LocalDate;

public interface ReservRepository extends JpaRepository<Reservation, Long>{
    Optional<Reservation> findReservationById(Long id);

    Reservation findByUseremail(String useremail);

    Reservation findByAreaname(String areaname);

    List<Reservation> findByStatusIn(List<String> statuses);
    
    List<Reservation> findAllByUseremail(String useremail);

    List<Reservation> findAllByAreaname(String areaname);

    List<Reservation> findAllByUsername(String username);

    List<Reservation> findByUseremailAndStatusIn(String useremail, List<String> statuses);

    Long countByUseremailAndStatusIn(String useremail, List<String> statuses);

    Long countByUseremailAndStatusInAndUserStartDateBetween(String useremail, List<String> status, LocalDate startDate, LocalDate endDate);

    Long countByStatusIn(List<String> statuses);
}
