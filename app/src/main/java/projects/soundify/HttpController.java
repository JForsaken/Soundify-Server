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

/**
 * Created by jderrico on 16-05-17.
 */
public class HttpController {
    private static HttpController _instance = new HttpController();

    private ArrayList<Song> _songList = new ArrayList<Song>();
    MediaPlayer _mediaPlayer = new MediaPlayer();
    private int _currentIndex = 0;

    public static HttpController getInstance() {
        return _instance;
    }

    private HttpController() {
    }
    
    public void init() {
        _currentIndex = 0;
        _songList.clear();

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
            }
        }

        try {
            _mediaPlayer.setDataSource(_songList.get(0).getPath());
            _mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        _mediaPlayer.start();
    }

    public void stop() {
        _mediaPlayer.pause();
    }

    public void shuffle() {
        // TODO
    }

    public void repeat() {
        // TODO
    }

    public void next() {
        final boolean isPlaying = _mediaPlayer.isPlaying();

        if (_currentIndex == _songList.size() - 1) {
            _currentIndex = 0;
        } else {
            _currentIndex++;
        }

        try {
            _mediaPlayer.stop();
            _mediaPlayer.reset();
            _mediaPlayer.setDataSource(_songList.get(_currentIndex).getPath());
            _mediaPlayer.prepare();
            if (isPlaying) { _mediaPlayer.start(); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void previous() {
        final boolean isPlaying = _mediaPlayer.isPlaying();

        if (_currentIndex == 0) {
            _currentIndex = _songList.size() - 1;
        } else {
            _currentIndex--;
        }

        try {
            _mediaPlayer.stop();
            _mediaPlayer.reset();
            _mediaPlayer.setDataSource(_songList.get(_currentIndex).getPath());
            _mediaPlayer.prepare();
            if (isPlaying) { _mediaPlayer.start(); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
