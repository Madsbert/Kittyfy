package org.example.kittyfy;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Objects;

public class EditPlaylistController {

    @FXML
    ImageView EditPlaylistImage;

    @FXML
    ChoiceBox<String> choosePictures;

    @FXML
    ComboBox<String> searchbarPlaylist;

    private ArrayList<Song> allSongs;

    private Playlist playlist;

    public void initialize() throws Exception {
        //adding a default picture
        Image editPlaylistImage = new Image(Objects.requireNonNull(getClass().getResource("/Pictures/CatMakingMusic.png")).toExternalForm());
        EditPlaylistImage.setImage(editPlaylistImage);

        //filling the choicebox with options
        choosePictures.getItems().addAll("Choose Picture Album", "Dansk Top", "Rock", "Klassisk");
        choosePictures.setValue("Choose Picture Album");

        //initialize Songs
        allSongs = Reader.readAllSongs();


        //initializing searchbar options
        for (Song song : allSongs) {
            ArrayList<String> trimmedArtists = new ArrayList<>();
            for (String artist : song.getArtist()) {
                trimmedArtists.add(artist.trim());
            }
            String artists = String.join(", ", trimmedArtists);
            searchbarPlaylist.getItems().add(song.getTitle().trim() + " by " + artists);
        }

        System.out.println(allSongs.size() + " songs initialized");
    }




    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        System.out.println("Editing playlist: " + playlist.getName());

    }

}
