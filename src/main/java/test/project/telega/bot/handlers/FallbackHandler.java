package test.project.telega.bot.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.TelegramSenderService;

@Component
@Order
@RequiredArgsConstructor
public class FallbackHandler implements UpdateHandler{
    private final TelegramSenderService senderService;

    @Override
    public boolean supportsUpdate(Update update) {
        return true;
    }

    @Override
    public void handleUpdate(Update update) {
        Long userId = update.hasMessage() ? update.getMessage().getFrom().getId() : update.getCallbackQuery().getFrom().getId();
        senderService.sendTextMessage(userId, "To see available commands print /help");
    }
}
