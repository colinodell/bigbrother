package net.hcfactions.core.log;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.LogRecord;

public class EnhancedLogger extends PluginLogger {

    public EnhancedLogger(Plugin context) {
        super(context);
    }

    @Override
    public void log(LogRecord logRecord)
    {
        //getServer().getConsole().sendMessage() ??
        logRecord.setMessage(fixColors(logRecord.getMessage()));
        super.log(logRecord);
    }

    public void logException(Exception ex)
    {
        this.warning(ex.getMessage());
        this.info(getStackTrace(ex));
    }

    // Eventually I'd like this to show the colors instead of stripping them
    private static String fixColors(String str)
    {
        return ChatColor.stripColor(str);
    }

    /**
     * Gets a string representation of a stack trace
     * @param ex The exception containing the stack trace
     * @return Stack trace as a multi-line String
     */
    private static String getStackTrace(Exception ex)
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
