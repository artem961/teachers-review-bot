package test.project.telega.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.project.telega.data.entities.Subject;
import test.project.telega.data.entities.Teacher;
import test.project.telega.data.repositories.SubjectRepository;
import test.project.telega.data.repositories.TeacherRepository;

import java.util.List;

@Service

public class TeacherService {
    @Getter
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public List<Teacher> findTeacherBySurnameFirstNameAndLastName(String surname,
                                                                  String firstName,
                                                                  String lastName
    ) {
        return teacherRepository.getTeacherBySurnameIgnoreCaseAndFirstNameIgnoreCaseAndLastNameIgnoreCase(surname, firstName, lastName);
    }

    public List<Teacher> findTeacherByFirstNameAndLastName(String firstName,
                                                           String lastName) {
        return teacherRepository.getTeacherByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
    }

    public List<Teacher> findTeacherBySurname(String surname) {
        return teacherRepository.getTeacherBySurnameIgnoreCase(surname);
    }

    public List<Teacher> findAll(){
        return teacherRepository.findAll();
    }

    public Teacher findById(Long id){
        return teacherRepository.findById(id).get();
    }


    @Transactional
    public void addTeacher(Teacher teacher) {
        if (teacher.getSubject() != null && teacher.getSubject().getId() != null) {
            Subject subject = subjectRepository.findById(teacher.getSubject().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + teacher.getSubject().getId()));
            teacher.setSubject(subject);
        }

        teacherRepository.save(teacher);
    }
}
