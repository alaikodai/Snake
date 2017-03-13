package snake;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


public class SnakeApp extends Application {
    // Window size and FPS constants
    public final static int APP_WIDTH = 800;
    public final static int APP_HEIGHT = 600;
    public final static int BLOCK_SIZE = 20;
    public final static double APP_FPS = 5;
    
    // Colors
    public final static Color BACKGROUND_COLOR = Color.rgb(190, 220, 145);
    
    // Fonts
    public static Font generalFont = new Font(30);
    public static Font titleFont = new Font(65);
    
    // Difficulty
    public static int difficulty = 3;
    public final static String[] DIFFICULTIES = {"", "Very Easy", "Easy", "Normal", "Hard", "Very Hard"};
    
    // Score
    public static int score = 0;


    @Override
    public void start(Stage primaryStage) {
        // Sets the title of the window
        primaryStage.setTitle("Snake");
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        
        // Load External fonts.
        try { 
            generalFont = Font.loadFont(new FileInputStream(new File("./fonts/Retro Computer.TTF")), 30);
            titleFont = Font.loadFont(new FileInputStream(new File("./fonts/Advanced Pixel LCD.TTF")), 65);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Creates the first blank pane to display
        Pane intro = new Pane();
        intro.setStyle("-fx-background-color: rgb(190, 220, 145);");
        Scene primaryScene = new Scene(intro, APP_WIDTH, APP_HEIGHT, BACKGROUND_COLOR);
        
        // Sets the scene and displays the main menu.
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        displayMainMenu(primaryScene);
    }
    
    // Displays the main menu.
    public static void displayMainMenu(Scene primaryScene) {
        //// MAIN MENU INITIALIZATION ////
        // Main title label
        Label mainTitle = new Label("SNAKE");
        mainTitle.setTextFill(Color.rgb(0, 32, 0));
        mainTitle.setStyle("-fx-padding: 0 0 55px 220px;");
        
        // Create and style the vertical box (background).
        VBox buttonBox = new VBox(-15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setStyle("-fx-background-color: rgb(190, 220, 145);");
        
        // Create and style the main menu buttons.
        // Play Game button
        Button playGameButton = new Button("Play Game");
        playGameButton.setStyle("-fx-background-color: none;-fx-text-fill: rgb(0, 32, 0);"
                              + "-fx-padding: 0 0 0 240px;");
        
        // Player Name input box
        Label playerNameLabel = new Label("Name:");
        playerNameLabel.setStyle("-fx-background-color: none;-fx-text-fill: rgb(0, 32, 0);"
                               + "-fx-padding: 0 0 0 20px;");
        TextField playerNameBox = new TextField("Player");
        playerNameBox.setStyle("-fx-background-color: none;-fx-text-fill: rgb(0, 32, 0);");
        HBox playerNameHBox = new HBox();
        playerNameHBox.setAlignment(Pos.CENTER_LEFT);
        playerNameHBox.setStyle("-fx-padding: 0 0 0 220px;");
        
        playerNameHBox.getChildren().addAll(playerNameLabel, playerNameBox);
        
        // Difficulty button
        Button difficultyButton = new Button("Difficulty: " + DIFFICULTIES[difficulty]);
        difficultyButton.setStyle("-fx-background-color: none;-fx-text-fill: rgb(0, 32, 0);"
                                + "-fx-padding: 0 0 0 240px;");
        
        // High Schores button
        Button highScoresButton = new Button("High Scores");
        highScoresButton.setStyle("-fx-background-color: none;-fx-text-fill: rgb(0, 32, 0);"
                                + "-fx-padding: 30px 0 0 240px;");

        // Set fonts
        playGameButton.setFont(generalFont);
        playerNameLabel.setFont(generalFont);
        playerNameBox.setFont(generalFont);
        difficultyButton.setFont(generalFont);
        highScoresButton.setFont(generalFont);
        mainTitle.setFont(titleFont);
        
        // Add the main menu elements to the vbox
        buttonBox.getChildren().addAll(mainTitle, playGameButton, playerNameHBox, difficultyButton, highScoresButton);

        // Applies the vbox to the scene.
        primaryScene.setRoot(buttonBox);
        
        // Adds button functionality
        playGameButton.setOnAction(e -> startGame(primaryScene));
        difficultyButton.setOnAction(e -> changeDifficulty(difficultyButton));
    }
    
    // Starts the main game.
    public static void startGame(Scene primaryScene) {
        //// GAME INITIALIZATION ////
        // Adds a group to a scene which is then added to the window.
        Group rootGroup = new Group();
        //Scene gameScene = new Scene(rootGroup, BACKGROUND_COLOR);
        primaryScene.setRoot(rootGroup);
        primaryScene.setFill(BACKGROUND_COLOR);
        
        // Places a canvas on the group and gets its graphic context.
        Canvas canvas = new Canvas(APP_WIDTH, APP_HEIGHT);
        rootGroup.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Sets the graphic context's font.
        gc.setFont(generalFont);
        
        // Create snake and food entities
        Snake snake = new Snake(0, 0);
        Food food = new Food();
        
        // Resets score
        score = 0;
        
        //// KEY HANDLING ////
        
        // Sets the snake's directional speed based on key presses.
       primaryScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:    case W: snake.setSpeed(0, -1); break;
                    case DOWN:  case S: snake.setSpeed(0, 1); break;
                    case LEFT:  case A: snake.setSpeed(-1, 0); break;
                    case RIGHT: case D: snake.setSpeed(1, 0); break;
                }
            }
        });
        
        //// GAME LOOP ////

        new AnimationTimer() {
            // Variables used to limit refresh rate.
            final long startNanoTime = System.nanoTime();
            private long timeSinceLastUpdate = 0;
            
            @Override
            public void handle(long currentNanoTime) {
                double secondsElapsed = (currentNanoTime - startNanoTime) / 1_000_000_000.0;
                
                // Limits canvas refresh rate to APP_FPS
                if (currentNanoTime - timeSinceLastUpdate >= 1 / (APP_FPS * difficulty) * 1_000_000_000) {
                    // Clear canvas
                    gc.clearRect(0, 0, APP_WIDTH, APP_HEIGHT);
                    
                    // Spawns new food if the snake eats food.
                    if (snake.eatFood(food)) {
                        food.randomLocation(snake);
                    }

                     // Update and draw snake
                    snake.update();
                    snake.render(gc);

                    // Checks if the snake is dead.
                    if (snake.isDead()) {
                        // Stop the game loop and display the end screen.
                        super.stop();
                        SnakeApp.displayEndScreen(rootGroup, primaryScene);
                    } else {
                        // Draw score if the snake is alive.
                        renderScore(gc);
                    }
                    
                    // Draw food
                    food.render(gc);
                    
                    timeSinceLastUpdate = currentNanoTime;
                }
            }
        }.start();
    }
    
    // Changes the game's difficulty level.
    public static void changeDifficulty(Button button) {
        if (difficulty < 5) {
            difficulty++;
        } else {
            difficulty = 1;
        }
        button.setText("Difficulty: " + DIFFICULTIES[difficulty]);
    }
    
    // Displays the end screen when the snake dies.
    public static void displayEndScreen(Group group, Scene primaryScene) {
        // Background rectangle
        Rectangle backgroundRect = new Rectangle(APP_WIDTH / 2 - (380 / 2), APP_HEIGHT / 2 - (160 / 2), 380, 160);
        backgroundRect.setStroke(Color.rgb(0, 32, 0));
        backgroundRect.setStrokeWidth(3);
        backgroundRect.setFill(BACKGROUND_COLOR);
        
        // Display score
        Label finalScore = new Label("Score: " + score);
        finalScore.setFont(generalFont);
        finalScore.setStyle("-fx-text-fill: rgb(0, 32, 0);");
        finalScore.setTranslateX(300);
        finalScore.setTranslateY(240);
        
        // Display retry button
        Button retryButton = new Button("Retry");
        retryButton.setFont(generalFont);
        retryButton.setStyle("-fx-background-color: none;-fx-text-fill: rgb(0, 32, 0);");
        retryButton.setTranslateX(220);
        retryButton.setTranslateY(300);
        
        // Display menu button
        Button menuButton = new Button("Menu");
        menuButton.setFont(generalFont);
        menuButton.setStyle("-fx-background-color: none;-fx-text-fill: rgb(0, 32, 0);");
        menuButton.setTranslateX(430);
        menuButton.setTranslateY(300);

        group.getChildren().addAll(backgroundRect, finalScore, retryButton, menuButton);
        
        // Adds button functionality
        retryButton.setOnAction(e -> startGame(primaryScene));
        menuButton.setOnAction(e -> displayMainMenu(primaryScene));
        
        //Button playGameButton = new Button("Play Game");
        //group.getChildren().add(playGameButton);
        //playGameButton.setStyle("-fx-background-color: none;-fx-text-fill: rgb(0, 32, 0);"
        //primaryScene.setRoot(playGameButton);
        //displayMainMenu(primaryScene);
    }
    
    public static void renderScore(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 32, 0));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("" + score, APP_WIDTH / 2, 25);
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
