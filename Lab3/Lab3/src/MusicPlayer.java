import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javazoom.jl.player.advanced.AdvancedPlayer;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer extends Application {
    private List<String> playlist = new ArrayList<>();
    private int currentTrackIndex = 0;
    private AdvancedPlayer player;
    private boolean isPlaying = false;
    private double volume = 0.5;
    private boolean isPaused = false;
    private Duration pausePosition;
    private MediaPlayer mediaPlayer;

    public static void main(String[] args) {
        MusicPlayer player = new MusicPlayer();
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Media Player Classic");

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        // Список треків
        ListView<String> trackList = new ListView<>();
        trackList.setPrefSize(400, 300);

        trackList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Перевіряємо, чи це подвійне натискання
                int selectedIndex = trackList.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < playlist.size()) {
                    currentTrackIndex = selectedIndex; // Встановлюємо поточний індекс для відтворення вибраного треку
                    play(); // Викликаємо метод play() для відтворення вибраного треку
                }
            }
        });

        // Кнопки управління
        HBox controlButtons = new HBox(10);
        controlButtons.setAlignment(Pos.CENTER);
        Button openButton = new Button("Open");
        Button saveButton = new Button("Save");
        Button playButton = new Button("Play");
        Button pauseButton = new Button("Pause");
        Button nextTrackButton = new Button("Next");
        Button previousTrackButton = new Button("Previous");
        Slider volumeSlider = new Slider(0, 1, volume);

        controlButtons.getChildren().addAll(openButton, saveButton, playButton, pauseButton, previousTrackButton, nextTrackButton, volumeSlider);

        // Обробники подій для кнопок
        openButton.setOnAction(e -> openFile(primaryStage, trackList, player));
        saveButton.setOnAction(e -> savePlaylist(primaryStage));
        playButton.setOnAction(e -> play());
        pauseButton.setOnAction(e -> togglePause());
        nextTrackButton.setOnAction(e -> playNextTrack());
        previousTrackButton.setOnAction(e -> playPreviousTrack());
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> setVolume(newValue.doubleValue()));

        root.getChildren().addAll(trackList, controlButtons);

        Scene scene = new Scene(root, 480, 360);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void openFile(Stage stage, ListView<String> trackList, AdvancedPlayer player) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 files", "*.mp3"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                String filePath = file.getAbsolutePath();
                playlist.add(filePath);
                trackList.getItems().add(file.getName());
            }
        }
    }
    private void savePlaylist(Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a directory to save the playlist");

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            for (String filePath : playlist) {
                File sourceFile = new File(filePath);
                File targetFile = new File(selectedDirectory, sourceFile.getName());

                try {
                    Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void play() {
        if (playlist.isEmpty()) {
            System.out.println("Playlist is empty. Add some music first.");
            return;
        }

        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            Media media = new Media(new File(playlist.get(currentTrackIndex)).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            // Тут ви можете додати властивість відслідковування позиції
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                Duration currentPosition = mediaPlayer.getCurrentTime();
                System.out.println("Current position: " + currentPosition.toMillis() + " ms");
            });

            mediaPlayer.setOnEndOfMedia(() -> playNextTrack());

            mediaPlayer.play();

            isPlaying = true;
        } catch (Exception e) {
            System.err.println("Error while playing music: " + e.getMessage());
        }
    }
    private void togglePause() {
        if (isPlaying) {
            pause();
        } else {
            resume();
        }
    }
    private void pause() {
        if (isPlaying()) {
            pausePosition = mediaPlayer.getCurrentTime();
            mediaPlayer.pause();
            isPlaying = false;
        }
    }
    private void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(pausePosition);
            mediaPlayer.play();
            isPlaying = true;
        }
    }
    private boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
    private void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }
    private void playNextTrack() {
        if (!playlist.isEmpty()) {
            if (currentTrackIndex < playlist.size() - 1) {
                currentTrackIndex++;
            } else {
                // Якщо це останній файл, перейти до першого
                currentTrackIndex = 0;
            }
            play();
        } else {
            System.out.println("Playlist is empty. Add some music first.");
        }
    }
    private void playPreviousTrack() {
        if (currentTrackIndex > 0) {
            currentTrackIndex--;
            play();
        } else {
            System.out.println("Beginning of playlist.");
        }
    }
}
