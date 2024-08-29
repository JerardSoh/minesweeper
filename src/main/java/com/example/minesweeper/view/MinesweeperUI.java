package com.example.minesweeper.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.stereotype.Component;

import com.example.minesweeper.service.TimerService;

@Component
public class MinesweeperUI {

    private final MinesweeperPanel minesweeperPanel;
    private final TimerService timerService;
    private JFrame frame;
    private JLabel mineCounterLabel;
    private JTextField minesField;
    private JTextField sizeField;
    private JLabel timerLabel;
    private int totalMines = 10;
    private int size = 10;

    public MinesweeperUI(MinesweeperPanel minesweeperPanel, TimerService timerService) {
        this.minesweeperPanel = minesweeperPanel;
        this.timerService = timerService;
    }

    public void initialize() {
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        JPanel topPanel = createTopPanel();
        frame.add(topPanel, BorderLayout.NORTH);

        minesweeperPanel.initialize(size, size, totalMines, mineCounterLabel);
        frame.add(minesweeperPanel, BorderLayout.CENTER);
        startTimer();

        frame.setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mineCounterLabel = new JLabel("Remaining Mines: " + totalMines);
        topPanel.add(mineCounterLabel);
        
        minesField = new JTextField(String.valueOf(totalMines), 5);
        sizeField = new JTextField(String.valueOf(size), 5);
        
        topPanel.add(new JLabel("Mines: "));
        topPanel.add(minesField);
        
        topPanel.add(new JLabel("Size: "));
        topPanel.add(sizeField);
        
        timerLabel = new JLabel("Time: 0");
        topPanel.add(timerLabel);
        
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());
        topPanel.add(resetButton);

        return topPanel;
    }

    private void startTimer() {
        timerService.startTimer(() -> {
            SwingUtilities.invokeLater(() -> {
                long elapsedSeconds = (System.currentTimeMillis() - timerService.getStartTime()) / 1000;
                timerLabel.setText("Time: " + elapsedSeconds);
            });
        });
    }

    public void stopTimer() {
        timerService.stopTimer();
    }

    private void resetGame() {
        stopTimer(); 
        try {
            totalMines = Integer.parseInt(minesField.getText());
            size = Integer.parseInt(sizeField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number of mines.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame.remove(minesweeperPanel);
        minesweeperPanel.initialize(size, size, totalMines, mineCounterLabel);
        frame.add(minesweeperPanel, BorderLayout.CENTER);

        mineCounterLabel.setText("Remaining Mines: " + totalMines);
        frame.revalidate();
        frame.repaint();
        startTimer();
    }
}
