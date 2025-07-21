package test.project.telega.bot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.AppContext;
import test.project.telega.bot.scenaries.FindTeacherScene;
import test.project.telega.bot.scenaries.states.UserState;

@Component
public class FindCommand extends Command {
    public FindCommand() {
        super("find");
    }

    @Override
    public BotApiMethod execute(Update update) {
        userService.setState(update.getMessage().getChatId(), UserState.FIND_TEACHER);
        AppContext.getBean(FindTeacherScene.class).resetUserSceneState(update.getMessage().getChatId());

        return AppContext.getBean(FindTeacherScene.class).process(update);
    }
}
