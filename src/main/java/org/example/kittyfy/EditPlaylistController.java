package org.example.kittyfy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class EditPlaylistController {

    @FXML
    ImageView editPlaylistImage;

    @FXML
    ChoiceBox<String> choosePictures;

    @FXML
    ComboBox<String> searchbarPlaylist;

    @FXML
    VBox songsInPlaylist;

    @FXML
    TextField playlistNameTextfield;

    private ArrayList<Song> allSongs;

    private Playlist playlist;

    public void initialize() throws Exception {
        //adding a default picture
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/Pictures/CatMakingMusic.png")).toExternalForm());
        editPlaylistImage.setImage(image);

        //filling the choicebox with options
        choosePictures.getItems().addAll("Choose Picture Album", "Dansk Top", "Rock", "Klassisk");
        choosePictures.setValue("Choose Picture Album");

        //initialize Songs
        allSongs = Reader.readAllSongs();


        //initializing searchbar options
       initializeSongsInSearchbar();

    }




    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        System.out.println("Editing playlist: " + playlist.getName());

        //initialize songs in playlist
        if(playlist != null) {
            for (Song song : playlist.getSongs()) {
                ArrayList<String> trimmedArtists = new ArrayList<>();
                for (String artist : song.getArtist()) {
                    trimmedArtists.add(artist.trim());
                }
                Label newLabel = new Label(song.getTitle().trim() + " by " + String.join(",", trimmedArtists));
                newLabel.setPrefWidth(650);
                newLabel.setPrefHeight(30);
                newLabel.setStyle("-fx-background-color: #000000 " + "; -fx-text-fill: white;");
                newLabel.setAlignment(Pos.CENTER_LEFT);
                newLabel.setPadding(new Insets(0, 10, 0, 10));

                songsInPlaylist.getChildren().add(newLabel);

                playlistNameTextfield.setText(playlist.getName());
            }
        }


    }

    public void initializeSongsInSearchbar(){
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

    public void addSongToPlaylist() {
        String selectedTitle = searchbarPlaylist.getValue();
        if (selectedTitle == null || selectedTitle.isEmpty()) {
            System.out.println("No song selected!");
        }
        Label newLabel = new Label (selectedTitle);
        newLabel.setPrefWidth(650);
        newLabel.setPrefHeight(30);
        newLabel.setStyle("-fx-background-color: #000000 " + "; -fx-text-fill: white;");
        newLabel.setAlignment(Pos.CENTER_LEFT);
        newLabel.setPadding(new Insets(0, 10, 0,10 ));

        songsInPlaylist.getChildren().add(newLabel);
    }

    public void cancelButton(ActionEvent event) throws IOException {
        shiftScene(event);
    }

    public void deletePlaylistButton(ActionEvent event) throws Exception {
        Playlist.deletePlaylist(playlist);
        shiftScene(event);
    }
    //method to shift scenes
    private void shiftScene(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloController.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("KittyFy");
        stage.setScene(new Scene(root));
        stage.show();
    }


}





