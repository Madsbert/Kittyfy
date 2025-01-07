package org.example.kittyfy;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class CreatePlaylistController {

    @FXML private ImageView createPlaylistImage;

    public void initialize() {
        Image playlistImage = new Image(Objects.requireNonNull(getClass().getResource("/Pictures/CatMakingMusic.png")).toExternalForm());
        createPlaylistImage.setImage(playlistImage);
    }
}
