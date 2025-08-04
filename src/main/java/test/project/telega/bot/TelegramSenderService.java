package test.project.telega.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramSenderService {
    private final TelegramClient telegramClient;

    public void execute(BotApiMethod<?> method) {
        if (method == null) {
            return;
        }

        try {
            telegramClient.executeAsync(method);
        } catch (TelegramApiException e) {
            if (e.getCause() instanceof java.io.IOException) {
                log.error("Network error while sending message", e);
            } else if (e.getMessage().equals("Error executing org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText query: [400] Bad Request: message is not modified: specified new message content and reply markup are exactly the same as a current content and reply markup of the message")) {
                log.warn("Message not modified: {}", e.getMessage());
            } else {
                log.error("Failed to execute telegram method: {}", method, e);
            }
        } catch (Exception e) {
            log.error("Failed to execute telegram method: {}", method, e);
        }
    }

    public void sendTextMessage(Long userId, String text) {
        execute(SendMessage
                .builder()
                .chatId(userId)
                .text(text)
                .build());
    }

}
