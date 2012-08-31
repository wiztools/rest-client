package org.wiztools.restclient.ui;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 *
 * @author subwiz
 */
public class RSyntaxScriptEditor implements ScriptEditor {
    
    private final RSyntaxTextArea textArea = new RSyntaxTextArea();

    public RSyntaxScriptEditor() {
        // remove the default popup:
        textArea.setPopupMenu(null);
    }

    public RSyntaxScriptEditor(TextEditorSyntax syntax) {
        this();
        setSyntax(syntax);
    }
    
    @Override
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
        else {
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
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

    @Override
    public void setEnabled(boolean enabled) {
        textArea.setEnabled(enabled);
    }

    @Override
    public void setPopupMenu(final JPopupMenu menu) {
        textArea.setPopupMenu(menu);
    }
}
