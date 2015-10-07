package org.wiztools.restclient.ui.component;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.persistence.XMLException;
import org.wiztools.restclient.ui.ScriptEditor;
import org.wiztools.restclient.ui.TextEditorSyntax;
import org.wiztools.restclient.util.HTMLIndentUtil;
import org.wiztools.restclient.util.JSONUtil;
import org.wiztools.restclient.util.XMLIndentUtil;

/**
 *
 * @author subhash
 */
public class BodyPopupMenu extends JPopupMenu {
    
    private BackgroundFormatterJob job = new BackgroundFormatterJob();
    
    public BodyPopupMenu(final ScriptEditor se,
            final BodyPopupMenuListener listener,
            final boolean isSeparateThread) {
        super();

        // Syntax Format:
        JMenu jm_format = new JMenu("Format");
        
        { // XML:
            JMenuItem jmi_fmt_xml = new JMenuItem("XML");
            jmi_fmt_xml.addActionListener((ActionEvent e) -> {
                final String unformatted = se.getText();
                if(StringUtil.isEmpty(unformatted)) {
                    listener.onFailure("Body is empty.");
                    return;
                }
                Runnable r = () -> {
                    try {
                        final String out = XMLIndentUtil.getIndented(unformatted);
                        se.setText(out);
                        listener.onSuccess("Formatted successfully.");
                    }
                    catch(IOException | XMLException ex) {                    
                        listener.onFailure("Formatting error: " + ex.getMessage());
                    }
                };
                job.run(r, listener, isSeparateThread);
            });
            jm_format.add(jmi_fmt_xml);
        }
        
        { // JSON:
            JMenuItem jmi_fmt_json = new JMenuItem("JSON");
            jmi_fmt_json.addActionListener((ActionEvent e) -> {
                final String unformatted = se.getText();
                if(StringUtil.isEmpty(unformatted)) {
                    listener.onFailure("Body is empty.");
                    return;
                }
                Runnable r = () -> {
                    try {
                        final String out = JSONUtil.indentJSON(unformatted);
                        se.setText(out);
                        listener.onSuccess("Formatted successfully.");
                    }
                    catch(JSONUtil.JSONParseException ex) {
                        listener.onFailure("Formatting error: " + ex.getMessage());
                    }
                };
                job.run(r, listener, isSeparateThread);
            });
            jm_format.add(jmi_fmt_json);
        }
        
        { // HTML:
            JMenuItem jmi_fmt_html = new JMenuItem("HTML");
            jmi_fmt_html.addActionListener((ActionEvent e) -> {
                final String unformatted = se.getText();
                if(StringUtil.isEmpty(unformatted)) {
                    listener.onFailure("Body is empty.");
                    return;
                }
                Runnable r = () -> {
                    try {
                        final String out = HTMLIndentUtil.getIndented(unformatted);
                        se.setText(out);
                        listener.onSuccess("Formatted successfully.");
                    }
                    catch(Exception ex) {
                        listener.onFailure("Formatting error: " + ex.getMessage());
                    }
                };
                job.run(r, listener, isSeparateThread);
            });
            jm_format.add(jmi_fmt_html);
        }
        
        this.add(jm_format);
        
        // Syntax Highlight:
        JMenu jm_syntax = new JMenu("Syntax Color");
        
        { // None:
            JMenuItem jmi_syntax_none = new JMenuItem("None");
            jmi_syntax_none.addActionListener((ActionEvent evt) -> {
                se.setSyntax(TextEditorSyntax.NONE);
            });
            jm_syntax.add(jmi_syntax_none);
        }
        
        { // XML:
            JMenuItem jmi_syntax_xml = new JMenuItem("XML");
            jmi_syntax_xml.addActionListener((ActionEvent evt) -> {
                se.setSyntax(TextEditorSyntax.XML);
            });
            jm_syntax.add(jmi_syntax_xml);
        }
     
        { // JSON:
            JMenuItem jmi_syntax_json = new JMenuItem("JSON");
            jmi_syntax_json.addActionListener((ActionEvent evt) -> {
                se.setSyntax(TextEditorSyntax.JSON);
            });
            jm_syntax.add(jmi_syntax_json);
        }
        
        { // HTML:
            JMenuItem jmi_syntax_html = new JMenuItem("HTML");
            jmi_syntax_html.addActionListener((ActionEvent evt) -> {
                se.setSyntax(TextEditorSyntax.HTML);
            });
            jm_syntax.add(jmi_syntax_html);
        }
        
        this.add(jm_syntax);
    }
    
    public void cancelRunningJob() {
        job.cancelRunningJob();
    }
}
