package test.project.telega.bot.scenarios.teacher.states;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.contexts.TeacherContext;

import static test.project.telega.bot.tools.keyboard.inline.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.getMessage;
import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.getUserId;

@Component
@RequiredArgsConstructor
public class InputTeacherSurnameState implements ScenarioState<TeacherContext> {
    private final InputTeacherLastNameState nextState;

    @Override
    public BotApiMethod<?> enterToState(TeacherContext context) {
        return sendMessage(context.getChatId(),
                "Write teacher surname");
    }

    @Override
    public StateResult<TeacherContext> handleUpdate(Update update, TeacherContext context) {
        if (update.hasMessage()) {
            context.getBuilder().surname(getMessage(update));
            return new StateResult<>(null, nextState);

        } else {
            return new StateResult<>(sendMessage(getUserId(update),
                    "Teacher surname is required!"),
                    this);
        }
    }
}
