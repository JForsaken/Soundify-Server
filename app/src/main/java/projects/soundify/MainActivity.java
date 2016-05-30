package projects.soundify;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Server _server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        _server = new Server();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(_server != null) {
           // _server.stop();
        }
    }
}
