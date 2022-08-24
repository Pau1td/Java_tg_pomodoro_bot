package com.pau1td;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;

public class EchoBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "Java_tg";
    }

    @Override
    public String getBotToken() {
        return "5369462851:AAG7YpSihaHEzSgesq-UyWCnijzocMHuRrY";
    }

        /**
         * Обработка входящих сообщений
         */
        @Override
        public void onUpdateReceived(Update update){
            if (update.hasMessage() && update.getMessage().hasText()) {
              if (update.getMessage().getText().equals("/start")) {
                  sendMsg(update.getMessage().getChatId(), "Привет, я попугай бот, буду повторять за тобой :-)", update.getMessage().getChat().getUserName());
                  return;
              }
                sendMsg(update.getMessage().getChatId(), update.getMessage().getText().toString(), update.getMessage().getChat().getUserName());
            }
      }

    private void sendMsg(Long chatId, String text, String userName) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        System.out.println("Обработка сообщений " + userName + ": " + text );
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
