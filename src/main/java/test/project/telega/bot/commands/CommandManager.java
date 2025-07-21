package test.project.telega.bot.commands;

import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommandManager {
    @Getter
    private Set<Command> commands;

    public CommandManager() {
        commands = Collections.synchronizedSet(new HashSet<>());
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public Optional<Command> getCommandByName(String commandName) {
        for (Command command : commands) {
            if (command.getCommandName().equals(commandName)) {
                return Optional.ofNullable(command);
            }
        }
        return Optional.empty();
    }
}
