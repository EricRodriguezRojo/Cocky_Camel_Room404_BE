package Cocky_Camel.Room404;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FakeEmailRepository extends JpaRepository<FakeEmail, Integer> {
    
    List<FakeEmail> findByPuzzleId(Integer puzzleId);
}