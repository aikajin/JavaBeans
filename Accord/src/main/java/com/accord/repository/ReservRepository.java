package com.accord.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accord.Entity.Area;
import com.accord.Entity.Reservation;
import com.accord.Entity.User;

import java.util.List;
import java.time.LocalDate;

public interface ReservRepository extends JpaRepository<Reservation, Long>{
    Optional<Reservation> findReservationById(Long id);

    Reservation findByUseremail(String useremail);

    Reservation findByAreaname(String areaname);

    Reservation findByArea(Area area);

    List<Reservation> findByStatusIn(List<String> statuses);
    
    List<Reservation> findAllByUseremail(String useremail);

    List<Reservation> findAllByAreaname(String areaname);

    List<Reservation> findAllByUsername(String username);

    List<Reservation> findAllByUser(User user);

    List<Reservation> findAllByAreaAndStatusIn(Area area, List<String> statuses);

    List<Reservation> findByUserAndStatusIn(User user, List<String> statuses);

    Long countByUserAndStatusIn(User user, List<String> statuses);

    Long countByUserAndStatusInAndUserStartDateBetween(User user, List<String> status, LocalDate startDate, LocalDate endDate);

    Long countByStatusIn(List<String> statuses);
}
