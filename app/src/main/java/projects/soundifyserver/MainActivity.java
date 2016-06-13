package projects.soundifyserver;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

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

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        byte[] ipAddress = BigInteger.valueOf(wm.getConnectionInfo().getIpAddress()).toByteArray();

        try {
            InetAddress myaddr = InetAddress.getByAddress(ipAddress);
            StringTokenizer hostaddr = new StringTokenizer(myaddr.getHostAddress(), ".");
            String ipaddr = "";

            while (hostaddr.hasMoreTokens()) {
                ipaddr = hostaddr.nextToken()+ "." + ipaddr;
            }

            TextView txtIP = (TextView) findViewById(R.id.txtIP);
            txtIP.setText(ipaddr);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }


        ListView lstView = (ListView) findViewById(R.id.lstvSongsTitle);
        String[] songs =  MusicController.getInstance().getSongNameList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, songs);

        lstView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(_server != null) {
           // _server.stop();
        }
    }
}
