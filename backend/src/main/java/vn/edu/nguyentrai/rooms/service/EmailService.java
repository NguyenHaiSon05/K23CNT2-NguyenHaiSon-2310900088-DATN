package vn.edu.nguyentrai.rooms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import vn.edu.nguyentrai.rooms.entity.Booking;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(JavaMailSender mailSender, @Value("${app.mail.from:}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Async
    public void sendBookingMail(Booking b, String title) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(b.getUser().getEmail());
            msg.setSubject("[RoomS] " + title);
            msg.setText("Xin chào " + b.getUser().getFullName() + ",\n\n"
                    + title + "\n"
                    + "Phòng: " + b.getRoom().getRoomCode() + " - " + b.getRoom().getName() + "\n"
                    + "Ngày: " + b.getBookingDate() + "\n"
                    + "Giờ: " + b.getStartTime() + " - " + b.getEndTime() + "\n"
                    + "Trạng thái: " + b.getStatus() + "\n\n"
                    + "Hệ thống RoomS - Trường Đại học Nguyễn Trãi");
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("Không gửi được email: {}", e.getMessage());
        }
    }
}