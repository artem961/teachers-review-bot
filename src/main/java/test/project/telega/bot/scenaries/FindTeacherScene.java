package test.project.telega.bot.scenaries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenaries.states.FindTeacherState;
import test.project.telega.bot.scenaries.states.InputFeedbackState;
import test.project.telega.bot.scenaries.states.InputTeacherState;
import test.project.telega.bot.scenaries.states.UserState;
import test.project.telega.data.entities.Feedback;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.TeacherService;

import java.util.*;

@Component
public class FindTeacherScene extends Scene {
    @Autowired
    private TeacherService teacherService;
    private Map<Long, FindTeacherState> userSceneStates;

    {
        userSceneStates = new HashMap<>();
    }

    public void resetUserSceneState(Long userId) {
        userSceneStates.put(userId, FindTeacherState.NONE);
    }

    private FindTeacherState getUserSceneState(Long userId) {
        FindTeacherState userSceneState = userSceneStates.get(userId);
        if (userSceneState == null) {
            userSceneState = FindTeacherState.NONE;
        }
        return userSceneState;
    }

    @Override
    public BotApiMethod process(Update update) {
        Long userId = getUserId(update);

        switch (getUserSceneState(userId)) {
            case NONE:
                userSceneStates.put(userId, FindTeacherState.INPUT_TEACHER_NAME);
                return SendMessage
                        .builder()
                        .chatId(userId)
                        .text("Write teachers name like:\n\n" +
                                "\"Ivanov Ivan Ivanovich\"\n" +
                                "\"Ivan Ivanovich\"\n" +
                                "\"Ivanovich\".")
                        .build();

            case INPUT_TEACHER_NAME:
                if (update.hasMessage()) {
                    List<String> data = List.of(getMessage(update).strip().split("\\s+"));

                    List<Teacher> teachers = new ArrayList<>();
                    if (data.size() >= 3) {
                        teachers = teacherService.findTeacherBySurnameFirstNameAndLastName(data.get(0), data.get(1), data.get(2));
                    } else if (data.size() == 2) {
                        teachers = teacherService.findTeacherByFirstNameAndLastName(data.get(0), data.get(1));
                    } else if (data.size() == 1) {
                        teachers = teacherService.findTeacherBySurname(data.get(0));
                    }

                    userService.setState(userId, UserState.NONE);
                    userSceneStates.remove(userId);

                    List<String> teachersString = teachers
                            .stream()
                            .map(teacher -> getCaptionForTeacher(teacher))
                            .toList();

                    return SendMessage
                            .builder()
                            .chatId(userId)
                            .text("Find teachers:\n" + String.join("\n\n", teachersString))
                            .build();
                } else {
                    return getErrorMessage(update, "Write teacher name!");
                }
        }
        return getErrorMessage(update);
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
