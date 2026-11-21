package vn.edu.nguyentrai.rooms.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import vn.edu.nguyentrai.rooms.dto.RoomRequest;
import vn.edu.nguyentrai.rooms.entity.BookingStatus;
import vn.edu.nguyentrai.rooms.entity.Room;
import vn.edu.nguyentrai.rooms.entity.RoomType;
import vn.edu.nguyentrai.rooms.repository.RoomRepository;
import vn.edu.nguyentrai.rooms.repository.RoomTypeRepository;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomController(RoomRepository roomRepository, RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    @GetMapping
    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    @GetMapping("/available")
    public List<Room> available(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end) {
        if (!start.isBefore(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giờ kết thúc phải sau giờ bắt đầu");
        }
        return roomRepository.findAvailable(date, start, end,
                List.of(BookingStatus.PENDING, BookingStatus.APPROVED));
    }

    @GetMapping("/{id}")
    public Room getOne(@PathVariable Long id) {
        return findOrThrow(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room create(@Valid @RequestBody RoomRequest req) {
        Room room = new Room();
        applyRequest(room, req);
        return roomRepository.save(room);
    }

    @PutMapping("/{id}")
    public Room update(@PathVariable Long id, @Valid @RequestBody RoomRequest req) {
        Room room = findOrThrow(id);
        applyRequest(room, req);
        return roomRepository.save(room);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        findOrThrow(id);
        roomRepository.deleteById(id);
    }

    private Room findOrThrow(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng"));
    }

    private void applyRequest(Room room, RoomRequest req) {
        room.setRoomCode(req.getRoomCode().trim());
        room.setName(req.getName().trim());
        room.setCapacity(req.getCapacity());
        room.setLocation(req.getLocation());
        room.setStatus(req.getStatus());
        if (req.getRoomTypeId() != null) {
            RoomType type = roomTypeRepository.findById(req.getRoomTypeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại phòng không tồn tại"));
            room.setRoomType(type);
        } else {
            room.setRoomType(null);
        }
    }
}