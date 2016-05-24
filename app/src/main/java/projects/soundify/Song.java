package projects.applicationtest;

/**
 * Created by jderrico on 16-05-17.
 */
public class Song {
    private long _id;
    private String _title;
    private String _artist;

    public Song(long songID, String songTitle, String songArtist) {
        _id = songID;
        _title = songTitle;
        _artist = songArtist;
    }

    public long getID(){return _id;}
    public String getTitle(){return _title;}
    public String getArtist(){return _artist;}
}
