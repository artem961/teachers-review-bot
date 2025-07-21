package test.project.telega.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import test.project.telega.data.entities.Teacher;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> getTeacherBySurnameIgnoreCaseAndFirstNameIgnoreCaseAndLastNameIgnoreCase(String surname, String firstName, String lastName);

    List<Teacher> getTeacherByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    List<Teacher> getTeacherBySurnameIgnoreCase(String surname);
}
