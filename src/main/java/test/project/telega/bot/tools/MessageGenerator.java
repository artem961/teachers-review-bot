package test.project.telega.bot.tools;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class MessageGenerator {
    public static SendMessage sendMessage(Long chatId, String text){
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    public static SendMessage sendMessage(Long chatId, String text, InlineKeyboardMarkup replyKeyboardMarkup){
        return SendMessage
                .builder()
                .chatId(chatId)
                .replyMarkup(replyKeyboardMarkup)
                .text(text)
                .build();
    }

    public static EditMessageText editMessage(Long chatId, Integer messageId, String text){
        return EditMessageText
                .builder()
                .messageId(messageId)
                .text(text)
                .chatId(chatId)
                .build();
    }

    public static EditMessageText editMessage(Long chatId, Integer messageId, String text, InlineKeyboardMarkup replyKeyboardMarkup){
        return EditMessageText
                .builder()
                .messageId(messageId)
                .replyMarkup(replyKeyboardMarkup)
                .text(text)
                .chatId(chatId)
                .build();
    }

    public static DeleteMessage deleteMessage(Long chatId, Integer messageId){
        return DeleteMessage
                .builder()
                .messageId(messageId)
                .chatId(chatId)
                .build();
    }
}
