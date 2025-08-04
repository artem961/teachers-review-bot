package test.project.telega.bot.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.TelegramSenderService;
import test.project.telega.bot.commands.Command;
import test.project.telega.bot.commands.CommandManager;
import test.project.telega.services.UserService;

import java.util.Optional;

import static test.project.telega.bot.tools.UpdateParser.getMessage;
import static test.project.telega.bot.tools.UpdateParser.getUserId;

@Component
@Order(2)
@RequiredArgsConstructor
public class CommandHandler implements UpdateHandler {
    private final CommandManager commandManager;
    private final UserService userService;
    private final TelegramSenderService senderService;

    @Override
    public boolean supportsUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText().startsWith("/");
        }
        return false;
    }

    @Override
    public void handleUpdate(Update update) {
        Long userId = getUserId(update);
        String text = getMessage(update);

        userService.clearUserScenario(userId);

        Optional<Command> commandOptional = commandManager.getCommandByName(text.substring(1));
        if (commandOptional.isPresent()) {
            Command command = commandOptional.get();
            senderService.execute(command.execute(update));
        } else {
            senderService.sendTextMessage(userId, "Command not found!");
        }
    }
}