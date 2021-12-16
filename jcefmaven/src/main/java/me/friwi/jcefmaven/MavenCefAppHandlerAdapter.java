package me.friwi.jcefmaven;

import org.cef.CefApp;
import org.cef.callback.CefCommandLine;
import org.cef.handler.CefAppHandlerAdapter;

/**
 * An extendable implementation of CefAppHandlerAdapter that fixes execution
 * issues on MacOSX. Prevents the method "onBeforeCommandLineProcessing" to
 * be overridden. If you need to evaluate arguments, do that before you pass
 * them to the builder.
 *
 * @author Fritz Windisch
 */
public abstract class MavenCefAppHandlerAdapter extends CefAppHandlerAdapter {
    public MavenCefAppHandlerAdapter() {
        super(null);
    }

    @Override
    public final void onBeforeCommandLineProcessing(String process_type, CefCommandLine command_line) {
        CefApp.getInstance().onBeforeCommandLineProcessing(process_type, command_line);
    }
}
