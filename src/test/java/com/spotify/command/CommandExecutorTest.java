package com.spotify.command;

import com.spotify.player.MusicPlayer;
import com.spotify.player.Playlist;
import com.spotify.player.Song;
import com.spotify.storage.InMemoryStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CommandExecutorTest {
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

    private static final Command REGISTER_VLADO = new Command(REGISTER, new String[]{"Vlado", "123"});
    private static final Command REGISTER_PETAR = new Command(REGISTER, new String[]{"Petar", "123"});
    private static final Command REGISTER_INVALID = new Command(REGISTER, new String[]{"asd#@asd", "123"});
    private static final Command LOGIN_VLADO = new Command(LOGIN, new String[]{"Vlado", "123"});
    private static final Command LOGIN_PETAR = new Command(LOGIN, new String[]{"Petar", "123"});
    private static final Command LOGIN_INVALID = new Command(LOGIN, new String[]{"Vlado", "1234"});
    private static final Command LOGOUT_COMMAND = new Command(LOGOUT, new String[]{});
    private static final Command DISCONNECT_COMMAND = new Command(DISCONNECT, new String[]{});

    private CommandExecutor executor;

    @Mock
    private InMemoryStorage storage;

    @Before
    public void setup() {
        executor = new CommandExecutor(storage, new MusicPlayer());
    }

    @Test
    public void testRegisterWithValidCredentials() {
        // When
        String expected = "Email Vlado successfully registered";
        when(storage.addUser("Vlado", "123")).thenReturn(true);
        String actual = executor.execute(REGISTER_VLADO);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testRegisterWithExistingUser() {
        // Given
        when(storage.addUser("Vlado", "123")).thenReturn(false);

        // When
        String expected = "Email Vlado is already taken, select another one";
        executor.execute(REGISTER_VLADO);
        String actual = executor.execute(REGISTER_VLADO);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testLoginWithValidCredentials() {
        // Given
        when(storage.doesUserExist("Vlado", "123")).thenReturn(true);

        // When
        String expected = "User Vlado successfully logged in";
        String actual = executor.execute(LOGIN_VLADO);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testLoginWithInvalidInput() {
        // When
        String expected = "Invalid username/password combination";
        String actual = executor.execute(LOGIN_INVALID);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testLogout() {
        // Given
        when(storage.doesUserExist("Vlado", "123")).thenReturn(true);

        // When
        executor.execute(LOGIN_VLADO);
        String expected = "Successfully logged out";
        String actual = executor.execute(LOGOUT_COMMAND);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testDisconnect() {
        // Given
        when(storage.doesUserExist("Vlado", "123")).thenReturn(true);

        // When
        executor.execute(LOGIN_VLADO);
        String expected = "Successfully disconnected";
        String actual = executor.execute(DISCONNECT_COMMAND);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testLogoutWhenNotLoggedIn() {
        // When
        String expected = "You are not logged in";
        String actual = executor.execute(LOGOUT_COMMAND);

        // Then
        assertEquals(expected, actual);
    }

    // The may fail
    @Test
    public void testSearchWithExistingSong() {
        // Given
        setupUser();
        Set<Song> songs = new HashSet<>();
        songs.add(new Song("song", "dummy"));
        songs.add(new Song("another", "dummy"));


        // When
        when(storage.getSongs()).thenReturn(songs);
        String expected = "[Song{songName='dummy', singersNames=[song]}, Song{songName='dummy', singersNames=[another]}]";
        String actual = executor.execute(new Command(SEARCH, new String[]{"dummy"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchWithNoExistingSong() {
        //Given
        setupUser();

        // When
        String expected = "The are no found songs";
        String actual = executor.execute(new Command(SEARCH, new String[]{"dummy"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testGetTopSongs() {
        // Given
        setupUser();
        Set<Song> songs = new HashSet<>();
        songs.add(new Song("song", "second", 5));
        songs.add(new Song("another", "third", 2));
        songs.add(new Song("onemore", "first", 7));

        // When
        when(storage.getSongs()).thenReturn(songs);
        String expected = "[Song{songName='first', singersNames=[onemore]}, Song{songName='second', singersNames=[song]}]";
        String actual = executor.execute(new Command(GET_TOP, new String[]{"2"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testGetTopSongsWithNegativeNumber() {
        // Given
        setupUser();

        // When
        String expected = "Please insert positive number";
        String actual = executor.execute(new Command(GET_TOP, new String[]{"-2"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testGetTopSongsWhenNoSongs() {
        // Given
        setupUser();

        // When
        String expected = "There are no songs";
        String actual = executor.execute(new Command(GET_TOP, new String[]{"1"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testCreatePlaylistWithNoExisting() {
        // Given
        setupUser();
        Playlist playlist = new Playlist("test");

        // When
        when(storage.addPlaylist(playlist)).thenReturn(true);
        String expected = "Playlist test successfully created";
        String actual = executor.execute(new Command(CREATE_PLAYLIST, new String[]{"test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testCreatePlaylistWithAlreadyExisting() {
        // Given
        setupUser();
        Playlist playlist = new Playlist("test");

        // When
        when(storage.addPlaylist(playlist)).thenReturn(false);
        String expected = "Playlist test is already existing";
        String actual = executor.execute(new Command(CREATE_PLAYLIST, new String[]{"test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testAddSongToPlaylistWithValidData() {
        // Given
        setupUser();
        Map<Song, Integer> songs = new HashMap<>();
        Song song = new Song("test", "test");
        songs.put(song, 0);
        Playlist playlist = new Playlist("test");

        // When
        when(storage.getSongByFullName("test - test")).thenReturn(song);
        when(storage.getPlaylistByName("test")).thenReturn(playlist);
        when(storage.addSongToPlaylist(playlist, song)).thenReturn(true);
        String expected = "Song test - test successfully added to playlist test";
        String actual = executor.execute(new Command(ADD_SONG_TO, new String[]{"test", "test - test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testAddSongToPlaylistWithNoExistingSong() {
        // Given
        setupUser();
        Map<Song, Integer> songs = new HashMap<>();
        Song song = new Song("test", "test");
        songs.put(song, 0);

        // When
        String expected = String.format("There is no song with name %s", song.getFullName());
        String actual = executor.execute(new Command(ADD_SONG_TO, new String[]{"test", "test - test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testAddSongToPlaylistWithNoExistingPlaylist() {
        // Given
        setupUser();
        Map<Song, Integer> songs = new HashMap<>();
        Song song = new Song("test", "test");
        songs.put(song, 0);
        Playlist playlist = new Playlist("test");

        // When
        when(storage.getSongByFullName("test - test")).thenReturn(song);
        String expected = String.format("There is no playlist with name %s", playlist.getName());
        String actual = executor.execute(new Command(ADD_SONG_TO, new String[]{"test", "test - test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testAddSongToPlaylistWithAlreadyExistingSongInPlaylist() {
        // Given
        setupUser();
        Map<Song, Integer> songs = new HashMap<>();
        Song song = new Song("test", "test");
        songs.put(song, 0);
        Playlist playlist = new Playlist("test");

        // When
        when(storage.getSongByFullName("test - test")).thenReturn(song);
        when(storage.getPlaylistByName("test")).thenReturn(playlist);
        when(storage.addSongToPlaylist(playlist, song)).thenReturn(false);
        String expected = "Song test - test is already in playlist test";
        String actual = executor.execute(new Command(ADD_SONG_TO, new String[]{"test", "test - test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testShowPlaylistWithExistingPlaylist() {
        // Given
        setupUser();
        Song song = new Song("test", "test");
        Playlist playlist = new Playlist("test");
        playlist.addSong(song);

        // When
        when(storage.getPlaylistByName("test")).thenReturn(playlist);
        String expected = "Playlist{name='test', songs=[Song{songName='test', singersNames=[test]}]}";
        String actual = executor.execute(new Command(SHOW_PLAYLIST, new String[]{"test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testShowPlaylistWithNoExistingPlaylist() {
        // Given
        setupUser();

        // When
        when(storage.getPlaylistByName("test")).thenReturn(null);
        String expected = "There is no playlist test";
        String actual = executor.execute(new Command(SHOW_PLAYLIST, new String[]{"test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testPlayPlaylistWithExistingPlaylist() {
        // Given
        setupUser();
        Song song = new Song("test", "test");
        Playlist playlist = new Playlist("test");
        playlist.addSong(song);

        // When
        when(storage.getPlaylistByName("test")).thenReturn(playlist);
        String expected = "Playlist test was successfully played";
        String actual = executor.execute(new Command(PLAY_PLAYLIST, new String[]{"test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testPlayPlaylistWithNoExistingPlaylist() {
        // Given
        setupUser();

        // When
        String expected = "There are no playlist with name test";
        String actual = executor.execute(new Command(PLAY_PLAYLIST, new String[]{"test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testPlayPlaylistWithoutSongs() {
        // Given
        setupUser();
        Playlist playlist = new Playlist("test");

        // When
        when(storage.getPlaylistByName("test")).thenReturn(playlist);
        String expected = "There are no songs in playlist test";
        String actual = executor.execute(new Command(PLAY_PLAYLIST, new String[]{"test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testPlayWithExistingSong() {
        // Given
        setupUser();
        Song song = new Song("test", "test");

        // When
        when(storage.getSongByFullName("test - test")).thenReturn(song);
        String expected = String.format("Song %s was successfully played", song.getFullName());
        String actual = executor.execute(new Command(PLAY, new String[]{"test - test"}));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void testPlayWithNoExistingSong() {
        // Given
        setupUser();

        // When
        String expected = "There is no song test - test";
        String actual = executor.execute(new Command(PLAY, new String[]{"test - test"}));

        // Then
        assertEquals(expected, actual);
    }
    private void setupUser() {
        when(storage.doesUserExist("Vlado", "123")).thenReturn(true);
        executor.execute(LOGIN_VLADO);
    }
}
