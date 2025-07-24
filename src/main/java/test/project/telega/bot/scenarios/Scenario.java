package test.project.telega.bot.scenarios;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Scenario {
    BotApiMethod<?> startScenario(Update update);

    BotApiMethod<?> processUpdate(Update update);
}
