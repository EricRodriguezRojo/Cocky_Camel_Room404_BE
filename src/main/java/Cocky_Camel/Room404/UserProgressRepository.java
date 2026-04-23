package Cocky_Camel.Room404;

import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {

    List<UserProgress> findByUserId(Integer userId);

    List<UserProgress> findByPuzzleId(Integer puzzleId);

    Optional<UserProgress> findByUserIdAndPuzzleId(Integer userId, Integer puzzleId);
    
    @Query("SELECT new Cocky_Camel.Room404.RankingDTO(u.nickname, SUM(p.maxScore), SUM(up.timeSeconds)) " +
            "FROM UserProgress up " +
            "JOIN up.user u " +
            "JOIN up.puzzle p " +
            "WHERE up.status = Cocky_Camel.Room404.UserProgress.Status.COMPLETED " +
            "GROUP BY u.id, u.nickname " +
            "ORDER BY SUM(p.maxScore) DESC, SUM(up.timeSeconds) ASC")
     List<RankingDTO> getGlobalRanking();
}
