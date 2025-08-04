package test.project.telega.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import test.project.telega.data.entities.Subject;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsSubjectByNameIgnoreCase(String name);

    List<Subject> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Subject> findSubjectByNameContainsIgnoreCase(String name);
}
