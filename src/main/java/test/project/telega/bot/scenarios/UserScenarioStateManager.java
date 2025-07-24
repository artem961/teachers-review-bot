package test.project.telega.bot.scenarios;

import org.springframework.stereotype.Component;
import test.project.telega.bot.scenarios.contexts.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserScenarioStateManager<T extends Context> {
    private final Map<Long, ScenarioState<T>> userState;

    {
        userState = new ConcurrentHashMap<>();
    }

    public ScenarioState<T> getUserState(Long userId){
        return userState.get(userId);
    }

    public void setUserState(Long userId, ScenarioState<T> userState){
        this.userState.put(userId, userState);
    }

    public void removeUserState(Long userId){
        userState.remove(userId);
    }
}
