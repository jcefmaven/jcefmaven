package me.friwi.jcefmaven.util;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Util to print a progress bar on the console.
 * Code stolen from: https://stackoverflow.com/questions/1001290/console-based-progress-in-java
 */
public class ProgressPrinter {
    public static void printProgress(long startTime, long total, long current, double printDivisor, String printUnit) {
        long eta = current == 0 ? 0 :
                (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A" :
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string
                .append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %s/%s, ETA: %s", String.format("%.1f", current / printDivisor) + printUnit,
                        String.format("%.1f", total / printDivisor) + printUnit, etaHms));

        System.out.print(string);
    }
}
