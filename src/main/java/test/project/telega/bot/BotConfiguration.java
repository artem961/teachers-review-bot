package test.project.telega.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import test.project.telega.bot.config.BotConfig;

@Configuration
public class BotConfiguration {

    @Bean
    public TelegramClient telegramClient(BotConfig botConfig) {
        return new OkHttpTelegramClient(botConfig.getToken());
    }
}
