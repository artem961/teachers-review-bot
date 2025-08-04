package test.project.telega.bot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommand extends Command {
    public StartCommand() {
        super("start", "Starts the bot");
    }

    @Override
    public BotApiMethod execute(Update update) {
        return SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("""
                        📌 ITMO Teachers Reviews
                        
                        A bot where students can anonymously leave reviews about ITMO professors.
                        
                        🔎 /find — search for teachers
                        ⭐ /review — submit your review
                        """)
                .build();
    }
}
