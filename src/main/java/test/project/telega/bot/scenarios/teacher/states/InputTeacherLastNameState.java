package test.project.telega.bot.scenarios.teacher.states;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.contexts.TeacherContext;

import static test.project.telega.bot.tools.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.UpdateParser.*;

@Component
@RequiredArgsConstructor
public class InputTeacherLastNameState implements ScenarioState<TeacherContext> {
    @Override
    public BotApiMethod<?> enterToState(TeacherContext context) {
        return sendMessage(context.getChatId(),
                "Write teacher last name");
    }

    @Override
    public StateResult<TeacherContext> handleUpdate(Update update, TeacherContext context) {
        if (update.hasMessage()) {
            context.getBuilder().lastName(getMessage(update));
            return new StateResult<>(sendMessage(getUserId(update), "Teacher created!"), null);

        } else {
            return new StateResult<>(sendMessage(getUserId(update),
                    "Teacher last name is required!"),
                    this);
        }
    }
}
