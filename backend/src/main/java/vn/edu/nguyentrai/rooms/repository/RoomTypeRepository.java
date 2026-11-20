package vn.edu.nguyentrai.rooms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.nguyentrai.rooms.entity.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
}