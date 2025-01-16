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


    /**
     * setting the playlist and initializing the songlabels
     * @param playlist
     */
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        System.out.println("Editing playlist: " + playlist.getName());

        //initialize songs in playlist
        if(playlist != null) {
            for (Song song : playlist.getSongs()) {
                addSongToVBox(song);
            }
        }



    }

    /**
     * Initialize Songs in Searchbar
     */
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

    /**
     * method to add song to playlist
     */
    public void addSongToPlaylist() {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        String selectedTitle = searchbarPlaylist.getValue();
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
     * @param event
     * @throws IOException
     */
    public void cancelButton(ActionEvent event) throws IOException {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        shiftScene(event);
    }

    /**
     * Finds the songs by titel
     * @param title
     * @return
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
     * Saves the changes made to either the playlistname or songs added or deleted
     * @param event
     * @throws Exception
     */
    public void saveChangesButton(ActionEvent event) throws Exception {
        SoundEffects.play(SoundEffects.kittySounds.SELECT);
        String playlistName = this.playlistNameTextfield.getText();
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



        playlist.setName(playlistName);
        playlist.setSongs(playlistSongs);

        BridgePlaylistSong.updateSongsInPlaylist(playlist);
        Playlist.updatePlaylist(playlist);

        shiftScene(event);
    }

    /**
     * deletes the playlist
     * @param event
     * @throws Exception
     */
    public void deletePlaylistButton(ActionEvent event) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ImageView imageView = new ImageView(getClass().getResource("/Pictures/StopSignCat.png").toExternalForm());
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
     * @param actionEvent
     * @throws IOException
     */
    private void shiftScene(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloController.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("KittyFy");
        stage.setScene(new Scene(root));
        stage.show();
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

            playlistNameTextfield.setText(playlist.getName());

            deleteSongButton.setOnAction(actionEvent -> {
                try {
                    BridgePlaylistSong.deleteSongFromPlaylist(playlist,song);
                    playlist.getSongs().remove(song);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if(songsInPlaylist.getChildren().contains(currentHBox)) {
                    songsInPlaylist.getChildren().remove(currentHBox);
                }
            });
    }

}





