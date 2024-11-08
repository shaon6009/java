import javax.swing.*;
import java.awt.BorderLayout;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 700;
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        
        JButton startButton = new JButton("Start Game by pressing SPACE BUTTON");
        startButton.addActionListener(e -> {
            flappyBird.startGame();
            flappyBird.requestFocus();
        });
        
        frame.add(startButton, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }
}
