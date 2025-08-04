package test.project.telega.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import test.project.telega.data.entities.Teacher;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {
    List<Teacher> getTeacherBySurnameContainsIgnoreCaseAndFirstNameContainsIgnoreCaseAndLastNameNotContainsIgnoreCase(String surname, String firstName, String lastName);

    List<Teacher> getTeacherByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCase(String firstName, String lastName);

    List<Teacher> getTeacherBySurnameContainsIgnoreCase(String surname);

    List<Teacher> findTeacherBySurnameContainsIgnoreCaseAndFirstNameContainsIgnoreCase(String surname, String firstName);

    List<Teacher> findTeacherBySurnameContainsIgnoreCaseOrFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String surname, String firstName, String lastName);

    List<Teacher> findTeacherBySurnameContainsIgnoreCaseAndFirstNameContainsIgnoreCaseOrSurnameContainsIgnoreCaseAndLastNameContainsIgnoreCaseOrFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCase(String surname, String firstName, String surname1, String lastName, String firstName1, String lastName1);
}
