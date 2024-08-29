package com.example.minesweeper.service;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.stereotype.Service;

@Service
public class TimerService {

    private Timer timer;
    private long startTime;
    private long elapsedSeconds;

    public void startTimer(Runnable onTick) {
        timer = new Timer();
        startTime = System.currentTimeMillis();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                elapsedSeconds = (System.currentTimeMillis() - startTime ) /1000;
                onTick.run();
            }
        }, 0, 1000); 
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public long getStartTime() {
        return this.startTime;
    }
}
