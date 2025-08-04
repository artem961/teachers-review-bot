package test.project.telega.bot.scenarios.feedback.states;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.contexts.FeedbackContext;

import static test.project.telega.bot.tools.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.UpdateParser.*;

@Component
@RequiredArgsConstructor
public class InputCommentState implements ScenarioState<FeedbackContext> {
    @Override
    public BotApiMethod<?> enterToState(FeedbackContext context) {
        return EditMessageText
                .builder()
                .messageId(context.getMessageId())
                .chatId(context.getChatId())
                .text("Write comment about teacher")
                .build();
    }

    @Override
    public StateResult<FeedbackContext> handleUpdate(Update update, FeedbackContext context) {
        if (update.hasMessage() && update.getMessage().hasText()) {
          context.getBuilder().comment(update.getMessage().getText());

            return new StateResult<>(sendMessage(context.getChatId(), "Feedback for teacher sent!"), null);
        } else{
            return new StateResult<>(sendMessage(getUserId(update), "Write comment!"), this);
        }
    }
}
