package org.wiztools.restclient.idea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightVirtualFile;
import org.wiztools.restclient.ui.ScriptEditor;

import javax.swing.*;

/**
 * groovy script editor
 */
public class GroovyScriptEditor implements ScriptEditor {
    private JComponent editorComponent;
    private boolean initialized = false;
    private Editor editor;
    private Project project;

    public GroovyScriptEditor(Project project) {
        this.project = project;
    }

    public void init() {
        LightVirtualFile groovyVirtualFile = new LightVirtualFile("Test.groovy", "");
        TextEditorProvider editorProvider = TextEditorProvider.getInstance();
        FileEditor fileEditor = editorProvider.createEditor(project, groovyVirtualFile);
        if (fileEditor instanceof TextEditor) {
            editor = ((TextEditor) fileEditor).getEditor();
        }
        this.editorComponent = fileEditor.getComponent();
    }

    /**
     * view component for test script editor
     *
     * @return JComponent object
     */
    public JComponent getEditorView() {
        if (!this.initialized) {
            init();
        }
        return editorComponent;
    }

    /**
     * get test script code
     *
     * @return script
     */
    public String getText() {
        return editor.getDocument().getText();
    }

    /**
     * set text script code
     *
     * @param text script code
     */
    public void setText(final String text) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                editor.getDocument().setText(text);
            }
        });
    }

    /**
     * set caret position
     *
     * @param offset offset
     */
    public void setCaretPosition(int offset) {
        editor.getCaretModel().moveToOffset(offset);
    }

    /**
     * set editable mark
     *
     * @param editable editable mark
     */
    public void setEditable(boolean editable) {
        editor.getDocument().setReadOnly(!editable);
    }
}
