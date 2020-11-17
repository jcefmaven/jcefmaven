package me.friwi.jcefmaven.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Small util to read from InputStreams
 *
 * @author Fritz Windisch
 */
public class InputStreamUtils {
    public static int readInputStreamToByteArray(InputStream in, byte[] data) throws IOException {
        int r = 0;
        int pos = 0;
        while ((r = in.read(data, pos, data.length - pos)) > 0) pos += r;
        in.close();
        return pos;
    }

    public static String readInputStreamToString(InputStream in, int limit, Charset charset) throws IOException {
        byte[] data = new byte[limit];
        int length = readInputStreamToByteArray(in, data);
        return new String(data, 0, length, charset);
    }
}