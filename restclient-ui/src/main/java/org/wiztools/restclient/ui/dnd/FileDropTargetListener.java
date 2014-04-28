package org.wiztools.restclient.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Usage:
 * 
 * <code>
 * FileDropTargetListener l = new FileDropTargetListener();
 * l.addDndAction(new DndAction() {
 *  void onDrop(List<File> files) {
 *      // what to do with dropped files.
 *  }
 *  void onDropRepaint() {
 *      // repaint the component.
 *  }
 * });
 * new java.awt.dnd.DropTarget(component, l);
 * </code>
 * 
 * @author subwiz
 */
public class FileDropTargetListener implements DropTargetListener {
    
    private static final Logger LOG = Logger.getLogger(FileDropTargetListener.class.getName());
    
    private final List<DndAction> actions = new ArrayList<>();
    
    public void addDndAction(DndAction action) {
        actions.add(action);
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // do nothing!
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // do nothing!
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // do nothing!
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        // do nothing!
    }

    @Override
    public void drop(DropTargetDropEvent evt) {
        final int action = evt.getDropAction();
        evt.acceptDrop(action);
        try {
            Transferable data = evt.getTransferable();
            if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                java.util.List<File> list = (java.util.List<File>) data.getTransferData(
                    DataFlavor.javaFileListFlavor);
                for(DndAction a: actions) {
                    a.onDrop(list);
                }
            }
        }
        catch (UnsupportedFlavorException | IOException e) {
            LOG.log(Level.WARNING, null, e);
        }
        finally {
            evt.dropComplete(true);
            for(DndAction a: actions) {
                a.onDropRepaint();
            }
        }
    }
    
}
