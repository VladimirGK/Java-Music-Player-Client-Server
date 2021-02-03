package com.spotify.player;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class MusicPlayer implements Runnable {
    private static final Logger logger = LogManager.getLogger(MusicPlayer.class);
    private static final int BUFFER_SIZE = 128_000;

    private File songFile;
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;
    private volatile boolean running = true;
    private Song song = new Song("Ujen Vqtar", "Bataliona");

    public synchronized void play(Song song) {
        this.song = song;
        this.running = true;
        new Thread(this).start();
    }

    public void stop() {
        this.running = false;
        sourceDataLine.drain();
        sourceDataLine.stop();
    }

    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                try {
                    System.out.println("Playing the song...");
                    songFile = song.getSongFile();
                    audioInputStream = AudioSystem.getAudioInputStream(songFile);
                    audioFormat = audioInputStream.getFormat();
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
                    sourceDataLine.open(audioFormat);
                    sourceDataLine.start();
                    byte[] bytesBuffer = new byte[BUFFER_SIZE];
                    int bytesRead = -1;
                    while (running && (bytesRead = audioInputStream.read(bytesBuffer)) != -1) {
                        sourceDataLine.write(bytesBuffer, 0, bytesRead);
                    }
                    this.running = false;
                } catch (LineUnavailableException e) {
                    logger.error("Line is unavailable");
                } catch (IOException e) {
                    logger.error("IOException is thrown");
                } catch (UnsupportedAudioFileException e) {
                    logger.error("Audio format is unsupported");
                } finally {
                    if (audioInputStream != null) {
                        try {
                            audioInputStream.close();
                        } catch (IOException e) {
                            logger.error("Could not close audi input stream");
                        }
                    }
                }
            }
        }
    }
}
