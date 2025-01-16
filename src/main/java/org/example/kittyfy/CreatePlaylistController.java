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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

//Class which controls the Create playlist fxml

public class CreatePlaylistController {

    //Annotations
    @FXML
    private ImageView createPlaylistImage;

    @FXML
    private TextField playlistName;

    @FXML
    private ComboBox<String> searchbarPlaylist;

    @FXML
    private ChoiceBox<String> choosePictures;

    @FXML
    private VBox songsInPlaylist;

    private ArrayList<Song> allSongs;
    private String selectedPicFolderFilepath = null;


    public void initialize() throws Exception {
        //adding a default picture
        Image playlistImage = new Image(Objects.requireNonNull(getClass().getResource("/Pictures/CatMakingMusic.png")).toExternalForm());
        createPlaylistImage.setImage(playlistImage);

        //filling the choicebox with options from database.
        ArrayList<String> genres = new ArrayList<>(Song.getAllGenreNames());
        for (String genre : genres){
            choosePictures.getItems().add(genre);
        }
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

    /**
     * Finds a song by searching the title.
     * @param title
     * @return
     */
    private Song findSongByTitle (String title) {
        for (Song song : allSongs) {
            if (title.contains(song.getTitle())) {
                return song;
            }
        }
        return null;
    }

    /**
     * Creates playlist and puts it in the database.
     * @param event
     * @throws Exception
     */
    public void createPlaylist(ActionEvent event) throws Exception {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        String playlistName = this.playlistName.getText();
        if (playlistName == null || playlistName.isEmpty()) {
            System.out.println("Playlist name cannot be empty");
            return;
        }
        ArrayList<Song> playlistSongs = new ArrayList<>();
        for (Node node : songsInPlaylist.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for(Node child: hbox.getChildren()){
                    if(child instanceof Label){
                        String labelText = ((Label) child).getText();
                        Song song = findSongByTitle(labelText);
                        if (song != null) {
                            playlistSongs.add(song);

                        }else{
                            System.out.println("No Song found for label " + labelText);
                        }
                        break;
                    }
                }
            }else{
                System.out.println("Node in sonsplaylist in not an hbox");
            }
        }
        if (playlistSongs.isEmpty()) {
            System.out.println("No songs found. You must add at least one song.");
            return;
        }

        getGenreFromChoiceBox();

        Playlist newPlaylist = new Playlist(playlistName, playlistSongs,selectedPicFolderFilepath);
        newPlaylist.setLastPlayed(0);

        int playlistID = Playlist.createPlaylist(newPlaylist);
        newPlaylist.setPlaylistId(playlistID);

        BridgePlaylistSong.addSongsToPlaylist(newPlaylist);

        shiftScene(event);
    }

    public void cancel(ActionEvent event) throws IOException {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
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

    public void addSongPlaylist() throws Exception {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        String selectedTitle = searchbarPlaylist.getValue();
        if (selectedTitle == null || selectedTitle.isEmpty()) {
            System.out.println("No song selected!");
        }
        else {

            addSongToVBox(findSongByTitle(selectedTitle));
        }
    }

    public void openFileExplorer(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Folder");

        //Shows the directory
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File selectedFolder = directoryChooser.showDialog(stage);
        //extracts the folder path.
        if (selectedFolder != null) {
            selectedPicFolderFilepath = selectedFolder.getAbsolutePath().trim();
            System.out.println("Selected Folder Path: " + selectedPicFolderFilepath);
            choosePictures.setValue(selectedPicFolderFilepath);
        }
    }
    public void getGenreFromChoiceBox() {
        if (choosePictures.getValue() != null) {
            switch (choosePictures.getValue()) {
                case "Rock":
                    selectedPicFolderFilepath = "src/main/resources/Pictures/catRockTheme";
                    choosePictures.setValue(selectedPicFolderFilepath);
                    break;
                case "Classical":
                    selectedPicFolderFilepath = "src/main/resources/Pictures/catClassicalTheme";
                    choosePictures.setValue(selectedPicFolderFilepath);
                    break;
                case "Dansk Top":
                    selectedPicFolderFilepath = "src/main/resources/Pictures/catDanskTopTheme";
                    choosePictures.setValue(selectedPicFolderFilepath);
                    break;
                default :
                    selectedPicFolderFilepath = null;
                    System.out.println("No folder was selected.");
            }
        }
    }

    private void addSongToVBox(Song song) {
        ArrayList<String> trimmedArtists = new ArrayList<>();
        for (String artist : song.getArtist()) {
            trimmedArtists.add(artist.trim());
        }
        Label songLabel = new Label(song.getTitle().trim() + " by " + String.join(", ", trimmedArtists));
        songLabel.setPrefWidth(650);
        songLabel.setPrefHeight(30);
        songLabel.setStyle("-fx-background-color: #000000 " + "; -fx-text-fill: white;");
        songLabel.setAlignment(Pos.CENTER_LEFT);
        songLabel.setPadding(new Insets(0, 10, 0, 10));


        //Delete Button
        Button deleteSongButton = new Button();
        deleteSongButton.setText("⎯");
        deleteSongButton.setFont(new Font("Berlin Sans FB Demi",14));
        deleteSongButton.setPrefWidth(25);
        deleteSongButton.setPrefHeight(30);
        deleteSongButton.setStyle("-fx-background-color: #000000;"+"-fx-text-fill: orange;"+"-fx-border-color: orange;");


        //Make HBox and add buttons
        HBox currentHBox = new HBox(songLabel, deleteSongButton);
        currentHBox.setSpacing(0);
        currentHBox.setPadding(new Insets(0, 0, 0, 0));
        songsInPlaylist.getChildren().add(currentHBox);


        deleteSongButton.setOnAction(actionEvent -> {

            if(songsInPlaylist.getChildren().contains(currentHBox)) {
                songsInPlaylist.getChildren().remove(currentHBox);
            }
        });
    }

}