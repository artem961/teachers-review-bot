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
import test.project.telega.bot.keyboard.inline.InlineKeyboardGenerator;
import test.project.telega.bot.scenaries.states.FindTeacherState;
import test.project.telega.bot.scenaries.states.InputFeedbackState;
import test.project.telega.bot.scenaries.states.InputTeacherState;
import test.project.telega.data.entities.Feedback;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.FeedbackService;
import test.project.telega.services.TeacherService;

import java.util.HashMap;
import java.util.Map;

@Component
public class InputFeedbackScene extends Scene {
    private TeacherService teacherService;
    private FeedbackService feedbackService;
    private InlineKeyboardController<Teacher> inlineController;
    private Map<Long, Teacher> userTeacher;
    private Map<Long, Feedback> userFeedback;
    private Map<Long, InputFeedbackState> userSceneStates;

    {
        userTeacher = new HashMap<>();
        userSceneStates = new HashMap<>();
        userFeedback = new HashMap<>();
    }

    @Autowired
    public InputFeedbackScene(TeacherService teacherService,
                              FeedbackService feedbackService) {
        this.inlineController = new InlineKeyboardController<>(Teacher::toString,
                teacher -> teacher.getId().toString(),
                teacherService.findAll(),
                1,
                4);
        this.teacherService = teacherService;
        this.feedbackService = feedbackService;
    }

    public void resetUserSceneState(Long userId) {
        userSceneStates.put(userId, InputFeedbackState.NONE);
    }

    private InputFeedbackState getUserSceneState(Long userId) {
        InputFeedbackState userSceneState = userSceneStates.get(userId);
        if (userSceneState == null) {
            userSceneState = InputFeedbackState.NONE;
        }
        return userSceneState;
    }

    @Override
    public BotApiMethod process(Update update) {
        inlineController.setCollectionToShow(teacherService.findAll());
        Long userId = getUserId(update);
        String message = getMessage(update);

        switch (getUserSceneState(userId)) {
            case NONE:
                userSceneStates.put(userId, InputFeedbackState.INPUT_TEACHER);
                userFeedback.put(userId, new Feedback());
                return SendMessage
                        .builder()
                        .chatId(userId)
                        .text("Select teacher:\n" +
                                "page " +
                                inlineController.getPagesLabelForUser(userId))
                        .replyMarkup(inlineController.getKeyboardForUser(userId))
                        .build();

            case INPUT_TEACHER:
                if (update.hasCallbackQuery()) {
                    String callbackData = getCallbackData(update);
                    Integer messageId = getMessageId(update);

                    if (inlineController.isPageButtonSelected(callbackData)){
                        inlineController.updateUserPage(userId, callbackData);
                        return EditMessageText
                                .builder()
                                .chatId(userId)
                                .messageId(messageId)
                                .text("Select teacher:\n" +
                                        "page " +
                                        inlineController.getPagesLabelForUser(userId))
                                .replyMarkup(inlineController.getKeyboardForUser(userId))
                                .build();
                    } else if (inlineController.isBackButtonSelected(callbackData)) {
                        userSceneStates.put(userId, InputFeedbackState.INPUT_TEACHER);
                        return EditMessageText
                                .builder()
                                .chatId(userId)
                                .messageId(messageId)
                                .text("Select teacher:\n" +
                                        "page " +
                                        inlineController.getPagesLabelForUser(userId))
                                .replyMarkup( inlineController.getKeyboardForUser(userId))
                                .build();
                    } else if (inlineController.isConfirmButtonSelected(callbackData)) {
                        userSceneStates.put(userId, InputFeedbackState.INPUT_RATING);
                        userFeedback.get(userId).setTeacher(userTeacher.get(userId));
                        InlineKeyboardMarkup keyboard = new InlineKeyboardGenerator(5)
                                .addButton("1", "1")
                                .addButton("2", "2")
                                .addButton("3", "3")
                                .addButton("4", "4")
                                .addButton("5", "5")
                                .getKeyboard();
                        return EditMessageText
                                .builder()
                                .chatId(userId)
                                .messageId(messageId)
                                .text("Rate " + userTeacher.get(userId) + " from 1 to 5:")
                                .replyMarkup(keyboard)
                                .build();
                    } else {
                        Long teacherId = Long.valueOf(callbackData);
                        Teacher teacher = teacherService.findById(teacherId);
                        userTeacher.put(userId, teacher);

                        if (teacher != null) {
                            InlineKeyboardMarkup keyboard = new InlineKeyboardGenerator(1)
                                    .addConfirmButton()
                                    .addBackButton()
                                    .getKeyboard();
                            return EditMessageText
                                    .builder()
                                    .text("Teacher selected: " + teacher)
                                    .chatId(userId)
                                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                                    .replyMarkup(keyboard)
                                    .build();
                        }
                    }
                } else{
                    return getErrorMessage(update, "Select teacher!");
                }

            case INPUT_RATING:
                if (update.hasCallbackQuery() && getCallbackData(update).matches("[1-5]")) {
                    String callbackData = getCallbackData(update);
                    Integer messageId = getMessageId(update);
                    userFeedback.get(userId).setRating(Integer.parseInt(callbackData));
                    userSceneStates.put(userId, InputFeedbackState.INPUT_COMMENT);

                    return EditMessageText
                            .builder()
                            .chatId(userId)
                            .messageId(messageId)
                            .text("Write comment about " + userTeacher.get(userId))
                            .build();
                } else{
                    return getErrorMessage(update, "Select rating!");
                }

            case INPUT_COMMENT:
                if (update.hasMessage()){
                    userFeedback.get(userId).setComment(message);
                    feedbackService.addFeedback(userFeedback.get(userId));

                    String teacher = userTeacher.get(userId).toString();
                    userTeacher.remove(userId);
                    userSceneStates.remove(userId);
                    userFeedback.remove(userId);

                    return SendMessage
                            .builder()
                            .chatId(userId)
                            .text("Feedback for " + teacher + " sent!")
                            .build();
                } else{
                    return getErrorMessage(update, "Write comment!");
                }
        }
        return getErrorMessage(update);
    }
}
