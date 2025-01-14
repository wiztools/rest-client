package org.wiztools.restclient.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.restclient.MessageI18N;
import org.wiztools.restclient.ui.dnd.DndAction;
import org.wiztools.restclient.ui.dnd.FileDropTargetListener;
import org.wiztools.restclient.util.MultiEntryUtil;
import org.wiztools.restclient.util.MultiEntryRes;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author Subhash
 */
class KeyValMultiEntryDialog extends EscapableDialog {

    private final JButton jb_file = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "load_from_file.png"));
    private final JButton jb_help = new JButton(UIUtil.getIconFromClasspath(RCFileView.iconBasePath + "question.png"));
    private final JButton jb_add = new JButton("Add");
    private final JButton jb_cancel = new JButton("Cancel");
    private JScrollPane jsp_in;
    private final JTextArea jta_in = new JTextArea(18, 35);
    private final JDialog me;
    private final RESTUserInterface ui;
    private final MultiEntryAdd callback;

    public KeyValMultiEntryDialog(RESTUserInterface ui, MultiEntryAdd callback) {
        super(ui.getFrame(), true);
        this.ui = ui;
        me = this;
        setTitle("Multi-entry");
        this.callback = callback;

        init();
        dnd();
    }
    
    private void dnd() {
        FileDropTargetListener l = new FileDropTargetListener();
        l.addDndAction(new DndAction() {
            @Override
            public void onDrop(List<File> files) {
                loadFromFile(files.get(0));
            }
        });
        new DropTarget(jta_in, l);
    }

    private void init() {
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());

        JPanel jp_north = new JPanel();
        jp_north.setLayout(new FlowLayout(FlowLayout.LEFT));
        jb_file.setToolTipText("Load from file");
        jb_file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFromFile();
            }
        });
        jp_north.add(jb_file);
        jb_help.setToolTipText("Help");
        jb_help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(me,
                        MessageI18N.getMessage("help.keyval.multi"),
                        "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        jp_north.add(jb_help);

        jp.add(jp_north, BorderLayout.NORTH);

        jsp_in = new JScrollPane(jta_in);
        jp.add(jsp_in, BorderLayout.CENTER);

        JPanel jp_south = new JPanel();
        jp_south.setLayout(new FlowLayout());
        jb_add.setMnemonic('a');
        getRootPane().setDefaultButton(jb_add);
        jb_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                add();
            }
        });
        jp_south.add(jb_add);
        jb_cancel.setMnemonic('c');
        jb_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                me.setVisible(false);
            }
        });
        jp_south.add(jb_cancel);

        jp.add(jp_south, BorderLayout.SOUTH);

        jp.setBorder(BorderFactory.createEmptyBorder(RESTViewImpl.BORDER_WIDTH,
                RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH, RESTViewImpl.BORDER_WIDTH));
        setContentPane(jp);
        pack();
        
        // By default have the focus on the text area:
        jta_in.requestFocus();
    }
    
    private void loadFromFile() {
        File f = ui.getOpenFile(FileChooserType.OPEN_TEST_SCRIPT, me);
        loadFromFile(f);
    }
    
    private void loadFromFile(File f) {
        if(f != null){
            try{
                String content = FileUtil.getContentAsString(f, Charsets.UTF_8);
                Dimension d = jsp_in.getPreferredSize();
                jta_in.setText(content);
                jta_in.setCaretPosition(0);
                jsp_in.setPreferredSize(d);
            }
            catch(IOException ex){
                ui.getView().showError(Util.getStackTrace(ex));
            }
        }
    }

    private void add() {
        String str = jta_in.getText();
        if ("".equals(str.trim())) {
            JOptionPane.showMessageDialog(me, "Please enter input text!", "No Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        MultiEntryRes res = MultiEntryUtil.parse(str);

        me.setVisible(false);
        callback.add(res);
    }

    public void load(MultiValueMap<String, String> map) {
        String str = MultiEntryUtil.join(map);
        jta_in.setText(str);
    }
    
    @Override
    public void setVisible(boolean boo){
        super.setVisible(boo);
        if(boo == true){
            jta_in.requestFocus();
        }
    }

    @Override
    public void doEscape(AWTEvent event) {
        me.setVisible(false);
    }
}
