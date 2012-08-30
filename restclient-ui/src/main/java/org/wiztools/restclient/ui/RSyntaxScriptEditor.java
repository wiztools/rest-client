package org.wiztools.restclient.ui;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 *
 * @author subwiz
 */
public class RSyntaxScriptEditor implements ScriptEditor {
    
    private final RSyntaxTextArea textArea = new RSyntaxTextArea();

    public RSyntaxScriptEditor(TextEditorSyntax syntax) {
        setSyntax(syntax);
    }
    
    public final void setSyntax(TextEditorSyntax syntax) {
        if(syntax == TextEditorSyntax.GROOVY){
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
        }
        else if(syntax == TextEditorSyntax.XML){
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        }
        else if(syntax == TextEditorSyntax.JSON){
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        }
    }

    @Override
    public JComponent getEditorView() {
        return textArea;
    }

    @Override
    public JTextComponent getEditorComponent() {
        return textArea;
    }

    @Override
    public String getText() {
        return textArea.getText();
    }

    @Override
    public void setText(String text) {
        textArea.setText(text);
    }

    @Override
    public void setCaretPosition(int offset) {
        textArea.setCaretPosition(offset);
    }

    @Override
    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }
    
}
