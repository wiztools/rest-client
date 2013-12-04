package org.wiztools.restclient.util;

import java.io.IOException;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.SourceFormatter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yeradis
 */
public final class HTMLIndentUtil {
    
    private static final Logger LOG = Logger.getLogger(HTMLIndentUtil.class.getName());

    private HTMLIndentUtil() {
    }

    public static String getIndented(String inHTML) {
        String formated_html = null;
        try {
            StringWriter writer = new StringWriter();
            new SourceFormatter(new Source(inHTML)).setIndentString("    ").setTidyTags(true).setCollapseWhiteSpace(true).writeTo(writer);
            formated_html = writer.toString();
        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return formated_html;
    }

}
