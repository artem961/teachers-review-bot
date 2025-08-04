package test.project.telega.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.handlers.UpdateHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateDispatcher {
    private final List<UpdateHandler> handlers;
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public void dispatch(Update update) {
        for (UpdateHandler handler : handlers) {
            if (handler.supportsUpdate(update)) {
                try {
                    executor.execute(() -> {
                        try{
                            handler.handleUpdate(update);
                        } catch (Exception e) {
                            log.error(e.toString());
                        }
                    });

                } catch (Exception e) {
                    log.error("Error handling update", e);
                }
                return;
            }
        }
        log.warn("No support for update {}", update);
    }
}
