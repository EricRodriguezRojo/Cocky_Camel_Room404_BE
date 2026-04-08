package Cocky_Camel.Room404;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PuzzleController {

    @Autowired
    private PuzzleRepository puzzleRepository;

    @GetMapping("/puzzles")
    public ResponseEntity<List<Puzzle>> getAllPuzzles(@RequestParam(required = false) Integer difficulty) {
        if (difficulty != null) {
            return ResponseEntity.ok(puzzleRepository.findByDifficultyOrderByIdAsc(difficulty));
        }
        return ResponseEntity.ok(puzzleRepository.findAll());
    }

    @GetMapping("/puzzle/{id}")
    public ResponseEntity<?> getPuzzleById(@PathVariable Integer id) {
        Optional<Puzzle> puzzleOptional = puzzleRepository.findById(id);

        if (puzzleOptional.isPresent()) {
            return ResponseEntity.ok(puzzleOptional.get());
        }

        return ResponseEntity.status(404).body("No se encontro ningun puzzle con el ID: " + id);
    }

    @PostMapping("/puzzle")
    public ResponseEntity<?> createPuzzle(@RequestBody Puzzle puzzle) {
        String validationError = validatePuzzlePayload(puzzle);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        Puzzle existing = puzzleRepository.findByNameIgnoreCase(puzzle.getName());
        if (existing != null) {
            return ResponseEntity.badRequest().body("Ya existe un puzzle con el nombre: " + puzzle.getName());
        }

        Puzzle savedPuzzle = puzzleRepository.save(puzzle);
        return ResponseEntity.status(201).body(savedPuzzle);
    }

    @PutMapping("/puzzle/{id}")
    public ResponseEntity<?> updatePuzzle(@PathVariable Integer id, @RequestBody Puzzle puzzleUpdate) {
        Optional<Puzzle> puzzleOptional = puzzleRepository.findById(id);
        if (puzzleOptional.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontro ningun puzzle con el ID: " + id);
        }

        Puzzle existingPuzzle = puzzleOptional.get();

        if (puzzleUpdate.getName() != null) {
            if (puzzleUpdate.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Datos invalidos. El campo name no puede estar vacio.");
            }

            Puzzle repeated = puzzleRepository.findByNameIgnoreCase(puzzleUpdate.getName());
            if (repeated != null && !repeated.getId().equals(id)) {
                return ResponseEntity.badRequest().body("Ya existe un puzzle con el nombre: " + puzzleUpdate.getName());
            }
            existingPuzzle.setName(puzzleUpdate.getName().trim());
        }

        if (puzzleUpdate.getDifficulty() != null) {
            if (puzzleUpdate.getDifficulty() < 0) {
                return ResponseEntity.badRequest().body("Datos invalidos. difficulty debe ser >= 0.");
            }
            existingPuzzle.setDifficulty(puzzleUpdate.getDifficulty());
        }

        if (puzzleUpdate.getSolutionHash() != null) {
            if (puzzleUpdate.getSolutionHash().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Datos invalidos. solutionHash no puede estar vacio.");
            }
            existingPuzzle.setSolutionHash(puzzleUpdate.getSolutionHash().trim());
        }

        if (puzzleUpdate.getMaxScore() != null) {
            if (puzzleUpdate.getMaxScore() < 0) {
                return ResponseEntity.badRequest().body("Datos invalidos. maxScore debe ser >= 0.");
            }
            existingPuzzle.setMaxScore(puzzleUpdate.getMaxScore());
        }

        return ResponseEntity.ok(puzzleRepository.save(existingPuzzle));
    }

    @DeleteMapping("/puzzle/{id}")
    public ResponseEntity<String> deletePuzzle(@PathVariable Integer id) {
        if (!puzzleRepository.existsById(id)) {
            return ResponseEntity.status(404).body("No se encontro ningun puzzle con el ID: " + id);
        }

        puzzleRepository.deleteById(id);
        return ResponseEntity.ok("Puzzle con ID " + id + " borrado correctamente.");
    }

    private String validatePuzzlePayload(Puzzle puzzle) {
        if (puzzle.getName() == null || puzzle.getName().trim().isEmpty()) {
            return "Datos invalidos. El campo name es obligatorio.";
        }
        if (puzzle.getDifficulty() == null || puzzle.getDifficulty() < 0) {
            return "Datos invalidos. El campo difficulty es obligatorio y debe ser >= 0.";
        }
        if (puzzle.getSolutionHash() == null || puzzle.getSolutionHash().trim().isEmpty()) {
            return "Datos invalidos. El campo solutionHash es obligatorio.";
        }
        if (puzzle.getMaxScore() == null || puzzle.getMaxScore() < 0) {
            return "Datos invalidos. El campo maxScore es obligatorio y debe ser >= 0.";
        }
        puzzle.setName(puzzle.getName().trim());
        puzzle.setSolutionHash(puzzle.getSolutionHash().trim());
        return null;
    }
}
