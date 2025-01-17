package org.example.kittyfy;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;

public class SoundEffects {
    public enum kittySounds {
        PAUSE,
        PLAY,
        SELECT
    }

    public static MediaPlayer mediaPlayer;

    private static ArrayList<String> soundEffectArray;
    private static int arraySize = 0;

    public static void readAllEffects() throws Exception {
        soundEffectArray = new ArrayList<>();

        soundEffectArray.add("pauseKitty.wav");
        soundEffectArray.add("playKitty.wav");
        soundEffectArray.add("selectKitty.wav");
    }


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

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                mediaPlayer.stop();
                if (soundEffect.equals(kittySounds.PLAY)) {
                    HelloController.mediaPlayer.play();
                }
            }
        });
    }
}
