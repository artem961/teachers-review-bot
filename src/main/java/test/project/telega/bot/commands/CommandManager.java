package test.project.telega.bot.commands;

import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class CommandManager {
    private Map<String, Command> commands;

    public CommandManager(List<Command> commands) {
        this.commands = commands.stream()
                .collect(Collectors.toMap(Command::getCommandName, command -> command));
    }

    public Optional<Command> getCommandByName(String commandName) {
        return Optional.ofNullable(commands.get(commandName));
    }
}
