package com.spotify.storage;

import com.spotify.player.Song;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;

public class InMemoryStorageTest {
    private Storage storage;

    @Before
    public void setup() {
        storage = new InMemoryStorage();
    }

    @Test
    public void doesUserExistWhenHeDoesNot() {
        boolean actual = storage.doesUserExist("mail", "password");
        assertFalse("User should not exist", actual);
    }

    @Test
    public void getSongByFullNameWhenSongExist() {
        Song song = new Song("test", "song");
        storage.addSong(song);
        Song actual = storage.getSongByFullName("test - song");
        assertEquals(song, actual);
    }

    @Test
    public void getSongByFullNameWhenSongDoesNotExist() {
        Song actual = storage.getSongByFullName("song - test");
        assertNull(actual);
    }
}
