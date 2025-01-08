package org.example.kittyfy;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Song {

    private String title;
    private String artist;
    private int genre;
    private String filePath;
    private int songID;

    public Song(String title, int genre, String filePath) {
        this.title = title;
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

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
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
