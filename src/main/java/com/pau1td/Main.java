package com.pau1td;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.ConcurrentHashMap;

public class Main {



    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
/*
        EchoBot echoBot = new EchoBot();
        telegramBotsApi.registerBot(echoBot);
*/
        PomodoroBot pomodoroBot = new PomodoroBot();
        telegramBotsApi.registerBot(pomodoroBot);

        new Thread(()->{
            try {
                pomodoroBot.checkTimer();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).run();
    }
}
