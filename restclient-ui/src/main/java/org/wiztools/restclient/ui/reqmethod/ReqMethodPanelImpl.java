package org.wiztools.restclient.ui.reqmethod;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.wiztools.restclient.bean.HTTPMethod;
import org.wiztools.restclient.ui.RESTView;

/**
 *
 * @author subwiz
 */
public class ReqMethodPanelImpl extends JPanel implements ReqMethodPanel {
    @Inject RESTView view;
    
    private final JRadioButton jrb_req_get = new JRadioButton("GET");
    private final JRadioButton jrb_req_post = new JRadioButton("POST");
    private final JRadioButton jrb_req_put = new JRadioButton("PUT");
    private final JRadioButton jrb_req_patch = new JRadioButton("PATCH");
    private final JRadioButton jrb_req_delete = new JRadioButton("DELETE");
    private final JRadioButton jrb_req_head = new JRadioButton("HEAD");
    private final JRadioButton jrb_req_options = new JRadioButton("OPTIONS");
    private final JRadioButton jrb_req_trace = new JRadioButton("TRACE");
    private final JRadioButton jrb_req_custom = new JRadioButton("Custom:");
    
    private final JTextField jtf_custom = new JTextField(10);
    
    @Override
    public boolean doesSelectedMethodSupportEntityBody() {
        return jrb_req_get.isSelected()
            || jrb_req_post.isSelected()
            || jrb_req_put.isSelected()
            || jrb_req_patch.isSelected()
            || jrb_req_delete.isSelected()
            || jrb_req_custom.isSelected();
    }
    
    @Override
    public HTTPMethod getSelectedMethod() {
        if(jrb_req_get.isSelected()){
            return HTTPMethod.GET;
        }
        else if(jrb_req_head.isSelected()){
            return HTTPMethod.HEAD;
        }
        else if(jrb_req_post.isSelected()){
            return HTTPMethod.POST;
        }
        else if(jrb_req_put.isSelected()){
            return HTTPMethod.PUT;
        }
        else if(jrb_req_patch.isSelected()) {
            return HTTPMethod.PATCH;
        }
        else if(jrb_req_delete.isSelected()){
            return HTTPMethod.DELETE;
        }
        else if(jrb_req_options.isSelected()){
            return HTTPMethod.OPTIONS;
        }
        else if(jrb_req_trace.isSelected()){
            return HTTPMethod.TRACE;
        }
        else if(jrb_req_custom.isSelected()) {
            return HTTPMethod.get(jtf_custom.getText());
        }
        else {
            throw new RuntimeException("Will NEVER come here!");
        }
    }
    
    @Override
    public void setSelectedMethod(HTTPMethod method) {
        switch(method.name()) {
            case "GET":
                jrb_req_get.setSelected(true);
                break;
            case "HEAD":
                jrb_req_head.setSelected(true);
                break;
            case "POST":
                jrb_req_post.setSelected(true);
                break;
            case "PUT":
                jrb_req_put.setSelected(true);
                break;
            case "PATCH":
                jrb_req_patch.setSelected(true);
                break;
            case "DELETE":
                jrb_req_delete.setSelected(true);
                break;
            case "OPTIONS":
                jrb_req_options.setSelected(true);
                break;
            case "TRACE":
                jrb_req_trace.setSelected(true);
                break;
            default:
                jrb_req_custom.setSelected(true);
                jtf_custom.setText(method.name());
                jtf_custom.setEnabled(true);
        }
    }
    
    @PostConstruct
    protected void init() {
        jtf_custom.setEnabled(false);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(jrb_req_get);
        bg.add(jrb_req_post);
        bg.add(jrb_req_put);
        bg.add(jrb_req_patch);
        bg.add(jrb_req_delete);
        bg.add(jrb_req_head);
        bg.add(jrb_req_options);
        bg.add(jrb_req_trace);
        bg.add(jrb_req_custom);
        
        // Default selected button
        jrb_req_get.setSelected(true);
        
        // Mnemonic
        jrb_req_get.setMnemonic('g');
        jrb_req_post.setMnemonic('p');
        jrb_req_put.setMnemonic('t');
        jrb_req_delete.setMnemonic('d');
        
        ActionListener jrbAL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(doesSelectedMethodSupportEntityBody()){
                    view.enableBody();
                }
                else{
                    view.disableBody();
                }
                
                if(jrb_req_custom.isSelected()) {
                    jtf_custom.setEnabled(true);
                    jtf_custom.requestFocus();
                }
                else {
                    jtf_custom.setEnabled(false);
                }
            }
        };
        
        jrb_req_get.addActionListener(jrbAL);
        jrb_req_post.addActionListener(jrbAL);
        jrb_req_put.addActionListener(jrbAL);
        jrb_req_patch.addActionListener(jrbAL);
        jrb_req_delete.addActionListener(jrbAL);
        jrb_req_head.addActionListener(jrbAL);
        jrb_req_options.addActionListener(jrbAL);
        jrb_req_trace.addActionListener(jrbAL);
        jrb_req_custom.addActionListener(jrbAL);
        
        // Placement
        JPanel jp_method = new JPanel();
        jp_method.setBorder(BorderFactory.createTitledBorder("HTTP Method"));
        jp_method.setLayout(new GridLayout(5, 2));
        jp_method.add(jrb_req_get);
        jp_method.add(jrb_req_post);
        jp_method.add(jrb_req_put);
        jp_method.add(jrb_req_patch);
        jp_method.add(jrb_req_delete);
        jp_method.add(jrb_req_head);
        jp_method.add(jrb_req_options);
        jp_method.add(jrb_req_trace);
        jp_method.add(jrb_req_custom);
        jp_method.add(jtf_custom);
        
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(jp_method);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void clear() {
        jtf_custom.setText("");
        jtf_custom.setEnabled(false);
        jrb_req_get.setSelected(true);
    }
}
