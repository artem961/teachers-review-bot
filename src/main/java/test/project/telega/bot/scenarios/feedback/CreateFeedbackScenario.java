package test.project.telega.bot.scenarios.feedback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import test.project.telega.bot.TelegramSenderService;
import test.project.telega.bot.scenarios.*;
import test.project.telega.bot.scenarios.contexts.FeedbackContext;
import test.project.telega.bot.scenarios.feedback.states.FindTeacherState;
import test.project.telega.bot.scenarios.feedback.states.InputTeacherState;
import test.project.telega.services.FeedbackService;
import test.project.telega.services.UserService;

@Component
public class CreateFeedbackScenario extends DefaultScenario<FeedbackContext, FindTeacherState> {
    private final FeedbackService feedbackService;

    public CreateFeedbackScenario(TelegramSenderService senderService,
                                  UserService userService,
                                  FindTeacherState initialState,
                                  FeedbackService feedbackService) {
        super(senderService, userService, initialState);
        this.feedbackService = feedbackService;
    }

    @Override
    protected FeedbackContext createInitialContext(Long userId) {
        return new FeedbackContext(userId);
    }

    @Override
    protected BotApiMethod<?> onScenarioFinished(FeedbackContext context, StateResult<FeedbackContext> finalResult) {
        feedbackService.addFeedback(context.getBuilder().build());
        return finalResult.getResponseMessage();
    }
}
