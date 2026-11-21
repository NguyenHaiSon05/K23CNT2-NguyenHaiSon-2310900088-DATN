package vn.edu.nguyentrai.rooms.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import vn.edu.nguyentrai.rooms.dto.BookingRequest;
import vn.edu.nguyentrai.rooms.entity.Booking;
import vn.edu.nguyentrai.rooms.entity.BookingStatus;
import vn.edu.nguyentrai.rooms.entity.Room;
import vn.edu.nguyentrai.rooms.entity.User;
import vn.edu.nguyentrai.rooms.repository.BookingRepository;
import vn.edu.nguyentrai.rooms.repository.RoomRepository;
import vn.edu.nguyentrai.rooms.repository.UserRepository;


@Service
public class BookingService {

    private static final List<BookingStatus> HOLDING = List.of(BookingStatus.PENDING, BookingStatus.APPROVED);

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository,
                          UserRepository userRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Booking create(String username, BookingRequest req) {
        // 1. Kiểm tra giờ hợp lệ
        if (!req.getStartTime().isBefore(req.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giờ kết thúc phải sau giờ bắt đầu");
        }
        if (req.getBookingDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể đặt phòng cho ngày đã qua");
        }
        if (req.getBookingDate().isEqual(LocalDate.now()) && req.getStartTime().isBefore(LocalTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giờ bắt đầu đã qua");
        }

        // 2. Khóa dòng phòng: các yêu cầu đặt cùng phòng phải xếp hàng
        Room room = roomRepository.findByIdForUpdate(req.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng"));
        if (!"ACTIVE".equals(room.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng đang bảo trì, không thể đặt");
        }

        // 3. Kiểm tra trùng lịch (đã nằm trong khóa nên kết quả đáng tin cậy)
        long overlap = bookingRepository.countOverlapping(
                room.getId(), req.getBookingDate(), req.getStartTime(), req.getEndTime(), HOLDING);
        if (overlap > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phòng đã có người đặt trong khung giờ này");
        }

        // 4. Quy tắc duyệt: giảng viên, quản lý, admin tự duyệt; sinh viên chờ duyệt
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy người dùng"));
        String role = user.getRole().getName();

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setBookingDate(req.getBookingDate());
        booking.setStartTime(req.getStartTime());
        booking.setEndTime(req.getEndTime());
        booking.setPurpose(req.getPurpose());
        booking.setStatus("STUDENT".equals(role) ? BookingStatus.PENDING : BookingStatus.APPROVED);
        Booking saved = bookingRepository.save(booking);
        emailService.sendBookingMail(saved, saved.getStatus() == BookingStatus.PENDING
                ? "Yêu cầu đặt phòng đã được ghi nhận, đang chờ duyệt"
                : "Đặt phòng thành công");
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Booking> findMine(String username) {
        return bookingRepository.findByUserUsernameOrderByBookingDateDescStartTimeDesc(username);
    }

    @Transactional(readOnly = true)
    public List<Booking> findByStatus(BookingStatus status) {
        return bookingRepository.findByStatusOrderByBookingDateAscStartTimeAsc(status);
    }

    @Transactional
    public Booking decide(Long id, boolean approve) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt đặt"));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ xử lý được lượt đang chờ duyệt");
        }
        booking.setStatus(approve ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);
        emailService.sendBookingMail(saved, approve ? "Yêu cầu đặt phòng đã được duyệt"
                : "Yêu cầu đặt phòng bị từ chối");
        return saved;
    }

    @Transactional
    public Booking cancel(Long id, String username) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lượt đặt"));

        User actor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy người dùng"));
        String role = actor.getRole().getName();
        boolean isManager = "ADMIN".equals(role) || "ROOM_MANAGER".equals(role);
        boolean isOwner = booking.getUser().getId().equals(actor.getId());

        if (!isOwner && !isManager) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền hủy lượt đặt này");
        }
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lượt đặt này không thể hủy");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> calendar(Long roomId, LocalDate from, LocalDate to) {
        return bookingRepository.findForCalendar(roomId, from, to, HOLDING);
    }
}