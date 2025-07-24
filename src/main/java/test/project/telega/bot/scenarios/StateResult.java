package test.project.telega.bot.scenarios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import test.project.telega.bot.scenarios.contexts.Context;

@Getter
@AllArgsConstructor
public class StateResult<T extends Context> {
    private final BotApiMethod<?> responseMessage;
    private final ScenarioState<T> nextState;

    public boolean hasNextState(){
        return nextState != null;
    }

    public boolean hasResponseMessage(){
        return responseMessage != null;
    }
}
