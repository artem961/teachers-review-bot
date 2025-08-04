package test.project.telega.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.subject.CreateSubjectScenario;

@Component
public class NewSubjectCommand extends Command {
    @Autowired
    private CreateSubjectScenario inputSubjectScenario;

    public NewSubjectCommand() {
        super("new_subject",
                "➕ Add new subject");
    }

    @Override
    public BotApiMethod execute(Update update) {
        return inputSubjectScenario.startScenario(update);
    }
}
