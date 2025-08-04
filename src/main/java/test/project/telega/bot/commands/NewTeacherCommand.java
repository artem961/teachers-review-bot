package test.project.telega.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.teacher.CreateTeacherScenario;

@Component
public class NewTeacherCommand extends Command {
    @Autowired
    private CreateTeacherScenario createTeacherScenario;

    public NewTeacherCommand() {
        super("new_teacher",
                "\uD83D\uDC68\uD83C\uDFEB Add new professor");
    }

    @Override
    public BotApiMethod execute(Update update) {
        return createTeacherScenario.startScenario(update);
    }
}
