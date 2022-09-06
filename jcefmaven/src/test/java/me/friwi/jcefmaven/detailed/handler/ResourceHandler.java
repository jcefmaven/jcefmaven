package me.friwi.jcefmaven.detailed.handler;

import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.nio.ByteBuffer;

public class ResourceHandler extends CefResourceHandlerAdapter {
    private static final String html = new String("<html>\n"
            + "  <head>\n"
            + "    <title>ResourceHandler Test</title>\n"
            + "  </head>\n"
            + "  <body>\n"
            + "    <h1>ResourceHandler Test</h1>\n"
            + "    <p>You have entered the URL: http://www.foo.bar. This page is generated by the application itself and<br/>\n"
            + "       no HTTP request was sent to the internet.\n"
            + "    <p>See class <u>tests.handler.ResourceHandler</u> and the <u>RequestHandler</u> implementation for details.</p>\n"
            + "  </body>\n"
            + "</html>");
    private int startPos = 0;

    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        System.out.println("processRequest: " + request);

        startPos = 0;
        callback.Continue();
        return true;
    }

    @Override
    public void getResponseHeaders(
            CefResponse response, IntRef response_length, StringRef redirectUrl) {
        System.out.println("getResponseHeaders: " + response);

        response_length.set(html.length());
        response.setMimeType("text/html");
        response.setStatus(200);
    }

    @Override
    public boolean readResponse(
            byte[] data_out, int bytes_to_read, IntRef bytes_read, CefCallback callback) {
        int length = html.length();
        if (startPos >= length) return false;

        // Extract up to bytes_to_read bytes from the html data
        int endPos = startPos + bytes_to_read;
        String dataToSend =
                (endPos > length) ? html.substring(startPos) : html.substring(startPos, endPos);

        // Copy extracted bytes into data_out and set the read length
        // to bytes_read.
        ByteBuffer result = ByteBuffer.wrap(data_out);
        result.put(dataToSend.getBytes());
        bytes_read.set(dataToSend.length());

        startPos = endPos;
        return true;
    }

    @Override
    public void cancel() {
        System.out.println("cancel");
        startPos = 0;
    }
}
