package test.project.telega.bot.scenarios.feedback.states;

import lombok.RequiredArgsConstructor;
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
import test.project.telega.bot.tools.keyboard.inline.InlineKeyboardController;
import test.project.telega.bot.tools.keyboard.inline.InlineKeyboardGenerator;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.TeacherService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static test.project.telega.bot.tools.keyboard.inline.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.*;

@Component
@RequiredArgsConstructor
public class InputTeacherState implements ScenarioState<FeedbackContext> {
    private final TeacherService teacherService;
    private final ConfirmTeacherState nextState;

    private InlineKeyboardController<Teacher> inlineController =
            new InlineKeyboardController<>(Teacher::toString,
                    teacher -> teacher.getId().toString(),
                    null,
                    1,
                    4);

    @Override
    public BotApiMethod<?> enterToState(FeedbackContext context) {
        inlineController.setCollectionToShow(teacherService.findAll());

        if (context.getMessageId() == null){
            inlineController.setUserPage(context.getChatId(), 0);
            return SendMessage
                    .builder()
                    .chatId(context.getChatId())
                    .text("Select teacher:\n" +
                            "page " +
                            inlineController.getPagesLabelForUser(context.getChatId()))
                    .replyMarkup(inlineController.getKeyboardForUser(context.getChatId()))
                    .build();
        } else{
            return EditMessageText
                    .builder()
                    .chatId(context.getChatId())
                    .messageId(context.getMessageId())
                    .text("Select teacher:\n" +
                            "page " +
                            inlineController.getPagesLabelForUser(context.getChatId()))
                    .replyMarkup(inlineController.getKeyboardForUser(context.getChatId()))
                    .build();
        }
    }

    @Override
    public StateResult<FeedbackContext> handleUpdate(Update update, FeedbackContext context) {
        if (update.hasCallbackQuery()) {
            String callbackData = getCallbackData(update);
            Long userId = context.getChatId();
            context.setMessageId(getMessageId(update));

            if (inlineController.isPageButtonSelected(callbackData)){
                inlineController.updateUserPage(userId, callbackData);
                return new StateResult<>(null, this);
            } else {
                Long teacherId = Long.valueOf(callbackData);
                Teacher teacher = teacherService.findById(teacherId);

                if (teacher != null) {
                    context.setTeacher(teacher);

                    return new StateResult<>(null, nextState);
                } else{
                    return new StateResult<>(sendMessage(getUserId(update), "Teacher not found!"), this);
                }
            }
        } else{
            return new StateResult<>(sendMessage(getUserId(update), "Select teacher!"), this);
        }
    }
}
