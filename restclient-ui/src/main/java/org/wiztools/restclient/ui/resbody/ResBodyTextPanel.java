package org.wiztools.restclient.ui.resbody;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.ServiceLocator;
import org.wiztools.restclient.persistence.XMLException;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.ui.*;
import org.wiztools.restclient.util.HTMLIndentUtil;
import org.wiztools.restclient.util.HttpUtil;
import org.wiztools.restclient.util.JSONUtil;
import org.wiztools.restclient.util.XMLIndentUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import org.wiztools.restclient.ui.component.BodyPopupMenu;
import org.wiztools.restclient.ui.component.BodyPopupMenuListener;

/**
 *
 * @author subwiz
 */
public class ResBodyTextPanel extends AbstractResBody implements FontableEditor {
    @Inject RESTView view;
    
    // Response
    private final ScriptEditor se_response = ScriptEditorFactory.getXMLScriptEditor();
    
    private BodyPopupMenu bodyPopupMenu;
    
    @PostConstruct
    protected void init() {
        se_response.setEditable(false);
        
        // First the pop-up menu for xml formatting:
        BodyPopupMenuListener listener = new BodyPopupMenuListener() {
            @Override
            public void onSuccess(String msg) {
                view.setStatusMessage(msg);
            }

            @Override
            public void onFailure(String msg) {
                view.setStatusMessage(msg);
            }

            @Override
            public void onMessage(String msg) {
                view.setStatusMessage(msg);
            }
        };
        final BodyPopupMenu bpm = new BodyPopupMenu(se_response, listener, true);
        this.bodyPopupMenu = bpm;
        
        // Attach popup menu
        if (se_response.getEditorComponent() instanceof RSyntaxTextArea) {
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
                        bpm.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
        
        // Layout
        setBorder(BorderFactory.createEmptyBorder());
        setLayout(new GridLayout());
        add(se_response.getEditorView());
    }
    
    @Override
    public void setEditorFont(Font font) {
        se_response.getEditorComponent().setFont(font);
    }

    @Override
    public Font getEditorFont() {
        return se_response.getEditorComponent().getFont();
    }

    @Override
    public void setBody(byte[] body, ContentType type) {
        // Call super method
        super.setBody(body, type);
        
        bodyPopupMenu.cancelRunningJob();
        
        // JSON or XML?
        final boolean isXml = HttpUtil.isXmlContentType(type.getContentType());
        final boolean isJson = HttpUtil.isJsonContentType(type.getContentType());
        final boolean isHTML = HttpUtil.isHTMLContentType(type.getContentType());
        
        // Get the options:
        IGlobalOptions options = ServiceLocator.getInstance(IGlobalOptions.class);
        
        // Set syntax color:
        if(options.isPropertyTrue("response.body.syntax.color")) {
            if(isXml) {
                se_response.setSyntax(TextEditorSyntax.XML);
            }
            if(isJson) {
                se_response.setSyntax(TextEditorSyntax.JSON);
            }
            if(HttpUtil.isJsContentType(type.getContentType())) {
                se_response.setSyntax(TextEditorSyntax.JS);
            }
            if(HttpUtil.isCssContentType(type.getContentType())) {
                se_response.setSyntax(TextEditorSyntax.CSS);
            }
            if(isHTML) {
                se_response.setSyntax(TextEditorSyntax.HTML);
            }
        }
        else { // No syntax!
            se_response.setSyntax(TextEditorSyntax.NONE);
        }
        
        // Find if you need to indent
        final String responseBody = new String(getBody(), HttpUtil.getCharsetDefault(type));
        if(options.isPropertyTrue("response.body.indent")) {
            if(isXml){
                try{
                    String indentedResponseBody = XMLIndentUtil.getIndented(responseBody);
                    se_response.setText(indentedResponseBody);
                }
                catch(IOException | XMLException ex){
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
                    se_response.setText(responseBody);
                }
            }
            else if(isHTML){
                try{
                    String indentedResponseBody = HTMLIndentUtil.getIndented(responseBody);
                    se_response.setText(indentedResponseBody);
                }
                catch(Exception ex){
                    view.setStatusMessage("HTML indentation failed.");
                    se_response.setText(responseBody);
                }
            }
            else{
                view.setStatusMessage("Response body neither XML,HTML nor JSON. No indentation.");
                se_response.setText(responseBody);
            }
        }
        else { // No indentation
            se_response.setText(responseBody);
        }
        se_response.setCaretPosition(0);
    }
    
    @Override
    public void clearUI() {
        se_response.setText("");
    }
}
