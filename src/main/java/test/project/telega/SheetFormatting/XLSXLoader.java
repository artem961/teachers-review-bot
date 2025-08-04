package test.project.telega.SheetFormatting;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import test.project.telega.data.entities.Feedback;
import test.project.telega.data.entities.Subject;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.FeedbackService;
import test.project.telega.services.SubjectService;
import test.project.telega.services.TeacherService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Transactional
public class XLSXLoader {
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final FeedbackService feedbackService;

    @SneakyThrows
    public void load(File file) {

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                String surname = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().strip();
                String name = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().strip();
                String lastName = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().strip();
                String subject = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().strip();
                String comment = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                Integer rating = (int) row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue();


                if (name != null & !name.isEmpty() && comment != null & !comment.isEmpty() && rating != null) {
                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                    surname = surname.substring(0, 1).toUpperCase() + surname.substring(1).toLowerCase();

                    if (lastName != null & !lastName.isEmpty()) {
                        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
                    }

                    Subject subject1 = new Subject();
                    subject1.setName(subject);

                    if (subjectService.findByName(subject).isEmpty()) {
                        subjectService.insertIfNotExist(subject1);
                    } else{
                        subject1 = subjectService.findByName(subject).get(0);
                    }

                    Teacher teacher = new Teacher();
                    teacher.setSurname(surname);
                    teacher.setLastName(lastName);
                    teacher.setFirstName(name);
                    teacher.setSubject(subject1);

                    if (teacherService.findTeacher(surname, name, lastName).isEmpty()) {
                        teacherService.addTeacher(teacher);
                    } else{
                        teacher = teacherService.findTeacherBySurnameAndFirstName(surname, name).get(0);
                    }

                    Feedback feedback = new Feedback();
                    feedback.setTeacher(teacher);
                    feedback.setRating(rating);
                    feedback.setComment(comment);

                    feedbackService.addFeedback(feedback);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
