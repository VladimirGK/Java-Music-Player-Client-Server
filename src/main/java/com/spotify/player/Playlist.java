package com.spotify.player;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Playlist implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234L;
    private final String name;
    private Set<Song> songs = new HashSet<>();

    public Playlist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public boolean addSong(Song song) {
        if (!songs.contains(song)) {
            songs.add(song);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(name, playlist.name) && Objects.equals(songs, playlist.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, songs);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "name='" + name + '\'' +
                ", songs=" + songs +
                '}';
    }
}
