package vn.edu.nguyentrai.rooms.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.edu.nguyentrai.rooms.entity.Booking;

@Getter
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long roomId;
    private String roomCode;
    private String roomName;
    private String username;
    private String fullName;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String purpose;
    private String status;

    public static BookingResponse from(Booking b) {
        return new BookingResponse(
                b.getId(), b.getRoom().getId(), b.getRoom().getRoomCode(), b.getRoom().getName(),
                b.getUser().getUsername(), b.getUser().getFullName(),
                b.getBookingDate(), b.getStartTime(), b.getEndTime(),
                b.getPurpose(), b.getStatus().name());
    }
}