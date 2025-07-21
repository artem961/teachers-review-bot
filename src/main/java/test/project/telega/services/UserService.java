package test.project.telega.services;

import org.springframework.stereotype.Service;
import test.project.telega.bot.scenaries.states.UserState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<Long, UserState> users;

    public UserService() {
        this.users = new ConcurrentHashMap<>();
    }

    public UserState getState(Long userId){
        if (this.users.get(userId) == null) {
            users.put(userId, UserState.NONE);
        }
        return this.users.get(userId);
    }

    public void setState(Long userId, UserState state){
        this.users.put(userId, state);
    }
}
