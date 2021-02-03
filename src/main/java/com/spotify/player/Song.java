package com.spotify.player;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Song implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234L;
    private final static String SONGS_DIR = "src/main/resources/Songs/";

    private final String songName;
    private Set<String> singersNames = new HashSet<>();
    private final File songFile;
    private final String fullName;
    private int rating = 0;

    public Song(String singersNames, String songName) {
        setSingersNames(singersNames);
        this.songName = songName;
        this.songFile = new File(SONGS_DIR + singersNames + " - " + songName + ".wav");
        this.fullName = singersNames + " - " + songName;
    }

    public Song(String singersNames, String songName, int rating) { // Needed for testing purpose
        this(singersNames, songName);
        this.rating = rating;
    }

    private void setSingersNames(String singers) {
        String[] names = singers.split(" ft. ");
        this.singersNames.addAll(Arrays.asList(names));
    }

    public void incrementRating() {
        rating++;
    }

    public int getRating() {
        return rating;
    }

    public String getSongName() {
        return songName;
    }

    public Set<String> getSingersNames() {
        return singersNames;
    }

    public File getSongFile() {
        return songFile;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(songName, song.songName) && Objects.equals(singersNames, song.singersNames) && Objects.equals(songFile, song.songFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(songName, singersNames, songFile);
    }

    @Override
    public String toString() {
        return "Song{" +
                "songName='" + songName + '\'' +
                ", singersNames=" + singersNames +
                '}';
    }
}
