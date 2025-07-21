package test.project.telega.bot.scenaries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import test.project.telega.bot.scenaries.states.FindTeacherState;
import test.project.telega.bot.scenaries.states.InputFeedbackState;
import test.project.telega.bot.scenaries.states.InputSubjectState;
import test.project.telega.bot.scenaries.states.UserState;
import test.project.telega.data.entities.Subject;
import test.project.telega.services.SubjectService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InputSubjectScene extends Scene {
    @Autowired
    private SubjectService subjectService;
    private Map<Long, InputSubjectState> userSceneStates;

    {
        userSceneStates = new HashMap<>();
    }

    public void resetUserSceneState(Long userId) {
        userSceneStates.put(userId, InputSubjectState.NONE);
    }

    private InputSubjectState getUserSceneState(Long userId) {
        InputSubjectState userSceneState = userSceneStates.get(userId);
        if (userSceneState == null) {
            userSceneState = InputSubjectState.NONE;
        }
        return userSceneState;
    }

    @Override
    public BotApiMethod process(Update update) {
        String message = getMessage(update);
        Long userId = getUserId(update);

        switch (getUserSceneState(userId)) {
            case NONE:
                userSceneStates.put(userId, InputSubjectState.INPUT_SUBJECT_NAME);
                return SendMessage
                        .builder()
                        .chatId(userId)
                        .text("Write subject name.")
                        .build();

            case INPUT_SUBJECT_NAME:
                if (update.hasMessage()) {
                    Subject subject = new Subject();
                    subject.setName(message);

                    if (subjectService.insertIfNotExist(subject)) {
                        userService.setState(userId, UserState.NONE);
                        userSceneStates.remove(userId);
                        return SendMessage
                                .builder()
                                .chatId(userId)
                                .text("Subject inserted!")
                                .build();
                    } else {
                        return SendMessage
                                .builder()
                                .chatId(userId)
                                .text("Subject already exist!")
                                .build();
                    }
                } else {
                    return getErrorMessage(update, "Write subject name!");
                }
        }
        return getErrorMessage(update);
    }
}
