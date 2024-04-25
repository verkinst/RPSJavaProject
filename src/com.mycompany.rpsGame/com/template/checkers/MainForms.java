package com.template.checkers;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.template.checkers.Checkers.*;

public class MainForms extends Application {


    private static final int BUTTON_HEIGHT = TILE_SIZE / 2;
    private static final int BUTTON_INTERVAL = TILE_SIZE / 4;
    public static GameState gameState = GameState.MENU;

    public static boolean gameStarted = false;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        Image icon = new Image(new FileInputStream("src/com.mycompany.rpsGame/com/images/icon.png"));
        primaryStage.getIcons().add(icon);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Tactic rock-paper-scissors");
        Parent menu = createMenu(primaryStage);
        Scene scene = new Scene(menu, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Parent createMenu(Stage primaryStage) throws FileNotFoundException {
        StackPane root = new StackPane();

        Image backgroundImage = new Image(new FileInputStream("src/com.mycompany.rpsGame/com/images/background.png"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());
        root.getChildren().add(backgroundImageView);

        VBox menuContent = new VBox(10);
        menuContent.setAlignment(Pos.CENTER);

        System.out.println("Рисуем кнопки в меню");
        Button startButton = new Button("Начать игру");
        Button exitButton = new Button("Выйти из игры");

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        startButton.setStyle("-fx-font-size: 16pt;");
        exitButton.setStyle("-fx-font-size: 16pt;");


        Image logoImage = new Image(new FileInputStream("src/com.mycompany.rpsGame/com/images/mainlabel.png"));
        ImageView logoImageView = new ImageView(logoImage);

        double desiredWidth = WIDTH * TILE_SIZE;
        double desiredHeight = TILE_SIZE * HEIGHT;


        VBox logoContainer = new VBox(10);
        logoContainer.setAlignment(Pos.CENTER);

        logoImageView.setPreserveRatio(true);
        logoImageView.setFitWidth(desiredWidth);
        logoImageView.setFitHeight(desiredHeight);

        logoContainer.getChildren().add(logoImageView);

        startButton.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.8));
        exitButton.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.8));

        buttonBox.getChildren().addAll(logoContainer, startButton, exitButton);

        startButton.setOnAction(event -> {
            gameState = GameState.PLAY;
            Scene gameScene;
            try {
                gameScene = new Scene(createGameWindow(primaryStage), WIDTH * TILE_SIZE + RIGHT_MENU_WIDTH, HEIGHT * TILE_SIZE);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            primaryStage.setScene(gameScene);
        });

        exitButton.setOnAction(event -> primaryStage.close());

        menuContent.getChildren().addAll(buttonBox);
        root.getChildren().add(menuContent);

        buttonBox.setAlignment(Pos.CENTER);

        return root;
    }


    private static Pane createGameWindow(Stage primaryStage) throws FileNotFoundException {
        Pane gamePane = new Pane();
        gamePane.setPrefSize(WIDTH * TILE_SIZE + RIGHT_MENU_WIDTH, HEIGHT * TILE_SIZE);

        Pane gameField = (Pane) Checkers.createContent(primaryStage);
        gameField.setLayoutX(0);
        gameField.setLayoutY(0);

        VBox menuButtons = new VBox();
        menuButtons.setSpacing(10);
        menuButtons.setAlignment(Pos.CENTER);

        Button menuButton = getMenuButton(primaryStage);
        Button restartButton = getRestartButton();

        menuButtons.getChildren().addAll(menuButton, restartButton);

        menuButtons.setLayoutX(WIDTH * TILE_SIZE + 10);
        menuButtons.setLayoutY((gamePane.getPrefHeight() - menuButtons.getHeight()) / 2);



        gamePane.getChildren().addAll(gameField, menuButtons);
        return gamePane;
    }

    private static Button getRestartButton() {
        Button restartButton = new Button("Перезапуск партии");
        restartButton.setLayoutX(WIDTH * TILE_SIZE + BUTTON_INTERVAL);
        restartButton.setPrefHeight(BUTTON_HEIGHT);
        restartButton.setPrefWidth(RIGHT_MENU_WIDTH - BUTTON_INTERVAL * 2);
        restartButton.setLayoutY(restartButton.getLayoutY() + BUTTON_HEIGHT + BUTTON_INTERVAL);
        restartButton.setOnAction(e -> {
            try {
                Checkers.restartGame();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        return restartButton;
    }

    private static Button getMenuButton(Stage primaryStage) {
        Button menuButton = new Button("Вернуться в меню");
        menuButton.setPrefWidth(RIGHT_MENU_WIDTH - BUTTON_INTERVAL * 2);
        menuButton.setLayoutX(WIDTH * TILE_SIZE + BUTTON_INTERVAL);
        menuButton.setPrefHeight(BUTTON_HEIGHT);
        menuButton.setLayoutY(10);
        menuButton.setOnAction(e -> {
            Scene scene;
            try {
                scene = new Scene(createMenu(primaryStage), WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
                primaryStage.setScene(scene);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        });
        return menuButton;
    }

    static Parent createGameOverWindow(Stage primaryStage, boolean redLost) throws FileNotFoundException {
        StackPane gameOverPane = new StackPane();

        Image backgroundImage = new Image(new FileInputStream("src/com.mycompany.rpsGame/com/images/background.png"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());
        gameOverPane.getChildren().add(backgroundImageView);
        gameOverPane.setPrefSize(WIDTH * TILE_SIZE + RIGHT_MENU_WIDTH, HEIGHT * TILE_SIZE);

        String message = redLost ? "Победа синих!" : "Победа красных!";

        Text gameOverText = new Text(message);
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gameOverText.setFill(redLost ? Color.BLUE : Color.RED);
        gameOverText.setBoundsType(TextBoundsType.VISUAL);

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        Button menuButton = new Button("Возврат в меню");
        Button restartButton = getRestartButton(primaryStage);

        menuButton.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.8));
        restartButton.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.8));

        buttonBox.getChildren().addAll(gameOverText, menuButton, restartButton);

        menuButton.setOnAction(e -> {
            Scene scene;
            try {
                scene = new Scene(createMenu(primaryStage), WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
                primaryStage.setScene(scene);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        });

        gameOverPane.getChildren().add(buttonBox);
        return gameOverPane;
    }

    private static Button getRestartButton(Stage primaryStage) {
        Button restartButton = new Button("Перезапуск партии");
        restartButton.setOnAction(e -> {
            try {
                Checkers.restartGame();
                gameState = GameState.PLAY;
                Scene gameScene;
                gameScene = new Scene(createGameWindow(primaryStage), WIDTH * TILE_SIZE + RIGHT_MENU_WIDTH, HEIGHT * TILE_SIZE);
                primaryStage.setScene(gameScene);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        return restartButton;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
