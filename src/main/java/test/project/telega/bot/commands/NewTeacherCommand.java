package test.project.telega.bot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.AppContext;
import test.project.telega.bot.scenaries.FindTeacherScene;
import test.project.telega.bot.scenaries.InputTeacherScene;
import test.project.telega.bot.scenaries.states.UserState;

@Component
public class NewTeacherCommand extends Command {
    public NewTeacherCommand() {
        super("newTeacher");
    }

    @Override
    public BotApiMethod execute(Update update) {
        userService.setState(update.getMessage().getChatId(), UserState.INPUT_TEACHER);
        AppContext.getBean(InputTeacherScene.class).resetUserSceneState(update.getMessage().getChatId());

        return AppContext.getBean(InputTeacherScene.class).process(update);
    }
}
