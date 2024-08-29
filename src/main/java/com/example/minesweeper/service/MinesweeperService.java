package com.example.minesweeper.service;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class MinesweeperService {

    private int rows;
    private int cols;
    private int totalMines;
    private boolean[][] mines;
    private boolean[][] revealed;
    private boolean[][] flagged;
    private boolean gameOver;

    public void initializeGame(int rows, int cols, int totalMines) {
        this.rows = rows;
        this.cols = cols;
        this.totalMines = totalMines;
        this.mines = new boolean[rows][cols];
        this.revealed = new boolean[rows][cols];
        this.flagged = new boolean[rows][cols];
        this.gameOver = false;

        placeMines();
    }

    private void placeMines() {
        Random random = new Random();
        int placedMines = 0;
        while (placedMines < totalMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);
            if (!mines[row][col]) {
                mines[row][col] = true;
                placedMines++;
            }
        }
    }

    public boolean isMine(int row, int col) {
        return mines[row][col];
    }

    public boolean isRevealed(int row, int col) {
        return revealed[row][col];
    }

    public boolean isFlagged(int row, int col) {
        return flagged[row][col];
    }

    public void revealCell(int row, int col) {
        if (gameOver || revealed[row][col] || flagged[row][col]) return;
        revealed[row][col] = true;
        if (mines[row][col]) {
            gameOver = true;
        }
    }

    public void toggleFlag(int row, int col) {
        if (!gameOver && !revealed[row][col]) {
            flagged[row][col] = !flagged[row][col];
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && mines[newRow][newCol]) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean checkWinCondition() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!mines[row][col] && !revealed[row][col]) return false;
            }
        }
        return true;
    }
}
