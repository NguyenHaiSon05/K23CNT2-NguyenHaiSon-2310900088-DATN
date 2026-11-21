package vn.edu.nguyentrai.rooms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import vn.edu.nguyentrai.rooms.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);

    @Query("""
        select r from Room r
        where r.status = 'ACTIVE'
          and r.id not in (
            select b.room.id from Booking b
            where b.bookingDate = :date
              and b.status in :statuses
              and b.startTime < :endTime
              and b.endTime > :startTime
          )
        order by r.roomCode
        """)
    List<Room> findAvailable(@Param("date") java.time.LocalDate date,
                             @Param("startTime") java.time.LocalTime startTime,
                             @Param("endTime") java.time.LocalTime endTime,
                             @Param("statuses") java.util.Collection<vn.edu.nguyentrai.rooms.entity.BookingStatus> statuses);
}