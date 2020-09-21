package org.example.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Override
    public synchronized void onUpdateReceived(Update update) {
        // Example: Echo bot
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            sendMsg(chatId, messageText);
        }
    }

    public synchronized void sendMsg(long chatID, String msg) {
        var sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatID);
        sendMessage.setText(msg);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.debug(e.getMessage(), e);
        }
    }

    public String getBotUsername() {
        return "ITClusterDemoBot";
    }

    public String getBotToken() {
        return "1355633692:AAHM2TKSG1FZCH_XazAtSlnJCXZoP1BzkLs";
    }
}
