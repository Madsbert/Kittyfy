package org.example.kittyfy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;

public class SearchableTextfield {

    public static void initializeSearchBar(TextField searchBar, ListView<String> listView, ArrayList<Song> allSongs) {
        // Original list to preserve all items
        ObservableList<String> originalItems = FXCollections.observableArrayList();

        // Populate the original list
        for (Song song : allSongs) {
            ArrayList<String> trimmedArtists = new ArrayList<>();
            for (String artist : song.getArtist()) {
                trimmedArtists.add(artist.trim());
            }
            String artists = String.join(", ", trimmedArtists);
            String item = song.getTitle().trim() + " by " + artists;
            originalItems.add(item);
        }

        // Create a ListView for displaying suggestions

        listView.setItems(originalItems);
        listView.setVisible(false);
/*
        // Set the ListView size and style
        suggestionList.setPrefHeight(32);
        suggestionList.setPrefWidth(searchBar.getPrefWidth());
        suggestionList.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");


        // Add the ListView to the parent pane (VBox)
        parentPane.getChildren().add(suggestionList);

 */


        // Add a listener to the textProperty of the TextField
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                // Hide the suggestion list when the search field is empty
                listView.setVisible(false);
                listView.getItems().clear();
            } else {
                // Filter the items based on user input
                ObservableList<String> filteredItems = originalItems.filtered(item ->
                        item.toLowerCase().contains(newValue.toLowerCase()));
                listView.setItems(filteredItems);
                listView.setVisible(!filteredItems.isEmpty());
                listView.toFront();
            }
        });

        // Handle selection from the suggestion list
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 0) {
                String selectedItem = listView.getSelectionModel().getSelectedItem();
                System.out.println("Selected item: " + selectedItem);
                System.out.println("Mouse event: " + event);
                if (selectedItem != null) {
                    searchBar.clear();
                    searchBar.setText(selectedItem);
                    listView.setVisible(false);
                }
            }
        });

        listView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    searchBar.setText(selectedItem);
                    listView.setVisible(false);
                }
            }
        });

        // Hide the suggestion list when focus is lost
        searchBar.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                listView.setVisible(false);
            }
        });
    }
}

