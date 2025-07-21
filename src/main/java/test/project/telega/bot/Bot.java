package test.project.telega.bot;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import test.project.telega.AppContext;
import test.project.telega.bot.commands.*;
import test.project.telega.bot.config.BotConfig;
import test.project.telega.bot.scenaries.states.UserState;
import test.project.telega.services.UserService;

import java.util.List;
import java.util.Optional;

@Component
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final BotConfig botConfig;
    private final CommandManager commandManager;
    private final UserService userService;


    private final TelegramClient telegramClient;

    @Autowired
    public Bot(BotConfig botConfig, CommandManager commandManager, UserService userService) {
        this.botConfig = botConfig;
        this.commandManager = commandManager;
        this.userService = userService;
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long userId = update.getMessage().getFrom().getId();
            String text = update.getMessage().getText();

            if (text.startsWith("/")) {
                userService.setState(update.getMessage().getChatId(), UserState.NONE);
                Optional<Command> commandOptional = commandManager.getCommandByName(text.substring(1));
                if (commandOptional.isPresent()) {
                    Command command = commandOptional.get();
                    execute(command.execute(update));
                } else {
                    execute(SendMessage
                            .builder()
                            .chatId(userId)
                            .text("Command not found!")
                            .build());
                }
            } else if (!userService.getState(userId).equals(UserState.NONE)) {
                execute(userService.getState(userId).getScene().process(update));
            } else {
                execute(SendMessage
                        .builder()
                        .chatId(userId)
                        .text("To see available commands write /help")
                        .build());
            }
        } else if (update.hasCallbackQuery()) {
            Long userId = update.getCallbackQuery().getFrom().getId();
            if (!userService.getState(userId).equals(UserState.NONE)) {
                execute(userService.getState(userId).getScene().process(update));
            } else {
                execute(EditMessageText
                        .builder()
                        .chatId(userId)
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .text("To see available commands write /help")
                        .build());
            }
        }
    }

    private void execute(BotApiMethod apiMethod) throws TelegramApiException {
        try {
            if (apiMethod != null) {
                telegramClient.execute(apiMethod);
            }
        } catch (Exception e) {
            if (!e.getMessage().equals("Error executing org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText query: [400] Bad Request: message is not modified: specified new message content and reply markup are exactly the same as a current content and reply markup of the message")) {
                throw e;
            }
        }
    }

    @EventListener({ContextRefreshedEvent.class})
    private void init() {
        commandManager.addCommand(AppContext.getBean(StartCommand.class));
        commandManager.addCommand(AppContext.getBean(FindCommand.class));
        commandManager.addCommand(AppContext.getBean(NewSubjectCommand.class));
        commandManager.addCommand(AppContext.getBean(NewTeacherCommand.class));
        commandManager.addCommand(AppContext.getBean(HelpCommand.class));
        commandManager.addCommand(AppContext.getBean(NewFeedbackCommand.class));
    }
}
