package test.project.telega.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.feedback.CreateFeedbackScenario;

@Component
public class NewFeedbackCommand extends Command {
    @Autowired
    private CreateFeedbackScenario createFeedbackScenario;
    public NewFeedbackCommand() {
        super("review",
                "\uD83D\uDCDD Submit new professor review");
    }

    @Override
    public BotApiMethod execute(Update update) {
        return createFeedbackScenario.startScenario(update);
    }
}
