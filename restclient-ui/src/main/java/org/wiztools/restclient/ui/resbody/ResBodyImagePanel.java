package org.wiztools.restclient.ui.resbody;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import org.wiztools.restclient.ui.RESTView;

/**
 *
 * @author subwiz
 */
public class ResBodyImagePanel extends AbstractResBody {
    @Inject private RESTView view;
    
    private JLabel jl = new JLabel();
    
    @PostConstruct
    protected void init() {
        setLayout(new GridLayout());
        
        JScrollPane jsp = new JScrollPane(jl);
        add(jsp);
    }
    
    public void setImage(byte[] data) {
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
            jl.setIcon(new ImageIcon(img));
        }
        catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void clearBody() {
        jl.setIcon(null);
    }
}
