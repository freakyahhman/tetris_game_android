package com.example.tetris;

import com.example.tetris.controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TetrisApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TetrisApp.class.getResource("game.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 600);

        // Lấy Controller để truyền key events vào
        GameController controller = fxmlLoader.getController();
        scene.setOnKeyPressed(event -> controller.handleInput(event));

        stage.setTitle("JavaFX Tetris");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}