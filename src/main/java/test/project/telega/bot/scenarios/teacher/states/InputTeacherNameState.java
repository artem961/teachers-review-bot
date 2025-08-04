package test.project.telega.bot.scenarios.teacher.states;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.contexts.TeacherContext;

import static test.project.telega.bot.tools.MessageGenerator.editMessage;
import static test.project.telega.bot.tools.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.UpdateParser.getMessage;
import static test.project.telega.bot.tools.UpdateParser.getUserId;

@Component
@RequiredArgsConstructor
public class InputTeacherNameState implements ScenarioState<TeacherContext> {
    private final InputTeacherSurnameState nextState;

    @Override
    public BotApiMethod<?> enterToState(TeacherContext context) {
        if (context.getMessageId() != null) {
            return editMessage(context.getChatId(),
                    context.getMessageId(),
                    "Write teacher name");
        }
        else{
            return sendMessage(context.getChatId(),
                    "Write teacher name");
        }
    }

    @Override
    public StateResult<TeacherContext> handleUpdate(Update update, TeacherContext context) {
        if (update.hasMessage()) {
            context.getBuilder().firstName(getMessage(update));
            return new StateResult<>(null, nextState);

        } else {
            return new StateResult<>(sendMessage(getUserId(update),
                    "Teacher name is required!"),
                    this);
        }
    }
}
