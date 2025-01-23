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

/**
 * Controls the CreatePlaylistFXML file
 */
public class CreatePlaylistController {

    //Annotations
    @FXML
    private ImageView createPlaylistImage;

    @FXML
    private TextField playlistName;

    @FXML
    private TextField searchbarPlaylist;

    @FXML
    private ListView<String> listView;

    @FXML
    private ChoiceBox<String> choosePictures;

    @FXML
    private VBox songsInPlaylist;

    private ArrayList<Song> allSongs;

    private String selectedPicFolderFilepath = "";


    /**
     * initializing everything that is needed to start the program
     */
    public void initialize()  {
        try {
            //adding a default picture
            Image playlistImage = new Image(Objects.requireNonNull(getClass().getResource("/Pictures/CatMakingMusic.png")).toExternalForm());
            createPlaylistImage.setImage(playlistImage);

            //filling the choicebox with options from database.
            ArrayList<String> genres = new ArrayList<>(Objects.requireNonNull(Song.getAllGenreNames()));
            for (String genre : genres) {
                choosePictures.getItems().add(genre);
            }
            choosePictures.setValue("Choose Picture Album");

            //initialize Songs
            allSongs = Reader.readAllSongs();

            SearchableTextfield.initializeSearchBar(searchbarPlaylist,listView,allSongs);

            System.out.println(allSongs.size() + " songs initialized");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds a song by searching the title.
     * @param title song title
     * @return a song
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
     * Creates playlist by reading the songs in the vbox and puts it in the database.
     * @param event needs an event to happen
     */
    public void createPlaylist(ActionEvent event) {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        String playlistName = this.playlistName.getText();
        if (playlistName == null || playlistName.isEmpty()) {
            System.out.println("Playlist name cannot be empty");
            return;
        }
        ArrayList<Song> playlistSongs = new ArrayList<>();
        for (Node node : songsInPlaylist.getChildren()) {
            if (node instanceof HBox hbox) {
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
                System.out.println("Node in sonsplaylist not in an hbox");
            }
        }
        if (playlistSongs.isEmpty()) {
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

    /**
     * cancels any changes made in the playlist and shifts scene
     * @param event needs an event to happen
     */
    public void cancel(ActionEvent event) {
       try {
           SoundEffects.play(SoundEffects.kittySounds.SELECT);
           shiftScene(event);
       }catch (Exception e){
           System.out.println(e.getMessage());
       }
    }


    /**
     * shifts scene to main scene
     * @param actionEvent needs an event to happen
     */
    private void shiftScene(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("Main-View.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("KittyFy");
            stage.setScene(new Scene(root));
            stage.show();
        }catch (IOException e){
            System.out.println("failed to ´shift scene" + e.getMessage());
        }
    }

    /**
     * Add songs to the VBox from the search
     */
    public void addSongPlaylist() {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        String selectedTitle = searchbarPlaylist.getSelectedText();
        if (selectedTitle == null || selectedTitle.isEmpty()) {
            System.out.println("No song selected!");
        }
        else {

            addSongToVBox(Objects.requireNonNull(findSongByTitle(selectedTitle)));
        }
    }


    /**
     * Opens the file explorer in users computer so the user can choose a picture folder
     * @param event if the file explorer button is pushed an event has happend
     */
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


    /**
     * The user can choose which genre the music is
     * and select a picture folder which the developer has created
     */
    public void getGenreFromChoiceBox() {
        if (choosePictures.getValue() != null) {
            // Skip switch case if the value is already a custom folder path
            if (selectedPicFolderFilepath != null && !selectedPicFolderFilepath.isEmpty()) {
                return;}
            switch (choosePictures.getValue().trim()) {
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
                    selectedPicFolderFilepath = choosePictures.getValue();
                    return;
            }
        }
        System.out.println("No folder was selected.");
    }


    /**
     * method to add songs to the Vbox and a delete Button
     * @param song a song object
     */
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


        deleteSongButton.setOnAction(_ -> songsInPlaylist.getChildren().remove(currentHBox));
    }

}