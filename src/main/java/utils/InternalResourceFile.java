/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016-2017 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */

package utils;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by tauraamui on 27/12/2016.
 */
public class InternalResourceFile {

    private String path = "";

    public InternalResourceFile(String path) {
        this.path = path;
    }

    public String getPath() { return path; }

    public InputStream getInputStream() {
        return InternalResourceFile.class.getResourceAsStream(path);
    }

    public List<File> getInternalFolderFiles() {
        ArrayList<File> files = new ArrayList<>();
        try {

            URI uri = InternalResourceFile.class.getResource(path).toURI();
            Path objPath;

            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                objPath = fileSystem.getPath(path);
            } else {
                objPath = Paths.get(uri);
            }

            Stream<Path> walk = Files.walk(objPath, 1);

            for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
                files.add(new File(it.next().toString()));
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return files;
    }
}
