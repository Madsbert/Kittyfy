package org.example.kittyfy;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class SearchableComboBox {

    public static boolean paused = false;

    public static void initializeSearchBar(ComboBox<String> searchBar, ArrayList<Song> allSongs) {
        if (paused)
        {
            return;
        }
        paused = true;

        // Original list to preserve all items
        ObservableList<String> originalItems = FXCollections.observableArrayList();

        // Populate the combobox and the original list
        for (Song song : allSongs) {
            ArrayList<String> trimmedArtists = new ArrayList<>();
            for (String artist : song.getArtist()) {
                trimmedArtists.add(artist.trim());
            }
            String artists = String.join(", ", trimmedArtists);
            String item = song.getTitle().trim() + " by " + artists;
            originalItems.add(item);
        }

        // Set the items in the ComboBox
        searchBar.setItems(FXCollections.observableArrayList(originalItems));

        // Make the ComboBox editable
        searchBar.setEditable(true);

        // Add a listener to the editor's textProperty for filtering
        TextField editor = searchBar.getEditor();
        editor.textProperty().addListener((_, _, newText) -> {
            if (newText.isEmpty()) {
                // Reset to the original list when the search field is empty
                searchBar.setItems(FXCollections.observableArrayList(originalItems));
            } else {
                // Filter the items based on the user input
                ObservableList<String> filteredItems = originalItems.filtered(item ->
                        item.toLowerCase().contains(newText.toLowerCase()));
                searchBar.setItems(filteredItems);

                // Show the dropdown menu
                searchBar.show();
            }
        });

        // Ensure the editor and selected item are synchronized
        searchBar.valueProperty().addListener((_, _, newVal) -> {
            if (newVal != null && !newVal.equals(editor.getText())) {
                editor.setText(newVal);
            }
        });

        searchBar.setValue(" ");
        Platform.runLater(() -> paused = false);
    }
}
