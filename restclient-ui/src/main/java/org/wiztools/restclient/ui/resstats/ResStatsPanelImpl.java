package org.wiztools.restclient.ui.resstats;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.annotation.PostConstruct;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.wiztools.restclient.ui.UIUtil;

/**
 *
 * @author subhash
 */
public class ResStatsPanelImpl extends JPanel implements ResStatsPanel {
    
    private long executionTime;
    private long bodySize;
    
    private static final String UNKNOWN_NUM = "[x]";
    
    private final JTextField jtf_execTime = new JTextField(UNKNOWN_NUM);
    private final JTextField jtf_bodySize = new JTextField(UNKNOWN_NUM);
    
    @PostConstruct
    protected void init() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Set big fonts:
        jtf_execTime.setFont(UIUtil.FONT_BIG);
        jtf_bodySize.setFont(UIUtil.FONT_BIG);
        jtf_execTime.setEditable(false);
        jtf_bodySize.setEditable(false);
        
        JPanel jp = new JPanel(new GridLayout(2, 3));
        // Response time:
        {
            JLabel jl = new JLabel("Response time: ");
            jl.setFont(UIUtil.FONT_BIG);
            jp.add(jl);
        }
        jp.add(jtf_execTime);
        jp.add(new JLabel(" ms"));
        
        // Body size:
        {
            JLabel jl = new JLabel("Body size: ");
            jl.setFont(UIUtil.FONT_BIG);
            jp.add(jl);
        }
        jp.add(jtf_bodySize);
        jp.add(new JLabel(" bytes"));
        
        this.add(jp);
    }

    @Override
    public long getExecutionTime() {
        return executionTime;
    }

    @Override
    public long getBodySize() {
        return bodySize;
    }

    @Override
    public void setExecutionTime(long time) {
        executionTime = time;
        String val = time == 0l? UNKNOWN_NUM: String.valueOf(time);
        jtf_execTime.setText(val);
    }

    @Override
    public void setBodySize(long size) {
        bodySize = size;
        String val = size == 0l? UNKNOWN_NUM: String.valueOf(size);
        jtf_bodySize.setText(val);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void clear() {
        setExecutionTime(0l);
        setBodySize(0l);
    }
    
}
