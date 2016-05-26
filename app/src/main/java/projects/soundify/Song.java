package projects.soundify;

/**
 * Created by jderrico on 16-05-17.
 */
public class Song {
    private int _id;
    private String _path;

    private String _title;
    private String _artist;
    private String _composer;
    private String _genre;
    private String _album;


    public Song(int songID, String songPath, String songTitle, String songArtist, String songComposer, String songGenre, String songAlbum) {
        _id = songID;
        _path = songPath;

        _title = songTitle;
        _artist = songArtist;
        _composer = songComposer;
        _genre = songGenre;
        _album = songAlbum;
    }

    public int getID(){return _id;}
    public String getPath(){return _path;}

    public String getTitle(){return _title;}
    public String getArtist(){return _artist;}
    public String getComposer(){return _composer;}
    public String getGenre(){return _genre;}
    public String getAlbum(){return _album;}
}
