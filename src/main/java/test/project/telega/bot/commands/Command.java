package test.project.telega.bot.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.services.UserService;

@RequiredArgsConstructor
public abstract class Command {
    @Autowired
    protected UserService userService;

    @Getter
    private final String commandName;

    public abstract BotApiMethod execute(Update update);
}
