package test.project.telega.bot.scenarios.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.TelegramSenderService;
import test.project.telega.bot.scenarios.*;
import test.project.telega.bot.scenarios.contexts.FeedbackContext;
import test.project.telega.bot.scenarios.feedback.states.InputTeacherState;
import test.project.telega.bot.scenarios.teacher.states.InputTeacherNameState;
import test.project.telega.bot.tools.keyboard.inline.UpdateParser;
import test.project.telega.services.FeedbackService;
import test.project.telega.services.TeacherService;
import test.project.telega.services.UserService;

@Component
@RequiredArgsConstructor
public class CreateFeedbackScenario implements Scenario {
    private final TelegramSenderService senderService;
    private final UserService userService;
    private final FeedbackService feedbackService;
    private final UserContextManager<FeedbackContext> userContextManager = new UserContextManager<>();
    private final UserScenarioStateManager<FeedbackContext> userStateManager = new UserScenarioStateManager<>();
    private final InputTeacherState initialState;

    @Override
    public BotApiMethod<?> startScenario(Update update) {
        Long userId = UpdateParser.getUserId(update);
        FeedbackContext context = new FeedbackContext(userId);

        userService.setUserScenario(userId, this);
        userStateManager.setUserState(userId, initialState);
        userContextManager.setUserContext(userId, context);

        return initialState.enterToState(context);
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = UpdateParser.getUserId(update);
        FeedbackContext context = userContextManager.getUserContext(userId);
        ScenarioState<FeedbackContext> currentState = userStateManager.getUserState(userId);

        if (context == null || currentState == null) {
            return finishScenario(userId, "Something went wrong.");
        }

        StateResult<FeedbackContext> result = currentState.handleUpdate(update, context);

        if (result.hasNextState()) {
            userStateManager.setUserState(userId, result.getNextState());
            sendMessageIfRequired(result);
            return result.getNextState().enterToState(context);
        } else {
            feedbackService.addFeedback(context.getBuilder().build());
            return result.getResponseMessage();
        }
    }

    private BotApiMethod<?> finishScenario(Long userId, String message) {
        userContextManager.removeUserContext(userId);
        userStateManager.removeUserState(userId);
        userService.clearUserScenario(userId);
        return new SendMessage(String.valueOf(userId), message);
    }

    private void sendMessageIfRequired(StateResult<FeedbackContext> result) {
        if (result.hasResponseMessage()){
            senderService.execute(result.getResponseMessage());
        }
    }
}
