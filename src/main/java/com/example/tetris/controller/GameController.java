package com.example.tetris.controller;

import com.example.tetris.model.GameModel;
import com.example.tetris.model.HighScoreManager;
import com.example.tetris.model.HighScoreManager.PlayerScore;
import com.example.tetris.model.Tetromino;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class GameController {
    // --- Game View FXML ---
    @FXML private Canvas gameCanvas;
    @FXML private Canvas nextCanvas;
    @FXML private Label scoreLabel;
    @FXML private Label increaseLabel;
    @FXML private StackPane overlayPane;
    @FXML private Label countdownLabel;
    @FXML private VBox pauseMenuBox;
    @FXML private VBox gameOverBox;
    @FXML private Label finalScoreLabel;
    @FXML private TextField nameInput;
    @FXML private Label newRecordLabel;
    @FXML private Button saveScoreBtn;
    
    // Nút Pause cần tham chiếu để bỏ Focus khi cần
    @FXML private Button pauseButton; 

    // --- Menu View FXML ---
    @FXML private TableView<PlayerScore> scoreTable;
    @FXML private TableColumn<PlayerScore, String> nameColumn;
    @FXML private TableColumn<PlayerScore, Number> scoreColumn;

    private GameModel model;
    private GraphicsContext gc;
    private GraphicsContext gn;
    private HighScoreManager highScoreManager;

    private Timeline dropLoop;
    private AnimationTimer renderLoop;
    private Timeline nextLoop;
    private Timeline scoreLoop;

    private boolean isPaused = false;
    private boolean isRunning = false;
    private int tickForNextLoop = 0;

    @FXML
    public void initialize() {
        highScoreManager = new HighScoreManager();

        // Xử lý cho Menu View
        if (scoreTable != null) {
            setupScoreTable();
        }

        // Xử lý cho Game View
        if (gameCanvas != null) {
            model = new GameModel();
            gc = gameCanvas.getGraphicsContext2D();
            gn = nextCanvas.getGraphicsContext2D();
            
            initLoops();
            
            // Ẩn overlay ban đầu
            overlayPane.setVisible(true);
            pauseMenuBox.setVisible(false);
            gameOverBox.setVisible(false);
            
            // QUAN TRỌNG: Cho phép canvas nhận focus
            gameCanvas.setFocusTraversable(true);
            
            // Bắt đầu countdown
            startCountdown();
            
            // Đảm bảo focus vào canvas ngay khi load xong
            Platform.runLater(() -> {
                gameCanvas.requestFocus();
            });
            
            // Lắng nghe sự kiện phím
            // Lưu ý: Gắn vào Scene hoặc trực tiếp vào Canvas đều được, nhưng Canvas an toàn hơn khi switch scene
            gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.setOnKeyPressed(this::handleInput);
                }
            });
        }
    }

    // --- Logic Menu ---
    private void setupScoreTable() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        scoreColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getScore()));
        scoreTable.getItems().setAll(highScoreManager.getScores());
    }

    // --- Logic Game State ---

    private void startCountdown() {
        isRunning = false; // Chặn input
        countdownLabel.setVisible(true);
        overlayPane.setVisible(true);
        overlayPane.setStyle("-fx-background-color: rgba(0,0,0,0.5);");

        Timeline countdown = new Timeline(
            new KeyFrame(Duration.seconds(0), e -> countdownLabel.setText("3")),
            new KeyFrame(Duration.seconds(1), e -> countdownLabel.setText("2")),
            new KeyFrame(Duration.seconds(2), e -> countdownLabel.setText("1")),
            new KeyFrame(Duration.seconds(3), e -> {
                countdownLabel.setVisible(false);
                overlayPane.setVisible(false);
                startGame();
            })
        );
        countdown.play();
    }

    private void startGame() {
        isRunning = true;
        isPaused = false;
        startLoops();
        draw();
        
        // FIX LỖI: Luôn request focus về Canvas khi game bắt đầu
        gameCanvas.requestFocus();
    }

    @FXML
    public void onPauseClick() {
        if (!isRunning || model.isGameOver()) return;
        
        // Nếu đang pause mà ấn nút pause lần nữa -> Resume
        if (isPaused) {
            onResumeClick();
            return;
        }
        
        isPaused = true;
        stopLoops();
        
        overlayPane.setVisible(true);
        overlayPane.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
        pauseMenuBox.setVisible(true);
        gameOverBox.setVisible(false);
        countdownLabel.setVisible(false);
    }

    @FXML
    public void onResumeClick() {
        pauseMenuBox.setVisible(false);
        overlayPane.setVisible(false);
        isPaused = false;
        startLoops();
        
        // FIX LỖI: Trả lại focus cho gameCanvas để nhận phím di chuyển
        gameCanvas.requestFocus();
    }

    @FXML
    public void onRestartClick() {
        model.clear();
        pauseMenuBox.setVisible(false);
        gameOverBox.setVisible(false);
        overlayPane.setVisible(false);
        
        // Reset speed
        dropLoop.setRate(1.0);
        
        startCountdown();
        // startCountdown sẽ gọi startGame -> requestFocus
    }

    private void handleGameOver() {
        stopLoops();
        isRunning = false;
        
        overlayPane.setVisible(true);
        overlayPane.setStyle("-fx-background-color: rgba(0,0,0,0.8);");
        gameOverBox.setVisible(true);
        pauseMenuBox.setVisible(false);
        
        finalScoreLabel.setText("Score: " + model.getScore());
        
        boolean isHigh = highScoreManager.isHighScore(model.getScore());
        if (isHigh) {
            newRecordLabel.setVisible(true);
            newRecordLabel.setText("NEW HIGH SCORE!");
            nameInput.setVisible(true);
            saveScoreBtn.setVisible(true);
            nameInput.clear();
            // Focus vào ô nhập tên
            nameInput.requestFocus();
        } else {
            newRecordLabel.setVisible(false);
            nameInput.setVisible(false);
            saveScoreBtn.setVisible(false);
        }
    }

    @FXML
    public void onSaveScore() {
        String name = nameInput.getText().trim();
        if (name.isEmpty()) name = "Unknown";
        
        highScoreManager.addScore(name, model.getScore());
        
        nameInput.setVisible(false);
        saveScoreBtn.setVisible(false);
        newRecordLabel.setText("Saved!");
    }

    // --- Loops & Input ---

    private void initLoops() {
        dropLoop = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            boolean gameOver = model.drop();
            if (gameOver) {
                handleGameOver();
            } else {
                updateGameSpeed();
            }
        }));
        dropLoop.setCycleCount(Animation.INDEFINITE);

        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
            }
        };

        nextLoop = new Timeline(new KeyFrame(Duration.millis(150), e -> 
            this.tickForNextLoop = (this.tickForNextLoop + 1) % 2
        ));
        nextLoop.setCycleCount(Animation.INDEFINITE);

        scoreLoop = new Timeline(new KeyFrame(Duration.millis(20), e -> model.updateScore()));
        scoreLoop.setCycleCount(Animation.INDEFINITE);
    }

    private void startLoops() {
        if (scoreLoop != null) scoreLoop.play();
        if (dropLoop != null) dropLoop.play();
        if (nextLoop != null) nextLoop.play();
        if (renderLoop != null) renderLoop.start();
    }

    private void stopLoops() {
        if (scoreLoop != null) scoreLoop.stop();
        if (dropLoop != null) dropLoop.stop();
        if (nextLoop != null) nextLoop.stop();
        if (renderLoop != null) renderLoop.stop();
    }

    private void updateGameSpeed() {
        if (model == null) return;
        int lines = model.getLine();
        double delayMillis = Math.max(1000 - 75 * (lines / 10.0), 50);
        dropLoop.setRate(1000.0 / delayMillis);
    }

    public void handleInput(KeyEvent event) {
        // Nếu đang nhập tên thì không xử lý phím game
        if (nameInput != null && nameInput.isVisible() && nameInput.isFocused()) return;

        // Xử lý ESCAPE riêng biệt (Toggle Pause)
        if (event.getCode() == KeyCode.ESCAPE) {
            if (isRunning && !model.isGameOver()) {
                if (isPaused) onResumeClick();
                else onPauseClick();
            }
            return;
        }

        if (model == null || !isRunning || isPaused || model.isGameOver()) return;
        
        KeyCode code = event.getCode();
        switch (code) {
            case LEFT, A -> model.moveTetromino(-1, 0);
            case RIGHT, D -> model.moveTetromino(1, 0);
            case UP, W -> model.rotateTetromino(1);
            case Q -> model.rotateTetromino(-1);
            case DOWN -> model.drop();
            case SPACE -> model.hardDrop(); 
            case R -> onRestartClick();
        }
        draw();
        
        // Chặn sự kiện lan truyền để tránh các nút khác nhận được phím Space/Enter
        event.consume();
    }

    // --- Navigation ---

    public void switchToMenu(ActionEvent event) throws IOException {
        stopLoops();
        loadScene(event, "/com/example/tetris/menu.fxml");
    }

    public void switchToGame(ActionEvent event) throws IOException {
        loadScene(event, "/com/example/tetris/game.fxml");
    }

    public void exitGame(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) throw new IOException("Cannot find resource: " + fxmlPath);
        Parent root = FXMLLoader.load(resource);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        if (fxmlPath.contains("game.fxml")) {
            // Đảm bảo focus khi vừa switch scene
            root.requestFocus();
        }
    }

    // --- Drawing ---
    private void draw() {
        if (gc == null) return;
        
        gc.setFill(Color.web("#2b2b33"));
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);
        gc.strokeRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        Color[][] grid = model.getGrid();
        for (int r = 0; r < GameModel.ROWS; r++) {
            for (int c = 0; c < GameModel.COLS; c++) {
                if (grid[r][c] != null) {
                    drawBlock(gc, c, r, grid[r][c], gameCanvas.getWidth());
                }
            }
        }

        drawGhostTetromino();

        Tetromino current = model.getCurrentTetromino();
        if (current != null) {
            int[][] shape = current.getShape();
            for (int r = 0; r < shape.length; r++) {
                for (int c = 0; c < shape[0].length; c++) {
                    if (shape[r][c] != 0) {
                        drawBlock(gc, current.x + c, current.y + r, current.getColor(), gameCanvas.getWidth());
                    }
                }
            }
        }

        drawNextTetromino();
        updateLabels();
    }
    
    private void updateLabels() {
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(model.getScore()));
            increaseLabel.setText(model.getIncreaseScore() > 0 ? "+" + model.getIncreaseScore() : "");
        }
    }

    private void drawNextTetromino() {
        if (model.getNextTetromino() == null) return;
        Tetromino n = model.getNextTetromino();
        int[][] shape = n.getShape();
        gn.setFill(Color.web("#2b2b33"));
        gn.fillRect(0, 0, nextCanvas.getWidth(), nextCanvas.getHeight());
        gn.setStroke(Color.WHITE);
        gn.setLineWidth(2.0);
        gn.strokeRect(0, 0, nextCanvas.getWidth(), nextCanvas.getHeight());
        double blockSize = 20;
        int heightTetromino = shape.length;
        if (n.getType() == 'I' || n.getType() != 'O') heightTetromino -= 1;
        double tetrominoWidthPx = shape[0].length * blockSize;
        double tetrominoHeightPx = heightTetromino * blockSize;
        double startX = (nextCanvas.getWidth() - tetrominoWidthPx) / 2;
        double startY = (nextCanvas.getHeight() - tetrominoHeightPx) / 2;
        gn.setFill(this.tickForNextLoop == 0 ? Color.GRAY : Color.DARKGRAY);
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) gn.fillRect(startX + (c * blockSize), startY + (r * blockSize), blockSize, blockSize);
            }
        }
    }

    private void drawGhostTetromino() {
        Tetromino current = model.getCurrentTetromino();
        if (current == null) return;
        int[][] shape = current.getShape();
        int ghostY = model.getGhostY();
        gc.setGlobalAlpha(0.1);
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) drawBlock(gc, current.x + c, ghostY + r, current.getColor(), gameCanvas.getWidth());
            }
        }
        gc.setGlobalAlpha(1.0);
    }

    private void drawBlock(GraphicsContext g, double x, double y, Color color, double canvasWidth) {
        g.setFill(color);
        double width = canvasWidth - 4;
        double blockSize = width / 12;
        g.fillRect(x * blockSize + 3, y * blockSize + 3, blockSize - 2, blockSize - 2);
    }
}