package me.friwi.jcefmaven.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Iterator that fetches a paginated json array from a remote url.
 * Only fetches further pages when there is demand.
 *
 * @author Fritz Windisch
 */
public class RemoteJsonIterator implements Iterator<JsonElement> {
    private String url;
    private Queue<JsonElement> jsonElementQueue;
    private boolean finished = false;
    private int page = 1;

    public RemoteJsonIterator(String url) {
        this.url = url;
        jsonElementQueue = new LinkedTransferQueue<>();
    }

    @Override
    public boolean hasNext() {
        if (jsonElementQueue.isEmpty()) {
            try {
                fillBuffer();
            } catch (Exception e) {
                e.printStackTrace();
                finished = true;
            }
        }
        return !finished;
    }

    @Override
    public JsonElement next() {
        if (!hasNext()) throw new NoSuchElementException();
        return jsonElementQueue.poll();
    }

    private void fillBuffer() throws IOException {
        JsonElement el = JsonFetcher.retrieve(url, page);
        if (el.isJsonArray()) {
            JsonArray arr = el.getAsJsonArray();
            for (JsonElement j : arr) {
                if (!j.isJsonNull()) jsonElementQueue.add(j);
            }
            if (jsonElementQueue.isEmpty()) finished = true;
            page++;
        } else {
            throw new IOException("Invalid content received from " + url);
        }
    }
}
