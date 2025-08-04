package test.project.telega.bot.scenarios.contexts;

import lombok.Getter;
import lombok.Setter;
import test.project.telega.data.entities.Subject;
import test.project.telega.data.entities.Teacher;

import java.util.List;

@Getter
public class TeacherContext extends Context {
    private final Teacher.TeacherBuilder builder = Teacher.builder();
    @Setter
    private Integer messageId;
    @Setter
    private List<Subject> findSubjects;

    public TeacherContext(Long chatId) {
        super(chatId);
        this.messageId = messageId;
    }
}
