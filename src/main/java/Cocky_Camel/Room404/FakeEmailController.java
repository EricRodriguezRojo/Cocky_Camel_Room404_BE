package Cocky_Camel.Room404;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/emails")
public class FakeEmailController {

    @Autowired
    private FakeEmailRepository emailRepository;

    @GetMapping
    public List<FakeEmail> getAllEmails() {
        return emailRepository.findAll();
    }

    @GetMapping("/puzzle/{puzzleId}")
    public List<FakeEmail> getEmailsByPuzzle(@PathVariable Integer puzzleId) {
        return emailRepository.findByPuzzleId(puzzleId);
    }

    @PostMapping
    public FakeEmail createEmail(@RequestBody FakeEmail email) {
        return emailRepository.save(email);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FakeEmail> updateEmail(@PathVariable Integer id, @RequestBody FakeEmail emailDetails) {
        return emailRepository.findById(id).map(email -> {
            email.setSender(emailDetails.getSender());
            email.setBodyText(emailDetails.getBodyText());
            return ResponseEntity.ok(emailRepository.save(email));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Integer id) {
        if(emailRepository.existsById(id)){
            emailRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}