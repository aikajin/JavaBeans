package com.accord.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import com.accord.repository.AreaRepository;
import com.accord.Entity.Area;
import com.accord.Entity.Rating;
import com.accord.Entity.Reservation;
import com.accord.Entity.User;
import com.accord.repository.ReservRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Service

public class ReservService {
    @Autowired
    private ReservRepository reservRepository;
    @Autowired
    private JavaMailSender mailSender; 
    @Autowired
private AreaRepository areaRepository;

    public void bookReservation(Reservation reservation, Area area, User user) {
        reservation.setArea(area);
        reservation.setUser(user);
        reservRepository.save(reservation);
        sendReservationConfirmationEmail(reservation);
    }

    public int checkReservation(Reservation reservation, Area area) {
        List<Reservation> allReservations = findReservationsByAreaAndStatusNotStarted(area);
        for(Reservation existReservation : allReservations) {
            if(reservation.getUserStartDate().equals(existReservation.getUserStartDate())) {
                if((reservation.getUser_end_time().isAfter(existReservation.getUser_start_time())) &&
                    (reservation.getUser_start_time().isBefore(existReservation.getUser_end_time()))) {
                        return 1;
                    }
            }
        }
        return 2;
    }

    private void sendReservationConfirmationEmail(Reservation reservation) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true); 
            Area area = areaRepository.findByName(reservation.getAreaname()).orElse(null); 
            String guidelines = area != null && area.getGuidelines() != null 
            ? area.getGuidelines() 
            : "No specific guidelines available.";
            helper.setFrom("extrahamham@gmail.com"); 
            helper.setTo(reservation.getUseremail());
            helper.setSubject("Reservation Confirmation - " + reservation.getAreaname());

 
            String emailContent = "Dear " + reservation.getUsername() + ",\n\n" +
            "Your reservation has been successfully confirmed. Here are the details:\n\n" +
            "Recreational Area: " + reservation.getAreaname() + "\n" +
            "Date: " + reservation.getUserStartDate()+ "\n" +
            "Time: " + reservation.getUser_start_time() + " to " + reservation.getUser_end_time() + "\n" +
    
            "Please note the following guidelines:\n" +
            guidelines + "\n\n" +
            "This email serves as proof of your reservation. Please present it on the day of use.\n\n" +
            "Thank you for using our services.\n\n" +
            "Best regards,\n" +
            "The Accord Team";

    helper.setText(emailContent);
            // Send the email
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    

    public List<Reservation> listReservation() {
        checkStatus();
        return reservRepository.findAll();
    }

    public Reservation findByUseremail(String email) {
        return reservRepository.findByUseremail(email);
    }

    public Reservation findReservationsByAreaName(String areaname) {
        return reservRepository.findByAreaname(areaname);
    }

    public Reservation findByArea(Area area) {
        return reservRepository.findByArea(area);
    }

    public List<Reservation> findAllByAreaname(String areaname) {
        return reservRepository.findAllByAreaname(areaname);
    }

    public List<Reservation> findAllByUsername(String username) {
        return reservRepository.findAllByUsername(username);
    }

    public List<Reservation> findAllByUser(User user) {
        return reservRepository.findAllByUser(user);
    }

    public void updateAllAreaname(List<Reservation> reservation, String areaname) {
        reservation.forEach(rA -> rA.setAreaname(areaname));
        reservRepository.saveAll(reservation);
    }
    public List<Reservation> findReservationsByUserEmail(String useremail) {
        return reservRepository.findAllByUseremail(useremail);
    }

    public List<Reservation> findReservationsByUserEmailStatusCompletedAndCancelled(User user) {
        List<String> statuses = Arrays.asList("COMPLETED", "CANCELLED");
        return reservRepository.findByUserAndStatusIn(user, statuses);
    }

    public List<Reservation> findReservationsByUserEmailStatusStartedAndNotStarted(User user) {
        List<String> statuses = Arrays.asList("STARTED", "NOT STARTED");
        return reservRepository.findByUserAndStatusIn(user, statuses);
    }

    public List<Reservation> findReservationsByUserEmailStatusAndNotStarted(User user) {
        List<String> statuses = Arrays.asList("NOT STARTED");
        return reservRepository.findByUserAndStatusIn(user, statuses);
    }

    public List<Reservation> findReservationsByAreaAndStatusNotStarted(Area area) {
        List<String> statuses = Arrays.asList("NOT STARTED");
        return reservRepository.findAllByAreaAndStatusIn(area, statuses);
    }

    public Long countReservationsStatusNotStartedAndCurrentMonth(User user) {
        List<String> statuses = Arrays.asList("NOT STARTED");
        LocalDate now = LocalDate.now();
        LocalDate startMonth = now.withDayOfMonth(1);
        LocalDate endMonth = startMonth.plusMonths(1);
        return reservRepository.countByUserAndStatusInAndUserStartDateBetween(user, statuses, startMonth, endMonth);
    }

    public Long countAllReservationStatusStartedAndNotStarted() {
        List<String> statuses = Arrays.asList("STARTED", "NOT STARTED");
        return reservRepository.countByStatusIn(statuses);
    }

    public Long countAllReservationStatusCompleted() {
        List<String> statuses = Arrays.asList("COMPLETED");
        return reservRepository.countByStatusIn(statuses);
    }

    public Long countAllReservationStatusCancelled() {
        List<String> statuses = Arrays.asList("CANCELLED");
        return reservRepository.countByStatusIn(statuses);
    }

    public Long countAllReservationStatusNotStarted() {
        List<String> statuses = Arrays.asList("NOT STARTED");
        return reservRepository.countByStatusIn(statuses);
    }

    public Reservation findReservationById(Long id) {
        return reservRepository.findById(id).orElse(null);
    }

    public void cancelBooking(Long id) {
        Reservation r =  reservRepository.findById(id).orElse(null);
        if("COMPLETED".equals(r.getStatus())) {
            return;
        }
        r.setStatus("CANCELLED");
        reservRepository.save(r);
    }

    public void checkStatus() {
        LocalDateTime dateTime = LocalDateTime.now();
        List<Reservation> r = reservRepository.findAll();
        r.forEach(rA -> {
            if("CANCELLED".equals(rA.getStatus())) {
                return;
            }
            if("COMPLETED".equals(rA.getStatus())) {
                return;
            }
            else if(dateTime.isAfter(LocalDateTime.of(rA.getUserStartDate(), rA.getUser_end_time()))) {
                rA.setStatus("COMPLETED");
            }
            else if((dateTime.isBefore(LocalDateTime.of(rA.getUserStartDate(), rA.getUser_end_time()))) &&
                    dateTime.isAfter(LocalDateTime.of(rA.getUserStartDate(), rA.getUser_start_time()))) {
                rA.setStatus("STARTED");
            }
            else {
                rA.setStatus("NOT STARTED");
            }
        });
        reservRepository.saveAll(r);
    }

    public void updateAllEmail(List<Reservation> reservations, String email) {
        reservations.forEach(rA -> rA.setUseremail(email));
        reservRepository.saveAll(reservations);
    }

    public void updateAllName(List<Reservation> reservations, String name) {
        reservations.forEach(rA -> rA.setUsername(name));
        reservRepository.saveAll(reservations);
    }

}