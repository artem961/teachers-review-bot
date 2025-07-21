package test.project.telega.bot.scenaries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import test.project.telega.bot.keyboard.inline.InlineKeyboardController;
import test.project.telega.bot.scenaries.states.InputFeedbackState;
import test.project.telega.bot.scenaries.states.InputSubjectState;
import test.project.telega.bot.scenaries.states.InputTeacherState;
import test.project.telega.bot.scenaries.states.UserState;
import test.project.telega.data.entities.Subject;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.SubjectService;
import test.project.telega.services.TeacherService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InputTeacherScene extends Scene {
    private Map<Long, InputTeacherState> userSceneStates;
    private Map<Long, Teacher> userTeacher;
    private InlineKeyboardController<Subject> inlineController;
    private TeacherService teacherService;
    private SubjectService subjectService;

    {
        userSceneStates = new ConcurrentHashMap<>();
        userTeacher = new ConcurrentHashMap<>();
    }

    @Autowired
    public InputTeacherScene(TeacherService teacherService,
                             SubjectService subjectService) {
        inlineController = new InlineKeyboardController<>(
                Subject::getName,
                subject -> subject.getId().toString(),
                subjectService.findAll(),
                1,
                4);
        this.teacherService = teacherService;
        this.subjectService = subjectService;
    }

    public void resetUserSceneState(Long userId) {
        userSceneStates.put(userId, InputTeacherState.NONE);
    }

    private InputTeacherState getUserSceneState(Long userId) {
        InputTeacherState userSceneState = userSceneStates.get(userId);
        if (userSceneState == null) {
            userSceneState = InputTeacherState.NONE;
        }
        return userSceneState;
    }

    @Override
    public BotApiMethod process(Update update) {
        inlineController.setCollectionToShow(subjectService.findAll());
        Long userId = getUserId(update);
        String message = getMessage(update);

        switch (getUserSceneState(userId)) {
            case NONE:
                userSceneStates.put(userId, InputTeacherState.INPUT_FIRST_NAME);
                userTeacher.put(userId, new Teacher());

                return SendMessage
                        .builder()
                        .chatId(userId)
                        .text("Write teacher name.")
                        .build();

            case INPUT_FIRST_NAME:
                if (update.hasMessage()) {
                    userSceneStates.put(userId, InputTeacherState.INPUT_SURNAME);
                    userTeacher.get(userId).setFirstName(message);
                    return SendMessage
                            .builder()
                            .chatId(userId)
                            .text("Write teacher surname.")
                            .build();
                } else {
                    return getErrorMessage(update, "Teacher name is required!");
                }

            case INPUT_SURNAME:
                if (update.hasMessage()) {
                    userSceneStates.put(userId, InputTeacherState.INPUT_LAST_NAME);
                    userTeacher.get(userId).setSurname(message);
                    return SendMessage
                            .builder()
                            .chatId(userId)
                            .text("Write teacher last name.")
                            .build();
                } else {
                    return getErrorMessage(update, "Surname is required!");
                }

            case INPUT_LAST_NAME:
                if (update.hasMessage()) {

                    userSceneStates.put(userId, InputTeacherState.INPUT_SUBJECT);
                    userTeacher.get(userId).setLastName(message);

                    inlineController.setUserPage(userId, 0);
                    ReplyKeyboard replyKeyboard = inlineController.getKeyboardForUser(userId);
                    return SendMessage
                            .builder()
                            .chatId(userId)
                            .text("Select teacher subject:\n" +
                                    "page " +
                                    inlineController.getPagesLabelForUser(userId))
                            .replyMarkup(replyKeyboard)
                            .build();
                } else {
                    return getErrorMessage(update, "Teachers last name is required!");
                }

            case INPUT_SUBJECT:
                if (update.hasCallbackQuery()) {
                    if (inlineController.isPageButtonSelected(update.getCallbackQuery().getData())) {
                        inlineController.updateUserPage(userId, update.getCallbackQuery().getData());

                        return EditMessageText
                                .builder()
                                .chatId(userId)
                                .messageId(getMessageId(update))
                                .text("Select teacher subject:\n" +
                                        "page " +
                                        inlineController.getPagesLabelForUser(userId))
                                .replyMarkup(inlineController.getKeyboardForUser(userId))
                                .build();
                    } else {
                        Long subjectId = Long.valueOf(update.getCallbackQuery().getData());
                        Subject subject = subjectService.findById(subjectId);

                        if (subject != null) {
                            userTeacher.get(userId).setSubject(subject);
                            teacherService.addTeacher(userTeacher.get(userId));

                            userTeacher.remove(userId);
                            userSceneStates.remove(userId);
                            userService.setState(userId, UserState.NONE);
                            return EditMessageText
                                    .builder()
                                    .messageId(getMessageId(update))
                                    .chatId(userId)
                                    .text("Teacher created!")
                                    .build();
                        } else {
                            userTeacher.remove(userId);
                            userSceneStates.remove(userId);
                            userService.setState(userId, UserState.NONE);
                            return EditMessageText
                                    .builder()
                                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                                    .chatId(userId)
                                    .text("Subject not found!")
                                    .build();
                        }
                    }
                } else {
                    return getErrorMessage(update, "Select subject!");
                }
        }
        return getErrorMessage(update);
    }
}
