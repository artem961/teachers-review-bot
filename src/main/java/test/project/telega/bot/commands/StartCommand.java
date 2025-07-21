package test.project.telega.bot.commands;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.AppContext;
import test.project.telega.bot.scenaries.states.UserState;
import test.project.telega.services.UserService;

@Component
public class StartCommand extends Command {
    public StartCommand() {
        super("start");
    }

    @Override
    public BotApiMethod execute(Update update) {
        AppContext.getBean(UserService.class).setState(update.getMessage().getFrom().getId(), UserState.NONE);
        return SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Nice to meet you in our bot!\n" +
                        "To see available commands print /help")
                .build();
    }
}
