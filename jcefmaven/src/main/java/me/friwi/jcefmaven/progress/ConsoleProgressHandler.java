package me.friwi.jcefmaven.progress;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleProgressHandler implements IProgressHandler {
    private static final Logger LOGGER = Logger.getLogger(ConsoleProgressHandler.class.getName());

    @Override
    public void handleProgress(EnumProgress state, float percent) {
        Objects.requireNonNull(state, "state cannot be null");
        if(percent!=-1f && (percent<0f || percent>100f)){
            throw new RuntimeException("percent has to be -1f or between 0f and 100f. Got "+percent+" instead");
        }
        LOGGER.log(Level.INFO, state + " |> "+(percent==-1f?"In progress...":percent));
    }
}
