package test.project.telega.bot.scenarios.contexts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Context {
    private final Long chatId;
}
