package org.wiztools.restclient.ui.dnd;

import java.io.File;
import java.util.List;

/**
 *
 * @author subwiz
 */
public interface DndAction {
    void onDrop(List<File> files);
    void onDropRepaint();
}
