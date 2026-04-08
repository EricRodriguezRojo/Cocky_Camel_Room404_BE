package Cocky_Camel.Room404;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PuzzleRepository extends JpaRepository<Puzzle, Integer> {

    List<Puzzle> findByDifficultyOrderByIdAsc(Integer difficulty);

    Puzzle findByNameIgnoreCase(String name);
}
