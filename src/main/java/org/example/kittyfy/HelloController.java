package org.example.kittyfy;

import javafx.application.Platform;
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
import java.util.Arrays;
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
    private Label ArtistNameLabel;

    @FXML
    private Label durationLabel;
    @FXML
    private Label currentDurationLabel;

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

        //creates a Media Player

        System.out.println(songs.size()+" songs initialized");

        if (songs.get(currentSongNumber) != null)
        {
            media = new Media(new File("src/main/resources/music/" + songs.get(currentSongNumber).getFilePath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            //show the song title of the first song when initialized
            SongTitleLabel.setText(songs.get(currentSongNumber).getTitle());

            //Displays the artists
            String[] asa = songs.get(currentSongNumber).getFilePath().split(" - ");
            ArrayList<String> artists = new ArrayList<>();
            artists.addAll(Arrays.asList(asa[1].split(", ")));
            String artistNames = "";
            if (artists.size() > 1)
            {
                for (String artist : artists)
                {
                    artistNames += artist + ", ";
                }
                artistNames = artistNames.substring(0, artistNames.length() - 2);
            }
            else
            {
                artistNames = artists.getFirst();
            }

            ArtistNameLabel.setText(artistNames);

        }

        //initialize Progressbar
        progressBar.setStyle("-fx-accent: #FFA500;");

    }


    public void reset() throws Exception {
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

            //when the song is finished, skip to the next song.
            if (media.getDuration().toSeconds() <= mediaPlayer.getCurrentTime().toSeconds()) {
                skip();
            }
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

            createMediaPlayer();

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

            createMediaPlayer();
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

    public void previousSong() throws Exception {
        if(currentSongNumber > 0){
            currentSongNumber--;
            mediaPlayer.stop();
            if(isRunning){cancelTimer();}

            createMediaPlayer();

            playButton.setText("😹");
            displayDuration();

        }
        else {
            currentSongNumber = songs.size() - 1;
            mediaPlayer.stop();
            if(isRunning){cancelTimer();}

            createMediaPlayer();

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

                    // forces it to update in the JavaFX thread
                    Platform.runLater(() -> {
                        // Safely update progress bar
                        progressBar.setProgress(currentSeconds / end);

                        // Math to display current song duration
                        int currentMinutesMath = (int) (currentSeconds / 60);
                        int currentSecondsMath = (int) (currentSeconds % 60);
                        String formattedCurrentDuration = String.format("%d:%02d", currentMinutesMath, currentSecondsMath);
                        currentDurationLabel.setText(formattedCurrentDuration);

                        // stops the timer if the media has ended, and skips to the next song
                        if (currentSeconds / end >= 1) {
                            cancelTimer();

                        try {
                            skip();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        }
                    });
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

    public void createMediaPlayer() throws Exception {
        //creates a Media Player
        media = new Media(new File("src/main/resources/music/" + songs.get(currentSongNumber).getFilePath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        //show the song title of the first song when initialized
        SongTitleLabel.setText(songs.get(currentSongNumber).getTitle());

        //Displays the artists
        String[] asa = songs.get(currentSongNumber).getFilePath().split(" - ");
        ArrayList<String> artists = new ArrayList<>();
        artists.addAll(Arrays.asList(asa[1].split(", ")));
        String artistNames = "";
        if (artists.size() > 1)
        {
            for (String artist : artists)
            {
                artistNames += artist + ", ";
            }
            artistNames = artistNames.substring(0, artistNames.length() - 2);
        }
        else
        {
            artistNames = artists.getFirst();
        }

        ArtistNameLabel.setText(artistNames);
        play();
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