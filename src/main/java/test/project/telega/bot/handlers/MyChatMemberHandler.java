package test.project.telega.bot.handlers;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(1)
public class MyChatMemberHandler implements UpdateHandler {
    @Override
    public boolean supportsUpdate(Update update) {
        if (update.hasMyChatMember()){
            return true;
        }
        return false;
    }

    @Override
    public void handleUpdate(Update update) {
    }
}
