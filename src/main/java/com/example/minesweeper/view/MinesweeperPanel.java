package com.example.minesweeper.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.springframework.stereotype.Component;

import com.example.minesweeper.service.MinesweeperService;
import com.example.minesweeper.service.TimerService;

@Component
public class MinesweeperPanel extends JPanel {

    private static final String MINE_EMOJI = "💣";
    private static final String FLAG_EMOJI = "🚩";

    private int rows;
    private int cols;
    private int remainingMines;
    private JButton[][] buttons;
    private JLabel mineCounterLabel;

    private final MinesweeperService minesweeperService;
    private final TimerService timerService;

    public MinesweeperPanel(MinesweeperService minesweeperService, TimerService timerService) {
        this.minesweeperService = minesweeperService;
        this.timerService = timerService;
    }

    public void initialize(int rows, int cols, int totalMines, JLabel mineCounterLabel) {
        removeAll();
        this.rows = rows;
        this.cols = cols;
        this.remainingMines = totalMines;
        this.buttons = new JButton[rows][cols];
        this.mineCounterLabel = mineCounterLabel;

        setLayout(new GridLayout(rows, cols));
        this.minesweeperService.initializeGame(rows, cols, totalMines);
        initializeGrid();
        updateMineCounter();
        revalidate();
        repaint();
    }

    private void initializeGrid() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                createButton(row, col);
                add(buttons[row][col]);
            }
        }
    }

    private void createButton(int row, int col) {
        buttons[row][col] = new JButton();
        buttons[row][col].setFocusable(false);
        buttons[row][col].setMargin(new Insets(0, 0, 0, 0));
        buttons[row][col].setFont(new Font("Arial Unicode MS", Font.BOLD, 20));
        buttons[row][col].addActionListener(e -> handleCellReveal(row, col));
        buttons[row][col].addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    handleFlagToggle(row, col);
                }
            }
        });
    }

    private void handleCellReveal(int row, int col) {
        if (this.minesweeperService.isFlagged(row, col) ||
            this.minesweeperService.isRevealed(row, col) ||
            this.minesweeperService.isGameOver()) return;
    
        this.minesweeperService.revealCell(row, col);
        buttons[row][col].setEnabled(false);
    
        if (this.minesweeperService.isMine(row, col)) {
            displayMine(row, col);
            endGame(false);
        } else {
            int mineCount = this.minesweeperService.countAdjacentMines(row, col);
            buttons[row][col].setText(mineCount > 0 ? String.valueOf(mineCount) : "");
    
            if (mineCount > 0) {
                buttons[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1 && !minesweeperService.isGameOver()) {
                            revealAdjacentCells(row, col);
                        }
                    }
                });
            }
    
            if (mineCount == 0) {
                revealAdjacentCells(row, col);
            }
    
            if (this.minesweeperService.checkWinCondition()) {
                endGame(true);
            }
        }
    }
    

    private void handleFlagToggle(int row, int col) {
        if (this.minesweeperService.isRevealed(row, col)) return;
        this.minesweeperService.toggleFlag(row, col);
        if (this.minesweeperService.isFlagged(row, col)) {
            buttons[row][col].setText(FLAG_EMOJI);
            remainingMines--;
        } else {
            buttons[row][col].setText("");
            remainingMines++;
        }

        updateMineCounter();
    }

    private void displayMine(int row, int col) {
        buttons[row][col].setText(MINE_EMOJI);
        buttons[row][col].setBackground(Color.RED);
        buttons[row][col].setEnabled(false);
    }

    private void revealAdjacentCells(int row, int col) {
        List<int[]> nonMines = new ArrayList<>();
        List<int[]> mines = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (isInBounds(newRow, newCol) && !this.minesweeperService.isRevealed(newRow, newCol)) {
                    if (this.minesweeperService.isMine(newRow, newCol)) {
                        mines.add(new int[]{newRow, newCol});
                    } else {
                        nonMines.add(new int[]{newRow, newCol});
                    }
                }
            }
        }

        for (int[] cell : nonMines) {
            handleCellReveal(cell[0], cell[1]);
        }

        if (!mines.isEmpty()) {
            for (int[] mineCell : mines) {
                handleCellReveal(mineCell[0], mineCell[1]);
            }
        }
    }
    

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    private void updateMineCounter() {
        mineCounterLabel.setText("Remaining Mines: " + remainingMines);
    }

    private void endGame(boolean win) {
        timerService.stopTimer();
        long elapsedTime = timerService.getElapsedSeconds();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                buttons[row][col].setEnabled(false); 
                for (MouseListener ml : buttons[row][col].getMouseListeners()) {
                    buttons[row][col].removeMouseListener(ml);
                }
                if (this.minesweeperService.isMine(row, col)) {
                    displayMine(row, col);
                }
            }
        }
        String message = win ? "You Win!" : "Game Over!";
        message += "\nTime: " + elapsedTime + " seconds";
        JOptionPane.showMessageDialog(this, message);
    }

}
