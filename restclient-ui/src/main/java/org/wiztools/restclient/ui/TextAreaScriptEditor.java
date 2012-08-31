package org.wiztools.restclient.ui;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

/**
 *  textarea script editor
 */
class TextAreaScriptEditor implements ScriptEditor {
    private JTextArea textArea = new JTextArea();
    private JScrollPane jsp = new JScrollPane(textArea);

    /**
     * view component for test script editor
     *
     * @return JComponent object
     */
    @Override
    public JComponent getEditorView() {
        return jsp;
    }

    @Override
    public JTextComponent getEditorComponent() {
        return textArea;
    }

    /**
     * get test script code
     *
     * @return script
     */
    @Override
    public String getText() {
        return textArea.getText();
    }

    /**
     * set text script code
     *
     * @param text script code
     */
    @Override
    public void setText(String text) {
        textArea.setText(text);
    }

    /**
     * set caret position
     *
     * @param offset offset
     */
    @Override
    public void setCaretPosition(int offset) {
        textArea.setCaretPosition(offset);
    }

    /**
     * set editable mark
     *
     * @param editable editable mark
     */
    @Override
    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }

    @Override
    public void setEnabled(boolean enabled) {
        textArea.setEnabled(enabled);
    }

    @Override
    public void setSyntax(TextEditorSyntax syntax) {
        // do nothing!
    }

    @Override
    public void setPopupMenu(JPopupMenu menu) {
        // do nothing!
    }
}
