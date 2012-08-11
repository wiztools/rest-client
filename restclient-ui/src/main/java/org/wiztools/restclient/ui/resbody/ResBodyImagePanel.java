package org.wiztools.restclient.ui.resbody;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.wiztools.restclient.ContentType;

/**
 *
 * @author subwiz
 */
public class ResBodyImagePanel extends AbstractResBody {
    
    private JLabel jl = new JLabel();
    
    @PostConstruct
    protected void init() {
        setLayout(new GridLayout());
        
        add(jl);
    }

    @Override
    public void setBody(byte[] body, ContentType type) {
        // Call parent:
        super.setBody(body, type);
        
        // Generate UI:
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(body));
            jl.setIcon(new ImageIcon(img));
        }
        catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void clearBody() {
        body = null;
        jl.setIcon(null);
    }
}
