package test.project.telega.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.teacher.FindTeacherScenario;

@Component
public class FindCommand extends Command {
    @Autowired
    private FindTeacherScenario findTeacherScenario;

    public FindCommand() {
        super("find",
                "\uD83D\uDD0D Search for professor reviews");
    }

    @Override
    public BotApiMethod execute(Update update) {
        return findTeacherScenario.startScenario(update);
    }
}
