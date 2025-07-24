package test.project.telega.bot.scenarios.teacher.states;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.contexts.TeacherContext;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.data.entities.Feedback;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.TeacherService;

import java.util.ArrayList;
import java.util.List;

import static test.project.telega.bot.tools.keyboard.inline.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.getMessage;
import static test.project.telega.bot.tools.keyboard.inline.UpdateParser.getUserId;

@Component
@RequiredArgsConstructor
public class InputTeacherFullNameState implements ScenarioState<TeacherContext> {
    private final TeacherService teacherService;
    private final TelegramClient telegramClient;

    @Override
    public BotApiMethod<?> enterToState(TeacherContext context) {
        return SendMessage
                .builder()
                .chatId(context.getChatId())
                .text("Write teachers name like:\n\n" +
                        "\"Ivanov Ivan Ivanovich\"\n" +
                        "\"Ivan Ivanovich\"\n" +
                        "\"Ivanovich\".")
                .build();
    }

    @Override
    public StateResult<TeacherContext> handleUpdate(Update update, TeacherContext context) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            List<String> data = List.of(getMessage(update).strip().split("\\s+"));

            List<Teacher> teachers = new ArrayList<>();
            if (data.size() >= 3) {
                teachers = teacherService.findTeacherBySurnameFirstNameAndLastName(data.get(0), data.get(1), data.get(2));
            } else if (data.size() == 2) {
                teachers = teacherService.findTeacherByFirstNameAndLastName(data.get(0), data.get(1));
            } else if (data.size() == 1) {
                teachers = teacherService.findTeacherBySurname(data.get(0));
            }


            List<String> teachersString = teachers
                    .stream()
                    .map(teacher -> getCaptionForTeacher(teacher))
                    .toList();

            return new StateResult<>(sendMessage(
                    getUserId(update),
                    "Find teachers:\n" + String.join("\n\n", teachersString)),
                    null);

        } else {
            return new StateResult<>(sendMessage(getUserId(update), "Write teacher name!"), this);
        }
    }

    private Double getAvgRating(List<Feedback> feedbackList) {
        Integer avgRating = feedbackList.stream().mapToInt(Feedback::getRating).sum();
        return (double) avgRating / feedbackList.size();
    }

    private String getCaptionForTeacher(Teacher teacher) {
        return teacher +
                ":\n\trating - " +
                getAvgRating(teacher.getFeedbackList()) +
                "\n\tcomments - (\"" +
                String.join("\", \"", teacher.getFeedbackList()
                        .stream()
                        .map(feedback -> feedback.getComment()).toList()) +
                "\")";
    }
}
