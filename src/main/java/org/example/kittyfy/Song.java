package org.example.kittyfy;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Song {

    private String title;
    private String artist;
    private String genre;
    private String filePath;
    private int songID;

    public Song(String title, String genre, String filePath) {
        this.title = title;
        this.genre = genre;
        this.filePath = filePath;
    }

    public Song(String title, String artist, String genre, String filePath) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }


}

