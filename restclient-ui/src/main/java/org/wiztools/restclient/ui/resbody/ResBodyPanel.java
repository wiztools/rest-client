package org.wiztools.restclient.ui.resbody;

import org.wiztools.restclient.util.JSONUtil;
import org.wiztools.restclient.util.HttpUtil;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import org.wiztools.restclient.*;
import org.wiztools.restclient.ui.*;

/**
 *
 * @author subwiz
 */
public class ResBodyPanel extends AbstractResBody {
    @Inject RESTView view;
    
    // Response
    private ScriptEditor se_response;
    {
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        final boolean enableSyntaxColoring = Boolean.valueOf(
                options.getProperty("response.body.syntax.color")==null?
                    "true": options.getProperty("response.body.syntax.color"));
        if(enableSyntaxColoring) {
            se_response = ScriptEditorFactory.getXMLScriptEditor();
        }
        else {
            se_response = ScriptEditorFactory.getTextAreaScriptEditor();
        }
    }
    
    private void actionTextEditorSyntaxChange(final ScriptEditor editor, final TextEditorSyntax syntax){
        ((JSyntaxPaneScriptEditor)editor).setSyntax(syntax);
    }
    
    @PostConstruct
    protected void init() {
        se_response.setEditable(false);
        
        // First the pop-up menu for xml formatting:
        final JPopupMenu popupMenu = new JPopupMenu();
        
        JMenu jm_indent = new JMenu("Indent");
        
        // Indent XML
        JMenuItem jmi_indentXml = new JMenuItem("Indent XML");
        jmi_indentXml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String resText = se_response.getText();
                if("".equals(resText.trim())){
                    view.setStatusMessage("No response body!");
                    return;
                }
                try {
                    final String indentedXML = XMLUtil.indentXML(resText);
                    se_response.setText(indentedXML);
                    se_response.setCaretPosition(0);
                    view.setStatusMessage("Indent XML: Success");
                } catch (XMLException ex) {
                    view.setStatusMessage("Indent XML: XML Parser Configuration Error.");
                } catch (IOException ex) {
                    view.setStatusMessage("Indent XML: IOError while processing XML.");
                }
            }
        });
        jm_indent.add(jmi_indentXml);
        
        // Indent JSON
        JMenuItem jmi_indentJson = new JMenuItem("Indent JSON");
        jmi_indentJson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String resText = se_response.getText();
                if("".equals(resText.trim())){
                    view.setStatusMessage("No response body!");
                    return;
                }
                try{
                    String indentedJSON = JSONUtil.indentJSON(resText);
                    se_response.setText(indentedJSON);
                    se_response.setCaretPosition(0);
                    view.setStatusMessage("Indent JSON: Success");
                }
                catch(JSONUtil.JSONParseException ex){
                    view.setStatusMessage("Indent JSON: Not a valid JSON text.");
                }
            };
        });
        jm_indent.add(jmi_indentJson);
        
        popupMenu.add(jm_indent);
        
        // Syntax color change
        JMenu jm_syntax = new JMenu("Syntax Color");
        JMenuItem jmi_syntax_xml = new JMenuItem("XML");
        jmi_syntax_xml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_response, TextEditorSyntax.XML);
            }
        });
        jm_syntax.add(jmi_syntax_xml);
        JMenuItem jmi_syntax_json = new JMenuItem("JSON");
        jmi_syntax_json.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_response, TextEditorSyntax.JSON);
            }
        });
        jm_syntax.add(jmi_syntax_json);
        JMenuItem jmi_syntax_none = new JMenuItem("None");
        jmi_syntax_none.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                actionTextEditorSyntaxChange(se_response, TextEditorSyntax.DEFAULT);
            }
        });
        jm_syntax.add(jmi_syntax_none);
        
        popupMenu.add(jm_syntax);
        
        // Attach popup menu
        if (se_response.getEditorComponent() instanceof JEditorPane) {
            se_response.getEditorComponent().addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }
                private void showPopup(final MouseEvent e) {
                    if("".equals(se_response.getText().trim())){
                        // No response body
                        return;
                    }
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
        
        // Layout
        setBorder(BorderFactory.createEmptyBorder());
        setLayout(new GridLayout());
        add(se_response.getEditorView());
    }
    
    public void setEditorFont(Font font) {
        se_response.getEditorComponent().setFont(font);
    }

    @Override
    public void setBody(byte[] body, ContentType type) {
        // Call super method
        super.setBody(body, type);
        
        // Find if you need to indent
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        String indentStr = options.getProperty("response.body.indent");
        boolean indent = indentStr==null? false: (indentStr.equals("true")? true: false);
        if(indent) {
            boolean isXml = false;
            boolean isJson = false;
            if(HttpUtil.isXmlContentType(type.getContentType())){
                isXml = true;
            }
            else if(HttpUtil.isJsonContentType(type.getContentType())){
                isJson = true;
            }
            final String responseBody = new String(getBody(), type.getCharset());
            if(isXml){
                try{
                    String indentedResponseBody = XMLUtil.indentXML(responseBody);
                    se_response.setText(indentedResponseBody);
                }
                catch(IOException ex){
                    view.setStatusMessage("XML indentation failed.");
                    // LOG.warning(ex.getMessage());
                    se_response.setText(responseBody);
                }
                catch(XMLException ex){
                    view.setStatusMessage("XML indentation failed.");
                    // LOG.warning(ex.getMessage());
                    se_response.setText(responseBody);
                }
            }
            else if(isJson){
                try{
                    String indentedResponseBody = JSONUtil.indentJSON(responseBody);
                    se_response.setText(indentedResponseBody);
                }
                catch(JSONUtil.JSONParseException ex){
                    view.setStatusMessage("JSON indentation failed.");
                    // LOG.warning(ex.getMessage());
                    se_response.setText(responseBody);
                }
            }
            else{
                view.setStatusMessage("Response body neither XML nor JSON. No indentation.");
                se_response.setText(responseBody);
            }
        }
        else { // No indentation
            se_response.setText(new String(getBody(), type.getCharset()));
        }
        se_response.setCaretPosition(0);
    }
    
    @Override
    public void clearBody() {
        se_response.setText("");
    }
}
