package org.example.kittyfy;

import java.time.Instant;
import java.util.ArrayList;

public class Playlist {

    private int playlistId;
    private String name;
    private long lastPlayed;
    private ArrayList<Song> songs;

    public long getLastPlayed() {
        return lastPlayed;
    }

    /**
     * Updates the lastPlayed time to the current epoch second.
     */
    public void updateLastPlayed() {
        setLastPlayed(Instant.now().getEpochSecond());
    }

    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public Playlist(String name, ArrayList<Song> songs) {
        this.name = name;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }
}
