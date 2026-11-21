package vn.edu.nguyentrai.rooms.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.nguyentrai.rooms.dto.StatsResponse;
import vn.edu.nguyentrai.rooms.entity.BookingStatus;
import vn.edu.nguyentrai.rooms.repository.BookingRepository;
import vn.edu.nguyentrai.rooms.repository.RoomRepository;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public StatsController(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public StatsResponse stats() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (BookingStatus s : BookingStatus.values()) {
            byStatus.put(s.name(), bookingRepository.countByStatus(s));
        }
        List<StatsResponse.TopRoom> top = bookingRepository
                .topRooms(List.of(BookingStatus.PENDING, BookingStatus.APPROVED)).stream()
                .limit(5)
                .map(o -> new StatsResponse.TopRoom((String) o[0], (String) o[1], (Long) o[2]))
                .toList();
        return new StatsResponse(roomRepository.count(), bookingRepository.count(), byStatus, top);
    }
}