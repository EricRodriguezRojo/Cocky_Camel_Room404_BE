package Cocky_Camel.hospital;

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

@Controller
@RequestMapping
public class NurseController {

	@Autowired
	private NurseRepository nurseRepository;

	@GetMapping("/index")
	public @ResponseBody Iterable<Nurse> getAllNurses() {
		return nurseRepository.findAll();
	}

	private List<Nurse> nurses = new ArrayList<>();

	/**
	 * public NurseController() { try { ObjectMapper mapper = new ObjectMapper();
	 * mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	 * 
	 * InputStream is = new
	 * ClassPathResource("static/nurses.json").getInputStream(); nurses =
	 * mapper.readValue(is, new TypeReference<List<Nurse>>() { }); } catch
	 * (Exception e) { e.printStackTrace(); } }
	 **/

	@GetMapping("/nurse/name/{name}")
	public ResponseEntity<?> findByName(@PathVariable String name) {
		Nurse nurse = nurseRepository.findByNameIgnoreCase(name);

		if (nurse != null) {
			return ResponseEntity.ok(nurse);
		}

		return ResponseEntity.status(404).body("No se encontró ningún enfermero con el nombre: " + name);
	}

	@GetMapping("/nurse/index")
	public ResponseEntity<List<Nurse>> getAll() {
		List<Nurse> nurses = nurseRepository.findAll();
		return ResponseEntity.ok(nurses);
	}

	@PostMapping("/nurse/login/{user}/{password}")
	public ResponseEntity<String> login(@PathVariable String user, @PathVariable String password) {
		Nurse nurse = nurseRepository.findByUserIgnoreCase(user);

		if (nurse != null && nurse.getPassword().equals(password)) {
			return ResponseEntity.ok("Login correcto. Bienvenido/a " + nurse.getName() + "!");
		} else {
			return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
		}
	}

	@DeleteMapping("/nurse/{requestedId}")
	public ResponseEntity<String> deleteNurse(@PathVariable Integer requestedId) {
		if (nurseRepository.existsById(requestedId)) {
			nurseRepository.deleteById(requestedId);	

			return ResponseEntity.ok().body("Enfermero con ID " + requestedId + " encontrado y borrado.");
		} else {
			return ResponseEntity.status(404).body("No se encontró ningún enfermero con el ID: " + requestedId);
		}
	}

	@GetMapping("/nurse/{id}")
	public ResponseEntity<?> getNurseById(@PathVariable Integer id) {
		Nurse nurse = nurseRepository.findById(id).orElse(null);

		if (nurse != null) {
			return ResponseEntity.ok(nurse); // 200 OK
		} else {
			return ResponseEntity.status(404).body("No se encontró ningún enfermero con el ID: " + id);
		}
	}

	@PostMapping("/nurse")
	public ResponseEntity<String> createNurse(@RequestBody Nurse nurse) {
		if (nurse.getName() == null || nurse.getName().trim().isEmpty() || nurse.getUser() == null
				|| nurse.getUser().trim().isEmpty() || nurse.getPassword() == null
				|| nurse.getPassword().trim().isEmpty()) {
			return ResponseEntity.badRequest()
					.body("Datos inválidos. Los campos 'name', 'user' y 'password' son obligatorios.");
		}

		try {
			Nurse savedNurse = nurseRepository.save(nurse);
			return ResponseEntity.ok("enfermero creado correctamente con ID: " + savedNurse.getId());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error 400: No se pudo crear el enfermero. " + e.getMessage());
		}
	}

	@PutMapping("/nurse/{requestedId}")
	public ResponseEntity<String> putNurse(@PathVariable Integer requestedId, @RequestBody Nurse nurseUpdate) {

		if (nurseUpdate.getName() == null || nurseUpdate.getName().trim().isEmpty() || nurseUpdate.getUser() == null
				|| nurseUpdate.getUser().trim().isEmpty() || nurseUpdate.getPassword() == null
				|| nurseUpdate.getPassword().trim().isEmpty()) {

			return ResponseEntity.status(400)
					.body("Error 400: Faltan datos obligatorios (nombre, usuario o contraseña) para la actualización.");
		}

		
		Optional<Nurse> nurseOptional = nurseRepository.findById(requestedId);

		if (nurseOptional.isPresent()) {
			Nurse existingNurse = nurseOptional.get();

			existingNurse.setName(nurseUpdate.getName());
			existingNurse.setUser(nurseUpdate.getUser());
			existingNurse.setPassword(nurseUpdate.getPassword());

			nurseRepository.save(existingNurse);

			return ResponseEntity.ok()
					.body("Enfermero con ID " + requestedId + " encontrado y actualizado correctamente (200 OK).");

		} else {
			return ResponseEntity.status(404)
					.body("Error 404: No se encontró ningún enfermero con el ID: " + requestedId + " para actualizar.");
		}
	}
}
