package org.wiztools.restclient.ui.update;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.wiztools.appupdate.Version;
import org.wiztools.appupdate.VersionImpl;
import org.wiztools.appupdate.VersionUrl;
import org.wiztools.appupdate.VersionWSUtil;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.Versions;

/**
 *
 * @author subwiz
 */
public class AppUpdateRunner implements Runnable {
    
    @Inject private IGlobalOptions options;
    
    private static final Logger LOG = Logger.getLogger(AppUpdateRunner.class.getName());
    
    private static final String UPDATE_URL = "http://static.wiztools.org/v/restclient.json";
    
    private static final String PROP_UPDATE_CHECK_LAST = "update.check.last";
    private static final String PROP_UPDATE_CHECK_ENABLED = "update.check.enabled";
    static final long TIME_GAP = 604800000l; // 1 week in millis
    
    private void openUrl(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
            }
            catch(URISyntaxException | IOException ex) {
                LOG.log(Level.INFO, "Error when opening browser", ex);
            }
        }
    }
    
    boolean doUpdateCheck(long lastUpdateCheck) {
        if((lastUpdateCheck + TIME_GAP) < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }
    
    boolean requiresUpdate(Version latestVersion) {
        if(latestVersion.isGreaterThan(new VersionImpl(Versions.CURRENT))) {
            return true;
        }
        return false;
    }
    
    private VersionUrl data;
    
    boolean requiresUpdate() throws IOException {
        data = VersionWSUtil.getLatestVersion(UPDATE_URL);
        final Version latestVersion = data.getVersion();
        if(requiresUpdate(latestVersion)) {
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        
        // Verify if the run needs to happen:
        final String strEnabled = options.getProperty(PROP_UPDATE_CHECK_ENABLED);
        if(strEnabled != null && strEnabled.equals("false")) {
            return;
        }
        final String strLastRun = options.getProperty(PROP_UPDATE_CHECK_LAST);
        if(strLastRun != null) {
            final long lastRun = Long.parseLong(strLastRun);
            if(!doUpdateCheck(lastRun)) {
                return;
            }
        }
        
        // Verify version:
        try {
            if(requiresUpdate()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        final int opt = JOptionPane.showConfirmDialog(null,
                                "An updated version of RESTClient is available. Do you want to download?",
                                "Download update?",
                                JOptionPane.YES_NO_OPTION);
                        if(opt == JOptionPane.YES_OPTION) {
                            openUrl(data.getDlUrl());
                        }
                    }
                });
            }
            
            // Record last update verification time:
            options.setProperty(PROP_UPDATE_CHECK_LAST,
                    String.valueOf(System.currentTimeMillis()));
        }
        catch(IOException ex) {
            LOG.log(Level.INFO, "Cannot perform update check...", ex);
        }
    }
    
}
