package test.project.telega.bot.scenarios.teacher.states;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.contexts.TeacherContext;
import test.project.telega.bot.tools.keyboard.inline.InlineKeyboardController;
import test.project.telega.data.entities.Subject;
import test.project.telega.services.SubjectService;

import static test.project.telega.bot.tools.keyboard.inline.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.*;

@Component
@RequiredArgsConstructor
public class InputTeacherSubjectState implements ScenarioState<TeacherContext> {
    private final SubjectService subjectService;

    private InlineKeyboardController<Subject> inlineController = new InlineKeyboardController<>(
            Subject::getName,
            subject -> subject.getId().toString(),
            null,
            1,
            4);
    ;

    @Override
    public BotApiMethod<?> enterToState(TeacherContext context) {
        inlineController.setCollectionToShow(subjectService.findAll());

        if (context.getMessageId() == null){
            inlineController.setUserPage(context.getChatId(), 0);
            InlineKeyboardMarkup replyKeyboard = inlineController.getKeyboardForUser(context.getChatId());
            return SendMessage
                    .builder()
                    .chatId(context.getChatId())
                    .text("Select teacher subject:\n" +
                            "page " +
                            inlineController.getPagesLabelForUser(context.getChatId()))
                    .replyMarkup(replyKeyboard)
                    .build();
        } else{
            InlineKeyboardMarkup replyKeyboard = inlineController.getKeyboardForUser(context.getChatId());
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
            if (inlineController.isPageButtonSelected(update.getCallbackQuery().getData())) {
                inlineController.updateUserPage(userId, update.getCallbackQuery().getData());
                context.setMessageId(getMessageId(update));

                return new StateResult<>(null, this);
            } else {
                Long subjectId = Long.valueOf(update.getCallbackQuery().getData());
                Subject subject = subjectService.findById(subjectId);

                if (subject != null) {
                    context.getBuilder().subject(subject);

                    return new StateResult<>(EditMessageText
                            .builder()
                            .messageId(getMessageId(update))
                            .chatId(userId)
                            .text("Teacher created!")
                            .build(), null);
                } else {
                    return new StateResult<>(EditMessageText
                            .builder()
                            .messageId(update.getCallbackQuery().getMessage().getMessageId())
                            .chatId(userId)
                            .text("Subject not found!")
                            .build(), null);
                }
            }
        } else {
            return new StateResult<>(sendMessage(getUserId(update), "Select subject!"), this);
        }
    }
}
