package test.project.telega.bot.scenarios.subject;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import test.project.telega.bot.TelegramSenderService;
import test.project.telega.bot.scenarios.*;
import test.project.telega.bot.scenarios.contexts.SubjectContext;
import test.project.telega.bot.scenarios.subject.states.InputSubjectNameState;
import test.project.telega.services.SubjectService;
import test.project.telega.services.UserService;

@Component
public class CreateSubjectScenario extends DefaultScenario<SubjectContext, InputSubjectNameState> {
    private final SubjectService subjectService;

    public CreateSubjectScenario(TelegramSenderService senderService,
                                 UserService userService,
                                 InputSubjectNameState initialState,
                                 SubjectService subjectService) {
        super(senderService, userService, initialState);
        this.subjectService = subjectService;
    }


    @Override
    protected SubjectContext createInitialContext(Long userId) {
        return new SubjectContext(userId);
    }

    @Override
    protected BotApiMethod<?> onScenarioFinished(SubjectContext context, StateResult<SubjectContext> finalResult) {
        if (subjectService.insertIfNotExist(context.getBuilder().build())){
            return finalResult.getResponseMessage();
        } else{
            return finishScenario(context.getChatId(), "Subject already exists!");
        }
    }
}
