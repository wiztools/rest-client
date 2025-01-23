package org.wiztools.restclient.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class Mime {
    public static String get(File file) {
        try {
            return Files.probeContentType(file.toPath());
        } catch(IOException ex) {
            return "application/octet-stream";
        }
    }
}
