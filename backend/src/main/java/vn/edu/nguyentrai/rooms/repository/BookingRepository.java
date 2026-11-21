package vn.edu.nguyentrai.rooms.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.nguyentrai.rooms.entity.Booking;
import vn.edu.nguyentrai.rooms.entity.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        select count(b) from Booking b
        where b.room.id = :roomId
          and b.bookingDate = :date
          and b.status in :statuses
          and b.startTime < :endTime
          and b.endTime > :startTime
        """)
    long countOverlapping(@Param("roomId") Long roomId,
                          @Param("date") LocalDate date,
                          @Param("startTime") LocalTime startTime,
                          @Param("endTime") LocalTime endTime,
                          @Param("statuses") Collection<BookingStatus> statuses);
    List<Booking> findByUserUsernameOrderByBookingDateDescStartTimeDesc(String username);
    List<Booking> findByStatusOrderByBookingDateAscStartTimeAsc(BookingStatus status);

    @Query("""
        select b from Booking b
        where b.room.id = :roomId
          and b.bookingDate between :from and :to
          and b.status in :statuses
        order by b.bookingDate, b.startTime
        """)
    List<Booking> findForCalendar(@Param("roomId") Long roomId,
                                  @Param("from") LocalDate from,
                                  @Param("to") LocalDate to,
                                  @Param("statuses") Collection<BookingStatus> statuses);

    long countByStatus(BookingStatus status);

    @Query("""
        select b.room.roomCode, b.room.name, count(b)
        from Booking b
        where b.status in :statuses
        group by b.room.id, b.room.roomCode, b.room.name
        order by count(b) desc
        """)
    List<Object[]> topRooms(@Param("statuses") Collection<BookingStatus> statuses);
}