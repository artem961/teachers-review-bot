package test.project.telega.bot.scenarios.contexts;

import lombok.Getter;
import lombok.Setter;
import test.project.telega.data.entities.Teacher;

@Getter
public class TeacherContext extends Context {
    private final Teacher.TeacherBuilder builder = Teacher.builder();
    @Setter
    private Integer messageId;

    public TeacherContext(Long chatId) {
        super(chatId);
        this.messageId = messageId;
    }
}
