import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    int birdX = boardWidth / 8;
    int birdY = boardWidth / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x= birdX;
        int y= birdY;
        int width= birdWidth;
        int height= birdHeight;
        Image img;
        Bird(Image img) { this.img = img; }
    }

    int pipeX= boardWidth;
    int pipeY= 0;
    int pipeWidth= 64;
    int pipeHeight= 512;

    class Pipe {
        int x= pipeX;
        int y= pipeY;
        int width= pipeWidth;
        int height= pipeHeight;
        Image img;
        boolean passed= false;
        Pipe(Image img) { this.img = img; }
    }

    Bird bird;
    int velocityX= -4;
    int velocityY= 0;
    int gravity= 1;
    boolean gameStarted = false;
    ArrayList<Pipe> pipes;
    Random random = new Random();
    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    String highScoreFile = "highscores.txt";

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { placePipes(); }
        });
        gameLoop = new Timer(1000 / 60, this);
    }

    public void createScore(double score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile, true))) {
            writer.write(String.valueOf(score));
            writer.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Double> readScores() {
        List<Double> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(highScoreFile))) {
            String line;
            while ((line = reader.readLine()) != null) { scores.add(Double.parseDouble(line)); }
        } catch (IOException e) { e.printStackTrace(); }
        return scores;
    }

    public void updateScore(double score) {
        List<Double> scores = readScores();
        if (scores.isEmpty() || score > Collections.max(scores)) { createScore(score); }
    }

    public void deleteScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile))) {
            writer.write("");
        } catch (IOException e) { e.printStackTrace(); }
    }

    void placePipes() {
        int randomPipeY= (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace= boardHeight / 4;
        Pipe topPipe= new Pipe(topPipeImg);
        topPipe.y= randomPipeY;
        pipes.add(topPipe);
        Pipe bottomPipe= new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);
        for (Pipe pipe : pipes) { g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null); }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + (int) score, 10, 35);
        } else if (!gameStarted) {
            g.drawString("Press Start or Space", 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        if (!gameStarted) return;
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
            }
            if (collision(bird, pipe)) {
                gameOver = true;
                stopGame();
            }
        }
        if (bird.y > boardHeight) {
            gameOver= true;
            stopGame();
        }
    }

    void startGame() {
        gameStarted = true;
        gameLoop.start();
        placePipeTimer.start();
    }

    void stopGame() {
        gameLoop.stop();
        placePipeTimer.stop();
        updateScore(score);
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (!gameStarted) startGame();
            if (gameOver) resetGame();
        }
    }

    void resetGame() {
        bird.y= birdY;
        velocityY= 0;
        pipes.clear();
        gameOver= false;
        score= 0;
        startGame();
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
} 
