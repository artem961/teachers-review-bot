package test.project.telega.bot.scenarios.subject.states;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.contexts.SubjectContext;
import test.project.telega.bot.scenarios.StateResult;

import static test.project.telega.bot.tools.keyboard.inline.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.getUserId;

@Component
public class InputSubjectNameState implements ScenarioState<SubjectContext> {
    @Override
    public BotApiMethod<?> enterToState(SubjectContext context) {
        return SendMessage
                .builder()
                .chatId(context.getChatId())
                .text("Write subject name.")
                .build();
    }

    @Override
    public StateResult<SubjectContext> handleUpdate(Update update, SubjectContext context) {
       if (update.hasMessage() && update.getMessage().hasText()) {
           context.getBuilder().name(update.getMessage().getText().strip());
           return new StateResult<>(sendMessage(getUserId(update), "Subject created!"), null);
       }
       return new StateResult<>(null, this);
    }
}
