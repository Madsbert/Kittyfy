package org.example.kittyfy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

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

    public void createPlaylist(){
    }

    public void stop(){
    }

}