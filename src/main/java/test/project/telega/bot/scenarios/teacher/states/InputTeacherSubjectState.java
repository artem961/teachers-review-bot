package test.project.telega.bot.scenarios.teacher.states;

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
import test.project.telega.bot.scenarios.contexts.TeacherContext;
import test.project.telega.bot.tools.keyboard.inline.BackConfirmController;
import test.project.telega.bot.tools.keyboard.inline.CollectionPeekerController;
import test.project.telega.data.entities.Subject;
import test.project.telega.services.SubjectService;

import static test.project.telega.bot.tools.MessageGenerator.deleteMessage;
import static test.project.telega.bot.tools.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.UpdateParser.*;

@Component
public class InputTeacherSubjectState implements ScenarioState<TeacherContext> {
    private final SubjectService subjectService;
    private final InputTeacherNameState nextState;
    private final FindSubjectState previousState;


    private CollectionPeekerController<Subject> inlineController = new CollectionPeekerController<>(
            Subject::getName,
            subject -> subject.getId().toString(),
            null,
            1,
            4);

    public InputTeacherSubjectState(SubjectService subjectService,
                                    InputTeacherNameState nextState,
                                    @Lazy FindSubjectState previousState) {
        this.subjectService = subjectService;
        this.nextState = nextState;
        this.previousState = previousState;
    }

    ;

    @Override
    public BotApiMethod<?> enterToState(TeacherContext context) {
        inlineController.setCollectionToShow(context.getFindSubjects());
        InlineKeyboardMarkup replyKeyboard = inlineController.getKeyboardForUser(context.getChatId());
        replyKeyboard = BackConfirmController.addBackButton(replyKeyboard);

        if (context.getMessageId() == null){
            inlineController.setUserPage(context.getChatId(), 0);

            return SendMessage
                    .builder()
                    .chatId(context.getChatId())
                    .text("Select teacher subject:\n" +
                            "page " +
                            inlineController.getPagesLabelForUser(context.getChatId()))
                    .replyMarkup(replyKeyboard)
                    .build();
        } else{

            return EditMessageText
                    .builder()
                    .chatId(context.getChatId())
                    .messageId(context.getMessageId())
                    .text("Select teacher subject:\n" +
                            "page " +
                            inlineController.getPagesLabelForUser(context.getChatId()))
                    .replyMarkup(replyKeyboard)
                    .build();
        }
    }

    @Override
    public StateResult<TeacherContext> handleUpdate(Update update, TeacherContext context) {
        if (update.hasCallbackQuery()) {
            Long userId = getUserId(update);
            String callbackData = getCallbackData(update);
            context.setMessageId(getMessageId(update));

            if (inlineController.isPageButtonSelected(callbackData)) {
                inlineController.updateUserPage(userId, getCallbackData(update));
                context.setMessageId(getMessageId(update));
                return new StateResult<>(null, this);

            } else if (BackConfirmController.isBackButtonSelected(callbackData)) {
                context.setMessageId(null);
                inlineController.clearUserData(userId);
                return new StateResult<>(deleteMessage(userId, getMessageId(update)), previousState);
            } else {
                Long subjectId = Long.valueOf(callbackData);
                Subject subject = subjectService.findById(subjectId);

                if (subject != null) {
                    context.getBuilder().subject(subject);

                    return new StateResult<>(null, nextState);
                } else {
                    return new StateResult<>(EditMessageText
                            .builder()
                            .messageId(getMessageId(update))
                            .chatId(userId)
                            .text("Subject not found!")
                            .build(), this);
                }
            }
        } else {
            return new StateResult<>(sendMessage(getUserId(update), "Select subject!"), this);
        }
    }
}
