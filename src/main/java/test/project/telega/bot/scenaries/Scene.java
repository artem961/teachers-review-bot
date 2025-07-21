package test.project.telega.bot.scenaries;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.services.UserService;

public abstract class Scene {
    @Autowired
    protected UserService userService;

    public abstract BotApiMethod process(Update update);

    protected BotApiMethod getErrorMessage(Update update, String text) {
        return SendMessage
                .builder()
                .chatId(getUserId(update))
                .text(text)
                .build();
    }

    protected BotApiMethod getErrorMessage(Update update) {
        return getErrorMessage(update, "Error processing update from " + getUserId(update));
    }

    protected Long getUserId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        } else if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        } else {
            return null;
        }
    }

    protected String getMessage(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText();
        } else{
            return null;
        }
    }

    protected String getCallbackData(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        } else {
            return null;
        }
    }

    protected Integer getMessageId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getMessageId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getMessageId();

        } else {
            return null;
        }
    }
}
