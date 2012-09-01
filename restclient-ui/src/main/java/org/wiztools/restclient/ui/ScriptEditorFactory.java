package org.wiztools.restclient.ui;

/**
 *
 * @author subwiz
 */
public final class ScriptEditorFactory {

    public static ScriptEditor getGroovyScriptEditor(){
        return new RSyntaxScriptEditor(TextEditorSyntax.GROOVY);
    }
    
    public static ScriptEditor getXMLScriptEditor(){
        return new RSyntaxScriptEditor(TextEditorSyntax.XML);
    }
    
    public static ScriptEditor getTextAreaScriptEditor(){
        return new RSyntaxScriptEditor(TextEditorSyntax.NONE);
    }
}
