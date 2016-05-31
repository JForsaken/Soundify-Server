package projects.soundify;

/**
 * Created by jderrico on 16-05-29.
 */

import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


public class Server extends NanoHTTPD {

    private static final String MIME_JSON = "application/json";
    private static final int PORT = 8080;

    MusicController _musicController = MusicController.getInstance();

    public Server() {
        super(PORT);
        _musicController.init();

        try {
            start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            Method method = session.getMethod();
            String uri = session.getUri();
            Map<String, String> parms = session.getParms();
            String responseString = serve(session, uri, method, parms);
            return new NanoHTTPD.Response(Response.Status.OK, MIME_JSON, responseString);

        } catch (IOException ioe) {
            return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
        } catch (ResponseException re) {
            return new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
        } catch (NotFoundException nfe) {
            return new NanoHTTPD.Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
        } catch (Exception ex) {
            return new Response(Response.Status.INTERNAL_ERROR, MIME_HTML, "<html><body><h1>Error</h1>" + ex.toString() + "</body></html>");
        }
    }

    private String serve(IHTTPSession session, String uri, Method method, Map<String, String> parms)  throws IOException, ResponseException {
        String responseString = "";
        do {
            if(Method.GET.equals(method)) {
                responseString = handleGet(session, parms);
                break;
            }

            if(Method.POST.equals(method)) {
                responseString = handlePost(session);
                break;
            }

            throw new Resources.NotFoundException();

        } while(false);

        return responseString;
    }

    private String handleGet(IHTTPSession session, Map<String, String> parms) {
        final String baseUri = session.getUri().substring(1).split("/")[0].toLowerCase();
        String response = "This was a no effect GET";

        Song song = null;
        switch(baseUri) {
            case "play":
                song = _musicController.play();
                response = serializer(song);
                break;
            case "pause":
                _musicController.pause();
                response = "Has paused a song";
                break;
            case "next":
                song = _musicController.next();
                response = serializer(song);
                break;
            case "previous":
                song = _musicController.previous();
                response = serializer(song);
                break;
            case "shuffle":
                _musicController.shuffle();
                response = "Shuffling playlist";
                break;
            case "loop":
                _musicController.repeat();
                response = "looping playlist";
                break;
        }
        return response;
    }

    private String handlePost(IHTTPSession session) throws IOException, ResponseException {
        Map<String, String> files = new HashMap<String, String>();
        session.parseBody(files);

        return "WAS A POST";
    }

    private String serializer(Song song) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(song);//Java­>JSON

        return json;
    }


    private class NotFoundException extends RuntimeException {
    }

}