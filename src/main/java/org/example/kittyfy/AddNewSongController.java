package org.example.kittyfy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class AddNewSongController  {
    @FXML
    private TextField songTitleTextField;
    @FXML
    private TextField Artist1Textfield;
    @FXML
    private TextField Artist2Textfield;
    @FXML
    private TextField GenreTextfield;
    @FXML
    private TextField FolderSelectionTextfield;
    @FXML
    private ImageView editPlaylistImage;

    String songTitle;
    String Artist1;
    String Artist2;
    String Genre ;
    String selectedFilePath;


    public void initialize(){

        Image defaultImage = new Image(getClass().getResource("/Pictures/MusicCat2.png").toExternalForm());
        editPlaylistImage.setImage(defaultImage);
    }

    public void openFileExplorer(ActionEvent event) {
        // Initialize fileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a .wav file");

        // Set filter
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("WAV Files", "*.wav");
        fileChooser.getExtensionFilters().add(filter);

        // Open fileExplorer
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Extract the file path if a valid .wav file is selected
        if (selectedFile != null) {
            selectedFilePath = selectedFile.getAbsolutePath().trim();
            System.out.println("Selected File Path: " + selectedFilePath);
            FolderSelectionTextfield.setText(selectedFilePath);
        }
    }

    public void cancelButton(ActionEvent actionEvent) throws IOException {
        {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("Main-View.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("KittyFy");
            stage.setScene(new Scene(root));
            stage.show();
        }
    }


    public void addSongToAllSongs(ActionEvent actionEvent) throws IOException {
        File songFile = new File(selectedFilePath);
        System.out.println(songFile.getAbsolutePath());
        songTitle = songTitleTextField.getText();
        Artist1 = Artist1Textfield.getText();
        Artist2 = ", " + Artist2Textfield.getText();
        Genre = GenreTextfield.getText();


        File renamedFile;
        if (Artist2Textfield.getText().isEmpty()) {
            renamedFile = new File("src/main/resources/music/" + songTitle + " - " + Artist1 + " - " + Genre + ".wav");
            //cancelButton(null);
        }
        else {
            renamedFile = new File("src/main/resources/music/" + songTitle + " - " + Artist1 + Artist2+ " - " + Genre + ".wav");
            //cancelButton(null);
        }
        // Rename the file
        if (songFile.renameTo(renamedFile)) {
            System.out.println("Renamed File Path: " + renamedFile.getAbsolutePath());

            // Add the renamed file to Git
            String gitCommand = "git add \"" + renamedFile.getAbsolutePath() + "\"";
            try {
                Process process = Runtime.getRuntime().exec(gitCommand);

                // Wait for the process to complete and check its status
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("File successfully added to Git.");
                } else {
                    System.out.println("Failed to add file to Git. Exit code: " + exitCode);
                }
            } catch (Exception e) {
                System.err.println("Error while adding file to Git: " + e.getMessage());
            }
        } else {
            System.out.println("File renaming failed.");
        }

    }
}
