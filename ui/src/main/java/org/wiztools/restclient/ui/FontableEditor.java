package org.wiztools.restclient.ui;

import java.awt.Font;

/**
 *
 * @author subwiz
 */
public interface FontableEditor {
    public String FONT_NAME_PROPERTY = "font.options.font";
    public String FONT_SIZE_PROPERTY = "font.options.fontSize";
    
    public void setEditorFont(Font font);
    public Font getEditorFont();
}
