package Cocky_Camel.Room404;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections; 
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@RestController 
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

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
		Map<String, String> error = new HashMap<>();
		error.put("message", "No se encontró ningún usuario con el email: " + email);
		return ResponseEntity.status(404).body(error);
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Integer id) {
		User user = userRepository.findById(id).orElse(null);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			Map<String, String> error = new HashMap<>();
			error.put("message", "No se encontró ningún usuario con el ID: " + id);
			return ResponseEntity.status(404).body(error);
		}
	}

	@PostMapping("/user/login/{email}/{password}")
	public ResponseEntity<?> login(@PathVariable String email, @PathVariable String password) {
		User user = userRepository.findByEmailIgnoreCase(email);

		if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
			String token = jwtUtil.generateToken(email);
			Map<String, String> response = new HashMap<>();
			response.put("token", token);
			response.put("message", "Login correcto. Bienvenido/a " + (user.getNickname() != null ? user.getNickname() : user.getEmail()));
			return ResponseEntity.ok(response);
		} else {
			Map<String, String> error = new HashMap<>();
			error.put("message", "Email o contraseña incorrectos");
			return ResponseEntity.status(401).body(error);
		}
	}

	@PostMapping("/user")
	public ResponseEntity<?> createUser(@RequestBody User user) {
		if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			Map<String, String> error = new HashMap<>();
			error.put("message", "Datos inválidos. El campo 'email' es obligatorio.");
			return ResponseEntity.badRequest().body(error);
		}

		try {
			User savedUser = userRepository.save(user);
			Map<String, String> response = new HashMap<>();
			response.put("message", "Usuario creado correctamente con ID: " + savedUser.getId());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("message", "Error 400: No se pudo crear el usuario. " + e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@PutMapping("/user/{requestedId}")
	public ResponseEntity<?> putUser(@PathVariable Integer requestedId, @RequestBody User userUpdate) {
		Optional<User> userOptional = userRepository.findById(requestedId);
		if (userOptional.isPresent()) {
			User existingUser = userOptional.get();
			if (userUpdate.getEmail() != null && !userUpdate.getEmail().isEmpty()) existingUser.setEmail(userUpdate.getEmail());
			if (userUpdate.getNickname() != null) existingUser.setNickname(userUpdate.getNickname());
			if (userUpdate.getPassword() != null) existingUser.setPassword(userUpdate.getPassword());
			if (userUpdate.getGoogleUid() != null) existingUser.setGoogleUid(userUpdate.getGoogleUid());
			if (userUpdate.getRole() != null) existingUser.setRole(userUpdate.getRole());
			existingUser.setPremium(userUpdate.isPremium());
			userRepository.save(existingUser);

			Map<String, String> response = new HashMap<>();
			response.put("message", "Usuario con ID " + requestedId + " actualizado correctamente.");
			return ResponseEntity.ok().body(response);
		} else {
			Map<String, String> error = new HashMap<>();
			error.put("message", "Error 404: No se encontró ningún usuario con el ID: " + requestedId);
			return ResponseEntity.status(404).body(error);
		}
	}

	@DeleteMapping("/user/{requestedId}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer requestedId) {
		if (userRepository.existsById(requestedId)) {
			userRepository.deleteById(requestedId);	
			Map<String, String> response = new HashMap<>();
			response.put("message", "Usuario con ID " + requestedId + " borrado.");
			return ResponseEntity.ok().body(response);
		} else {
			Map<String, String> error = new HashMap<>();
			error.put("message", "No se encontró ningún usuario con el ID: " + requestedId);
			return ResponseEntity.status(404).body(error);
		}
	}
	
	@PostMapping("/user/google-login")
	public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> data) {
	    String idTokenString = data.get("idToken");
	    
	    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
	        .setAudience(Collections.singletonList("436902612551-pt3s24i3uth56jebunl199phsh3d30ks.apps.googleusercontent.com"))
	        .build();

	    try {
	        GoogleIdToken idToken = verifier.verify(idTokenString);
	        if (idToken != null) {
	            GoogleIdToken.Payload payload = idToken.getPayload();
	            String email = payload.getEmail();
	            String googleUid = payload.getSubject();
	            String name = (String) payload.get("name");

	            User user = userRepository.findByEmailIgnoreCase(email);

	            if (user == null) {
	                user = new User();
	                user.setEmail(email);
	                user.setNickname(name);
	                user.setGoogleUid(googleUid);
	                user.setRole(User.Role.User);
	                user.setPremium(true);
	                userRepository.save(user);
	            }
	            
	            String token = jwtUtil.generateToken(email);
	            Map<String, String> response = new HashMap<>();
	            response.put("token", token);
	            response.put("message", "Login social correcto");
	            return ResponseEntity.ok(response);
	        } else {
	            Map<String, String> error = new HashMap<>();
	            error.put("message", "Token de Google inválido");
	            return ResponseEntity.status(401).body(error);
	        }
	    } catch (Exception e) {
	        Map<String, String> error = new HashMap<>();
	        error.put("message", "Error validando Google Token: " + e.getMessage());
	        return ResponseEntity.status(401).body(error);
	    }
	}
	
	@Autowired
	private EmailService emailService;
	 
	@PostMapping("/game/trigger-malware")
	public ResponseEntity<?> triggerMalware(@RequestHeader("Authorization") String token) {
	    try {
	        String cleanToken = token.replace("Bearer ", "");
	        String email = jwtUtil.getEmailFromToken(cleanToken);
	        User user = userRepository.findByEmailIgnoreCase(email);
	        emailService.sendMalwareEmail(email, user.getNickname());
	        Map<String, String> response = new HashMap<>();
	        response.put("message", "Email enviado con éxito");
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        Map<String, String> error = new HashMap<>();
	        error.put("message", "Error al procesar el email: " + e.getMessage());
	        return ResponseEntity.status(500).body(error);
	    }
	}
}