package com.accord.controller;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.accord.Entity.Area;
import com.accord.Entity.Rating;
import com.accord.Entity.Reservation;
import com.accord.Entity.User;
import com.accord.repository.UserRepository;
import com.accord.service.AreaService;
import com.accord.service.RatingService;
import com.accord.service.ReservService;
import com.accord.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class RegisterLoginController {
    
    @Autowired
    private UserService userService;
    @Autowired
    UserRepository repo;

    @Autowired
    private AreaService areaService;

    @Autowired
    private ReservService reservService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/")
    public String index() {
        return "login_page";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerRequest", new User());;
        return "register_page";
    }

    @GetMapping("/register_page_admin")
    public String getRegisterPageAdmin(Model model) {
        model.addAttribute("registerRequest", new User());
        return "register_page_admin";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute User user, @RequestParam("file") MultipartFile tenancy, MultipartFile valid, Model model) throws IOException {
        Boolean checkUserEmail = userService.checkEmail(user.getEmail());
        Boolean checkUserPhone = userService.checkPhone(user.getContactnumber());
        if(checkUserEmail == null || checkUserPhone == null) {
            model.addAttribute("error", "Duplicate email/Contact Number");
            return "register_page";
        }
        else {
            User registeredUser = userService.registerUser(user.getName(), user.getPassword(), user.getEmail(), user.getContactnumber(),
                    user.getBlock_num(), user.getLot_num(), user.getProperty_status(), tenancy, valid, "ROLE_USER");
            model.addAttribute("error", "Duplicate email/phone number");
            return registeredUser == null ? "register_page" : "redirect:/";
        }
    }

    @PostMapping("/register_page_admin")
    public String registerAdmin(@ModelAttribute User user, Model model) throws IOException {
        Boolean checkUser = userService.checkEmail(user.getEmail());
        if(checkUser == null) {
            model.addAttribute("error", "Duplicate email");
            return "register_page";
        }
        else {
            User adminRegister = userService.registerAdmin(user.getEmail(), user.getPassword(), "ROLE_ADMIN");
            model.addAttribute("error", "Duplicate email");
            return adminRegister == null ? "redirect:/register_page_admin" : "redirect:/";
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        User user = userService.findByEmail(email).orElse(null);
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            session.setAttribute("userId", user.getId());
            User currentUser = userService.findById(user.getId()).orElse(null);
            session.setAttribute("loggedInUser", currentUser);

            if (currentUser.getRole().contains("ROLE_USER") && (currentUser.getConfirmationAccount() != null)) {
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/dash_user")).build();
            }
            else if (currentUser.getRole().contains("ROLE_USER") && currentUser.getConfirmationAccount() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account not confirmed");
            } 
            else {
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/dash_admin")).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/dash_user")
    public String showDashboard(Model m, HttpSession session) {
        reservService.checkStatus();
        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findById(userId).orElse(null);
        m.addAttribute("reservation", reservService.findReservationsByUserEmailStatusAndNotStarted(currentUser));
        m.addAttribute("number", reservService.countReservationsStatusNotStartedAndCurrentMonth(currentUser));
        m.addAttribute("rating", ratingService.listByUser(currentUser));
        if (currentUser != null) {
            if (currentUser.getProfile_picture() != null) {
                String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
                m.addAttribute("profilePictureBase64", base64Image);
            }
            m.addAttribute("user", currentUser); 
        }
        return "dashboard_user";
    }
        
    @GetMapping("/dash_admin")
    public String showDashboardAdmin(Model m, HttpSession session) {
        reservService.checkStatus();

        m.addAttribute("recentUsers",repo.findAll());
        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findById(userId).orElse(null);
        long cancelledBookingsCount = reservService.listReservation()
        .stream()
        .filter(reservation -> "CANCELLED".equalsIgnoreCase(reservation.getStatus()))
        .count();
        m.addAttribute("reservationActiveAndNot", reservService.countAllReservationStatusStartedAndNotStarted());
        m.addAttribute("reservationNotStarted", reservService.countAllReservationStatusNotStarted());
        m.addAttribute("reservationCompleted", reservService.countAllReservationStatusCompleted());
        m.addAttribute("cancelledBookings", cancelledBookingsCount);
        m.addAttribute("areas", areaService.getAllAreas());
        
        List<User> users = userService.getAllUser();
        m.addAttribute("users", users);
        
        if (currentUser != null) {
            if (currentUser.getProfile_picture() != null) {
                String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
                m.addAttribute("profilePictureBase64", base64Image);
            }
            m.addAttribute("user", currentUser); 
        }
        return "dashboard_admin";
    }

    @GetMapping("/dashboard_admin")
    public String allUsers(@ModelAttribute("form") User form, Model model) {
        List<User> users = userService.getAllUser();
        model.addAttribute("result", users);
        return "viewusers_page";
    }

    @GetMapping("/forgotPassword_page")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("resetRequest", new User());
        return "forgotPassword_page";
    }

    @GetMapping("/profile")
    public String manageProfile(Model model, HttpSession session, MultipartFile prof) throws IOException {
        reservService.checkStatus();
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId).orElse(null);
        if (user != null) {
            if (user.getProfile_picture() != null) {
                String base64Image = Base64.getEncoder().encodeToString(user.getProfile_picture());
                model.addAttribute("profilePictureBase64", base64Image);
            }
            model.addAttribute("user", user);
        }
        return "manage_profile";
    }
    
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, @RequestParam("prof") MultipartFile prof, HttpSession session, Model model, RedirectAttributes redirectAttributes) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        if(user.getPassword() != null) {
            userService.updatePassword(userId, user.getPassword());
            if(user.getName() != null && user.getEmail() != null) {
                String s = userService.update4(userId, user.getName(), user.getEmail());
                if(s == "1") {
                    redirectAttributes.addFlashAttribute("error", "Email Already Exists");
                    return "redirect:/profile";
                }
            }
        }
        userService.update2(userId, prof);
        return "redirect:/profile";
    }

    @GetMapping("/mb-user")
    public String manageBookingsUser(Model m, HttpSession session) {
        reservService.checkStatus();
        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findById(userId).orElse(null);
        m.addAttribute("area", areaService.getAllAreas());
        m.addAttribute("reservation", reservService.findReservationsByUserEmailStatusStartedAndNotStarted(currentUser));
        m.addAttribute("reservationHistory", reservService.findReservationsByUserEmailStatusCompletedAndCancelled(currentUser));
        if (currentUser != null) {
            if (currentUser.getProfile_picture() != null) {
                String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
                m.addAttribute("profilePictureBase64", base64Image);
            }
            m.addAttribute("user", currentUser);
        }
        return "managebookingsUser";
    }

    @GetMapping("/mb-admin")
    public String manageBookingsAdmin(Model model) {
        reservService.checkStatus();
        List<Reservation> reservationList = reservService.listReservation();
        model.addAttribute("reservationList", reservationList);
        return "managebookingsAdmin";
    }

    @GetMapping("/analytics")
public String facilityRatingAdmin(
    @RequestParam(required = false) String area,
    @RequestParam(required = false) String timeFrame,
    @RequestParam(required = false) String startDate,
    @RequestParam(required = false) String endDate,
    Model model) {
   
    try {
        // Get all areas for the dropdown
        List<Area> allAreas = areaService.getAllAreas();
        final List<Rating> allRatings = ratingService.findAll();
        
        // Apply area and date filters
        List<Rating> finalFilteredRatings = allRatings;
        List<Area> filteredAreas;

        // Apply area filter
        if (area != null && !area.isEmpty()) {
            filteredAreas = allAreas.stream()
                .filter(a -> a.getId().toString().equals(area))
                .collect(Collectors.toList());
            
            finalFilteredRatings = finalFilteredRatings.stream()
                .filter(r -> r.getArea().getId().toString().equals(area))
                .collect(Collectors.toList());
        } else {
            filteredAreas = allAreas;
        }

        // Apply date filters
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            finalFilteredRatings = finalFilteredRatings.stream()
                .filter(r -> !r.getRatingDate().isBefore(start) && !r.getRatingDate().isAfter(end))
                .collect(Collectors.toList());
        }

        // Create chart data
        Map<String, Object> chartData = new HashMap<>();
        
        // Prepare area names and average ratings
        List<String> areaNames = filteredAreas.stream()
            .map(Area::getName)
            .collect(Collectors.toList());
        
        // Calculate average ratings using the final filtered ratings
        final List<Rating> finalRatings = finalFilteredRatings;
        List<Double> averageRatings = filteredAreas.stream()
            .map(a -> {
                List<Rating> areaRatings = finalRatings.stream()
                    .filter(r -> r.getArea().getId().equals(a.getId()))
                    .collect(Collectors.toList());
                
                return areaRatings.isEmpty() ? 0.0 : 
                    areaRatings.stream()
                        .mapToInt(Rating::getStars)
                        .average()
                        .orElse(0.0);
            })
            .collect(Collectors.toList());

        // Calculate rating distribution
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int stars = i;
            long count = finalFilteredRatings.stream()
                .filter(r -> r.getStars() == stars)
                .count();
            ratingDistribution.put(i, count);
        }

        // Add data to chart data map
        chartData.put("areaNames", areaNames);
        chartData.put("averageRatings", averageRatings);
        chartData.put("ratingDistribution", ratingDistribution);

        // Add everything to model
        model.addAttribute("areas", allAreas);
        model.addAttribute("filteredAreas", filteredAreas);
        model.addAttribute("ratings", finalFilteredRatings);
        model.addAttribute("chartData", chartData);
        model.addAttribute("selectedArea", area);
        model.addAttribute("selectedTimeFrame", timeFrame);
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);
       
    } catch (Exception e) {
        System.err.println("Error in facilityRatingAdmin: " + e.getMessage());
        e.printStackTrace();
    }
   
    return "facilityRating";
}

    @GetMapping("/ratings")
    public String viewRatingsList(Model model) {
        reservService.checkStatus();
        model.addAttribute("areas", areaService.getAllAreas());
        return "viewRatingsList";
    }

    @GetMapping("/rate-area/{id}")
    public String viewRateArea(@PathVariable Long id, HttpSession session, Model model) {
        reservService.checkStatus();
        Reservation reservation = reservService.findReservationById(id);
        Area area = areaService.getAreaById(reservation.getArea().getId());
        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findById(userId).orElse(null);
        if (currentUser != null) {
            if (currentUser.getProfile_picture() != null) {
                String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
                model.addAttribute("profilePictureBase64", base64Image);
            }
            model.addAttribute("user", currentUser); 
        }
        if(ratingService.returnRatingUseremailAndAreaname(currentUser.getEmail(), area.getName()) != null) {
            Rating ratingCurrent = ratingService.returnRatingUseremailAndAreaname(currentUser.getEmail(), area.getName());
            model.addAttribute("ratingCurr", ratingCurrent);
        }
        Rating rating = ratingService.findByUserAndArea(currentUser, area);
        if(rating == null) {
            rating = new Rating();
        }
        model.addAttribute("reservation", reservation);
        model.addAttribute("rating", rating);
        model.addAttribute("area", area);
        return "ratingPageUser";
    }

    @PostMapping("/submit-rating/{id}")
    public String submitRating(@PathVariable Long id, @ModelAttribute Rating rating, @RequestParam int stars, @RequestParam String feedback, HttpSession session, Model model) {
        Reservation reservation = reservService.findReservationById(id);
        Area area = areaService.getAreaById(reservation.getArea().getId());
        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findById(userId).orElse(null);
        if (currentUser != null) {
            Rating checkRating = ratingService.findByUserAndArea(currentUser, area);
            if(checkRating == null) {
                ratingService.createRating(currentUser, area, feedback, stars);
            }
            else {
                checkRating.setFeedback(feedback);
                checkRating.setStars(stars);
                ratingService.saveUpdateRating(checkRating);
            }
        }

        return "redirect:/mb-user";
    }

    @GetMapping("/bookings/{id}")
    public String viewBookingsDetails(@PathVariable Long id, Model model) {
        reservService.checkStatus();
        Area area = areaService.getAreaById(id);
        model.addAttribute("area", area);
        return "view_recreational_area";
    }

    @GetMapping("/booking-area/{id}")
    public String viewBookingArea(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        reservService.checkStatus();
        Area area = areaService.getAreaById(id);
        model.addAttribute("area", area);
        model.addAttribute("reservation", new Reservation());
        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findById(userId).orElse(null);
        if (currentUser != null) {
            if (currentUser.getProfile_picture() != null) {
                String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
                model.addAttribute("profilePictureBase64", base64Image);
            }
            model.addAttribute("user", currentUser); 
        }
        if(area.getAvailable() == false) {
            redirectAttributes.addFlashAttribute("error", "Area Not Available");
            return "redirect:/areas-user";
        }
        return "book_area2";
    }

    @GetMapping("/submit_book")
    public String addBooking(@ModelAttribute Reservation reservation, HttpSession session, RedirectAttributes redirectAttributes) {
        Area area = areaService.getByName(reservation.getAreaname());
        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findById(userId).orElse(null);
        LocalDate startDate = LocalDate.now();
        if ((reservation.getUser_start_time().isBefore(area.getStartTime())) || 
            (reservation.getUser_end_time().isAfter(area.getEndTime())) || 
            (reservation.getUserStartDate().isBefore(startDate)) ||
            (reservation.getUserStartDate().isEqual(startDate))) {
            redirectAttributes.addFlashAttribute("error", "Invalid Time/Date Input");
            return "redirect:/booking-area/" + area.getId();
        }
        else if((reservService.checkReservation(reservation, area)) == 1) {
            redirectAttributes.addFlashAttribute("error", "Another User Has Booked The Same Time");
            return "redirect:/booking-area/" + area.getId();
        }
        reservService.bookReservation(reservation, area, currentUser);
        return "redirect:/areas-user";
    }

    @GetMapping("/areas-admin")
    public String recreationalAreasList(Model model) {
        reservService.checkStatus();
        List<Area> areaList = areaService.getAllAreas();
        model.addAttribute("areaList", areaList);
        return "am_recreationalAreasList";
    }

    @GetMapping("/areas-user")
    public String showAreas(Model m, HttpSession session) {
        reservService.checkStatus();
        m.addAttribute("areaList", areaService.getAllAvailableAreas());
        m.addAttribute("areaListFalse", areaService.getAllUnavailableAreas());
        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findById(userId).orElse(null);
        if (currentUser != null) {
            if (currentUser.getProfile_picture() != null) {
                String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
                m.addAttribute("profilePictureBase64", base64Image);
            }
            m.addAttribute("user", currentUser);
        }
        return "am_recreationalAreasList_user";
    }

    @GetMapping("/cancelBooking/{id}")
    public String cancelBookingUser(@PathVariable Long id, Model model) {
        reservService.checkStatus();
        reservService.cancelBooking(id);
        return "redirect:/mb-user";
    }

    @GetMapping("/cancelAdmin/{id}")
    public String cancelBookingAdmin(@PathVariable Long id, Model model) {
        reservService.checkStatus();
        reservService.cancelBooking(id);
        return "redirect:/mb-admin";
    }

    @GetMapping("/view-recreational-area-user/{id}")
    public String viewRecreationalAreasUser(@PathVariable("id") Long id, Model m, HttpSession session) {
        reservService.checkStatus();
        Area area = areaService.getAreaById(id);
        m.addAttribute("area", area);
        Long userId = (Long) session.getAttribute("userId");
        User currentUser = userService.findById(userId).orElse(null);
        if (currentUser != null) {
            if (currentUser.getProfile_picture() != null) {
                String base64Image = Base64.getEncoder().encodeToString(currentUser.getProfile_picture());
                m.addAttribute("profilePictureBase64", base64Image);
            }
            m.addAttribute("user", currentUser); 
        }
        return "view_recreational_area_user";
    }

    @GetMapping("/view-recreational-area/{id}")
    public String viewRecreationalArea(@PathVariable Long id, Model model) {
        reservService.checkStatus();
        Area area = areaService.getAreaById(id);
        model.addAttribute("area", area);
        return "view_recreational_area";
    }

    @GetMapping("/accounts")
    public String UserAccountsAdmin(Model model) {
        List<User> pendingUsers = userService.pendingAccountConfirmation();
        model.addAttribute("pendingUsers", pendingUsers);
        List<User> users = userService.getAllUser();
        model.addAttribute("users", users);
        return "UserAccounts_page";
    }

    @GetMapping("/confirm-account/{id}")
    public String confirmAccount(@PathVariable Long id, Model model) {
        userService.confirmAccount(id);
        return "redirect:/accounts";
    }

    @GetMapping("/add_area")
    public String addRecreationalArea(Model model) {
        model.addAttribute("area", new Area());
        return "addNewRecreational_area";
    }

    @PostMapping("/add-area")
    public String addArea(Area area, @RequestParam("fileCover") MultipartFile cover, @RequestParam("fileAdd") MultipartFile add, RedirectAttributes redirectAttributes) {
        if((areaService.checkName(area.getName())) == 1) {
            redirectAttributes.addAttribute("error", "Area Name Already Exists");
            return "redirect:/add_area";
        }
        areaService.createArea(area, cover, add);
        return "view_recreational_area";
    }

    @GetMapping("/modifyrec_admin/{id}")
    public String showModify(@PathVariable Long id, Model model) {
        Area area = areaService.getAreaById(id);
        model.addAttribute("area", area);
        return "modifyDelete_area";
    }

    @PostMapping("/modifyrec_admin/{id}")
    public String modifyRecreationalArea(@ModelAttribute("area") Area area, @PathVariable Long id, @RequestParam("fileCover") MultipartFile cover, @RequestParam("fileAdd") MultipartFile add,
                                    @RequestParam("schedule-start-time") String startTime, @RequestParam("schedule-end-time") String endTime,
                                    @RequestParam(value = "available", required = false) Boolean available, Model model, RedirectAttributes redirectAttributes) throws IOException {

        Area areaEdit = areaService.getAreaById(id);
        Area check = areaService.getByName(area.getName());
        List<Reservation> reservation = reservService.findAllByAreaname(areaEdit.getName());
        List<Rating> rating = ratingService.listByAreaname(areaEdit.getName());
        
        if((areaService.checkName(area.getName())) == 1 && (areaEdit.getId() != check.getId())) {
            redirectAttributes.addAttribute("error", "Area Name Already Exists");
            return "redirect:/modifyrec_admin/" + area.getId();
        }
        areaEdit.setStartTime(LocalTime.parse(startTime));
        areaEdit.setEndTime(LocalTime.parse(endTime));
        areaEdit.setAvailable(available != null && available);

        areaEdit.setName(area.getName());
        areaEdit.setGuidelines(area.getGuidelines());

        areaService.updateArea(areaEdit, cover, add);
        reservService.updateAllAreaname(reservation, area.getName());
        ratingService.updateAllAreaname(rating, area.getName()); 

        Area updatedArea = areaService.getAreaById(id);
        model.addAttribute("area", updatedArea);
    
        return "redirect:/areas-admin"; 
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        model.addAttribute("delete", "Are you sure you would like to delete this user?");
        userService.deleteUser(id);
        return "redirect:/dash_admin";
    }

    @GetMapping("/deleteArea/{id}")
    public String deleteArea(@PathVariable Long id, Model model) {
        model.addAttribute("delete", "Are you sure you would like to delete this area?");
        areaService.deleteArea(id);
        return "redirect:/areas-admin";
    }

    @GetMapping("/details/{id}")
    public String UserAcc_VIewDetails(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElse(null);
        model.addAttribute("user", user);
        return "UserAccounts_viewDetails";
    }

    @PostMapping("/forgotPassword_email")
    public String processForgotPassword(@ModelAttribute("resetRequest") User resetRequest, Model model) {
        User user = userService.findByEmail(resetRequest.getEmail()).orElse(null);
        if (user != null) {
            boolean isEmailSent = userService.sendPasswordResetEmail(user);
            if (isEmailSent) {
                model.addAttribute("message", "Password reset email sent successfully.");
                return "forgotPassword_email";
            } else {
                model.addAttribute("error", "Failed to send password reset email. Please try again.");
            }
        } else {
            model.addAttribute("error", "No user found with the provided email.");
        }
        return "forgotPassword_page"; 
    }

    @GetMapping("/forgotPassword_setPass")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        model.addAttribute("loginRequest", new User());
        model.addAttribute("token", token); 
        return "forgotPassword_setPass";
    }

    @PostMapping("/forgotPassword_setPass")
    public String setPassword(@RequestParam("token") String token,
                          @RequestParam("newPassword") String newPassword,
                          @RequestParam("confirmPassword") String confirmPassword,
                          Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "forgotPassword_setPass"; 
        }

        boolean success = userService.resetPassword(token, newPassword);
        if (success) {
            return "redirect:/?resetSuccess=true"; 
        } else {
            model.addAttribute("error", "An error occurred. Please try again.");
            return "forgotPassword_setPass"; 
        }
    }
}