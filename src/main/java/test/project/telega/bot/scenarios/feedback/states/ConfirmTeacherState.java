package test.project.telega.bot.scenarios.feedback.states;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.contexts.FeedbackContext;
import test.project.telega.bot.tools.keyboard.inline.BackConfirmController;
import test.project.telega.bot.tools.keyboard.inline.CollectionPeekerController;
import test.project.telega.bot.tools.keyboard.inline.InlineKeyboardGenerator;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.TeacherService;

import static test.project.telega.bot.tools.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.UpdateParser.*;

@Component
public class ConfirmTeacherState implements ScenarioState<FeedbackContext> {
    private final InputRatingState nextState;
    private final InputTeacherState previousState;

    private BackConfirmController inlineController =  new BackConfirmController();

    public ConfirmTeacherState(InputRatingState nextState,
                               @Lazy InputTeacherState previousState) {
        this.nextState = nextState;
        this.previousState = previousState;
    }

    @Override
    public BotApiMethod<?> enterToState(FeedbackContext context) {
        InlineKeyboardMarkup keyboard = inlineController.getKeyboard();

        if (context.getMessageId() != null) {
            return EditMessageText
                    .builder()
                    .text("Teacher selected: " + context.getTeacher())
                    .chatId(context.getChatId())
                    .messageId(context.getMessageId())
                    .replyMarkup(keyboard)
                    .build();
        } else {
            return SendMessage
                    .builder()
                    .text("Teacher selected: " + context.getTeacher())
                    .chatId(context.getChatId())
                    .replyMarkup(keyboard)
                    .build();
        }
    }

    @Override
    public StateResult<FeedbackContext> handleUpdate(Update update, FeedbackContext context) {
        if (update.hasCallbackQuery()) {
            String callbackData = getCallbackData(update);
            context.setMessageId(getMessageId(update));

            if (inlineController.isBackButtonSelected(callbackData)) {
                return new StateResult<>(null, previousState);
            } else if (inlineController.isConfirmButtonSelected(callbackData)) {
                context.getBuilder().teacher(context.getTeacher());
                return new StateResult<>(null, nextState);
            }
        }
        return new StateResult<>(sendMessage(getUserId(update), "Select teacher!"), this);
    }
}
