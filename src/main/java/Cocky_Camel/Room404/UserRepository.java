package Cocky_Camel.Room404;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    User findByEmailIgnoreCase(String email);
    
    User findByNicknameIgnoreCase(String nickname);

    User findByGoogleUid(String googleUid);
}