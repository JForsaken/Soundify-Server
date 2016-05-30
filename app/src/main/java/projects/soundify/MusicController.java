package projects.soundify;

import android.media.MediaPlayer;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jderrico on 16-05-17.
 */
public class MusicController {
    private static MusicController _instance = new MusicController();

    private ArrayList<Song> _songList = new ArrayList<Song>();
    private ArrayList<Integer> _songOrder  = new ArrayList<Integer>();
    private int _currentIndex = 0;

    MediaPlayer _mediaPlayer = new MediaPlayer();
    private boolean _isPlaylistLooping = false;
    private boolean _isSongLooping = false;

    public static MusicController getInstance() {
        return _instance;
    }

    private MusicController() {
    }

    public void init() {
        _songList.clear();
        _songOrder.clear();
        _mediaPlayer.reset();
        _currentIndex = 0;

        File folder = new File("/storage/sdcard/Download");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                AudioFile f = null;
                try {
                    f = AudioFileIO.read(listOfFiles[i]);
                } catch (CannotReadException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TagException e) {
                    e.printStackTrace();
                } catch (ReadOnlyFileException e) {
                    e.printStackTrace();
                } catch (InvalidAudioFrameException e) {
                    e.printStackTrace();
                }

                Tag tag = f.getTag();

                String title = tag.getFirst(FieldKey.TITLE);
                String artist = tag.getFirst(FieldKey.ARTIST);
                String composer = tag.getFirst(FieldKey.COMPOSER);
                String genre = tag.getFirst(FieldKey.GENRE);
                String album = tag.getFirst(FieldKey.ALBUM);

                Song currentSong = new Song(i, listOfFiles[i].getPath(), title, artist, composer, genre, album);
                _songList.add(currentSong);
                _songOrder.add(i);
            }
        }

        // adds a listener to the song completion event
        _mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                performOnSongEnd();
            }
        });

        prepareSong(_songList.get(0));
    }

    public void play() {
        _mediaPlayer.start();
    }

    public void pause() {
        _mediaPlayer.pause();
    }

    public void shuffle() {
        randomizeOrder();
    }

    public void repeat() {
        _isSongLooping = !_isSongLooping;
    }

    public void next() {
        changeSong(true, false);
    }

    public void previous() {
        changeSong(false, false);
    }

    private void changeSong(boolean isForward, boolean autoPlay) {
        final boolean isPlaying = _mediaPlayer.isPlaying();

        if (isForward) {
            if (_currentIndex == _songList.size() - 1) {
                _currentIndex = 0;
            } else {
                _currentIndex++;
            }
        }
        else {
            if (_currentIndex == 0) {
                _currentIndex = _songList.size() - 1;
            } else {
                _currentIndex--;
            }
        }

        _mediaPlayer.pause();
        _mediaPlayer.reset();
        prepareSong(_songList.get(_songOrder.get(_currentIndex)));
        if (isPlaying || autoPlay) { _mediaPlayer.start(); }
    }

    private void prepareSong(Song song) {
        try {
            _mediaPlayer.setDataSource(song.getPath());
            _mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void randomizeOrder() {
        final int currentSong = _songOrder.get(_currentIndex);

        _songOrder.clear();
        for (int i = 0; i < _songList.size(); i++) {
            if (i != currentSong) { _songOrder.add(i); }
        }
        Collections.shuffle(_songOrder);

        // start the order with the currently playing song
        _songOrder.add(0, currentSong);
    }

    private void performOnSongEnd() {
        if (_isSongLooping) {
            _mediaPlayer.pause();
            _mediaPlayer.reset();
            prepareSong(_songList.get(_songOrder.get(_currentIndex)));
        }
        else {
            // if the playlist ends
            if (!_isPlaylistLooping && _currentIndex == _songList.size() - 1) {
                _mediaPlayer.pause();
                _mediaPlayer.reset();
                _currentIndex = 0;
            }
            else { changeSong(true, true); }
        }
    }
}