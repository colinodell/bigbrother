package net.hcfactions.core.util;

import java.io.*;

public class FileUtils {

    public static void copy(InputStream source, File destination) throws IOException
    {
        if(source == null)
            return;

        OutputStream out = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int length;
        while((length = source.read(buffer)) > 0)
        {
            out.write(buffer, 0, length);
        }
        out.close();
        source.close();
    }
}
