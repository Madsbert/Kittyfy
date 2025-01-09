package org.example.kittyfy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class HelloController {

    @FXML
    private ImageView pictures;

    @FXML
    private ComboBox<String> searchBar;

    @FXML
    private Button addSong;

    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;

    @FXML
    private ScrollPane playlistPane;

    @FXML
    private ScrollPane songsPane;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label SongTitleLabel;

    @FXML
    private Label durationLabel;

    private File directory;
    private File[] files;

    private Media media;
    private static MediaPlayer mediaPlayer;

    private ArrayList<Song> songs;

    private int currentSongNumber = 0;

    private static Timer timer;
    private TimerTask timerTask;
    private boolean isRunning = false;
    private int resetCounter;


    public void initialize() throws Exception {
        //Adding a default picture
        Image defaultImage = new Image(getClass().getResource("/Pictures/MusicCat.png").toExternalForm());
        pictures.setImage(defaultImage);
        playButton.setText("😿");
        stopButton.setText("\uD83D\uDE40");


        //initialize Songs
        songs = Reader.readAllSongs();

        for (Song song : songs) {
            System.out.println(song.getTitle());
        }

        //creates a Media Player

        System.out.println(songs.size());

        if (songs.get(currentSongNumber) != null)
        {
            media = new Media(new File("src/main/resources/music/" + songs.get(currentSongNumber).getFilePath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            //show the song title of the first song when initialized
            SongTitleLabel.setText(songs.get(currentSongNumber).getTitle());
        }


        //initialize Progressbar
        progressBar.setStyle("-fx-accent: #FFA500;");

    }


    public void reset() {
        resetCounter++;
        if (resetCounter == 2) {
            previousSong();
            displayDuration();
            resetCounter = 0;
        }
        if (isRunning) {
            progressBar.setProgress(0);
            mediaPlayer.seek(Duration.seconds(0));
        }

    }

    /**
     * Plays the currently selected song.
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public void play() throws Exception {
        if (mediaPlayer == null)
        {
            songs = Reader.readAllSongs();
            mediaPlayer = new MediaPlayer(new Media("src/main/resources/music/" + songs.get(currentSongNumber).getFilePath()));
        }

        if (isRunning)
        {
            cancelTimer();
            isRunning = false;
            mediaPlayer.pause();
            playButton.setText("😿");

        }
        else
        {
            beginTimer();
            isRunning = true;
            mediaPlayer.play();
            playButton.setText("😹");
        }
        displayDuration();
    }

    public void addSongClick() {
    }

    public void skip() throws Exception {

        if(currentSongNumber < songs.size()-1){
            currentSongNumber++;
            stop();
            if(isRunning){cancelTimer();}

            //creates a Media Player
            media = new Media(new File("src/main/resources/music/" + songs.get(currentSongNumber).getFilePath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            //show the song title of the first song when initialized
            SongTitleLabel.setText(songs.get(currentSongNumber).getTitle());
            //starts the song, and changes the icon.
            play();

            /*isRunning = true;
            mediaPlayer.play();
            playButton.setText("😹");
            displayDuration();

            */


        }
        else {
            currentSongNumber = 0;
            stop();
            if(isRunning){cancelTimer();}

            //creates a Media Player
            media = new Media(new File("src/main/resources/music/" + songs.get(currentSongNumber).getFilePath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            //show the song title of the first song when initialized
            SongTitleLabel.setText(songs.get(currentSongNumber).getTitle());
            //starts the song, and changes the icon.
            mediaPlayer.play();
            isRunning = true;
            mediaPlayer.play();
            playButton.setText("😹");
            displayDuration();

        }
    }

    public void stop(){
        mediaPlayer.stop();
        playButton.setText("😿");
        isRunning = false;
    }

    public void previousSong(){
        if(currentSongNumber > 0){
            currentSongNumber--;
            mediaPlayer.stop();
            if(isRunning){cancelTimer();}

            //creates a Media Player
            media = new Media(new File("src/main/resources/music/" + songs.get(currentSongNumber).getFilePath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            //show the song title of the first song when initialized
            SongTitleLabel.setText(songs.get(currentSongNumber).getTitle());
            //starts the song, and changes the icon.
            mediaPlayer.play();
            isRunning = true;
            mediaPlayer.play();
            playButton.setText("😹");
            displayDuration();

        }
        else {
            currentSongNumber = songs.size() - 1;
            mediaPlayer.stop();
            if(isRunning){cancelTimer();}


            //creates a Media Player
            media = new Media(new File("src/main/resources/music/" + songs.get(currentSongNumber).getFilePath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            //show the song title of the first song when initialized
            SongTitleLabel.setText(songs.get(currentSongNumber).getTitle());
            //starts the song, and changes the icon.
            mediaPlayer.play();
            isRunning = true;
            mediaPlayer.play();
            playButton.setText("😹");
            displayDuration();

        }
    }

    public void displayDuration(){
        double totalSeconds = media.getDuration().toSeconds();
        int minutes = (int) (totalSeconds / 60); // Extract minutes
        int seconds = (int) (totalSeconds % 60); // Extract remaining seconds

        // Format as "min:seconds" with two digits for seconds
        String formattedDuration = String.format("%d:%02d", minutes, seconds);
        durationLabel.setText(formattedDuration);
    }
    public void beginTimer() {
        if (mediaPlayer == null || media == null) {
            System.err.println("MediaPlayer or Media is not initialized.");
            return;
        }

        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    double currentSeconds = mediaPlayer.getCurrentTime().toSeconds();
                    double end = media.getDuration().toSeconds();

                    // Safely update progress bar
                    progressBar.setProgress(currentSeconds / end);

                    // Stop the timer if the media has ended
                    if (currentSeconds / end >= 1) {
                        cancelTimer();
                    }
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void cancelTimer() {
        isRunning = false;
        timer.cancel();
    }

    //A method that cancel the timer, and releases the mediaPlayer. (is called when stage is closed)
    public static void onClose() {
        if (timer != null) {
            timer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }


    public void createPlaylist(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CreatePlaylistController.class.getResource("Create-Playlist.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("Create Playlist");
        stage.setScene(new Scene(root));
        stage.show();
    }




}