package test.project.telega.services;

import org.springframework.stereotype.Service;
import test.project.telega.bot.scenarios.Scenario;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<Long, Scenario> userScenarios;

    public UserService() {
        userScenarios = new ConcurrentHashMap<>();
    }

    public Scenario getUserScenario(Long userId){
        return userScenarios.get(userId);
    }

    public void setUserScenario(Long userId, Scenario scenario){
        this.userScenarios.put(userId, scenario);
    }

    public void clearUserScenario(Long userId){
        userScenarios.remove(userId);
    }

    public boolean isUserInScenario(Long userId){
        if (userScenarios.containsKey(userId)){
            return userScenarios.get(userId) != null;
        }
        return false;
    }
}
