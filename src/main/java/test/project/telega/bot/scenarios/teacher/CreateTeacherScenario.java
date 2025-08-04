package test.project.telega.bot.scenarios.teacher;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import test.project.telega.bot.TelegramSenderService;
import test.project.telega.bot.scenarios.*;
import test.project.telega.bot.scenarios.contexts.TeacherContext;
import test.project.telega.bot.scenarios.teacher.states.FindSubjectState;
import test.project.telega.bot.scenarios.teacher.states.InputTeacherNameState;
import test.project.telega.data.entities.Teacher;
import test.project.telega.services.TeacherService;
import test.project.telega.services.UserService;

@Component
public class CreateTeacherScenario extends DefaultScenario<TeacherContext, FindSubjectState> {
    private final TeacherService teacherService;

    public CreateTeacherScenario(TelegramSenderService senderService,
                                 UserService userService,
                                 FindSubjectState initialState,
                                 TeacherService teacherService) {
        super(senderService, userService, initialState);
        this.teacherService = teacherService;
    }

    @Override
    protected TeacherContext createInitialContext(Long userId) {
        return new TeacherContext(userId);
    }

    @Override
    protected BotApiMethod<?> onScenarioFinished(TeacherContext context, StateResult<TeacherContext> finalResult) {
        Teacher teacher = context.getBuilder().build();
        teacherService.addTeacher(teacher);
        return finalResult.getResponseMessage();
    }
}
