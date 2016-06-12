package projects.soundifyserver;

/**
 * Created by jderrico on 16-05-29.
 */

import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import projects.soundify.ActionName;
import projects.soundify.ActionType;


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

            if (uri.endsWith(".mp3")) {
                return new NanoHTTPD.Response(Response.Status.OK, "audio/mp3", new FileInputStream(uri));
            }

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
        final String uri = session.getUri().substring(1);
        final String actionType = uri.split("/")[0].toLowerCase();
        final String actionName = uri.split("/")[1].toLowerCase();


        final boolean isStreaming = ActionType.valueOf(actionType) == ActionType.stream;

        String response = "This was a no effect GET";

        Song song = null;

        switch(ActionName.valueOf(actionName)) {
            case play:
                song = _musicController.play(isStreaming);
                response = serializer(song);
                break;
            case pause:
                _musicController.pause(isStreaming);
                response = "Has paused a song";
                break;
            case stop:
                _musicController.stop(isStreaming);
                response = "Has stopped a song";
                break;
            case next:
                song = _musicController.next(isStreaming);
                response = serializer(song);
                break;
            case previous:
                song = _musicController.previous(isStreaming);
                response = serializer(song);
                break;
            case shuffle:
                _musicController.shuffle();
                response = "Shuffling playlist";
                break;
            case repeat:
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