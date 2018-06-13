package com.isorasoft.mllibrary.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by MaiNam on 4/21/2016.
 */
public class CloseUtils {
    public static boolean closeStream(Closeable stream) {
        try {
            if (stream != null)
                stream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();

        }
        return false;
    }
}
