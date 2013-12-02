package org.wiztools.restclient.util;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.SourceFormatter;
import java.io.StringWriter;

/**
 * @author yeradis
 */
public final class HTMLIndentUtil {
    private HTMLIndentUtil() {
    }

    public static String getIndented(String inHTML) {
        String formated_html = null;
        try {
            StringWriter writer = new StringWriter();
            new SourceFormatter(new Source(inHTML)).setIndentString("\t").setTidyTags(true).setCollapseWhiteSpace(true).writeTo(writer);
            formated_html = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formated_html;
    }

}
