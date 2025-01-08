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
    private MediaPlayer mediaPlayer;

    private ArrayList<File> songs;

    private int currentSongNumber;

    private Timer timer;
    private TimerTask timerTask;
    private boolean isRunning = false;
    private int resetCounter;


    public void initialize() {
        //Adding a default picture
        Image defaultImage = new Image(getClass().getResource("/Pictures/MusicCat.png").toExternalForm());
        pictures.setImage(defaultImage);
        playButton.setText("😿");
        stopButton.setText("\uD83D\uDE40");


        //initialize Songs
        songs = new ArrayList<File>();
        directory = new File("src/main/resources/Genre/Dansk-Top");
        //this gets all the files from directory and put them into our files array
        files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                songs.add(file);
                System.out.println(file);
            }
        }

        //creates a Media Player
        media = new Media(songs.get(currentSongNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        //show the song title of the first song when initialized
        SongTitleLabel.setText(songs.get(currentSongNumber).getName());
    }


    public void reset() {
        resetCounter++;
        if (resetCounter == 2) {
            previousSong();
            displayDuration();
        }
        if (isRunning) {
            mediaPlayer.seek(Duration.seconds(0));
        }

    }

    /**
     * Plays the currently selected song.
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public void play() throws UnsupportedAudioFileException, IOException {

        if (isRunning)
        {
            isRunning = false;
            mediaPlayer.pause();
            playButton.setText("😿");

        }
        else
        {
            isRunning = true;
            mediaPlayer.play();
            playButton.setText("😹");
        }
        displayDuration();
    }

    public void addSongClick() {
    }

    public void skip() throws UnsupportedAudioFileException, IOException {

        if(currentSongNumber < files.length-1){
            currentSongNumber++;
            stop();
            //creates a Media Player
            media = new Media(songs.get(currentSongNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            //show the song title of the first song when initialized
            SongTitleLabel.setText(songs.get(currentSongNumber).getName());
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

            //creates a Media Player
            media = new Media(songs.get(currentSongNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            //show the song title of the first song when initialized
            SongTitleLabel.setText(songs.get(currentSongNumber).getName());
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

            //creates a Media Player
            media = new Media(songs.get(currentSongNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            //show the song title of the first song when initialized
            SongTitleLabel.setText(songs.get(currentSongNumber).getName());
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

            //creates a Media Player
            media = new Media(songs.get(currentSongNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            //show the song title of the first song when initialized
            SongTitleLabel.setText(songs.get(currentSongNumber).getName());
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


    public void createPlaylist(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CreatePlaylistController.class.getResource("Create-Playlist.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("Create Playlist");
        stage.setScene(new Scene(root));
        stage.show();
    }




}