package org.example.kittyfy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.event.ActionEvent;

import java.util.Objects;


public class HelloController {

    @FXML
    private ImageView pictures;

    @FXML
    private ComboBox<String> searchBar;

    @FXML
    private Button addSong;

    @FXML
    private ScrollPane playlistPane;

    @FXML
    private ScrollPane songsPane;


    public void initialize() {
        Image defaultImage = new Image(getClass().getResource("/Pictures/MusicCat.png").toExternalForm());
        pictures.setImage(defaultImage);
    }


    public void reset() {
    }

    public void play() throws UnsupportedAudioFileException, IOException {
        Song song1 = new Song("Himmelhunden","Teddy Edelmann", "DanskTop","C:\\Users\\jakob\\Desktop\\Programering\\Kittyfy\\src\\main\\resources\\Genre\\Dansk-Top\\Himmelhunden - Teddy Edelmann - DanskTop.wav");

        System.out.println("Song1 : Title" + song1.getTitle() + ", Artist: " + song1.getArtist() + ", Genre: " + song1.getGenre() + ", Duration: " + song1.getDuration());

    }

    public void addSongClick() {
    }

    public void skip(){
    }

    public void createPlaylist(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CreatePlaylistController.class.getResource("Create-Playlist.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Create Playlist");
        stage.setScene(scene);
        stage.show();
    }

    public void stop(){
    }

}