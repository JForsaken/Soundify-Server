package projects.soundifyserver;

/**
 * Created by jderrico on 16-05-17.
 */
public class Song {
    private int id;
    private String path;

    private String title;
    private String artist;
    private String composer;
    private String genre;
    private String album;
    private String duration;


    public Song(int songID, String songPath, String songTitle, String songArtist, String songComposer, String songGenre, String songAlbum, String songDuration) {
        id = songID;
        path = songPath;

        title = songTitle;
        artist = songArtist;
        composer = songComposer;
        genre = songGenre;
        album = songAlbum;
        duration = songDuration;
    }

    public int getID(){return id;}
    public String getPath(){return path;}

    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getComposer(){return composer;}
    public String getGenre(){return genre;}
    public String getAlbum(){return album;}
    public String getDuration(){return duration;}
}
