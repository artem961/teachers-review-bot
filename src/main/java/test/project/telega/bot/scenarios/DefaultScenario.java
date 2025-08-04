package test.project.telega.bot.scenarios;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.TelegramSenderService;
import test.project.telega.bot.scenarios.contexts.Context;
import test.project.telega.bot.tools.UpdateParser;
import test.project.telega.services.UserService;

@RequiredArgsConstructor
public abstract class DefaultScenario<T extends Context, S extends ScenarioState<T>> implements Scenario {
    private final TelegramSenderService senderService;
    private final UserService userService;
    protected final UserContextManager<T> userContextManager = new UserContextManager<>();
    protected final UserScenarioStateManager<T> userStateManager = new UserScenarioStateManager<>();
    @Getter
    private final S initialState;

    protected abstract T createInitialContext(Long userId);


    @Override
    public BotApiMethod<?> startScenario(Update update) {
        Long userId = UpdateParser.getUserId(update);
        T context = createInitialContext(userId);

        userService.setUserScenario(userId, this);
        userStateManager.setUserState(userId, initialState);
        userContextManager.setUserContext(userId, context);

        return initialState.enterToState(context);
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = UpdateParser.getUserId(update);
        T context = userContextManager.getUserContext(userId);
        ScenarioState<T> currentState = userStateManager.getUserState(userId);

        if (context == null || currentState == null) {
            return finishScenario(userId, "Something went wrong.");
        }

        StateResult<T> result = currentState.handleUpdate(update, context);

        if (result.hasNextState()) {
            sendMessageIfRequired(result);
            userStateManager.setUserState(userId, result.getNextState());
            return result.getNextState().enterToState(context);
        } else {
            finishScenario(userId);
            return onScenarioFinished(context, result);
        }
    }

    protected BotApiMethod<?> finishScenario(Long userId, String message) {
        finishScenario(userId);
        return new SendMessage(String.valueOf(userId), message);
    }

    protected void finishScenario(Long userId) {
        userContextManager.removeUserContext(userId);
        userStateManager.removeUserState(userId);
        userService.clearUserScenario(userId);
    }

    private void sendMessageIfRequired(StateResult<T> result) {
        if (result.hasResponseMessage()){
            senderService.execute(result.getResponseMessage());
        }
    }

    protected BotApiMethod<?> onScenarioFinished(T context, StateResult<T> finalResult) {
        return finalResult.getResponseMessage();
    }
}
