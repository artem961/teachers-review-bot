package test.project.telega.bot.scenarios.teacher.states;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.bot.scenarios.contexts.TeacherContext;
import test.project.telega.data.entities.Subject;
import test.project.telega.services.SubjectService;

import java.util.List;

import static test.project.telega.bot.tools.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.UpdateParser.getMessage;
import static test.project.telega.bot.tools.UpdateParser.getUserId;

@Component
@RequiredArgsConstructor
public class FindSubjectState implements ScenarioState<TeacherContext> {
    private final SubjectService subjectService;
    private final InputTeacherSubjectState nextState;

    @Override
    public BotApiMethod<?> enterToState(TeacherContext context) {
        if (context.getMessageId() == null) {
            return SendMessage
                    .builder()
                    .chatId(context.getChatId())
                    .text("Write subject name to search")
                    .build();
        } else{
            return EditMessageText
                    .builder()
                    .messageId(context.getMessageId())
                    .chatId(context.getChatId())
                    .text("Write subject name to search")
                    .build();
        }

    }

    @Override
    public StateResult<TeacherContext> handleUpdate(Update update, TeacherContext context) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = getMessage(update).strip();
            Long userId = getUserId(update);

            List<Subject> subjects = subjectService.findByName(message);
            if (subjects.isEmpty()) {
                subjects = subjectService.findByName(message.substring(0, message.length() / 2));
            }

            if (subjects.isEmpty()) {
                return new StateResult<>(sendMessage(userId, "Subjects not found!\n\n" +
                        "To create new subject write /new_subject"), this);
            } else{
                context.setFindSubjects(subjects);
                return new StateResult<>(null, nextState);
            }
        } else{
            return new StateResult<>(null, this);
        }
    }
}
