package test.project.telega.bot.scenarios.feedback.states;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.contexts.FeedbackContext;
import test.project.telega.bot.tools.keyboard.inline.BackConfirmController;
import test.project.telega.bot.tools.keyboard.inline.CollectionPeekerController;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.TeacherService;

import static test.project.telega.bot.tools.MessageGenerator.*;
import static test.project.telega.bot.tools.UpdateParser.*;

@Component
public class InputTeacherState implements ScenarioState<FeedbackContext> {
    private final TeacherService teacherService;
    private final ConfirmTeacherState nextState;
    private final FindTeacherState previousState;

    private CollectionPeekerController<Teacher> inlineController =
            new CollectionPeekerController<>(Teacher::toString,
                    teacher -> teacher.getId().toString(),
                    null,
                    1,
                    4);

    public InputTeacherState(TeacherService teacherService,
                             ConfirmTeacherState nextState,
                             @Lazy FindTeacherState previousState) {
        this.teacherService = teacherService;
        this.nextState = nextState;
        this.previousState = previousState;
    }

    @Override
    public BotApiMethod<?> enterToState(FeedbackContext context) {
        inlineController.setCollectionToShow(context.getFoundTeachers());
        InlineKeyboardMarkup markup = inlineController.getKeyboardForUser(context.getChatId());
        markup = BackConfirmController.addBackButton(markup);

        if (context.getMessageId() == null) {
            inlineController.setUserPage(context.getChatId(), 0);
            return sendMessage(context.getChatId(),
                    "Select teacher:\n" +
                            "page " +
                            inlineController.getPagesLabelForUser(context.getChatId()),
                    markup);
        } else {
            return editMessage(context.getChatId(),
                    context.getMessageId(),
                    "Select teacher:\n" +
                            "page " +
                            inlineController.getPagesLabelForUser(context.getChatId()),
                    markup);
        }
    }

    @Override
    public StateResult<FeedbackContext> handleUpdate(Update update, FeedbackContext context) {
        if (update.hasCallbackQuery()) {
            String callbackData = getCallbackData(update);
            context.setMessageId(getMessageId(update));

            if (inlineController.checkAndUpdateKeyboard(update)) {
                return new StateResult<>(null, this);
            } else if (BackConfirmController.isBackButtonSelected(callbackData)){
                context.setMessageId(null);
                inlineController.clearUserData(getUserId(update));

                return new StateResult<>(deleteMessage(getUserId(update), getMessageId(update)), previousState);
            } else {
                Long teacherId = Long.valueOf(callbackData);
                Teacher teacher = teacherService.findById(teacherId);

                if (teacher != null) {
                    context.setTeacher(teacher);

                    return new StateResult<>(null, nextState);
                } else {
                    return new StateResult<>(sendMessage(getUserId(update), "Teacher not found!"), this);
                }
            }
        } else {
            return new StateResult<>(sendMessage(getUserId(update), "Select teacher!"), this);
        }
    }
}
