package Cocky_Camel.Room404;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import org.springframework.data.jpa.repository.JpaRepository;

@RestController 
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userRepository.findAll();
		return ResponseEntity.ok(users);
	}

	@GetMapping("/user/email/{email}")
	public ResponseEntity<?> findByEmail(@PathVariable String email) {
		User user = userRepository.findByEmailIgnoreCase(email);

		if (user != null) {
			return ResponseEntity.ok(user);
		}

		return ResponseEntity.status(404).body("No se encontró ningún usuario con el email: " + email);
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Integer id) {
		User user = userRepository.findById(id).orElse(null);

		if (user != null) {
			return ResponseEntity.ok(user); // 200 OK
		} else {
			return ResponseEntity.status(404).body("No se encontró ningún usuario con el ID: " + id);
		}
	}

	@PostMapping("/user/login/{email}/{password}")
	public ResponseEntity<String> login(@PathVariable String email, @PathVariable String password) {
		User user = userRepository.findByEmailIgnoreCase(email);

		if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
			String displayName = (user.getNickname() != null) ? user.getNickname() : user.getEmail();
			return ResponseEntity.ok("Login correcto. Bienvenido/a " + displayName + "!");
		} else {
			return ResponseEntity.status(401).body("Email o contraseña incorrectos");
		}
	}

	@PostMapping("/user")
	public ResponseEntity<String> createUser(@RequestBody User user) {
		if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			return ResponseEntity.badRequest()
					.body("Datos inválidos. El campo 'email' es obligatorio.");
		}

		try {
			User savedUser = userRepository.save(user);
			return ResponseEntity.ok("Usuario creado correctamente con ID: " + savedUser.getId());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error 400: No se pudo crear el usuario. " + e.getMessage());
		}
	}

	@PutMapping("/user/{requestedId}")
	public ResponseEntity<String> putUser(@PathVariable Integer requestedId, @RequestBody User userUpdate) {

		Optional<User> userOptional = userRepository.findById(requestedId);

		if (userOptional.isPresent()) {
			User existingUser = userOptional.get();

			if (userUpdate.getEmail() != null && !userUpdate.getEmail().isEmpty()) {
				existingUser.setEmail(userUpdate.getEmail());
			}
			if (userUpdate.getNickname() != null) {
				existingUser.setNickname(userUpdate.getNickname());
			}
			if (userUpdate.getPassword() != null) {
				existingUser.setPassword(userUpdate.getPassword());
			}
			if (userUpdate.getGoogleUid() != null) {
				existingUser.setGoogleUid(userUpdate.getGoogleUid());
			}
			if (userUpdate.getRole() != null) {
				existingUser.setRole(userUpdate.getRole());
			}
			
			existingUser.setPremium(userUpdate.isPremium());

			userRepository.save(existingUser);

			return ResponseEntity.ok()
					.body("Usuario con ID " + requestedId + " actualizado correctamente con todos sus campos (200 OK).");

		} else {
			return ResponseEntity.status(404)
					.body("Error 404: No se encontró ningún usuario con el ID: " + requestedId + " para actualizar.");
		}
	}

	@DeleteMapping("/user/{requestedId}")
	public ResponseEntity<String> deleteUser(@PathVariable Integer requestedId) {
		if (userRepository.existsById(requestedId)) {
			userRepository.deleteById(requestedId);	

			return ResponseEntity.ok().body("Usuario con ID " + requestedId + " encontrado y borrado.");
		} else {
			return ResponseEntity.status(404).body("No se encontró ningún usuario con el ID: " + requestedId);
		}
	}
}
