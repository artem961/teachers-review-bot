package test.project.telega.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.services.UserService;

import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.getUserId;

@Component
public class StartCommand extends Command {
    public StartCommand() {
        super("start");
    }

    @Override
    public BotApiMethod execute(Update update) {
        return SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Nice to meet you in our bot!\n" +
                        "To see available commands print /help")
                .build();
    }
}
