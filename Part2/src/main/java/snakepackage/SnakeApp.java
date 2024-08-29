package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
            new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
            new Cell(GridSize.GRID_WIDTH - 2, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
            new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
            new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
            new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
            new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
            new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
            new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2)};
    private JFrame frame;
    private static Board board;
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];
    private JButton startButton, pauseButton, resumeButton;
    private JLabel infoLabel;
    private boolean gameStarted = false;
    private boolean gamePaused = false;

    public SnakeApp() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 100);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);

        // Initialize snakes here
        for (int i = 0; i < MAX_THREADS; i++) {
            snakes[i] = new Snake(i + 1, spawn[i], i + 1);
        }

        board = new Board();

        frame.add(board,BorderLayout.CENTER);

        JPanel actionsBPanel = new JPanel();
        actionsBPanel.setLayout(new FlowLayout());

        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resumeButton = new JButton("Resume");

        startButton.addActionListener(e -> startGame());
        pauseButton.addActionListener(e -> pauseGame());
        resumeButton.addActionListener(e -> resumeGame());

        actionsBPanel.add(startButton);
        actionsBPanel.add(pauseButton);
        actionsBPanel.add(resumeButton);

        frame.add(actionsBPanel,BorderLayout.SOUTH);

        infoLabel = new JLabel("Game not started");
        frame.add(infoLabel, BorderLayout.NORTH);

        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
    }

    public static void main(String[] args) {
        app = new SnakeApp();
        app.frame.setVisible(true);
    }

    private void startGame() {
        if (!gameStarted) {
            gameStarted = true;
            for (int i = 0; i < MAX_THREADS; i++) {
                snakes[i].addObserver(board);
                thread[i] = new Thread(snakes[i]);
                thread[i].start();
            }
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
        }
    }

    private void pauseGame() {
        if (gameStarted && !gamePaused) {
            gamePaused = true;
            for (Snake snake : snakes) {
                snake.pause();
            }
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(true);
            updateInfoLabel();
        }
    }

    private void resumeGame() {
        if (gameStarted && gamePaused) {
            gamePaused = false;
            for (Snake snake : snakes) {
                snake.resume();
            }
            pauseButton.setEnabled(true);
            resumeButton.setEnabled(false);
            infoLabel.setText("Game resumed");
        }
    }

    private void updateInfoLabel() {
        Snake longestSnake = findLongestSnake();
        Snake worstSnake = findWorstSnake();
        String info = "Longest snake: " + longestSnake.getIdt() +
                " (length: " + longestSnake.getBody().size() + "), " +
                "Worst snake: " + worstSnake.getIdt();
        infoLabel.setText(info);
    }

    private Snake findLongestSnake() {
        Snake longest = snakes[0];
        for (Snake snake : snakes) {
            if (!snake.isSnakeEnd() && snake.getBody().size() > longest.getBody().size()) {
                longest = snake;
            }
        }
        return longest;
    }

    private Snake findWorstSnake() {
        for (Snake snake : snakes) {
            if (snake.isSnakeEnd()) {
                return snake;
            }
        }
        return snakes[0]; // If no snake has died yet, return the first one
    }

    public static SnakeApp getApp() {
        return app;
    }
}