package Cocky_Camel.Room404;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PUZZLES")
public class Puzzle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "difficulty", nullable = false)
    private Integer difficulty;

    @Column(name = "solution_hash", nullable = false, length = 255)
    private String solutionHash;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    public Puzzle() {
    }

    public Puzzle(String name, Integer difficulty, String solutionHash, Integer maxScore) {
        this.name = name;
        this.difficulty = difficulty;
        this.solutionHash = solutionHash;
        this.maxScore = maxScore;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getSolutionHash() {
        return solutionHash;
    }

    public void setSolutionHash(String solutionHash) {
        this.solutionHash = solutionHash;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }
}
