package test.project.telega.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.project.telega.data.entities.Subject;
import test.project.telega.data.entities.Teacher;
import test.project.telega.data.repositories.SubjectRepository;
import test.project.telega.data.repositories.TeacherRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service

public class TeacherService {
    @Getter
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public List<Teacher> findTeacherBySurnameAndFirstName(String surname, String firstName) {
        return teacherRepository.findTeacherBySurnameContainsIgnoreCaseAndFirstNameContainsIgnoreCase(surname, firstName);
    }

    public List<Teacher> findTeacher(List<String> data) {
        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        Specification<Teacher> spec = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (String s : data) {
                String pattern = "%" + s.toLowerCase() + "%";
                Predicate predicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("surname")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern)
                );
                predicates.add(predicate);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
       return teacherRepository.findAll(spec);
    }

    public List<Teacher> findTeacher(String... data) {
        if (data != null && data.length > 0) {
            return findTeacher(Arrays.asList(data));
        } else{
            return Collections.emptyList();
        }
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
