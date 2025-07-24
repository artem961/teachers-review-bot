package test.project.telega.bot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {
    boolean supportsUpdate(Update update);

    void handleUpdate(Update update);
}
