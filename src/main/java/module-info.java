module com.example.tetris {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.tetris to javafx.fxml;
    opens com.example.tetris.controller to javafx.fxml;
    exports com.example.tetris;
    exports com.example.tetris.controller;
}