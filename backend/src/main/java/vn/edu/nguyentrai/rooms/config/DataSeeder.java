package vn.edu.nguyentrai.rooms.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import vn.edu.nguyentrai.rooms.entity.Role;
import vn.edu.nguyentrai.rooms.entity.User;
import vn.edu.nguyentrai.rooms.repository.RoleRepository;
import vn.edu.nguyentrai.rooms.repository.UserRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
        return args -> {
            createIfMissing(userRepo, roleRepo, encoder, "admin", "Quản trị viên", "admin@nguyentrai.edu.vn", "ADMIN");
            createIfMissing(userRepo, roleRepo, encoder, "quanly", "Quản lý phòng", "quanly@nguyentrai.edu.vn", "ROOM_MANAGER");
            createIfMissing(userRepo, roleRepo, encoder, "giangvien", "Giảng viên Demo", "giangvien@nguyentrai.edu.vn", "LECTURER");
            createIfMissing(userRepo, roleRepo, encoder, "sinhvien", "Sinh viên Demo", "sinhvien@nguyentrai.edu.vn", "STUDENT");
        };
    }

    private void createIfMissing(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder,
                                 String username, String fullName, String email, String roleName) {
        if (userRepo.findByUsername(username).isPresent()) return;
        Role role = roleRepo.findByName(roleName).orElseThrow();
        User u = new User();
        u.setUsername(username);
        u.setPassword(encoder.encode("123456"));
        u.setFullName(fullName);
        u.setEmail(email);
        u.setRole(role);
        userRepo.save(u);
    }
}