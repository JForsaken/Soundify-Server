package projects.applicationtest;
import java.util.ArrayList;
/**
 * Created by jderrico on 16-05-17.
 */
public class HttpController {
    private static HttpController _instance = new HttpController();

    private ArrayList<Song> _songList;
    private int _currentIndex = 0;

    public static HttpController getInstance() {
        return _instance;
    }

    private HttpController() {
    }
    
    public void init() {
        // build song list
    }

    public void play() {

    }

    public void stop() {

    }

    public void shuffle() {

    }

    public void repeat() {

    }

    public void next() {

    }

    public void previous() {

    }
}
