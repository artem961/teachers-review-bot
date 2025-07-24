package test.project.telega.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.config.BotConfig;

import java.util.List;


@Component
@RequiredArgsConstructor
public class Bot implements SpringLongPollingBot{
    private final BotConfig botConfig;
    private final UpdateDispatcher updateDispatcher;

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updates -> updates.forEach(updateDispatcher::dispatch);
    }
}
