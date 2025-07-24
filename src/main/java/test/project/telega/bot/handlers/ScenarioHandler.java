package test.project.telega.bot.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.TelegramSenderService;
import test.project.telega.services.UserService;

@Component
@Order(2)
@RequiredArgsConstructor
public class ScenarioHandler implements UpdateHandler {
    private final UserService userService;
    private final TelegramSenderService senderService;

    @Override
    public boolean supportsUpdate(Update update) {
        Long userId = update.hasMessage() ? update.getMessage().getFrom().getId() : update.getCallbackQuery().getFrom().getId();
        return userService.isUserInScenario(userId);
    }

    @Override
    public void handleUpdate(Update update) {
        Long userId = update.hasMessage() ? update.getMessage().getFrom().getId() : update.getCallbackQuery().getFrom().getId();
        senderService.execute(userService.getUserScenario(userId).processUpdate(update));
    }
}
