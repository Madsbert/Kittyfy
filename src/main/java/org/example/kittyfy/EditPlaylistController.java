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
 * class to control EditPLaylistFXML file.
 */
public class EditPlaylistController {

    @FXML
    ImageView editPlaylistImage;

    @FXML
    ListView<String> listView;
    @FXML
    ChoiceBox<String> choosePictures;

    @FXML
    TextField searchbarPlaylist;

    @FXML
    VBox songsInPlaylist;

    @FXML
    TextField playlistNameTextfield;

    private ArrayList<Song> allSongs;

    private Playlist playlist;
    private String selectedPicFolderFilepath = null;

    /**
     * method to initialize scene
     */
    public void initialize() {
        //adding a default picture
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/Pictures/CatMakingMusic.png")).toExternalForm());
        editPlaylistImage.setImage(image);

        //filling the choice box with options
        choosePictures.getItems().addAll("Choose Picture Album", "Dansk Top", "Rock", "Classical");

        //initialize Songs
        allSongs = Reader.readAllSongs();


        SearchableTextfield.initializeSearchBar(searchbarPlaylist,listView,allSongs, false);
    }


    /**
     * setting the playlist and initializing the song labels
     * @param playlist playlist instance to set.
     */
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        System.out.println("Editing playlist: " + playlist.getName());
        playlistNameTextfield.setText(playlist.getName());

        //initialize songs in playlist
        for (Song song : playlist.getSongs()) {
            addSongToVBox(song);
        }
    }

    /**
     * method to choose pictures
     */
    public void setChoosePictures() {
        System.out.println("Filepath: " + playlist.getFolderPath());
        if (playlist.getFolderPath() != null) {
            switch (playlist.getFolderPath()) {
                case "src/main/resources/Pictures/catDanskTopTheme":
                    choosePictures.setValue("Dansk Top");
                    break;

                case "src/main/resources/Pictures/catRockTheme":
                    choosePictures.setValue("Rock");
                    break;

                case "src/main/resources/Pictures/catClassicalTheme":
                    choosePictures.setValue("Classical");
                    break;

                default:
                    choosePictures.setValue(playlist.getFolderPath());
                    break;
            }
        }
        else
        {
            choosePictures.setValue("Choose Picture Album");
        }
    }

    /**
     * method to add song to playlist
     */
    public void addSongToPlaylist() {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        String selectedTitle = searchbarPlaylist.getText();
        if (selectedTitle == null || selectedTitle.isEmpty()) {
            System.out.println("No song selected!");
            return;
        }
        Song selectedSong = findSongByTitle(selectedTitle);
        if (selectedSong != null && !playlist.getSongs().contains(selectedSong)) {
            playlist.getSongs().add(selectedSong);
            addSongToVBox(selectedSong);
        } else {
            System.out.println("Song is already in the playlist or not found.");
        }

    }

    /**
     * shifts scene and doesn't add to database
     * @param event caller event.
     */
    public void cancelButton(ActionEvent event) {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        shiftScene(event);
    }

    /**
     * Finds the songs by Title
     * @param title title of song to find.
     * @return Song instance.
     */
    private Song findSongByTitle (String title) {
        for (Song song : allSongs) {
            if (title.contains(song.getTitle().trim())) {
                return song;
            }
        }
        return null;
    }

    /**
     * Saves the changes made to either the playlist name or songs added or deleted
     * @param event caller event.
     */
    public void saveChangesButton(ActionEvent event) {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        String playlistName = this.playlistNameTextfield.getText();
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
                System.out.println("Node in songs playlist is not an hbox");
            }
        }
        if (playlistSongs.isEmpty()) {
            System.out.println("No songs found. You must add at least one song.");
            return;
        }


        getGenreFromChoiceBox1();
        playlist.setName(playlistName);
        playlist.setSongs(playlistSongs);
        playlist.setFolderPath(selectedPicFolderFilepath);

        BridgePlaylistSong.updateSongsInPlaylist(playlist);
        Playlist.updatePlaylist(playlist);

        shiftScene(event);
    }

    /**
     * deletes the playlist
     * @param event caller event.
     */
    public void deletePlaylistButton(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ImageView imageView = new ImageView(Objects.requireNonNull(getClass().getResource("/Pictures/StopSignCat.png")).toExternalForm());
        alert.setTitle("Delete Playlist");
        alert.setHeaderText("Are you sure you want to delete this playlist?");
        alert.setContentText("This action cannot be undone.");
        alert.setGraphic(imageView);

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            SoundEffects.play(SoundEffects.kittySounds.SELECT);
            BridgePlaylistSong.deleteSongsInPlaylist(playlist);
            Playlist.deletePlaylist(playlist);
            System.out.println("Playlist deleted: " + playlist.getName());
            shiftScene(event);
        }
    }


    /**
     * method to shift scenes
     * @param actionEvent event that called method.
     */
    private void shiftScene(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("Main-View.fxml"));
        Parent root;
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        try {
            root = fxmlLoader.load();
            stage.setTitle("KittyFy");
            stage.setScene(new Scene(root));
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        stage.show();
    }

    /**
     * Method to add a song button and a delete button in vbox
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

    /**
     * method to open file explorer in user laptop and choosing pictures.
     * @param event
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
     * Method to choose genre pictures. which will be displayed in main application
     */
    public void getGenreFromChoiceBox1() {
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
                default:
                    selectedPicFolderFilepath = choosePictures.getValue();
            }
            return;
        }
        System.out.println("No folder was selected.");
    }
}





