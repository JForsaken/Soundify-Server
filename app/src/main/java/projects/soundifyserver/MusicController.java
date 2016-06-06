package projects.soundifyserver;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

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

        File folder = new File("/storage/emulated/0/Music/");
        File[] listOfFiles = folder.listFiles();
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {

                metadataRetriever.setDataSource(listOfFiles[i].getPath());

                try {
                    String title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    String composer = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
                    String genre = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
                    String album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                    Song currentSong = new Song(i, listOfFiles[i].getPath(), title, artist, composer, genre, album, duration);
                    _songList.add(currentSong);
                    _songOrder.add(i);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
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

    public Song play(boolean isStreaming) {
        if (!isStreaming) {
            _mediaPlayer.start();
        }
        return _songList.get(_songOrder.get(_currentIndex));
    }

    public void pause(boolean isStreaming) {
        if (!isStreaming) {
            _mediaPlayer.pause();
        }
    }

    public void stop(boolean isStreaming) {
        if (!isStreaming) {
            _mediaPlayer.stop();
        }
    }

    public void shuffle() {
        randomizeOrder();
    }

    public void loop() {
        _isPlaylistLooping = !_isPlaylistLooping;
    }

    public Song next(boolean isStreaming) {
        return changeSong(true, false, isStreaming);
    }

    public Song previous(boolean isStreaming) {

        return changeSong(false, false, isStreaming);
    }

    private Song changeSong(boolean isForward, boolean autoPlay, boolean isStreaming) {
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

        if (!isStreaming) {
            _mediaPlayer.pause();
            _mediaPlayer.reset();
            prepareSong(_songList.get(_songOrder.get(_currentIndex)));
            if (isPlaying || autoPlay) {
                _mediaPlayer.start();
            }
        }
        return _songList.get(_songOrder.get(_currentIndex));
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
            else { changeSong(true, true, true); }
        }
    }
}