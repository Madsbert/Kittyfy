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

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
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
    private VBox songsVbox;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label SongTitleLabel;
    @FXML
    private Label ArtistNameLabel;

    @FXML
    private Label totalDurationLabel;
    @FXML
    private Label currentDurationLabel;

    private File directory;
    private File[] files;

    private Media media;
    private static MediaPlayer mediaPlayer;

    private ArrayList<Song> allSongs;

    private int currentSongNumber = 0;

    private static Timer timer;
    private TimerTask timerTask;
    private boolean isRunning = false;
    private int resetCounter;
    private Playlist currentPlaylist;


    public void initialize() throws Exception {
        //Adding a default picture
        Image defaultImage = new Image(getClass().getResource("/Pictures/MusicCat.png").toExternalForm());
        pictures.setImage(defaultImage);
        playButton.setText("😿");
        stopButton.setText("\uD83D\uDE40");
        currentPlaylist = new Playlist("test", new ArrayList<>());

        //initialize Songs
        allSongs = Reader.readAllSongs();


        //initializing searchbar options

        for (Song song : allSongs) {
            String artists = String.join(", ", song.getArtist());
            searchBar.getItems().add(song.getTitle() + " by " + artists);
        }

        System.out.println(allSongs.size()+" songs initialized");


        //initialize Progressbar
        progressBar.setStyle("-fx-accent: #FFA500;");

        //initialize currentPlaylist
        for (int i = 0; i < allSongs.size(); i += 2) {
            currentPlaylist.addSong(allSongs.get(i));
        }
        updateSongList();

        if (currentPlaylist.getSongs().get(currentSongNumber) != null)
        {
            createMediaPlayer();

        }
        SongTitleLabel.setText("Welcome To Kittyfy");
        ArtistNameLabel.setText("playing playlist: "+ currentPlaylist.getName());


    }

    /**
     * Updates songlist in scrollbar and set the buttons to a certain size.
     * When button pushed the song is played and current song playing stops when a new song button is pushed.
     */
    public void updateSongList() {
        for (Song song : currentPlaylist.getSongs()) {
            Button newButton = new Button(song.getTitle() + " by " + song.getArtist());
            newButton.setPrefWidth(650);
            newButton.setPrefHeight(30);
            newButton.setOnAction((ActionEvent event) -> {
                try {
                    stopMusic();
                    playSong(song);


                } catch (Exception e) {
                    System.out.println("Failed to play song");
                    e.printStackTrace();
                }
            });
            songsVbox.getChildren().add(newButton);
        }
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
     * Connecting the musicfile to the label and makes the functions work.//uddyb mere og bedre.
     * @param song
     * @throws Exception
     */
    public void playSong(Song song) throws Exception {
        allSongs = Reader.readAllSongs();
                try {
                    mediaPlayer = new MediaPlayer(new Media(new File("src/main/resources/music/" + song.getFilePath()).toURI().toString()));

                    // the MediaPlayer doesn't update the metadata every time it's created, so we need a listener.
                    mediaPlayer.setOnReady(() -> {
                        displayDuration();
                    });

                    //displays artist based on song object instead of currentSongNumber.
                    Platform.runLater(() -> {
                    displayArtistBasedOnSong(song);});

                    //displays title based on song object.
                    Platform.runLater(() -> {
                    SongTitleLabel.setText(song.getTitle());});
                    
                    //starts the song, and changes the icon.
                    mediaPlayer.pause();
                    isRunning = false;
                    playMusic();

                    //displays total duration based on media. (doesn't work)
                    if(timer != null) {
                        timer.cancel();
                        beginTimer();
                    }

                }catch (Exception e) {
                    System.out.println("Failed to play song");
                    e.printStackTrace();
                }
            }


    /**
     * Plays the currently selected song.
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public void playMusic() throws Exception {
        if (isRunning)
        {
            if (timer == null){beginTimer();}
            else {cancelTimer();}

            cancelTimer();
            isRunning = false;
            mediaPlayer.pause();
            playButton.setText("😿");
            displayArtistOnLabel();
            displaySongTitleOnLabel();

        }
        else
        {
            beginTimer();
            isRunning = true;
            mediaPlayer.play();
            playButton.setText("😹");
            displayArtistOnLabel();
            displaySongTitleOnLabel();

            //when the song is finished, skip to the next song.
            if (media.getDuration().toSeconds() <= mediaPlayer.getCurrentTime().toSeconds()) {
                skip();
            }
        }

    }

    public void addSongClick() {
    }

    public void playSongOnClick() throws Exception {

        String selectedTitle = searchBar.getValue();
        if (selectedTitle == null) {
            System.out.println("No song selected!");
        }
        /*
        String search = "To";

        for (Song song : songs) {
            if (song.getTitle().toLowerCase().contains(selectedTitle.toLowerCase())) {

            }

            for (String artist : song.getArtist())
            {
                if (artist.toLowerCase().contains(selectedTitle.toLowerCase())) {
                    searchBar.getItems().add(artist);
                }
            }
            if (song.getTitle().toLowerCase().contains(search.toLowerCase())) {}


        }
*/


        for (Song song : currentPlaylist.getSongs()) {

            String artists = String.join(", ", song.getArtist());

            if (selectedTitle.equals(song.getTitle() + " by " + artists)) {
                currentSongNumber = allSongs.indexOf(song);
                mediaPlayer.stop();

                if (isRunning) {
                    cancelTimer();
                }

                createMediaPlayer();
                playMusic();
                break;
            }
        }
    }

    public void skip() throws Exception {

        if(currentSongNumber < currentPlaylist.getSongs().size()-1){
            currentSongNumber++;
            stopMusic();
            if(isRunning){cancelTimer();}

            createMediaPlayer();

            isRunning = true;
            mediaPlayer.play();
            playButton.setText("😹");
            displayDuration();




        }
        else {
            currentSongNumber = 0;
            stopMusic();
            if(isRunning){cancelTimer();}

            createMediaPlayer();
            isRunning = true;
            mediaPlayer.play();
            playButton.setText("😹");
            displayDuration();

        }
    }

    public void stopMusic(){
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
            currentSongNumber = allSongs.size() - 1;
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

        totalDurationLabel.setText(formattedDuration);
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
                        // Updates progress bar
                        progressBar.setProgress(currentSeconds / end);

                        // Math to display current song duration
                        int currentMinutesMath = (int) (currentSeconds / 60);
                        int currentSecondsMath = (int) (currentSeconds % 60);
                        String formattedCurrentDuration = String.format("%d:%02d", currentMinutesMath, currentSecondsMath);
                        currentDurationLabel.setText(formattedCurrentDuration);
                        displayDuration();

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
        media = new Media(new File("src/main/resources/music/" + currentPlaylist.getSongs().get(currentSongNumber).getFilePath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        displaySongTitleOnLabel();
        displayArtistOnLabel();
    }
    public void displaySongTitleOnLabel() {
        SongTitleLabel.setText(currentPlaylist.getSongs().get(currentSongNumber).getTitle());

        if (currentSongNumber + 1 >= currentPlaylist.getSongs().size()) {
            currentSongNumber = 0;
            System.out.println("Coming up: " + currentPlaylist.getSongs().get(currentSongNumber).getTitle());
        } else {
            System.out.println("Coming up: " + currentPlaylist.getSongs().get(currentSongNumber + 1).getTitle());
        }
    }
    public void displayArtistOnLabel(){
        //Displays the artists
        String[] artistArray = currentPlaylist.getSongs().get(currentSongNumber).getFilePath().split(" - ");
        ArrayList<String> artists = new ArrayList<>();
        artists.addAll(Arrays.asList(artistArray[1].split(", ")));
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
    //displays artist based on song object instead of currentSongNumber.
    public void displayArtistBasedOnSong(Song song){
        String[] artistArray = song.getFilePath().split(" - ");
        ArrayList<String> artists = new ArrayList<>();
        artists.addAll(Arrays.asList(artistArray[1].split(", ")));
        String artistNames = "";
        if (artists.size() > 1) {
            for (String artist : artists) {
                artistNames += artist + ", ";
            }
            // Remove the trailing ", "
            artistNames = artistNames.substring(0, artistNames.length() - 2);
        } else {
            artistNames = artists.get(0);
        }

        ArtistNameLabel.setText(artistNames);
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