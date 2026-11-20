package vn.edu.nguyentrai.rooms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.nguyentrai.rooms.entity.RoomType;
import vn.edu.nguyentrai.rooms.repository.RoomTypeRepository;

@RestController
@RequestMapping("/api/room-types")
public class RoomTypeController {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeController(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    @GetMapping
    public List<RoomType> getAll() {
        return roomTypeRepository.findAll();
    }
}