package test.project.telega.bot.scenarios;

import test.project.telega.bot.scenarios.contexts.Context;
import test.project.telega.bot.scenarios.contexts.SubjectContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserContextManager<T extends Context> {
    private final Map<Long, T> userContexts = new ConcurrentHashMap<>();

    public T getUserContext(Long userId){
        return userContexts.get(userId);
    }

    public void setUserContext(Long userId, T context){
        userContexts.put(userId, context);
    }

    public void removeUserContext(Long userId){
        userContexts.remove(userId);
    }
}
