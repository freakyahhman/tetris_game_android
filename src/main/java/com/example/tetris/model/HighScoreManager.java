package com.example.tetris.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager {
    private static final String FILE_NAME = "highscores.dat";
    private List<PlayerScore> scores;

    public HighScoreManager() {
        scores = loadScores();
    }

    public List<PlayerScore> getScores() {
        return scores;
    }

    public void addScore(String name, int score) {
        scores.add(new PlayerScore(name, score));
        scores.sort(Comparator.comparingInt(PlayerScore::getScore).reversed());
        if (scores.size() > 5) { // Giữ top 5
            scores.remove(scores.size() - 1);
        }
        saveScores();
    }

    public boolean isHighScore(int score) {
        if (score == 0) return false;
        if (scores.size() < 5) return true;
        return score > scores.get(scores.size() - 1).getScore();
    }

    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<PlayerScore> loadScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<PlayerScore>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    // Class con để lưu dữ liệu
    public static class PlayerScore implements Serializable {
        private String name;
        private int score;

        public PlayerScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() { return name; }
        public int getScore() { return score; }
    }
}