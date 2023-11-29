package com.alphalaneous.Interactive.Timers;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Utilities.Utilities;

import java.time.LocalDateTime;

public class TimerHandler {

    @OnLoad
    public static void startTimerHandler(){
        new Thread(() -> {
            while(true){
                Utilities.sleep(60000);
                for(TimerData data : TimerData.getRegisteredTimers()){
                    if(data.isEnabled()) {
                        int minute = LocalDateTime.now().getMinute();
                        if (minute == 0) minute = 60;
                        data.runTimer(minute);
                    }
                }
            }
        }).start();
    }
}
