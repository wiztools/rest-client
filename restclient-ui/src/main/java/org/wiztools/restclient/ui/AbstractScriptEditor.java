package org.wiztools.restclient.ui;

/**
 *
 * @author subwiz
 */
public abstract class AbstractScriptEditor implements ScriptEditor {
    private String source;

    @Override
    public final void setText(String text) {
        source = text;
        setViewText(text);
    }
    
    public abstract void setViewText(String text);
    
}
