package info.inpureprojects.core.Utils;

import org.apache.commons.compress.utils.IOUtils;

import java.io.*;

/**
 * Created by den on 7/25/2014.
 */
public class Streams {

    public static final Streams instance = new Streams();

    public InputStream getStream(File f) {
        try {
            return new FileInputStream(f);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public OutputStream getStreamOut(File f) {
        try {
            return new FileOutputStream(f);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public void IO(InputStream i, OutputStream o) {
        try {
            IOUtils.copy(i, o);
            this.close(i);
            this.close(o);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public InputStream getByteStream(byte[] bytes) {
        try {
            return new ByteArrayInputStream(bytes);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public void close(Closeable c) {
        try {
            c.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}