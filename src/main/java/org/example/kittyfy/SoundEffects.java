package org.example.kittyfy;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;

//Adds sound effects to the specific buttons.
public class SoundEffects {
    public enum kittySounds {
        PAUSE,
        PLAY,
        SELECT
    }

    public static MediaPlayer mediaPlayer;

    private static ArrayList<String> soundEffectArray;

    /**
     * Arraylist with all the different sound effects.
     */
    public static void readAllEffects() {
        soundEffectArray = new ArrayList<>();

        soundEffectArray.add("pauseKitty.wav");
        soundEffectArray.add("playKitty.wav");
        soundEffectArray.add("selectKitty.wav");
    }

    /**
     * Plays sound effect when using the buttons in the media player, sets volume of the sound effect to be the same.
     * Sets delay on the music file so the sound effect does not play over the music.
     * @param soundEffect adds sound effects to the specific buttons.
     */
    public static void play(kittySounds soundEffect) {
        try {
            Media media = new Media(new File("src/main/resources/sound effects/" + soundEffectArray.get(soundEffect.ordinal())).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
        }
        catch (Exception e) {
            System.out.println("Sound effect could not be found");
        }

        if (soundEffect.equals(kittySounds.PAUSE)) {
            mediaPlayer.setVolume(0.1);
        }
        mediaPlayer.play();

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
            if (soundEffect.equals(kittySounds.PLAY)) {
                HelloController.mediaPlayer.play();
            }
        });
    }
}
