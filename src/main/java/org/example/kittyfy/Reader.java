package org.example.kittyfy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * a class which reads the song object into the database
 */
public class Reader {

    /**
     * Method to read all songs from the song file to the database
     * @return
     */
    public static ArrayList<Song> readAllSongs() {

        File directory = new File("src/main/resources/music");

        File[] files = directory.listFiles();
        ArrayList<String> songsFileNames = new ArrayList<>(20);

        assert files != null;
        for (File file : files) {
            songsFileNames.add("\"" + file.getName().trim() + "\"");
            songsFileNames.add(file.getName());
        }

        ArrayList<Song> songs = new ArrayList<>(songsFileNames.size());


        for (String fileName : songsFileNames) {
            if (!fileName.endsWith(".wav\"") && !fileName.endsWith(".mp3\"")) { continue; } // If the file doesn't end on either .wav or mp3 it is skipped.
            String[] rows = fileName.split(" - ");
            String songName = rows[0].substring(1);

            if (Song.doesSongExist(songName)) {
                songs.add(Song.getSong(songName));
                continue;
            }

            ArrayList<String> artists = new ArrayList<>(3);
            artists.addAll(Arrays.asList(rows[1].split(",")));

            String genre = rows[2].split("\\.")[0];

            if (!Song.doesGenreExist(genre))
            {
                Song.createGenre(genre);
            }

            Song curSong = new Song(songName, artists, genre, fileName.substring(1, fileName.length() - 1));
            curSong.setSongID(Song.createSong(curSong));
            songs.add(curSong);
        }

        return songs;
    }
}
