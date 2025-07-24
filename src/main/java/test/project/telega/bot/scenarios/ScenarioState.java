package test.project.telega.bot.scenarios;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.contexts.Context;

/**
 * Interface for one stage of the bot's interaction according to the scenario.
 * @param <T> The type of transmitted context
 */
public interface ScenarioState<T extends Context> {
    /**
     * This method calls when user enter to this state
     * @param context
     * @return
     */
    BotApiMethod<?> enterToState(T context);

    /**
     * This method calls when user send an update, while user exists in this state
     * @param update
     * @param context
     */
    StateResult<T> handleUpdate(Update update, T context);
}
