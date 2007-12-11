/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiztools.restclient;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JDialog;

/**
 *
 * @author schandran
 */
public abstract class EscapableDialog extends JDialog implements KeyListener, ContainerListener {

    public EscapableDialog(Frame f, boolean modal) {
        super(f, modal);
        registerKeyAction(this);
    }

    public abstract void doEscape(KeyEvent event);

    //KeyListener interface
    public void keyPressed(KeyEvent e) {


        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

            doEscape(e);
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    //ContainerListener interface
    public void componentAdded(ContainerEvent e) {
        registerKeyAction(e.getChild());
    }

    public void componentRemoved(ContainerEvent e) {
        registerKeyAction(e.getChild());
    }

    private void registerKeyAction(Component c) {
        if (c instanceof EscapableDialog == false) {
            c.removeKeyListener(this);
            c.addKeyListener(this);
        }

        if (c instanceof Container) {
            Container cnt = (Container) c;
            cnt.removeContainerListener(this);
            cnt.addContainerListener(this);
            Component[] ch = cnt.getComponents();
            for (int i = 0; i < ch.length; i++) {
                registerKeyAction(ch[i]);
            }
        }
    }
}
