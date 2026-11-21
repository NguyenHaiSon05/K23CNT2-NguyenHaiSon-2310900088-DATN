package vn.edu.nguyentrai.rooms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import vn.edu.nguyentrai.rooms.dto.BookingRequest;
import vn.edu.nguyentrai.rooms.dto.BookingResponse;
import vn.edu.nguyentrai.rooms.entity.BookingStatus;
import vn.edu.nguyentrai.rooms.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody BookingRequest req, Authentication auth) {
        return BookingResponse.from(bookingService.create(auth.getName(), req));
    }

    @GetMapping("/my")
    public List<BookingResponse> myBookings(Authentication auth) {
        return bookingService.findMine(auth.getName()).stream()
                .map(BookingResponse::from).toList();
    }

    @GetMapping("/pending")
    public List<BookingResponse> pending() {
        return bookingService.findByStatus(BookingStatus.PENDING).stream()
                .map(BookingResponse::from).toList();
    }

    @PutMapping("/{id}/approve")
    public BookingResponse approve(@PathVariable Long id) {
        return BookingResponse.from(bookingService.decide(id, true));
    }

    @PutMapping("/{id}/reject")
    public BookingResponse reject(@PathVariable Long id) {
        return BookingResponse.from(bookingService.decide(id, false));
    }

    @PutMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id, Authentication auth) {
        return BookingResponse.from(bookingService.cancel(id, auth.getName()));
    }
}