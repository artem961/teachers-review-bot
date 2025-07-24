package test.project.telega.bot.scenarios.feedback.states;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.contexts.FeedbackContext;
import test.project.telega.bot.tools.keyboard.inline.InlineKeyboardGenerator;


import static test.project.telega.bot.tools.keyboard.inline.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.*;
import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.getUserId;

@Component
@RequiredArgsConstructor
public class InputRatingState implements ScenarioState<FeedbackContext> {
    private final InputCommentState nextState;

    @Override
    public BotApiMethod<?> enterToState(FeedbackContext context) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardGenerator(5)
                .addButton("1", "1")
                .addButton("2", "2")
                .addButton("3", "3")
                .addButton("4", "4")
                .addButton("5", "5")
                .getKeyboard();
        return EditMessageText
                .builder()
                .chatId(context.getChatId())
                .messageId(context.getMessageId())
                .text("Rate teacher from 1 to 5:")
                .replyMarkup(keyboard)
                .build();
    }

    @Override
    public StateResult<FeedbackContext> handleUpdate(Update update, FeedbackContext context) {
        if (update.hasCallbackQuery() && getCallbackData(update).matches("[1-5]")) {
            String callbackData = getCallbackData(update);
            context.getBuilder().rating(Integer.parseInt(callbackData));

            return new StateResult<>(null, nextState);
        } else{
            return new StateResult<>(sendMessage(getUserId(update), "Select rating!"), this);
        }
    }
}
