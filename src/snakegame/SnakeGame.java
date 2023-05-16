/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakegame;

import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Amanuel
 */
public class SnakeGame extends Application {
    
    private static final int PREFERRED_WIDTH = 600;
    private static final int PREFERRED_HIGHT = 500;
    private static final int RADIUS = 5;
    
    private Pane root;
    private Text score;
    private Circle food;
    private Random random;
    private Snake snake;
    
    private void newFood() {
        food = new Circle(random.nextInt(PREFERRED_WIDTH), random.nextInt(PREFERRED_HIGHT), RADIUS);
        food.setFill(Color.RED);
        root.getChildren().add(food);
    }
    
    private void newSnake() {
        snake = new Snake(PREFERRED_WIDTH / 2, PREFERRED_HIGHT / 2, RADIUS + 2);
        root.getChildren().add(snake);
        for (int i = 0; i < 25; i++) {
            newFood();
            snake.eat(food);
        }
    }
    
    private boolean hit() {
        return food.intersects(snake.getBoundsInLocal());
    }
    
    private boolean gameOver() {
        return snake.eatSelf();
    }
    
    private void move() {
        Platform.runLater(() -> {
            snake.step();
            adjustLocation();
            if (hit()) {
                snake.eat(food);
                score.setText("" + snake.getLength());
                newFood();
            } else if (gameOver()) {
                root.getChildren().clear();
                root.getChildren().add(score);
                score.setText("Game Over " + snake.getLength());
                newSnake();
                newFood();
            }
        });
    }
    
    private void adjustLocation() {
        if (snake.getCenterX() < 0) {
            snake.setCenterX(PREFERRED_WIDTH);
        } else if (snake.getCenterX() > PREFERRED_WIDTH) {
            snake.setCenterX(0);
        }
        if (snake.getCenterY() < 0) {
            snake.setCenterY(PREFERRED_HIGHT);
        } else if (snake.getCenterY() > PREFERRED_HIGHT) {
            snake.setCenterY(0);
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        root = new Pane();
        root.setPrefSize(PREFERRED_WIDTH, PREFERRED_HIGHT);
        random = new Random();
        score = new Text(0, 32, "0");
        score.setFont(Font.font(32));
        root.getChildren().add(score);
        
        newSnake();
        newFood();
        
        Runnable r = () -> {
            try {
                for (;;) {
                    move();
                    Thread.sleep(100 / (1 + (snake.getLength() / 10)));
                }
            } catch (InterruptedException ie) {
            }
        };
        
        Scene scene = new Scene(root);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.UP) {
                snake.setCurrentDirection(Direction.UP);
            } else if (code == KeyCode.DOWN) {
                snake.setCurrentDirection(Direction.DOWN);
            } else if (code == KeyCode.LEFT) {
                snake.setCurrentDirection(Direction.LEFT);
            } else if (code == KeyCode.RIGHT) {
                snake.setCurrentDirection(Direction.RIGHT);
            }
            
        });
        primaryStage.setTitle("Snake Game!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        Thread th = new Thread(r);
        th.setDaemon(true);
        th.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
