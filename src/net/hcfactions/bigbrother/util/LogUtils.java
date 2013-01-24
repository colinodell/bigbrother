package net.hcfactions.bigbrother.util;

public class LogUtils {
    public static String getStackTrace(Exception ex)
    {
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement el : ex.getStackTrace())
        {
            sb.append(el.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
