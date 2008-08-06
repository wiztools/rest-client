/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import jsyntaxpane.SyntaxKit;

/**
 *
 * @author subwiz
 */
class JSyntaxPaneScriptEditor implements ScriptEditor {
    
    static final String GROOVY = "groovy";
    static final String XML = "xml";
    
    private JEditorPane jep = new JEditorPane();
    
    JSyntaxPaneScriptEditor(String type){
        jep.setEditorKit(new SyntaxKit(type));
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
