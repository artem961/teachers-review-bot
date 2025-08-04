package test.project.telega.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.project.telega.data.entities.Subject;
import test.project.telega.data.repositories.SubjectRepository;

import java.util.List;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    @Transactional
    public boolean insertIfNotExist(Subject subject) {
        if (subjectRepository.existsSubjectByNameIgnoreCase(subject.getName())){
            return false;
        } else{
            subjectRepository.save(subject);
            return true;
        }
    }

    public List<Subject> findAll(){
        return subjectRepository.findAll();
    }

    public Subject findById(Long id){
        return subjectRepository.findById(id).get();
    }

    public List<Subject> findByName(String name){
        return subjectRepository.findSubjectByNameContainsIgnoreCase(name);
    }
}
