package utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tauraamui on 27/12/2016.
 */
public class InternalResourceFile {

    private String path = "";

    public InternalResourceFile(String path) {
        this.path = path;
        System.out.println(getInputStream());
    }

    public InputStream getInputStream() {
        return InternalResourceFile.class.getResourceAsStream(path);
    }
}
