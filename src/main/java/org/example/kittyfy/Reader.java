package org.example.kittyfy;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Reader {
    public static ArrayList<Song> readAllSongs() throws Exception {

        File directory = new File("src/main/resources/music");

        File[] files = directory.listFiles();
        ArrayList<String> songsFileNames = new ArrayList<>(20);

        for (File file : files) {
            songsFileNames.add("\"" + file.getName().trim() + "\"");
            songsFileNames.add(file.getName());

        }

        ArrayList<Song> songs = new ArrayList<>(songsFileNames.size());


        for (String fileName : songsFileNames) {
            if (!fileName.endsWith(".wav\"")) { continue; }
            fileName = fileName;
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
