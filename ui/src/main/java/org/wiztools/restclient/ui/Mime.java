package org.wiztools.restclient.ui;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;

public final class Mime {
    /**
     * This is the most accurate way to determine the content-type.
     * @param file
     * @return
     */
    public static String forFile(File file) {
        try {
            return Files.probeContentType(file.toPath());
        } catch(IOException ex) {
            return "application/octet-stream";
        }
    }

    /**
     * This is a less accurate implementation of determining the content-type.
     * @param name
     * @return
     */
    public static String forFileName(String name) {
        return URLConnection.guessContentTypeFromName(name);
    }
}
