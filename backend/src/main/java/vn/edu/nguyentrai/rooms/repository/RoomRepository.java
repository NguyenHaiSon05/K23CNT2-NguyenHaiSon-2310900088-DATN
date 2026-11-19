package vn.edu.nguyentrai.rooms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.nguyentrai.rooms.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}