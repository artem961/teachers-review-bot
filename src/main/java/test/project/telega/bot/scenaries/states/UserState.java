package test.project.telega.bot.scenaries.states;

import test.project.telega.AppContext;
import test.project.telega.bot.scenaries.*;


public enum UserState {
    NONE(null),
    FIND_TEACHER(FindTeacherScene.class),
    INPUT_SUBJECT(InputSubjectScene.class),
    INPUT_TEACHER(InputTeacherScene.class),
    INPUT_FEEDBACK(InputFeedbackScene.class);

    private final Class<? extends Scene> sceneClass;

    UserState(Class<? extends Scene> sceneClass) {
        this.sceneClass = sceneClass;
    }

    public Scene getScene() {
        return AppContext.getBean(sceneClass);
    }
}

