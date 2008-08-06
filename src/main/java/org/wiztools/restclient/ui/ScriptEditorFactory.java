/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

/**
 *
 * @author subwiz
 */
public final class ScriptEditorFactory {

    public static ScriptEditor getGroovyScriptEditor(){
        return new JSyntaxPaneScriptEditor(JSyntaxPaneScriptEditor.GROOVY);
    }
    
    public static ScriptEditor getXMLScriptEditor(){
        return new JSyntaxPaneScriptEditor(JSyntaxPaneScriptEditor.XML);
    }
    
    public static ScriptEditor getTextAreaScriptEditor(){
        return new TextAreaScriptEditor();
    }
}
