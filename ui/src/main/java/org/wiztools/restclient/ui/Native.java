package org.wiztools.restclient.ui;

public final class Native {
    public static final boolean isMac;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        isMac = osName.startsWith("mac");
    }

    static {
        if(isMac) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
    }
}
