package Cocky_Camel.Room404;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserProgressController {

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PuzzleRepository puzzleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/progress")
    public ResponseEntity<List<UserProgress>> getProgress(
        @RequestParam(required = false) Integer userId,
        @RequestParam(required = false) Integer puzzleId
    ) {
        if (userId != null && puzzleId != null) {
            Optional<UserProgress> progress = userProgressRepository.findByUserIdAndPuzzleId(userId, puzzleId);
            return ResponseEntity.ok(progress.map(List::of).orElse(Collections.emptyList()));
        }

        if (userId != null) {
            return ResponseEntity.ok(userProgressRepository.findByUserId(userId));
        }

        if (puzzleId != null) {
            return ResponseEntity.ok(userProgressRepository.findByPuzzleId(puzzleId));
        }

        return ResponseEntity.ok(userProgressRepository.findAll());
    }

    @GetMapping("/progress/{id}")
    public ResponseEntity<?> getProgressById(@PathVariable Integer id) {
        Optional<UserProgress> progress = userProgressRepository.findById(id);
        if (progress.isPresent()) {
            return ResponseEntity.ok(progress.get());
        }
        return ResponseEntity.status(404).body("No se encontro ningun progreso con ID: " + id);
    }

    @PostMapping("/progress")
    public ResponseEntity<?> createProgress(@RequestBody UserProgress payload) {
        if (payload.getUser() == null || payload.getUser().getId() <= 0
            || payload.getPuzzle() == null || payload.getPuzzle().getId() == null) {
            return ResponseEntity.badRequest().body("Datos invalidos. user.id y puzzle.id son obligatorios.");
        }

        Integer userId = payload.getUser().getId();
        Integer puzzleId = payload.getPuzzle().getId();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("No se encontro ningun usuario con ID: " + userId);
        }

        Puzzle puzzle = puzzleRepository.findById(puzzleId).orElse(null);
        if (puzzle == null) {
            return ResponseEntity.status(404).body("No se encontro ningun puzzle con ID: " + puzzleId);
        }

        if (userProgressRepository.findByUserIdAndPuzzleId(userId, puzzleId).isPresent()) {
            return ResponseEntity.badRequest().body("Ya existe progreso para ese usuario y puzzle.");
        }

        UserProgress progress = new UserProgress();
        progress.setUser(user);
        progress.setPuzzle(puzzle);
        progress.setStatus(payload.getStatus() != null ? payload.getStatus() : UserProgress.Status.NOT_STARTED);
        progress.setTimeSeconds(payload.getTimeSeconds() != null ? payload.getTimeSeconds() : 0);

        if (progress.getStatus() == UserProgress.Status.COMPLETED && payload.getCompletedAt() == null) {
            progress.setCompletedAt(LocalDateTime.now());
        } else {
            progress.setCompletedAt(payload.getCompletedAt());
        }

        return ResponseEntity.status(201).body(userProgressRepository.save(progress));
    }

    @PutMapping("/progress/{id}")
    public ResponseEntity<?> updateProgress(@PathVariable Integer id, @RequestBody UserProgress payload) {
        Optional<UserProgress> progressOptional = userProgressRepository.findById(id);
        if (progressOptional.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontro ningun progreso con ID: " + id);
        }

        UserProgress progress = progressOptional.get();

        if (payload.getUser() != null && payload.getUser().getId() > 0) {
            User user = userRepository.findById(payload.getUser().getId()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(404).body("No se encontro ningun usuario con ID: " + payload.getUser().getId());
            }
            progress.setUser(user);
        }

        if (payload.getPuzzle() != null && payload.getPuzzle().getId() != null) {
            Puzzle puzzle = puzzleRepository.findById(payload.getPuzzle().getId()).orElse(null);
            if (puzzle == null) {
                return ResponseEntity.status(404).body("No se encontro ningun puzzle con ID: " + payload.getPuzzle().getId());
            }
            progress.setPuzzle(puzzle);
        }

        Optional<UserProgress> repeated = userProgressRepository.findByUserIdAndPuzzleId(
            progress.getUser().getId(),
            progress.getPuzzle().getId()
        );
        if (repeated.isPresent() && !repeated.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Ya existe progreso para ese usuario y puzzle.");
        }

        if (payload.getStatus() != null) {
            progress.setStatus(payload.getStatus());
        }

        if (payload.getTimeSeconds() != null) {
            if (payload.getTimeSeconds() < 0) {
                return ResponseEntity.badRequest().body("Datos invalidos. timeSeconds debe ser >= 0.");
            }
            progress.setTimeSeconds(payload.getTimeSeconds());
        }

        if (payload.getCompletedAt() != null) {
            progress.setCompletedAt(payload.getCompletedAt());
        } else if (progress.getStatus() == UserProgress.Status.COMPLETED && progress.getCompletedAt() == null) {
            progress.setCompletedAt(LocalDateTime.now());
        }

        return ResponseEntity.ok(userProgressRepository.save(progress));
    }

    @DeleteMapping("/progress/{id}")
    public ResponseEntity<Map<String, String>> deleteProgress(@PathVariable Integer id) {
        if (!userProgressRepository.existsById(id)) {
            return ResponseEntity.status(404).body(message("No se encontro ningun progreso con ID: " + id));
        }

        userProgressRepository.deleteById(id);
        return ResponseEntity.ok(message("Progreso con ID " + id + " borrado correctamente."));
    }

    private Map<String, String> message(String text) {
        Map<String, String> response = new HashMap<>();
        response.put("message", text);
        return response;
    }
    
    @PostMapping("/progress/complete/{puzzleName}")
    public ResponseEntity<?> completePuzzle(
        @RequestHeader("Authorization") String token, 
        @PathVariable String puzzleName,
        @RequestBody Map<String, Integer> body
    ) {
        try {
            String cleanToken = token.replace("Bearer ", "");
            String email = jwtUtil.getEmailFromToken(cleanToken);
            User user = userRepository.findByEmailIgnoreCase(email);

            Puzzle puzzle = puzzleRepository.findByNameIgnoreCase(puzzleName);
            if (puzzle == null) return ResponseEntity.status(404).body("Puzzle no encontrado");

            UserProgress progress = userProgressRepository
                .findByUserIdAndPuzzleId(user.getId(), puzzle.getId())
                .orElse(new UserProgress());

            progress.setUser(user);
            progress.setPuzzle(puzzle);
            progress.setStatus(UserProgress.Status.COMPLETED);
            progress.setCompletedAt(LocalDateTime.now());
            
            Integer seconds = body.getOrDefault("timeSeconds", 0);
            progress.setTimeSeconds(seconds);

            userProgressRepository.save(progress);
            return ResponseEntity.ok(Collections.singletonMap("message", "Progreso guardado correctamente"));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/ranking")
    public ResponseEntity<List<RankingDTO>> getRanking() {
        return ResponseEntity.ok(userProgressRepository.getGlobalRanking());
    }
}