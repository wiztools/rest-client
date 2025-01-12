package org.wiztools.restclient.ui;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;

/**
 *
 * @author subwiz
 */
public abstract class EscapableDialog extends JDialog implements KeyListener, ContainerListener {
    
    private final Frame _frame;

    public EscapableDialog(Frame f, boolean modal) {
        super(f, modal);
        _frame = f;
        registerKeyAction(this);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event){
                doEscape(event);
            }
        });
    }

    public abstract void doEscape(AWTEvent event);

    //KeyListener interface
    @Override
    public void keyPressed(KeyEvent e) {


        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

            doEscape(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    //ContainerListener interface
    @Override
    public void componentAdded(ContainerEvent e) {
        registerKeyAction(e.getChild());
    }

    @Override
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
    
    /**
     * Center the dialog relative to parent before displaying.
     */
    @Override
    public void setVisible(boolean boo){
        if(boo){
            this.setLocationRelativeTo(_frame);
        }
        super.setVisible(boo);
    }
    
}
