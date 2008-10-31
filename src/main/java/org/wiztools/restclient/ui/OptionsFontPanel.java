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
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author subwiz
 */
public class OptionsFontPanel extends JPanel implements IOptionsPanel {
    
    private static final String PROP_PREFIX = "font.options.";
    
    private static final String[] TEXTAREAS = new String[]{"Request Body",
            "Response Body", "Test Script"};
    private JComboBox jcb_textarea = new JComboBox(TEXTAREAS);
    
    private JList jl_font, jl_fontSize;
    private JLabel jl_preview = new JLabel("WizTools.org RESTClient");
    
    public OptionsFontPanel(){
        JPanel jp = this;
        jp.setLayout(new BorderLayout());
        
        // North Panel
        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp_north.add(jcb_textarea);
        final JCheckBox jcb_all = new JCheckBox("All share one font.");
        jcb_all.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(jcb_all.isSelected()){
                    jcb_textarea.setEnabled(false);
                }
                else{
                    jcb_textarea.setEnabled(true);
                }
            }
        });
        jp_north.add(jcb_all);
        jp.add(jp_north, BorderLayout.NORTH);
        
        // Center Panel
        ListSelectionListener previewListner = new Preview();
        
        Dimension d = new Dimension(250, 80);
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
        jp_south.setPreferredSize(new Dimension(500, 40));
        jp.add(jp_south, BorderLayout.SOUTH);
    }

    @Override
    public void initOptions() {
        
    }

    @Override
    public void shutdownOptions() {
        
    }

    @Override
    public List<String> validateInput() {
        return null;
    }

    @Override
    public boolean saveOptions() {
        return true;
    }

    @Override
    public boolean revertOptions() {
        return true;
    }
    
    class Preview implements ListSelectionListener{

        public void valueChanged(ListSelectionEvent arg0) {
            String fontName = (String)jl_font.getSelectedValue();
            int fontSize = Integer.parseInt((String)jl_fontSize.getSelectedValue());
            Font f = new Font(fontName, Font.PLAIN, fontSize);
            jl_preview.setFont(f);
        }
        
    }
}
