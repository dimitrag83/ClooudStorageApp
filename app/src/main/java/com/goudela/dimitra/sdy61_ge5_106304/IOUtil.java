package com.goudela.dimitra.sdy61_ge5_106304;


import java.io.Closeable;
import java.io.IOException;

public class IOUtil {

    /*
     * @param closeable
     */
    public static void forceClose(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (IOException e) {
            // ignore
        }
    }

}
