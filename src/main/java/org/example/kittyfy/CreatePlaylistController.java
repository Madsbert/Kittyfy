package org.example.kittyfy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;



import java.io.IOException;
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



    public void initialize() {
        //adding a default picture
        Image playlistImage = new Image(Objects.requireNonNull(getClass().getResource("/Pictures/CatMakingMusic.png")).toExternalForm());
        createPlaylistImage.setImage(playlistImage);

        //filling the choicebox with options
        choosePictures.getItems().addAll("Choose Picture Album", "Dansk Top", "Rock", "Klassisk");
        choosePictures.setValue("Choose Picture Album");
    }

    public void createPlaylist(ActionEvent event) throws IOException {
        shiftScene(event);
    }

    public void cancel (ActionEvent event) throws IOException {
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
