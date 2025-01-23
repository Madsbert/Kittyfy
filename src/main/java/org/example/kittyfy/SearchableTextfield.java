package org.example.kittyfy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchableTextfield {

    /**
     * Initializes a searchbar to allow for searching for songs and getting suggestions and matches.
     * @param searchBar the searchbar that contains the search text.
     * @param listView listview to contain matches and suggestions.
     * @param allSongs all song that are available to search for.
     */
    public static void initializeSearchBar(TextField searchBar, ListView<String> listView, ArrayList<Song> allSongs) {
        // Original list to preserve all items
        ObservableList<String> originalItems = FXCollections.observableArrayList();
        HashMap<String, Song> songHashMap = new HashMap<>();

        // Populate the original list
        for (Song song : allSongs) {
            ArrayList<String> trimmedArtists = new ArrayList<>();
            for (String artist : song.getArtist()) {
                trimmedArtists.add(artist.trim());
            }
            String artists = String.join(", ", trimmedArtists);
            String item = song.getTitle().trim() + " by " + artists;
            originalItems.add(item);
            songHashMap.put(item, song);
        }

        // Create a ListView for displaying suggestions
        listView.setItems(originalItems);
        listView.setVisible(false);

        // Add a listener to the textProperty of the TextField
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                // Hide the suggestion list when the search field is empty
                listView.setVisible(false);
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
        listView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                try {
                    // Regex search to find the song title from the event target
                    Pattern pattern = Pattern.compile("\"([^\"]+)\"");
                    Matcher matcher = pattern.matcher(event.getTarget().toString());
                    if (matcher.find()) {
                        searchBar.setText(matcher.group(1));
                        MainController.getInstance().playSong(songHashMap.get(searchBar.getText()), false);
                    }
                    else
                    {
                        System.out.println("No match found from listview : initializeSearchBar onMousePressed");
                    }
                }
                catch (Exception e) {
                    System.out.println("Error with RegexPattern in initializeSearchBar : onMousePressed");
                }
                listView.setVisible(false);
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

