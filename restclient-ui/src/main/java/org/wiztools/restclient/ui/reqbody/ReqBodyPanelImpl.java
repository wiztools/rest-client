package org.wiztools.restclient.ui.reqbody;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.wiztools.restclient.ReqEntity;
import org.wiztools.restclient.ReqEntityFile;
import org.wiztools.restclient.ReqEntityMultipart;
import org.wiztools.restclient.ReqEntityString;
import org.wiztools.restclient.ui.FontableEditor;
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.RESTView;

/**
 *
 * @author subwiz
 */
public class ReqBodyPanelImpl extends JPanel implements ReqBodyPanel, FontableEditor {
    @Inject RESTView view;
    @Inject RESTUserInterface rest_ui;
    
    @Inject private ReqBodyPanelNone jp_body_none;
    @Inject private ReqBodyPanelString jp_req_body_string;
    @Inject private ReqBodyPanelFile jp_req_body_file;
    @Inject private ReqBodyPanelMultipart jp_req_body_multipart;
    
    private final List<ReqBodyPanel> allPanels = new ArrayList<ReqBodyPanel>();
    
    private static final String NONE_BODY = "None";
    private static final String STRING_BODY = "String body";
    private static final String FILE_BODY = "File body";
    private static final String MULTIPART_BODY = "Multipart body";
    
    
    private static final String[] comboValues = new String[]{NONE_BODY, STRING_BODY, FILE_BODY, MULTIPART_BODY};
    private JComboBox jcb_body_type = new JComboBox(comboValues);
    
    private JPanel getPanelFromSelection() {
        if(jcb_body_type.getSelectedItem().equals(NONE_BODY)) {
            return jp_body_none;
        }
        else if(jcb_body_type.getSelectedItem().equals(STRING_BODY)) {
            return jp_req_body_string;
        }
        else if(jcb_body_type.getSelectedItem().equals(FILE_BODY)) {
            return jp_req_body_file;
        }
        else if(jcb_body_type.getSelectedItem().equals(MULTIPART_BODY)) {
            return jp_req_body_multipart;
        }
        else {
            throw new RuntimeException("Will NEVER reach here!");
        }
    }
    
    @Override
    public void setEntity(ReqEntity entity) {
        if(entity instanceof ReqEntityString) {
            jcb_body_type.setSelectedItem(STRING_BODY);
            jp_req_body_string.setEntity(entity);
        }
        else if(entity instanceof ReqEntityFile) {
            jcb_body_type.setSelectedItem(FILE_BODY);
            jp_req_body_file.setEntity(entity);
        }
        else if(entity instanceof ReqEntityMultipart) {
            jcb_body_type.setSelectedItem(MULTIPART_BODY);
            jp_req_body_multipart.setEntity(entity);
        }
        else {
            jcb_body_type.setSelectedItem(NONE_BODY);
        }
    }

    @Override
    public ReqEntity getEntity() {
        ReqBodyPanel ops = (ReqBodyPanel) getPanelFromSelection();
        return ops.getEntity();
    }
    
    @Override
    public void enableBody() {
        jcb_body_type.setEnabled(true);
        ((ReqBodyPanel) getPanelFromSelection()).enableBody();
    }
    
    @Override
    public void disableBody() {
        jcb_body_type.setEnabled(false);
        ((ReqBodyPanel) getPanelFromSelection()).disableBody();
    }
    
    @Override
    public void clear() {
        jcb_body_type.setSelectedItem(NONE_BODY);
        for(ReqBodyPanel panel: allPanels) {
            panel.clear();
        }
    }
    
    @PostConstruct
    protected void init() {
        // Populate allPanels:
        allPanels.add(jp_req_body_string);
        allPanels.add(jp_req_body_file);
        allPanels.add(jp_req_body_multipart);
        
        // Create UI:
        final JScrollPane jsp = new JScrollPane();
        jsp.setViewportView(jp_body_none);

        setLayout(new BorderLayout());
        add(jcb_body_type, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);


        jcb_body_type.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JPanel jp = getPanelFromSelection();
                jsp.setViewportView(jp);
                jp.requestFocus();
            }
        });
    }

    @Override
    public void setEditorFont(Font font) {
        jp_req_body_string.setEditorFont(font);
    }

    @Override
    public Font getEditorFont() {
        return jp_req_body_string.getEditorFont();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
