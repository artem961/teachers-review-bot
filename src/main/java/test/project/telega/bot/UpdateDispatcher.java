package test.project.telega.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.handlers.UpdateHandler;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateDispatcher {
    private final List<UpdateHandler> handlers;

    public void dispatch(Update update) {
        for (UpdateHandler handler : handlers) {
            if (handler.supportsUpdate(update)) {
                try {
                    handler.handleUpdate(update);
                } catch (Exception e) {
                    log.error("Error handling update {}", update, e);
                }
                return;
            }
        }
        log.warn("No support for update {}", update);
    }
}
