package org.wiztools.restclient.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.DIFramework;

/**
 *
 * @author subwiz
 */
public class OptionsFontPanel extends JPanel implements IOptionsPanel {
    
    private static final Logger LOG = Logger.getLogger(OptionsFontPanel.class.getName());
    
    private static final String PROP_PREFIX = "font.options.";
    
    private JButton jb_default = new JButton("Restore Default");
    
    private JList jl_font, jl_fontSize;
    private JLabel jl_preview = new JLabel("WizTools.org RESTClient");
    
    public OptionsFontPanel(){
        JPanel jp = this;
        jp.setLayout(new BorderLayout());
        
        // North Panel
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel jl = new JLabel("Font for Request Body & Response Body");
        jp_north.add(jl);
        jb_default.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                jl_font.setSelectedValue(Font.DIALOG, true);
                jl_fontSize.setSelectedValue("12", true);
            }
        });
        jp_north.add(jb_default);
        jp.add(jp_north, BorderLayout.NORTH);
        
        // Center Panel
        ListSelectionListener previewListner = new Preview();
        
        final int _W = 200;
        final int _H = 100;
        
        Dimension d = new Dimension(_W, _H);
        JPanel jp_center = new JPanel();
        jp_center.setLayout(new GridLayout(1, 2));
        String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        jl_font = new JList(fontFamilyNames);
        jl_font.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jl_font.addListSelectionListener(previewListner);
        JScrollPane jsp_font = new JScrollPane(jl_font);
        jsp_font.setPreferredSize(d);
        jp_center.add(jsp_font);
        String fontSizes[] = { "8", "10", "11", "12", "14", "16", "18",
            "20", "24", "30", "36", "40", "48", "60", "72" };
        jl_fontSize = new JList(fontSizes);
        jl_fontSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jl_fontSize.addListSelectionListener(previewListner);
        JScrollPane jsp_fontSize = new JScrollPane(jl_fontSize);
        jsp_fontSize.setPreferredSize(d);
        jp_center.add(jsp_fontSize);
        jp.add(jp_center, BorderLayout.CENTER);
        
        // South Panel
        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
        jp_south.add(jl_preview);
        jp_south.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jp_south.setPreferredSize(new Dimension(_W, _H / 2));
        jp.add(jp_south, BorderLayout.SOUTH);
    }

    @Override
    public void initOptions() {
        Font f = null;
        String fontName = DIFramework.getInstance(IGlobalOptions.class).getProperty(PROP_PREFIX + "font");
        String fontSizeStr = DIFramework.getInstance(IGlobalOptions.class).getProperty(PROP_PREFIX + "fontSize");
        int fontSize = 12;
        if(fontSizeStr != null){
            try{
                fontSize = Integer.parseInt(fontSizeStr);
            }
            catch(NumberFormatException ex){
                // leave the default font size of 12
            }
        } // else leave default font size of 12
        if(fontName == null){
            LOG.info("Font configuration not available in configuration. Reverting to default font.");
            f = new Font(Font.DIALOG, Font.PLAIN, 12);
        }
        else{
            f = new Font(fontName, Font.PLAIN, fontSize);
        }
        jl_font.setSelectedValue(f.getFamily(), true);
        jl_fontSize.setSelectedValue(String.valueOf(f.getSize()), true);
        
        UIRegistry.getInstance().view.setTextAreaFont(f);
    }

    @Override
    public void shutdownOptions() {
        DIFramework.getInstance(IGlobalOptions.class).setProperty(PROP_PREFIX + "font", (String)jl_font.getSelectedValue());
        DIFramework.getInstance(IGlobalOptions.class).setProperty(PROP_PREFIX + "fontSize", (String)jl_fontSize.getSelectedValue());
    }

    @Override
    public List<String> validateInput() {
        return null;
    }

    @Override
    public boolean saveOptions() {
        String fontName= (String)jl_font.getSelectedValue();
        String fontSizeStr = (String)jl_fontSize.getSelectedValue();
        int fontSize = Integer.parseInt(fontSizeStr);
        Font f = new Font(fontName, Font.PLAIN, fontSize);
        UIRegistry.getInstance().view.setTextAreaFont(f);
        return true;
    }

    @Override
    public boolean revertOptions() {
        Font f = UIRegistry.getInstance().view.getTextAreaFont();
        jl_font.setSelectedValue(f.getFamily(), true);
        jl_fontSize.setSelectedValue(String.valueOf(f.getSize()), true);
        return true;
    }
    
    class Preview implements ListSelectionListener{

        public void valueChanged(ListSelectionEvent evt) {
            if(jl_font.getSelectedValue()==null || jl_fontSize.getSelectedValue()==null){
                return;
            }
            String fontName = (String)jl_font.getSelectedValue();
            int fontSize = Integer.parseInt((String)jl_fontSize.getSelectedValue());
            Font f = new Font(fontName, Font.PLAIN, fontSize);
            jl_preview.setFont(f);
        }
        
    }
}
