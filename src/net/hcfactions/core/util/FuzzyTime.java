package net.hcfactions.core.util;

public class FuzzyTime {

    public static String durationFromSeconds(long seconds)
    {
        return String.format("%dh:%02dm:%02ds", seconds/3600, (seconds%3600)/60, (seconds%60));
    }
}
