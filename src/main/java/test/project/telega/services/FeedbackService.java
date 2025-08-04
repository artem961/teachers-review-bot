package test.project.telega.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.project.telega.data.entities.Feedback;
import test.project.telega.data.entities.Subject;
import test.project.telega.data.entities.Teacher;
import test.project.telega.data.repositories.FeedbackRepository;
import test.project.telega.data.repositories.SubjectRepository;
import test.project.telega.data.repositories.TeacherRepository;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public void addFeedback(Feedback feedback){
        if (feedback.getTeacher() != null && (feedback.getTeacher().getId() != null)) {
            Teacher teacher = teacherRepository.findById(feedback.getTeacher().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + feedback.getTeacher().getId()));
            feedback.setTeacher(teacher);
        }
        feedbackRepository.save(feedback);
    }
}
