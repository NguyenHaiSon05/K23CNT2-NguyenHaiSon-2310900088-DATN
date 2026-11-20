package vn.edu.nguyentrai.rooms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.edu.nguyentrai.rooms.dto.BookingRequest;
import vn.edu.nguyentrai.rooms.dto.BookingResponse;
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
}