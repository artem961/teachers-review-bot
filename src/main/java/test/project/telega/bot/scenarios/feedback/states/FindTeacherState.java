package test.project.telega.bot.scenarios.feedback.states;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.contexts.FeedbackContext;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.TeacherService;

import java.util.ArrayList;
import java.util.List;

import static test.project.telega.bot.tools.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.UpdateParser.*;

@Component
@RequiredArgsConstructor
public class FindTeacherState implements ScenarioState<FeedbackContext> {
    private final TeacherService teacherService;
    private final InputTeacherState nextState;

    @Override
    public BotApiMethod<?> enterToState(FeedbackContext context) {
        if (context.getMessageId() != null) {
            return EditMessageText
                    .builder()
                    .messageId(context.getMessageId())
                    .chatId(context.getChatId())
                    .text("Write teacher's surname")
                    .build();
        } else {
            return SendMessage
                    .builder()
                    .chatId(context.getChatId())
                    .text("Write teacher's surname")
                    .build();
        }
    }

    @Override
    public StateResult<FeedbackContext> handleUpdate(Update update, FeedbackContext context) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            List<String> data = List.of(getMessage(update).strip().split("\\s+"));

            List<Teacher> teachers = teacherService.findTeacher(data);

            if (teachers.size() != 0) {
                if (teachers.size() >= 30) {
                    return new StateResult<>(SendMessage
                            .builder()
                            .chatId(getUserId(update))
                            .text("❗Found too many teachers❗\n" +
                                    "❗Modify your search criteria❗")
                            .build(),
                            this);
                } else {
                    context.setFoundTeachers(teachers);
                    return new StateResult<>(null, nextState);
                }
            } else {
                context.setMessageId(null);
                return new StateResult<>(SendMessage
                        .builder()
                        .chatId(getUserId(update))
                        .text("Teachers not found!\n\n" +
                                "To create new teacher write /new_teacher")
                        .build(),
                        this);
            }
        } else {
            return new StateResult<>(sendMessage(getUserId(update), "Write teacher name!"), this);
        }
    }
}
