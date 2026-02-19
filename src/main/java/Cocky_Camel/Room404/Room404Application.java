package Cocky_Camel.Room404;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Room404Application {

	public static void main(String[] args) {
		SpringApplication.run(Room404Application.class, args);
	}

	@GetMapping("/user/prueba/{prueba}")
	public String testUser(@PathVariable String prueba) {
		return String.format("Hello %s, ¡bienvenido a Room404!", prueba);
	}
}