package org.example.kittyfy;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Song {

    private String title;
    private String artist;
    private String genre;
    private String filePath;

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

    public double getDuration() throws UnsupportedAudioFileException, IOException {
        // Get audio format and frame length
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
        AudioFormat format = audioInputStream.getFormat();
        //get amount of frames
        long frames = audioInputStream.getFrameLength();
        //get framerate
        float frameRate = format.getFrameRate();
        // Calculate the duration of the audio in seconds, and returns it as a double.
        return frames / frameRate;
    }
}
