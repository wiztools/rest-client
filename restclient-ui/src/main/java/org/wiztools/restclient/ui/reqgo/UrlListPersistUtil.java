package org.wiztools.restclient.ui.reqgo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.util.ConfigUtil;

/**
 *
 * @author subwiz
 */
public final class UrlListPersistUtil {
    
    private static final File LIST_FILE = ConfigUtil.getConfigFile("urls.list");

    private UrlListPersistUtil() {}
    
    
    public static void persist(List<String> urlList) throws IOException {
        StringBuilder sb = new StringBuilder();
        for(String url: urlList) {
            sb.append(url).append("\n");
        }
        FileUtil.writeString(LIST_FILE, sb.toString(), Charsets.UTF_8);
    }
    
    public static List<String> load() throws IOException {
        if(!LIST_FILE.exists()) {
            return Collections.EMPTY_LIST;
        }
        else {
            List<String> out = new ArrayList<String>();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(LIST_FILE)));
            String line;
            while((line = br.readLine()) != null) {
                if(!line.trim().equals("")) { // ignore empty line
                    out.add(line);
                }
            }
            
            return out;
        }
    }
}
