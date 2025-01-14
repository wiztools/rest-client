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
    
    @Override
    public final void setSourceText(String text) {
        source = text;
    }
    
    @Override
    public final String getSourceText() {
        return source;
    }
    
}
