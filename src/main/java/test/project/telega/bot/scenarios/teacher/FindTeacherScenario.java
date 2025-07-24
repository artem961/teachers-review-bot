package test.project.telega.bot.scenarios.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.TelegramSenderService;
import test.project.telega.bot.scenarios.Scenario;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.UserContextManager;
import test.project.telega.bot.scenarios.UserScenarioStateManager;
import test.project.telega.bot.scenarios.contexts.SubjectContext;
import test.project.telega.bot.scenarios.contexts.TeacherContext;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.teacher.states.InputTeacherFullNameState;
import test.project.telega.bot.tools.keyboard.inline.UpdateParser;
import test.project.telega.services.UserService;

import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.getMessageId;

@Component
@RequiredArgsConstructor
public class FindTeacherScenario implements Scenario {
    private final TelegramSenderService senderService;
    private final UserService userService;
    private final UserContextManager<TeacherContext> userContextManager = new UserContextManager<>();
    private final UserScenarioStateManager<TeacherContext> userStateManager = new UserScenarioStateManager<>();
    private final InputTeacherFullNameState initialState;

    @Override
    public BotApiMethod<?> startScenario(Update update) {
        Long userId = UpdateParser.getUserId(update);
        TeacherContext context = new TeacherContext(userId);

        userService.setUserScenario(userId, this);
        userStateManager.setUserState(userId, initialState);
        userContextManager.setUserContext(userId, context);

        return initialState.enterToState(context);
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = UpdateParser.getUserId(update);
        TeacherContext context = userContextManager.getUserContext(userId);
        ScenarioState<TeacherContext> currentState = userStateManager.getUserState(userId);

        if (context == null || currentState == null) {
            return finishScenario(userId, "Something went wrong.");
        }

        StateResult<TeacherContext> result = currentState.handleUpdate(update, context);

        if (result.hasNextState()) {
            sendMessageIfRequired(result);
            userStateManager.setUserState(userId, result.getNextState());
            return result.getNextState().enterToState(context);
        } else {
            return result.getResponseMessage();
        }
    }

    private BotApiMethod<?> finishScenario(Long userId, String message) {
        userContextManager.removeUserContext(userId);
        userStateManager.removeUserState(userId);
        userService.clearUserScenario(userId);
        return new SendMessage(String.valueOf(userId), message);
    }

    private void sendMessageIfRequired(StateResult<TeacherContext> result) {
        if (result.hasResponseMessage()){
            senderService.execute(result.getResponseMessage());
        }
    }
}
