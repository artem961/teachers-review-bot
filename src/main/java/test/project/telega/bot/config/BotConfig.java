package test.project.telega.bot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class BotConfig {
    @Value("${bot.token}")
    private String token;
    @Value("${bot.name}")
    private String name;
}
