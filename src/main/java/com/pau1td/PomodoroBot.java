package com.pau1td;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

public class PomodoroBot extends TelegramLongPollingBot {

    public PomodoroBot() {
        super();

    }

    // ключ/значение
    private static final ConcurrentHashMap<Timer, Long> userTimers = new ConcurrentHashMap();

    enum TimerType {
        WORK,
        BREAK
    }

    record Timer(Instant userTimer, TimerType timerType) {
    }

    ;

    @Override
    public String getBotUsername() {
        return "Pomodoro";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();
            if (update.getMessage().getText().equals("/start")) {
                sendMsg("""
                        Pomodoro - сделай свое время более эффективным.\n
                        Задай мне время работы, отдыха и количество повторов через пробел.\n 
                        Например, '10 (время работы) 1 (время отдыха) 2 (количество повторов)'.\n
                        PS Я работаю пока в минутах.
                        """, chatId.toString());
            } else {

                var args = update.getMessage().getText().split(" ");

                if (args.length == 3) {
                    sendMsg("Вы задали: \n" +
                            "работать - " + args[0] + " мин.,\n" +
                            "отдыхать - " + args[1] + ", мин.,\n" +
                            "повторить: " + args[2] + " раз(а)", chatId.toString());
                    //10:30 (текущее время, не вводится) 30-работать 10-отдыхать 3-повторить раз
                    // worktime 11:00
                    // breaktime 11:10

                    var workTime = Instant.now().plus(Long.parseLong(args[0]), ChronoUnit.MINUTES);
                    var breakTime = workTime.plus(Long.parseLong(args[1]), ChronoUnit.MINUTES);

                    for (int i = 1; i <= Integer.parseInt(args[2]); i++) {

                        if (i > 1) {
                            workTime = breakTime.plus(Long.parseLong(args[0]), ChronoUnit.MINUTES);
                            breakTime = workTime.plus(Long.parseLong(args[1]), ChronoUnit.MINUTES);
                        }

                        userTimers.put(new Timer(workTime, TimerType.WORK), chatId);
                        if (i == 1) {
                            sendMsg("Начни работать!", chatId.toString());
                        }

                        // System.out.printf("Поставила таймер работать до :", workTime.toEpochMilli(), "\n");
                        //        System.out.printf("Проверка userId = %d, server_time = %s, user_timer = %s\n",
                        //        chatId.toString(), Instant.now().toString(), userTimers.containsValue());

                        userTimers.put(new Timer(breakTime, TimerType.BREAK), chatId);
                        // System.out.printf("Поставила таймер, отдыхать до ", breakTime.toString(), "\n");
                        //    System.out.printf("Проверка userId = %d, server_time = %s, user_timer = %s\n",
                        //            chatId.toString(), Instant.now().toString(), userTimers.values());
                    }

                } else {
                    sendMsg("Введи правильно через пробел:\n" +
                            "время работы, время отдыха, количество повторов!", chatId.toString());
                }
            }
        }
    }

    public void checkTimer() throws InterruptedException {
        while (true) {
            System.out.println("Количество таймеров пользователей " + userTimers.size());

            userTimers.forEach((timer, userId) -> {
                //           System.out.printf("Проверка userId = %d, server_time = %s, user_timer = %s\n",
                //                   userId, Instant.now().toString(), timer.userTimer.toString());

                switch (timer.timerType) {
                    case WORK -> {
                        System.out.printf("TimerType = WORK, server_time = %s, user_timer = %s\n",
                                Instant.now().toString(), timer.userTimer.toString());
                    }
                    case BREAK -> {
                        System.out.printf("TimerType = BREAK, server_time = %s, user_timer = %s\n",
                                Instant.now().toString(), timer.userTimer.toString());
                    }
                }

                if (Instant.now().isAfter(timer.userTimer)) {

                    switch (timer.timerType) {
                        case WORK -> sendMsg("Отдохни", userId.toString());
                        case BREAK -> {
                            if (userTimers.size() != 1) {
                                sendMsg("Продолжи работать!", userId.toString());
                            } else {
                                sendMsg("Таймер завершил свою работу", userId.toString());
                            }
                        }
                    }
                    userTimers.remove(timer);
                }
            });
            Thread.sleep(10000);
        }
    }

    private void sendMsg(String text, String chatId) {
        SendMessage msg = new SendMessage();
        // пользователь чата
        msg.setChatId(chatId);
        msg.setText(text);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            System.out.println("Уппс");
        }
    }

}
