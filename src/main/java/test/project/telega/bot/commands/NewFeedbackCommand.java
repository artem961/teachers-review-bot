package test.project.telega.bot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.AppContext;
import test.project.telega.bot.scenaries.InputFeedbackScene;
import test.project.telega.bot.scenaries.InputTeacherScene;
import test.project.telega.bot.scenaries.states.UserState;

@Component
public class NewFeedbackCommand extends Command {
    public NewFeedbackCommand() {
        super("newFeedback");
    }

    @Override
    public BotApiMethod execute(Update update) {
        userService.setState(update.getMessage().getChatId(), UserState.INPUT_FEEDBACK);
        AppContext.getBean(InputFeedbackScene.class).resetUserSceneState(update.getMessage().getChatId());

        return AppContext.getBean(InputFeedbackScene.class).process(update);
    }
}
