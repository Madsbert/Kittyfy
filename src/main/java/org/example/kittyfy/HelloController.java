package org.example.kittyfy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

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

    public void play(){
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