package com.spotify.storage;

import com.spotify.player.Playlist;
import com.spotify.player.Song;

import java.util.Set;

public interface Storage {
    boolean addUser(String email, String password);
    void deleteUser(String email);
    boolean addPlaylist(Playlist playlist);
    boolean addSongToPlaylist(Playlist playlist, Song song);
    boolean doesUserExist(String email, String password);
    Set<Song> getSongs();
    Song getSongByFullName(String songFullName);
    void updateSongRating(Song song);
    Playlist getPlaylistByName(String playlistName);
    void addSong(Song song);
}
