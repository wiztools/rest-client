package org.wiztools.restclient.ui;

import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import jsyntaxpane.DefaultSyntaxKit;

/**
 *
 * @author subwiz
 */
class JSyntaxPaneScriptEditor implements ScriptEditor {
    
    private JEditorPane jep = new JEditorPane();
    private JScrollPane jsp = new JScrollPane(jep);
    
    JSyntaxPaneScriptEditor(TextEditorSyntax syntax){
        DefaultSyntaxKit.initKit();
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

    public JComponent getEditorView() {
        return jep;
    }

    public JComponent getScrollableEditorView(){
        return jsp;
    }

    public String getText() {
        return jep.getText();
    }

    public void setText(String text) {
        jep.setText(text);
    }

    public void setCaretPosition(int offset) {
        jep.setCaretPosition(offset);
    }

    public void setEditable(boolean editable) {
        jep.setEditable(editable);
    }

}
