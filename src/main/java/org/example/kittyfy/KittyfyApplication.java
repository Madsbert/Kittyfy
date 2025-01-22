package org.example.kittyfy;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

/**
 * Class to open application
 */
public class KittyfyApplication extends Application {

    /**
     * method to open application
     * @param stage a stage
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(KittyfyApplication.class.getResource("Main-View.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Kittyfy");

            Image icon = new Image(Objects.requireNonNull(getClass().getResource("/Pictures/Icon.png")).toExternalForm());
            stage.getIcons().add(icon);

            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest(_ -> {
                Platform.exit();
                System.exit(0);
                MainController.onClose();
            });


            DB.getConnection();
        } catch (IOException e) {
        System.out.println("Error: " + e.getMessage());}
    }

    public static void main(String[] args) {
        launch();
    }

}