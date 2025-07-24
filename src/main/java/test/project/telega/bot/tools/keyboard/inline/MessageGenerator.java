package test.project.telega.bot.tools.keyboard.inline;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class MessageGenerator {
    public static SendMessage sendMessage(Long chatId, String text){
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}
