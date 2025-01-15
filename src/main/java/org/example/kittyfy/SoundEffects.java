package org.example.kittyfy;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SoundEffects {
    public enum kittySounds {
        PAUSE,
        PLAY,
        STOP,
        SELECT
    }

    private static ArrayList<String> soundEffectArray;
    private static int arraySize = 0;

    public static void readAllEffects() throws Exception {
        soundEffectArray = new ArrayList<>();

        soundEffectArray.add("pauseKitty.wav");
    }


    public static void play(kittySounds soundEffect) throws Exception {
        Media media = new Media(new File("src/main/resources/sound effects/" + soundEffectArray.get(soundEffect.ordinal())).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }
}
