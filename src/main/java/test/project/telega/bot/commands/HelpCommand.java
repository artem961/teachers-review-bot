package test.project.telega.bot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HelpCommand extends Command {
    private List<Command> commands;

    public HelpCommand(List<Command> commands) {
        super("help");
        this.commands = commands;
    }

    @Override
    public BotApiMethod execute(Update update) {
       List<String> commandNames = commands.stream()
               .map(Command::getCommandName)
               .map(name -> "/"+name)
               .collect(Collectors.toList());

        return SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text(String.join("\n", commandNames))
                .build();
    }
}
