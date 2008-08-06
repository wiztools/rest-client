/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.text.EditorKit;
import jsyntaxpane.SyntaxKit;

/**
 *
 * @author subwiz
 */
class JSyntaxPaneScriptEditor implements ScriptEditor {
    
    private JEditorPane jep = new JEditorPane();
    
    private static final String GROOVY = "groovy";
    private static final String XML = "xml";
    private static final String JSON = "javascript";
    
    private final EditorKit defaultEditorKit;
    
    private SyntaxKit groovySyntaxKit;
    private SyntaxKit xmlSyntaxKit;
    private SyntaxKit jsonSyntaxKit;
    
    JSyntaxPaneScriptEditor(TextEditorSyntax syntax){
        defaultEditorKit = jep.getEditorKit();
        if(syntax == TextEditorSyntax.GROOVY){
            groovySyntaxKit = new SyntaxKit(GROOVY);
            jep.setEditorKit(groovySyntaxKit);
        }
        else if(syntax == TextEditorSyntax.XML){
            xmlSyntaxKit = new SyntaxKit(XML);
            jep.setEditorKit(xmlSyntaxKit);
        }
        else if(syntax == TextEditorSyntax.JSON){
            jsonSyntaxKit = new SyntaxKit(JSON);
            jep.setEditorKit(jsonSyntaxKit);
        }
    }
    
    public void setSyntax(TextEditorSyntax syntax){
        String text = jep.getText();
        if(syntax == TextEditorSyntax.GROOVY){
            groovySyntaxKit = groovySyntaxKit==null? new SyntaxKit(GROOVY): groovySyntaxKit;
            jep.setEditorKit(groovySyntaxKit);
        }
        else if(syntax == TextEditorSyntax.XML){
            xmlSyntaxKit = xmlSyntaxKit==null? new SyntaxKit(XML): xmlSyntaxKit;
            jep.setEditorKit(xmlSyntaxKit);
        }
        else if(syntax == TextEditorSyntax.JSON){
            jsonSyntaxKit = jsonSyntaxKit==null? new SyntaxKit(JSON): jsonSyntaxKit;
            jep.setEditorKit(jsonSyntaxKit);
        }
        else if(syntax == TextEditorSyntax.DEFAULT){
            jep.setEditorKit(defaultEditorKit);
        }
        jep.validate();
        jep.setText(text);
        jep.setCaretPosition(0);
    }

    public JComponent getEditorView() {
        return jep;
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
