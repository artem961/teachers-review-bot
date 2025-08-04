package test.project.telega.bot.scenarios.contexts;

import lombok.Getter;
import lombok.Setter;
import test.project.telega.data.entities.Feedback;
import test.project.telega.data.entities.Teacher;

import java.util.List;

@Getter
public class FeedbackContext extends Context {
    private final Feedback.FeedbackBuilder builder = Feedback.builder();
    @Setter
    private Integer messageId;
    @Setter
    private Teacher teacher = null;
    @Setter
    private List<Teacher> foundTeachers;

    public FeedbackContext(Long chatId) {
        super(chatId);
    }
}
