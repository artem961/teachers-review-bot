package test.project.telega.bot.scenarios.teacher.states;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenarios.ScenarioState;
import test.project.telega.bot.scenarios.contexts.TeacherContext;
import test.project.telega.bot.scenarios.StateResult;
import test.project.telega.data.entities.Feedback;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.TeacherService;

import java.util.ArrayList;
import java.util.List;

import static test.project.telega.bot.tools.MessageGenerator.sendMessage;
import static test.project.telega.bot.tools.UpdateParser.getMessage;
import static test.project.telega.bot.tools.UpdateParser.getUserId;

@Component
@RequiredArgsConstructor
public class FindTeachersState implements ScenarioState<TeacherContext> {
    private final TeacherService teacherService;
    private final Integer MAX_MESSAGE_LENGTH = 4000;
    private final Integer TEACHER_CAPTION_AVG_LENGTH = 100;
    private Integer maxCommentsLength = 100;

    @Override
    public BotApiMethod<?> enterToState(TeacherContext context) {
        return SendMessage
                .builder()
                .chatId(context.getChatId())
                .text("🔍 Write teacher's name to search:")
                .build();
    }

    @Override
    public StateResult<TeacherContext> handleUpdate(Update update, TeacherContext context) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            List<String> data = List.of(getMessage(update).strip().split("\\s+"));

            List<Teacher> teachers = teacherService.findTeacher(data);

            if (teachers.size() != 0) {
                Integer comments_length = MAX_MESSAGE_LENGTH;
                comments_length -= teachers.size() * TEACHER_CAPTION_AVG_LENGTH;
                comments_length /= teachers.size();
                maxCommentsLength = comments_length;

                if (maxCommentsLength <= 0) {
                    return new StateResult<>(SendMessage
                            .builder()
                            .chatId(getUserId(update))
                            .text("🔍 <b>Found " + teachers.size() + " teachers:</b>\n\n" +
                                    "❗Too many for display❗\n" +
                                    "❗Modify your search criteria❗")
                            .parseMode("HTML")
                            .build(),
                            null);
                }

                return new StateResult<>(SendMessage
                        .builder()
                        .chatId(getUserId(update))
                        .text("🔍 <b>Found " + teachers.size() + " teachers:</b>\n\n" +
                                String.join("\n\n", teachers.stream()
                                        .map(this::getCaptionForTeacher)
                                        .toArray(String[]::new)))
                        .parseMode("HTML")
                        .build(),
                        null);
            } else {
                return new StateResult<>(SendMessage
                        .builder()
                        .chatId(getUserId(update))
                        .text("Teachers not found \uD83D\uDE14")
                        .build(),
                        null);
            }

        } else {
            return new StateResult<>(sendMessage(getUserId(update), "Write teacher name!"), this);
        }
    }

    private Double getAvgRating(List<Feedback> feedbackList) {
        Double avgRating = feedbackList.stream().mapToDouble(Feedback::getRating).sum();
        Double avg = avgRating / feedbackList.size();
        return Math.round(avg * 100) / 100.0;
    }

    private String getCaptionForTeacher(Teacher teacher) {
        List<String> comments = teacher.getFeedbackList().stream()
                .map(Feedback::getComment)
                .filter(comment -> comment != null && !comment.trim().isEmpty())
                .toList();

        String commentsString = String.join("\n", comments.stream()
                .map(c -> "• \"" + c + "\"\n")
                .map(this::shieldString)
                .toArray(String[]::new));

        if (commentsString.length() > maxCommentsLength) {
            commentsString = commentsString.substring(0, maxCommentsLength) + "...";
        }

        return String.format(
                "<b>%s</b>\n" +
                        "⭐ <i>Rating: %s / 5.0</i>\n" +
                        "%s",
                teacher,
                getAvgRating(teacher.getFeedbackList()),
                comments.isEmpty() ?
                        "<i>No feedbacks</i>" :
                        "📝 <i>Feedbacks:</i>\n\n<code>" +
                                commentsString +
                                "</code>"
        );
    }

    private String shieldString(String string) {
        return string
                .replaceAll("&", "&amp;")
                .replaceAll("=", "&#61;")
                .replaceAll(">", "&gt;")
                .replaceAll("<", "&lt;");

    }
}
