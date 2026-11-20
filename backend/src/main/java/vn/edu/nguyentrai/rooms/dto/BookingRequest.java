package vn.edu.nguyentrai.rooms.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

    @NotNull(message = "Phải chọn phòng")
    private Long roomId;

    @NotNull(message = "Phải chọn ngày")
    private LocalDate bookingDate;

    @NotNull(message = "Phải chọn giờ bắt đầu")
    private LocalTime startTime;

    @NotNull(message = "Phải chọn giờ kết thúc")
    private LocalTime endTime;

    private String purpose;
}