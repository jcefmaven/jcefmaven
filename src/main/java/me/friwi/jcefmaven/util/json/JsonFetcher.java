package me.friwi.jcefmaven.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Util to fetch json elements from urls
 *
 * @author Fritz Windisch
 */
public class JsonFetcher {
    public static JsonElement retrieve(String url, int page) throws IOException {
        URL u = new URL(url.replace("{page}", String.valueOf(page)));
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        try {
            return JsonParser.parseReader(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            InputStream err = conn.getErrorStream();
            if (err == null) throw e;
            else return JsonParser.parseReader(new InputStreamReader(err));
        }
    }
}
