package Cocky_Camel.Room404;

import org.springframework.beans.factory.annotation.Autowired;
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
}