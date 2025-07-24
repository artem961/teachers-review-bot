package test.project.telega.bot.scenarios.contexts;

import lombok.Getter;
import lombok.Setter;
import test.project.telega.data.entities.Teacher;

@Getter
@Setter
public class FindContext extends Context {
    private String findString = null;

    public FindContext(Long chatId) {
        super(chatId);
    }
}
