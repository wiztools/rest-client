package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

/**
 *
 * @author Subhash
 */
public class SplashScreen extends JWindow {
    public SplashScreen(ImageIcon image){
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        
        c.add(new JLabel(image), BorderLayout.CENTER);
        JProgressBar jpb = new JProgressBar();
        jpb.setIndeterminate(true);
        jpb.setBackground(Color.WHITE);
        jpb.setForeground(Color.BLACK);
        c.add(jpb, BorderLayout.SOUTH);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}
