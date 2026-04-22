package Cocky_Camel.Room404;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {

    List<UserProgress> findByUserId(Integer userId);

    List<UserProgress> findByPuzzleId(Integer puzzleId);

    Optional<UserProgress> findByUserIdAndPuzzleId(Integer userId, Integer puzzleId);
}
