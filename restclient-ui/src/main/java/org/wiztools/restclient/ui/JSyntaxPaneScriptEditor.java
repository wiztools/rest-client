package org.wiztools.restclient.ui;

import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

/**
 *
 * @author subwiz
 */
public class JSyntaxPaneScriptEditor implements ScriptEditor {
    
    private JEditorPane jep = new JEditorPane();
    private JScrollPane jsp = new JScrollPane(jep);
    
    public JSyntaxPaneScriptEditor(TextEditorSyntax syntax){
        jsyntaxpane.DefaultSyntaxKit.initKit();
        if(syntax == TextEditorSyntax.GROOVY){
            jep.setContentType("text/groovy");
        }
        else if(syntax == TextEditorSyntax.XML){
            jep.setContentType("text/xml");
        }
        else if(syntax == TextEditorSyntax.JSON){
            jep.setContentType("text/javascript");
        }
    }
    
    public void setSyntax(TextEditorSyntax syntax){
        String text = jep.getText();
        Font f = jep.getFont();
        if(syntax == TextEditorSyntax.GROOVY){
            jep.setContentType("text/groovy");
        }
        else if(syntax == TextEditorSyntax.XML){
            jep.setContentType("text/xml");
        }
        else if(syntax == TextEditorSyntax.JSON){
            jep.setContentType("text/javascript");
        }
        else if(syntax == TextEditorSyntax.DEFAULT){
            jep.setContentType("text/plain");
        }
        jep.validate();
        jep.setText(text);
        jep.setCaretPosition(0);
        jep.setFont(f);
    }

    @Override
    public JComponent getEditorView() {
        return jsp;
    }

    @Override
    public JTextComponent getEditorComponent() {
        return jep;
    }

    @Override
    public String getText() {
        return jep.getText();
    }

    @Override
    public void setText(String text) {
        jep.setText(text);
    }

    @Override
    public void setCaretPosition(int offset) {
        jep.setCaretPosition(offset);
    }

    @Override
    public void setEditable(boolean editable) {
        jep.setEditable(editable);
    }

}
