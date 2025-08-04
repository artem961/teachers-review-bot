package test.project.telega.bot.scenarios.teacher;

import org.springframework.stereotype.Component;
import test.project.telega.bot.TelegramSenderService;
import test.project.telega.bot.scenarios.*;
import test.project.telega.bot.scenarios.contexts.TeacherContext;
import test.project.telega.bot.scenarios.teacher.states.FindTeachersState;
import test.project.telega.services.UserService;


@Component
public class FindTeacherScenario extends DefaultScenario<TeacherContext, FindTeachersState> {

    public FindTeacherScenario(TelegramSenderService senderService,
                               UserService userService,
                               FindTeachersState initialState) {
        super(senderService, userService, initialState);
    }

    @Override
    protected TeacherContext createInitialContext(Long userId) {
        return new TeacherContext(userId);
    }
}
