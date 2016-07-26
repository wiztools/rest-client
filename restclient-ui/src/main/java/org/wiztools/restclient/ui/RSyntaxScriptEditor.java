package org.wiztools.restclient.ui;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 *
 * @author subwiz
 */
public class RSyntaxScriptEditor extends AbstractScriptEditor {
    
    private final RSyntaxTextArea textArea = new RSyntaxTextArea();

    public RSyntaxScriptEditor() {
        // remove the default popup:
        textArea.setPopupMenu(null);
        
        // Anti-aliased:
        textArea.setAntiAliasingEnabled(true);
    }

    public RSyntaxScriptEditor(TextEditorSyntax syntax) {
        this();
        setSyntax(syntax);
    }
    
    @Override
    public final void setSyntax(TextEditorSyntax syntax) {
        switch(syntax) {
            case GROOVY:
                textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
                break;
            case XML:
                textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
                break;
            case JSON:
            case JS:
                textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                break;
            case HTML:
                textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
                break;
            case CSS:
                textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);
                break;
            default:
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
    public void setViewText(String text) {
        textArea.setText(text);
    }
    
    @Override
    public String getViewText() {
        return textArea.getText();
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
