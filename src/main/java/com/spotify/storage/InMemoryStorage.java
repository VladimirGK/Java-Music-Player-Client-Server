package com.spotify.storage;

import com.spotify.player.Playlist;
import com.spotify.player.Song;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class InMemoryStorage implements Storage {
    private static final Logger logger = LogManager.getLogger(InMemoryStorage.class);
    private static final String USERS_DIR = "src/main/resources/Users/users.txt";
    private static final String SONGS_DIR = "src/main/resources/Songs";
    private static final String PLAYLISTS_DIR = "src/main/resources/Playlists";

    private static Map<String, String> users = new HashMap<>();
    private static Set<Song> songs = new HashSet<>();
    private static Set<Playlist> playlists = new HashSet<>();

    static {
        readUsers();
        readSongs();
        readRating();
        readPlaylists();
    }

    private static void readSongs() {
        try {
            File songsFolder = new File(SONGS_DIR);
            for (File f : Objects.requireNonNull(songsFolder.listFiles())) {
                String fileName = f.getName();
                String singerName = fileName.substring(0, fileName.indexOf("-") - 1);
                String songName = fileName.substring(fileName.indexOf("-") + 2, fileName.lastIndexOf("."));
                Song currentSong = new Song(singerName, songName);
                if (!songs.contains(currentSong)) {
                    songs.add(currentSong);
                }
            }
        } catch (NullPointerException e) {
            logger.error("Could not read songs from folder");
        }
    }

    private static void readRating() {
        try {
            FileInputStream fileIn = new FileInputStream(SONGS_DIR.concat("Rating.txt"));
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Set<Song> listOfSongs = (HashSet<Song>) objectIn.readObject();
            songs.addAll(listOfSongs);
            objectIn.close();
        } catch (FileNotFoundException e) {
            logger.error("File for reading rating not found");
        } catch (IOException e) {
            logger.error("Could not read rating from file");
        } catch (ClassNotFoundException e) {
            logger.error("Class song not found");
        }
    }

    private static void readUsers() {
        try {
            FileInputStream fileIn = new FileInputStream(USERS_DIR);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Map<String, String> loadedUsers = (HashMap<String, String>) objectIn.readObject();
            users.putAll(loadedUsers);
            objectIn.close();
        } catch (FileNotFoundException e) {
            logger.error("Clients file not found");
        } catch (IOException e) {
            logger.error("Clients error initializing stream");
        } catch (ClassNotFoundException e) {
            logger.error("Clients class not found");
        }
    }

    private static void readPlaylists() {
        try {
            File playlistsFolder = new File(PLAYLISTS_DIR);
            for (File f : Objects.requireNonNull(playlistsFolder.listFiles())) {
                FileInputStream fileIn = new FileInputStream(f);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                Playlist playlist = (Playlist) objectIn.readObject();
                playlists.add(playlist);
            }
        } catch (NullPointerException e) {
            logger.error("No playlist files in directory");
        } catch (FileNotFoundException e) {
            logger.error("Playlist file not found");
        } catch (IOException e) {
            logger.error("Playlist error initializing stream");
        } catch (ClassNotFoundException e) {
            logger.error("Class not found");
        }
    }

    @Override
    public boolean addUser(String email, String password) {
        if (users.containsKey(email)) {
            return false;
        } else {
            users.put(email, DigestUtils.sha256Hex(password));
            writeUsers();
            return true;
        }
    }

    @Override
    public void deleteUser(String email) {
        users.remove(email);
        writeUsers();
    }

    private void writeUsers() {
        try {
            FileOutputStream fileOut = new FileOutputStream(USERS_DIR, false);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(users);
            objectOut.close();
        } catch (FileNotFoundException e) {
            logger.error("File not found when writing users");
        } catch (IOException e) {
            logger.error("Could not write users to file");
        }
    }

    @Override
    public boolean addPlaylist(Playlist playlist) {
        if (!playlists.contains(playlist)) {
            playlists.add(playlist);
            File file = new File(PLAYLISTS_DIR.concat("/").concat(playlist.getName()).concat(".txt"));
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addSongToPlaylist(Playlist playlist, Song song) {
        if (playlist.addSong(song)) {
            writePlaylist(playlist);
            return true;
        }
        return false;
    }

    private void writePlaylist(Playlist playlist) {
        try {
            String path = PLAYLISTS_DIR.concat("/").concat(playlist.getName()).concat(".txt");
            FileOutputStream fileOut = new FileOutputStream(path, false);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(playlist);
            objectOut.close();
        } catch (FileNotFoundException e) {
            logger.error("File not found when writing playlist");
        } catch (IOException e) {
            logger.error("Could not write playlist to file");
        }
    }

    @Override
    public boolean doesUserExist(String email, String password) {
        if (!users.containsKey(email)) {
            return false;
        } else {
            return users.get(email).equals(DigestUtils.sha256Hex(password));
        }
    }

    @Override
    public Set<Song> getSongs() {
        return songs;
    }

    @Override
    public Song getSongByFullName(String songFullName) {
        for (Song song : songs) {
            if (song.getFullName().equals(songFullName)) {
                return song;
            }
        }
        return null;
    }

    @Override
    public void updateSongRating(Song song) {
        song.incrementRating();
        writeRating();
    }

    private void writeRating() {
        try {
            FileOutputStream fileOut = new FileOutputStream(SONGS_DIR.concat("Rating.txt"), false);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(songs);
            objectOut.close();
        } catch (FileNotFoundException e) {
            logger.error("File not found when writing rating");
        } catch (IOException e) {
            logger.error("Could not write rating to file");
        }
    }

    @Override
    public Playlist getPlaylistByName(String playlistName) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(playlistName)) {
                return playlist;
            }
        }
        return null;
    }

    @Override
    public void addSong(Song song) { // Only used for testing purposes, not good idea, but it is what it is
        songs.add(song);
    }
}
