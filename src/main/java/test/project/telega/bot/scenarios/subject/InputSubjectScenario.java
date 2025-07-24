package test.project.telega.bot.scenarios.subject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import test.project.telega.bot.TelegramSenderService;
import test.project.telega.bot.scenarios.*;
import test.project.telega.bot.scenarios.contexts.SubjectContext;
import test.project.telega.bot.scenarios.subject.states.InputSubjectNameState;
import test.project.telega.bot.tools.keyboard.inline.UpdateParser;
import test.project.telega.services.SubjectService;
import test.project.telega.services.UserService;

@Component
@RequiredArgsConstructor
public class InputSubjectScenario implements Scenario {
    private final TelegramSenderService senderService;
    private final UserService userService;
    private final SubjectService subjectService;
    private final UserContextManager<SubjectContext> userContextManager = new UserContextManager<>();
    private final UserScenarioStateManager<SubjectContext> userStateManager = new UserScenarioStateManager<>();
    private final InputSubjectNameState initialState;

    @Override
    public BotApiMethod<?> startScenario(Update update) {
        Long userId = UpdateParser.getUserId(update);
        SubjectContext context = new SubjectContext(userId);

        userService.setUserScenario(userId, this);
        userStateManager.setUserState(userId, initialState);
        userContextManager.setUserContext(userId, context);

        return initialState.enterToState(context);
    }

    @Override
    public BotApiMethod<?> processUpdate(Update update) {
        Long userId = UpdateParser.getUserId(update);
        SubjectContext context = userContextManager.getUserContext(userId);
        ScenarioState<SubjectContext> currentState = userStateManager.getUserState(userId);

        if (context == null || currentState == null) {
            return finishScenario(userId, "Something went wrong.");
        }


        StateResult<SubjectContext> result = currentState.handleUpdate(update, context);

        if (result.hasNextState()) {
            sendMessageIfRequired(result);
            userStateManager.setUserState(userId, result.getNextState());
            return result.getNextState().enterToState(context);
        } else {
            if (subjectService.insertIfNotExist(context.getBuilder().build())){
                return result.getResponseMessage();
            } else{
                return finishScenario(userId, "Subject already exists!");
            }

        }
    }

    private BotApiMethod<?> finishScenario(Long userId, String message) {
       userContextManager.removeUserContext(userId);
        userStateManager.removeUserState(userId);
        userService.clearUserScenario(userId);
        return new SendMessage(String.valueOf(userId), message);
    }

    private void sendMessageIfRequired(StateResult<SubjectContext> result) {
        if (result.hasResponseMessage()){
           senderService.execute(result.getResponseMessage());
        }
    }
}
