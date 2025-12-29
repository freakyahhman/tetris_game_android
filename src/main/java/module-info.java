module com.example.tetris {
    requires javafx.controls;
    requires javafx.fxml;
	requires java.desktop;
	requires javafx.graphics;
	requires jdk.compiler;
	requires javafx.base;

    opens com.example.tetris to javafx.fxml;
    opens com.example.tetris.controller to javafx.fxml;
    exports com.example.tetris;
    exports com.example.tetris.controller;
}