package test.project.telega.bot.scenarios.contexts;

import lombok.Getter;
import test.project.telega.data.entities.Subject;

@Getter
public class SubjectContext extends Context {
    private final Subject.SubjectBuilder builder = Subject.builder();

    public SubjectContext(Long chatId) {
        super(chatId);
    }
}
