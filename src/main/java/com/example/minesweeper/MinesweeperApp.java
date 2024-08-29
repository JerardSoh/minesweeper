package com.example.minesweeper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.minesweeper.view.MinesweeperUI;

@SpringBootApplication
public class MinesweeperApp {

    private final MinesweeperUI minesweeperUI;

    public MinesweeperApp(MinesweeperUI minesweeperUI) {
        this.minesweeperUI = minesweeperUI;
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(MinesweeperApp.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            minesweeperUI.initialize();
        };
    }
}