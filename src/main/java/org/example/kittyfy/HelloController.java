package org.example.kittyfy;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.util.*;
import java.util.Timer;

import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.CENTER_LEFT;


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
    private VBox vBoxPlaylists;
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
    @FXML
    private Label PlaylistTitleAndDuration;

    private File directory;
    private File[] files;

    private Media media;
    public static MediaPlayer mediaPlayer;

    public static ArrayList<Song> allSongs;
    private ArrayList<Playlist> allPlaylists;

    private int currentSongNumber = 0;

    @FXML
    private Button shuffleButton;

    private static Timer timer;
    private TimerTask timerTask;
    private boolean isRunning = false;
    private int resetCounter;
    private Playlist currentPlaylist;
    private Song currentSong;
    private boolean isShuffleMode = false;

    /**
     * Initializes different aspects of the program, including: Pictures, songs from database, searchbar, buttons
     * and welcome messages
     * @throws Exception
     */

    public void initialize() throws Exception {

        //Adding a default picture
        Image defaultImage = new Image(getClass().getResource("/Pictures/MusicCat.png").toExternalForm());
        pictures.setImage(defaultImage);
        playButton.setText("😿");
        stopButton.setText("\uD83D\uDE40");
        shuffleButton.setText("Shuffle");

        // initialize Songs
        allSongs = Reader.readAllSongs();

        // initialize sound effects
        SoundEffects.readAllEffects();

        Playlist allSongsPlaylist = new Playlist("All songs", allSongs, "src/main/resources/Pictures/DefaultPlaylistPictures");

        SearchableComboBox.initializeSearchBar(searchBar, allSongs);

        System.out.println(allSongs.size() + " songs initialized");

        //initializing playlists
        initzializePlaylists();

        //initializing playlists options
       initzializePlaylistOptions();


        if (!allPlaylists.isEmpty())
        {
            currentPlaylist = allPlaylists.getFirst();
        }
        else
        {
            currentPlaylist = allSongsPlaylist;
            currentSong = allSongsPlaylist.getSongs().getFirst();
        }

        if (!currentPlaylist.getSongs().isEmpty()) {
            currentSong = currentPlaylist.getSongs().getFirst();
        }

        //initialize Progressbar
        progressBar.setStyle("-fx-accent: #FFA500;");

        //initialize all songs in the song in playlist box
        updateSongList();

        createMediaPlayer();

        if (timer == null){beginTimer();}
        else {cancelTimer();}

        displayPlaylistTitleAndTotalPlaylistDuration();
        SongTitleLabel.setText("Welcome To Kittyfy");
        ArtistNameLabel.setText("playing playlist: " + currentPlaylist.getName());
    }

    /**
     * Updates songlist in scrollbar and set the buttons to a certain size.
     * When the button is pushed, the song that is currently playing stops, and the playSong (song) is called while a random picture shows.
     */
    public void updateSongList() {
        songsVbox.getChildren().clear();

        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) {
            System.out.println("Playlist is empty.");
            return;
        }

        for (Song song : currentPlaylist.getSongs()) {
            ArrayList<String> trimmedArtists = new ArrayList<>();
            for (String artist : song.getArtist()) {
                trimmedArtists.add(artist.trim());
            }
            Button newButton = new Button(song.getTitle().trim() + " by " + String.join(", ", trimmedArtists));
            newButton.setPrefWidth(642);
            newButton.setPrefHeight(30);
            newButton.setStyle(
                            "-fx-background-color: #000000; " +
                            "-fx-text-fill: orange; " +
                            "-fx-border-color: #FFCC00; " +
                            "-fx-border-width: 0.5; " +
                            "-fx-border-radius: 0.5;" +
                            "-fx-underline: true;" +
                            "-fx-cursor: hand;"
            );


            newButton.setAlignment(Pos.CENTER);
            newButton.setPadding(new Insets(0, 0, 0, 0));


            newButton.setOnAction((ActionEvent event) -> {
                try {
                    stopMusic();
                    playSong(song, true);
                    showRandomImage();

                } catch (Exception e) {
                    System.out.println("Failed to play song");
                    e.printStackTrace();
                }
            });
            songsVbox.getChildren().add(newButton);

        }
    }

    /**
     * Takes random picture from picture folder and shows it on the imageview on the hello-view UI.
     */
    public void showRandomImage(){
        try {
            String imageFolderPath = Playlist.getFolderPath(currentPlaylist.getName());
            File folder;
            if (imageFolderPath != null) {
                folder = new File(imageFolderPath.trim());
            }
            else
            {
                // uses default folder if no folder is declared in the database.
                folder = new File("src/main/resources/Pictures/DefaultPlaylistPictures");
            }


            File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".png"));


            if (listOfFiles != null && listOfFiles.length > 0) {
                Random rand = new Random();
                File ranImageFile = listOfFiles[rand.nextInt(listOfFiles.length)];

                Image randomImage = new Image(ranImageFile.toURI().toString());
                pictures.setImage(randomImage);

            } else {
                System.out.println("No image found.");
            }
        }catch (Exception e) {
            System.out.println("Failed to load image.");
            e.printStackTrace();
        }
    }

    /**
     * Connecting the musicfile to the label, calls the display Artist, Title and Duration methods, and begins the timer.
     * @param song song to be played
     * @param fromPlaylist is the song from the currentPlaylist or is it singular.
     */
    public void playSong(Song song, Boolean fromPlaylist) {
        stopMusic();

        try {
            // the MediaPlayer doesn't update the metadata every time it's created, so we need a listener.
            mediaPlayer.setOnReady(() -> {
                displayDuration(mediaPlayer.getMedia().getDuration());
            });

            //displays artist based on song object instead of currentSongNumber.
            Platform.runLater(() -> {
                displayArtistBasedOnSong(song);
            });

            //displays title based on song object.
            Platform.runLater(() -> {
                displaySongTitleOnLabel(song);
            });

            if (fromPlaylist) {
                currentSongNumber = currentPlaylist.getSongIndex(song);
            }

            currentSong = song;

            if (timer == null){beginTimer();}
            else {cancelTimer();}


            isRunning = true;

            mediaPlayer.dispose();
            createMediaPlayer();

            //displays total duration based on media.
            if (timer != null) {
                timer.cancel();
                beginTimer();
            }
            showRandomImage();
            SoundEffects.play(SoundEffects.kittySounds.PLAY); // mediaPlayer.play() is called in here

            //mediaPlayer.play();

            checkIcon();

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to play song");
        }

    }

    private void checkIcon() {
        if (isRunning) {
            playButton.setText("😹");
        } else {
            playButton.setText("😿");
        }
    }


    /**
     * Plays the currently selected song and displays the artist + title + total duration.
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public void playMusic() throws Exception {

        if (isRunning) {
            isRunning = false;
            checkIcon();
            mediaPlayer.pause();
            SoundEffects.play(SoundEffects.kittySounds.PAUSE);
        } else {
            isRunning = true;
            checkIcon();
            SoundEffects.play(SoundEffects.kittySounds.PLAY); // mediaPlayer.play() is called in here
            displayPlaylistTitleAndTotalPlaylistDuration();
            displayArtistOnLabel();
            displaySongTitleOnLabel(currentSong);
        }
    }

    private Song findSongByTitle (String title) {
        for (Song song : allSongs) {
            if (title.contains(song.getTitle().trim())) {
                return song;
            }
        }
        return null;
    }

    public void addSongClick() throws Exception {
        String selectedSongTitle = searchBar.getValue();
        Song songToAdd = findSongByTitle(selectedSongTitle);

        if (songToAdd != null) {
            BridgePlaylistSong.addSongToPlaylist(currentPlaylist, Song.getSong(songToAdd.getTitle()));
            updateSongList();
        }
    }

    /**
     * plays the song the user clicks on in the combobox, and calls the playMusic method.
     * @throws Exception
     */
    public void playSongOnClick() {

        String selectedTitle = searchBar.getValue();
        if (selectedTitle == null || selectedTitle.isEmpty()) {
            System.out.println("No song selected!");
            return;
        }

        for (Song song : allSongs) {
            ArrayList<String> trimmedArtists = new ArrayList<>();
            for (String artist : song.getArtist()) {
                trimmedArtists.add(artist.trim());
            }
            String artists = String.join(", ", trimmedArtists);


            if (selectedTitle.equals(song.getTitle().trim() + " by " + artists)) {

                playSong(song, false);
                return;
            }
        }
        System.out.println("No song selected!");
    }
    /**
     * restarts the song that is currently playing, and calls the previousSong method after 2 clicks.
     * @throws Exception
     */
    public void reset() throws Exception {
        resetCounter++;
        if (resetCounter == 2) {
            previousSong();
            resetCounter = 0;
        }
        if (isRunning) {
            progressBar.setProgress(0);
            mediaPlayer.seek(Duration.seconds(0));
        }
    }

    /**
     * skips to the next song in the order, and if the order is finished, circle back to the first song.
     * and play the music.
     * @throws Exception
     */
    public void skip() throws Exception {
        showRandomImage();
        int forward = 1;
        if (isShuffleMode)
        {
            if (currentPlaylist.getSongs().size() > 2)
            {
                forward = new Random().nextInt(1, currentPlaylist.getSongs().size()/2 + 1);
            }

        }

        if(currentSongNumber + forward <= currentPlaylist.getSongs().size()-1){
            currentSongNumber += forward;
        }
        else {
            currentSongNumber = (currentSongNumber + forward) - currentPlaylist.getSongs().size() - 1;
        }

        if (currentSongNumber < 0)
        {
            currentSongNumber = 0;
        }
        playSong(currentPlaylist.getSongs().get(currentSongNumber), true);
    }

    /**
     * stop the music and restarts the song, but doesn't play it.
     */
    public void stopMusic() {
        mediaPlayer.stop();
        playButton.setText("😿");
        isRunning = false;
    }

    /**
     * just skip method in reverse.
     * @throws Exception
     */
    public void previousSong() throws Exception {
        currentSongNumber--;
        if (currentSongNumber >= 0) {
            playSong(currentPlaylist.getSongs().get(currentSongNumber), true);
        } else {
            currentSongNumber = currentPlaylist.getSongs().size() - 1;
            playSong(currentPlaylist.getSongs().get(currentSongNumber), true);
        }
    }

    /**
     * displays the total time the song has, and displays in the correct Label.
     * @param duration is an object created by the media, and ensures that the time displayed is the same and the
     *                 song that is currently playing
     */
    public void displayDuration(Duration duration) {
        if (duration != null) {
            double seconds = duration.toSeconds();
            int minutes = (int) seconds / 60;
            int remainingSeconds = (int) seconds % 60;

            // Format as "min:seconds" with two digits for seconds
            String formattedDuration = String.format("%d:%02d", minutes, remainingSeconds);
            totalDurationLabel.setText(formattedDuration);
        }
    }

    /**
     * begins the timer, that is used for the progress bar, the timer is attached to the MediaPlayer object.
     * and displays the progress bar + current time, and skips to the next song, if total time and current time is that same.
     */
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
                        displayDuration(mediaPlayer.getMedia().getDuration());

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
        timer.schedule(timerTask, 0, 100);
    }

    /**
     * stops the timer, and removes it, sets isRunning to false.
     */
    private void cancelTimer() {
        isRunning = false;
        timer.cancel();
    }

    /**
     * A method that cancel the timer, and releases the mediaPlayer. (is called when stage is closed)
     */
    public static void onClose() {
        if (timer != null) {
            timer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    /**
     * Creates a media and mediaPlayer object.
     * and displays the song title and artist.
     * @throws Exception
     */
    public void createMediaPlayer() throws Exception {
        //creates a Media Player
        media = new Media(new File("src/main/resources/music/" + currentSong.getFilePath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        displaySongTitleOnLabel(currentSong);
        displayArtistOnLabel();
    }

    /**
     * displays the song title, based on current song that is played, and displays the song next song.
     */
    public void displaySongTitleOnLabel(Song song) {
        SongTitleLabel.setText(song.getTitle());

        if (currentSongNumber + 1 >= currentPlaylist.getSongs().size()) {
            System.out.println("Coming up: " + currentPlaylist.getSongs().getFirst().getTitle());
        } else {
            System.out.println("Coming up: " + currentPlaylist.getSongs().get(currentSongNumber + 1).getTitle());
        }
    }

    /**
     * displays the artist/artists of the song that is played, based on currentSongNumber.
     */
    public void displayArtistOnLabel() {
        //Displays the artists
        String[] artistArray = currentPlaylist.getSongs().get(currentSongNumber).getFilePath().split(" - ");
        ArrayList<String> artists = new ArrayList<>();
        artists.addAll(Arrays.asList(artistArray[1].split(", ")));
        String artistNames = "";
        if (artists.size() > 1) {
            for (String artist : artists) {
                artistNames += artist + ", ";
            }
            artistNames = artistNames.substring(0, artistNames.length() - 2);
        } else {
            artistNames = artists.getFirst();
        }

        ArtistNameLabel.setText(artistNames);
        ArtistNameLabel.setAlignment(CENTER);
    }

    /**
     * displays the artist/artists of the song that is played, based on a song object instead of currentSongNumber.
     * @param song a song.
     */
    public void displayArtistBasedOnSong(Song song) {
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

    public void displayPlaylistTitleAndTotalPlaylistDuration() throws UnsupportedAudioFileException, IOException {
        double TotalPlaylistDuration = 0;
        for (Song song : currentPlaylist.getSongs()) {
            File file = new File("src/main/resources/music/" + song.getFilePath());
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();

            if (frames >= 50000000)
            {
                TotalPlaylistDuration += (double) frames / (format.getFrameRate() * 142.948717949);
            }
            else
            {
                TotalPlaylistDuration += (double) frames / format.getFrameRate();
            }

        }
            int hours = (int) TotalPlaylistDuration / 3600;
            int minutes = (int) ((TotalPlaylistDuration % 3600) / 60);
            int seconds = (int) (TotalPlaylistDuration % 60);
            String formattedTotalDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        PlaylistTitleAndDuration.setText("Playing playlist: " + currentPlaylist.getName().trim() + " - Total Purrlist Duration: " + formattedTotalDuration);
    }


    /**
     * Switches the scene to the "Create Playlist" scene.
     * @param actionEvent
     * @throws IOException
     */
    public void createPlaylist(ActionEvent actionEvent) throws IOException {
        mediaPlayer.stop();
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        FXMLLoader fxmlLoader = new FXMLLoader(CreatePlaylistController.class.getResource("Create-Playlist.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("Create Playlist");
        stage.setScene(new Scene(root));
        stage.show();
    }
    public void NewSongSceneClick(ActionEvent actionEvent) throws IOException {
        mediaPlayer.stop();
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        FXMLLoader fxmlLoader = new FXMLLoader(CreatePlaylistController.class.getResource("AddNewSong.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("Create Playlist");
        stage.setScene(new Scene(root));
        stage.show();
    }


    public void shuffle()
    {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        if (isShuffleMode)
        {
            isShuffleMode = false;
            shuffleButton.setStyle("-fx-text-fill: ORANGE; -fx-background-color: BLACK;");
        }
        else
        {
            isShuffleMode = true;
            shuffleButton.setStyle("-fx-text-fill: WHITE; -fx-background-color: BLACK;");
            shuffleButton.setUnderline(true);
        }
    }

    public void initzializePlaylists() throws Exception {
        //initializing playlists
        allPlaylists = Playlist.getAllPlaylists();
        System.out.println(allPlaylists.size()+" playlists initialized");

        if(!allPlaylists.isEmpty()) {
            currentPlaylist = allPlaylists.get(0);
        }
        else
        {
            currentPlaylist = new Playlist("All songs", allSongs, "src/main/resources/Pictures/DefaultPlaylistPictures");
        }
    }

    public void initzializePlaylistOptions() {
        HBox currentHBox= null;

        //PLaylist buttons
        for(Playlist playlist : allPlaylists){
            Button playlistButton = new Button(playlist.getName());
            playlistButton.setPrefWidth(150);
            playlistButton.setPrefHeight(30);
            playlistButton.setStyle("-fx-background-color: #000000 " + "; -fx-text-fill: orange;");
            playlistButton.setAlignment(CENTER_LEFT);
            playlistButton.setPadding(new Insets(0, 0, 0, 5));
            playlist.playlistButton = playlistButton;

            //Edit Button
            Button editButton = new Button();
            editButton.setText("⚙");
            editButton.setStyle("-fx-font-size: 25;");
            editButton.setStyle("-fx-background-color: #000000 " + "; -fx-text-fill: orange;");
            editButton.setPrefWidth(30);
            editButton.setPrefHeight(30);

            //Make HBox and add buttons
            currentHBox = new HBox();
            currentHBox.setSpacing(0);
            currentHBox.setPadding(new Insets(0, 0, 0, 0));
            currentHBox.getChildren().addAll(playlistButton, editButton);
            vBoxPlaylists.getChildren().add(currentHBox);

            playlistButton.setOnAction(event -> {
                currentPlaylist = playlist;
                if (isShuffleMode) {
                    currentSongNumber = new Random().nextInt(currentPlaylist.getSongs().size());
                }
                else
                {
                    currentSongNumber=0;
                }

                if (!currentPlaylist.getSongs().isEmpty())
                {
                    currentSong = currentPlaylist.getSongs().get(currentSongNumber);
                }
                else
                {
                    currentSong = allSongs.getFirst();
                }
                try {
                    displayPlaylistTitleAndTotalPlaylistDuration();
                    String folderPath;
                    if (currentPlaylist.getFolderPath(currentPlaylist.getName()) != null){
                        folderPath = currentPlaylist.getFolderPath(currentPlaylist.getName());
                    } else {folderPath = "src/main/resources/Pictures/DefaultPlaylistPictures";}

                    System.out.println("pictureFolderPath for selected Playlist: " + folderPath);

                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    playSong(currentSong,true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                updateSongList();
                ArtistNameLabel.setText("Playing playlist: " + currentPlaylist.getName());
                System.out.println("Current playlist set to: " + currentPlaylist.getName());
                playlistButton.setStyle("-fx-background-color: #000000; " +"-fx-text-fill: white;");
                playlistButton.setUnderline(true);

                for(Playlist curPlaylist: allPlaylists){
                    if(!curPlaylist.getName().equals(currentPlaylist.getName())){
                        curPlaylist.playlistButton.setStyle("-fx-background-color: #000000; " +"-fx-text-fill: orange;");
                        curPlaylist.playlistButton.setUnderline(false);
                    }
                }
            });

            editButton.setOnAction(event -> {
                SoundEffects.play(SoundEffects.kittySounds.SELECT);
                onClose();
                try{
                FXMLLoader fxmlLoader = new FXMLLoader(EditPlaylistController.class.getResource("Edit-Playlist.fxml"));
                Parent root = fxmlLoader.load();

                EditPlaylistController controller = fxmlLoader.getController();
                controller.setPlaylist(playlist);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Edit Playlist");
                stage.setScene(new Scene(root));
                stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }
}