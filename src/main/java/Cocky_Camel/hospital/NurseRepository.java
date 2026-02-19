package Cocky_Camel.hospital;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NurseRepository extends JpaRepository<Nurse, Integer> {
    Nurse findByNameIgnoreCase(String name);
    Nurse findByUserIgnoreCase(String user);
}
