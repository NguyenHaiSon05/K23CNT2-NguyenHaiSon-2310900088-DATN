package vn.edu.nguyentrai.rooms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class RoomsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoomsApplication.class, args);
	}

}
