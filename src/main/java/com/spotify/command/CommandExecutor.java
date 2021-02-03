package com.spotify.command;

import com.spotify.player.MusicPlayer;
import com.spotify.player.Playlist;
import com.spotify.player.Song;
import com.spotify.storage.InMemoryStorage;
import com.spotify.storage.Storage;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandExecutor {
    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
            "Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\"";
    private static final int INVALID_ARGS_COUNT_ONE_MESSAGE_FORMAT = 1;
    private static final int INVALID_ARGS_COUNT_TWO_MESSAGE_FORMAT = 2;
    private static final String NOT_LOGGED_IN = "You are not logged in";
    private static final String LOGIN = "login";
    private static final String REGISTER = "register";
    private static final String LOGOUT = "logout";
    private static final String DISCONNECT = "disconnect";
    private static final String SEARCH = "search";
    private static final String GET_TOP = "top";
    private static final String CREATE_PLAYLIST = "create-playlist";
    private static final String ADD_SONG_TO = "add-song-to";
    private static final String SHOW_PLAYLIST = "show-playlist";
    private static final String PLAY_PLAYLIST = "play-playlist";
    private static final String PLAY = "play";
    private static final String STOP = "stop";
    private static final String MANUAL = "man";

    private Storage storage;
    private final MusicPlayer musicPlayer;
    private String loggedUser = null;


    public CommandExecutor(InMemoryStorage storage, MusicPlayer musicPlayer) {
        this.storage = storage;
        this.musicPlayer = musicPlayer;
    }

    public String execute(Command command) {
        return switch (command.command()) {
            case LOGIN -> login(command.arguments());
            case REGISTER -> register(command.arguments());
            case LOGOUT -> logout();
            case DISCONNECT -> disconnect();
            case SEARCH -> search(command.arguments());
            case GET_TOP -> getTop(command.arguments());
            case CREATE_PLAYLIST -> createPlaylist(command.arguments());
            case ADD_SONG_TO -> addSongTo(command.arguments());
            case SHOW_PLAYLIST -> showPlaylist(command.arguments());
            case PLAY_PLAYLIST -> playPlaylist(command.arguments());
            case PLAY -> play(command.arguments());
            case STOP -> stop(command.arguments());
            case MANUAL -> showManual();
            default -> "Unknown command";
        };
    }

    private String login(String[] args) {
        if (args.length != 2) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, LOGIN, INVALID_ARGS_COUNT_TWO_MESSAGE_FORMAT, LOGIN + " <email> <password>");
        }

        String email = args[0];
        String password = args[1];

        if (storage.doesUserExist(email, password)) {
            loggedUser = email;
            return String.format("User %s successfully logged in", email);
        } else {
            return "Invalid email/password combination";
        }
    }

    private String register(String[] args) {
        if (args.length != 2) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, REGISTER, INVALID_ARGS_COUNT_TWO_MESSAGE_FORMAT, REGISTER + " <email> <password>");
        }

        String email = args[0];
        String password = args[1];

        if (storage.addUser(email, password)) {
            return String.format("User %s successfully registered", email);
        }
        return String.format("Email %s is already taken, select another one", email);

    }

    private String logout() {
        if (loggedUser == null) {
            return NOT_LOGGED_IN;
        } else {
            loggedUser = null;
            return "Successfully logged out";
        }
    }

    private String disconnect() {
        if (loggedUser == null) {
            return NOT_LOGGED_IN;
        } else {
            storage.deleteUser(loggedUser);
            loggedUser = null;
            return "Successfully disconnected";
        }

    }

    private String search(String[] args) {
        if (loggedUser == null) {
            return NOT_LOGGED_IN;
        }
        Set<Song> searchedSongs = new HashSet<>();
        for (String word : args) {
            for (Song song : storage.getSongs()) {
                if (song.getFullName().contains(word)) {
                    searchedSongs.add(song);
                }
            }
        }
        return searchedSongs.isEmpty() ? "The are no found songs" : searchedSongs.toString();
    }

    private String getTop(String[] args) {
        if (args.length != 1) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, GET_TOP, INVALID_ARGS_COUNT_ONE_MESSAGE_FORMAT, GET_TOP + " <number>");
        }
        if (loggedUser == null) {
            return NOT_LOGGED_IN;
        }
        int topOf;
        try {
            topOf = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return "Please insert a valid number";
        }
        if (topOf <= 0) {
            return "Please insert positive number";
        }
        List<Song> topSongs = storage.getSongs().stream()
                .sorted(Comparator.comparingInt(Song::getRating).reversed())
                .limit(topOf)
                .collect(Collectors.toList());
        return topSongs.isEmpty() ? "There are no songs" : topSongs.toString();
    }

    private String createPlaylist(String[] args) {
        if (args.length != 1) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, CREATE_PLAYLIST, INVALID_ARGS_COUNT_ONE_MESSAGE_FORMAT, CREATE_PLAYLIST + " <playlistName>");
        }
        if (loggedUser == null) {
            return NOT_LOGGED_IN;
        }

        String playlistName = args[0];
        Playlist playlist = new Playlist(playlistName);
        if (storage.addPlaylist(playlist)) {
            return String.format("Playlist %s successfully created", playlistName);
        }
        return String.format("Playlist %s is already existing", playlistName);
    }

    private String addSongTo(String[] args) {
        if (loggedUser == null) {
            return NOT_LOGGED_IN;
        }

        String playlistName = args[0];
        args = ArrayUtils.remove(args, 0);
        String songFullName = String.join(" ", args);
        Song song = storage.getSongByFullName(songFullName);
        if (song == null) {
            return String.format("There is no song with name %s", songFullName);
        }
        Playlist playlist = storage.getPlaylistByName(playlistName);
        if (playlist == null) {
            return String.format("There is no playlist with name %s", playlistName);
        }
        if (storage.addSongToPlaylist(playlist, song)) {
            return String.format("Song %s successfully added to playlist %s", songFullName, playlistName);
        }
        return String.format("Song %s is already in playlist %s", songFullName, playlistName);

    }

    private String showPlaylist(String[] args) {
        if (args.length != 1) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, SHOW_PLAYLIST, 1, SHOW_PLAYLIST + " <playlistName>");
        }
        if (loggedUser == null) {
            return NOT_LOGGED_IN;
        }

        String playlistName = args[0];
        Playlist playlist = storage.getPlaylistByName(playlistName);
        if (playlist == null) {
            return String.format("There is no playlist %s", playlistName);
        }
        if (playlist.getSongs().isEmpty()) {
            return String.format("There are no songs in playlist %s", playlistName);
        }
        return playlist.toString();
    }

    private String playPlaylist(String[] args) {
        if (args.length != 1) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, PLAY_PLAYLIST, 1, PLAY_PLAYLIST + " <playlistName>");
        }
        if (loggedUser == null) {
            return NOT_LOGGED_IN;
        }

        String playlistName = args[0];
        Playlist playlist = storage.getPlaylistByName(playlistName);
        if (playlist == null) {
            return String.format("There are no playlist with name %s", playlistName);
        }
        if (playlist.getSongs().isEmpty()) {
            return String.format("There are no songs in playlist %s", playlistName);
        }
        for (Song song : playlist.getSongs()) {
            musicPlayer.play(song);
        }
        return String.format("Playlist %s was successfully played", playlistName);
    }

    private String play(String[] args) {
        if (loggedUser == null) {
            return NOT_LOGGED_IN;
        }

        String songFullName = String.join(" ", args);
        Song song = storage.getSongByFullName(songFullName);
        if (song == null) {
            return String.format("There is no song %s", songFullName);
        }
        storage.updateSongRating(song);
        musicPlayer.play(song);
        return String.format("Song %s was successfully played", songFullName);
    }

    private String stop(String[] args) {
        if (loggedUser == null) {
            return NOT_LOGGED_IN;
        }
        try {
            musicPlayer.stop();
        } catch(NullPointerException e) {
            return "There is not song playing";
        }
        return "Music player successfully stopped";
    }

    private String showManual() {
        return """
                Spotify manual
                Available commands
                register(email, password) - register new client
                login(email, password) - login existing client
                disconnect() - disconnect a client from server
                search(words) - search for songs by keywords
                top(number) - print the (number) most listened songs
                create-playlist(name_of_playlist, song_name) - create new playlist
                show-playlist(name_of_playlist) - print songs of playlist
                play(song_name) - play song
                play-playlist(name_of_playlist) - play playlist
                stop() - stop playing song
                exit() - to exit
                """;
    }

}
