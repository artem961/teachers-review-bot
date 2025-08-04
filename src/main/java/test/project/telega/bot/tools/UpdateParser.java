package test.project.telega.bot.tools;

import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateParser {
    public static Long getUserId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        } else if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        } else {
            return null;
        }
    }

    public static String getMessage(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText();
        } else{
            return null;
        }
    }

    public static String getCallbackData(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        } else {
            return null;
        }
    }

    public static Integer getMessageId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getMessageId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getMessageId();

        } else {
            return null;
        }
    }
}
