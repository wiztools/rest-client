package org.wiztools.restclient.ui.customrest;

/**
 * created by 10192065 on 2017/8/31
 * User: 10192065(yzg)
 * Date: 2017/8/31
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.wiztools.restclient.IGlobalOptions;
import org.wiztools.restclient.ui.RESTUserInterface;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.util.ConfigUtil;
import org.wiztools.restclient.util.Util;


public class CustomDebugViewImpl extends JPanel implements CustomDebugView {
    private static final Logger LOG = Logger.getLogger(CustomDebugViewImpl.class.getName());
    @Inject RESTView view;

    @Inject private RESTUserInterface rest_ui;

    // 上一次使用的filePath记录放到用户目录文件夹中的xmlPath.recode文件中
    File recodeFile = ConfigUtil.getConfigFile("xmlPath.recode");

    private List<String> xmlFilePaths = getXmlPath();

    XmlParse xmlParse = new XmlParse(view, xmlFilePaths);

    private Boolean isIpNeedSync = true;

    private Map<String, CommandDefined> commandUnion = xmlParse.getAllCommand();
    private Map<String, List<String>> commandNames = xmlParse.getAllCmdName();

    private FormCommand formUtil = new FormCommand();

    private String lastCommand = "tools init";

    // 加载新的xml文件的按钮，点击之后会加载新的XML文件，并将记录写入文件记录起来
    private final JButton loadXmlButn = new JButton("Load Xml");
    // ip输入下拉框，并将新的ip写入数据库，以供可以选择历史数据

    private JComboBox ipComboBox = new JComboBox();

    // 类别选择框
    private JComboBox catageComboBox = new JComboBox();

    // 命令选择框
    private JComboBox comComboBox = new JComboBox();

    // 搜索框
    private JTextField searchCtrl = new JTextField("");

    // 参数输入框
    private final RSyntaxTextArea paramCtrl = new RSyntaxTextArea();


    private final SqlDataBase sqlObject = new SqlDataBase(view);

    @PostConstruct
    protected void init() {
        initcatageComboBox();
        initcmdComboBox();
        initIpCombox();
        initParamCtrl();
        initSearchCtrl();
        Sizer();
        initListener();
        lastCommand = getCommand();
    }

    // 页面布局
    private void Sizer() {
        setLayout(new BorderLayout());

        JPanel admaPanelRow1 = new JPanel();
        admaPanelRow1.setLayout(new FlowLayout(FlowLayout.LEFT));


        JLabel jl_url = new JLabel("Host   : ");
        jl_url.setLabelFor(ipComboBox);
        jl_url.setDisplayedMnemonic('u');
        ipComboBox.setToolTipText("Enter or choose a Ip");
        loadXmlButn.setToolTipText("Load Cmd from Xml files!!!");

        JTextField blank = new JTextField();
        blank.setPreferredSize(new Dimension(320,0));

        admaPanelRow1.add(jl_url);
        admaPanelRow1.add(ipComboBox);
        admaPanelRow1.add(blank);
        admaPanelRow1.add(loadXmlButn);

        add(admaPanelRow1, BorderLayout.NORTH);

        JPanel admaPanelRow2 = new JPanel();
        admaPanelRow2.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel catagLabel = new JLabel("Group: ");
        catagLabel.setLabelFor(catageComboBox);
        catagLabel.setDisplayedMnemonic('u');

        catageComboBox.setToolTipText("Choose the Group......");



        JLabel cmdLabel = new JLabel("CMD : ");
        cmdLabel.setLabelFor(comComboBox);
        cmdLabel.setDisplayedMnemonic('u');
        comComboBox.setToolTipText("Choose Command......");
        Dimension cd = catageComboBox.getPreferredSize();
        cd.width = cd.width + 100;
        catageComboBox.setPreferredSize(cd);

        Dimension scd = comComboBox.getPreferredSize();
        scd.width = scd.width + 200;
        comComboBox.setPreferredSize(scd);

        admaPanelRow2.add(catagLabel);
        admaPanelRow2.add(catageComboBox);
        admaPanelRow2.add(cmdLabel);
        admaPanelRow2.add(comComboBox);

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setLabelFor(searchCtrl);
        searchLabel.setDisplayedMnemonic('u');
        searchCtrl.setToolTipText("Search Command...");

        Dimension sscd = searchCtrl.getPreferredSize();
        sscd.width = sscd.width + 100;
        searchCtrl.setPreferredSize(sscd);

        admaPanelRow2.add(searchLabel);
        admaPanelRow2.add(searchCtrl);

        add(admaPanelRow2, BorderLayout.CENTER);

        JScrollPane jsp = new JScrollPane(paramCtrl);
        Dimension jcd = jsp.getPreferredSize();
        jcd.height = jcd.height + 150;
        jsp.setPreferredSize(jcd);
        add(jsp, BorderLayout.SOUTH);

    }


    @Override
    public String getTipsString() {
        String catalog = null;
        String subCmd = null;
        Object cataItem = catageComboBox.getSelectedItem();
        Object subCmdItem = comComboBox.getSelectedItem();

        if (null != cataItem) {
            catalog = cataItem.toString();
        }
        if (null != subCmdItem) {
            subCmd = subCmdItem.toString();
        }

        String cmd = null;
        if (catalog != null && subCmd != null) {
            if (catalog == "无类别" || catalog == "all") {
                cmd = subCmd;
            } else {
                cmd = catalog + "_" + subCmd;
            }
        }

        if (null != cmd) {
            return commandUnion.get(cmd).tips;
        }
        return "";
    }

    @Override
    public String getPostData() {
        String catalog = "";
        String subCmd = "";
        Object catalogItem = catageComboBox.getSelectedItem();
        Object subCmdItem = comComboBox.getSelectedItem();

        if(null != catalogItem) {
            catalog = catalogItem.toString();
        }
        if (null != subCmdItem) {
            subCmd = subCmdItem.toString();
        }
        String cmd = null;
        if (catalog != "" && subCmd != "") {
            if (catalog == "无类别" || catalog == "all") {
                cmd = subCmd;
            } else {
                cmd = catalog + "_" + subCmd;
            }
        }

        if (null != cmd) {
            return commandUnion.get(cmd).postData;
        }
        return "";
    }

    private String  getContentType() {
        String catalog = "";
        String subCmd = "";
        Object catalogItem = catageComboBox.getSelectedItem();
        Object subCmdItem = comComboBox.getSelectedItem();

        if(null != catalogItem) {
            catalog = catalogItem.toString();
        }
        if (null != subCmdItem) {
            subCmd = subCmdItem.toString();
        }
        String cmd = null;
        if (catalog != "" && subCmd != "") {
            if (catalog == "无类别" || catalog == "all") {
                cmd = subCmd;
            } else {
                cmd = catalog + "_" + subCmd;
            }
        }

        if (null != cmd) {
            return commandUnion.get(cmd).contentType;
        }
        return "";
    }

    @Override
    public String getCommandCatag() {
        return catageComboBox.getSelectedItem().toString();
    }

    @Override
    public String getCommandSubName() {
        return comComboBox.getSelectedItem().toString();
    }

    @Override
    public String getCommandFullName() {
        String catalog = catageComboBox.getSelectedItem().toString();
        String subCmd = comComboBox.getSelectedItem().toString();
        String cmd = null;
        if (catalog != "" && subCmd != "") {
            if (catalog == "无类别" || catalog == "all") {
                cmd = subCmd;
            } else {
                cmd = catalog + "_" + subCmd;
            }
        }

        if (null != cmd) {
            return cmd;
        }
        return "";
    }

    @Override
    public Boolean isPost() {
        String catalog = catageComboBox.getSelectedItem().toString();
        String subCmd = comComboBox.getSelectedItem().toString();
        String cmd = null;
        if (catalog != "" && subCmd != "") {
            if (catalog == "无类别" || catalog == "all") {
                cmd = subCmd;
            } else {
                cmd = catalog + "_" + subCmd;
            }
        }

        if (null != cmd) {
            return commandUnion.get(cmd).method.toLowerCase().trim() == "post";
        }
        return false;
    }

    @Override
    public void clear() {
        catageComboBox.removeAllItems();
        comComboBox.removeAllItems();
        ipComboBox.removeAllItems();
        searchCtrl.setText("");
        paramCtrl.setText("");
    }

    @Override
    public Component getComponent() {
        return this;
    }


    private String getParams() {
        String catalog;
        String subCmd;
        Object catalogItem = catageComboBox.getSelectedItem();
        if(null != catalogItem){
            catalog = catalogItem.toString();
        }
        else {
            catalog = "";
        }
        Object subCmdItem = comComboBox.getSelectedItem();
        if (null != subCmdItem) {
            subCmd = subCmdItem.toString();
        } else {
            subCmd = "";
        }
        String cmd = null;
        if (catalog != "" && subCmd != "") {
            if (catalog == "无类别" || catalog == "all") {
                cmd = subCmd;
            } else {
                cmd = catalog + "_" + subCmd;
            }
        }

        if (null != cmd) {
            return commandUnion.get(cmd).params;
        }
        return "";
    }


    private void initParamCtrl() {
        // 自动换行
        paramCtrl.setLineWrap(true);
        paramCtrl.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        paramCtrl.setText(getParams());

    }

    private void initSearchCtrl() {
        searchCtrl.setName("SEARCH");
    }

    private void initListener() {
        // 重新加载xml文件按钮事件
        loadXmlButn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFileChooser jFile = new JFileChooser(IGlobalOptions.CONF_DIR);
                xmlFileFilter fileFilter=new xmlFileFilter();
                jFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFile.setMultiSelectionEnabled(true);
                jFile.setFileFilter(fileFilter);
                jFile.showDialog(new JLabel(), "Choose.......");
                File[] files = jFile.getSelectedFiles();
                List<String> lFiles = new ArrayList();
                for (File file : files) {
                    lFiles.add(file.getAbsolutePath());
                }
                if (!lFiles.isEmpty()) {
                    writeXmlPath(lFiles);
                }
            }
        });

        // ip选择框输入更新事件
        final JTextField editorComponent = (JTextField) ipComboBox.getEditor().getEditorComponent();
        editorComponent.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                String ip = editorComponent.getText();
                if (ipCheck(ip)) {
                    editorComponent.setForeground(Color.GREEN);
                } else {
                    editorComponent.setForeground(Color.RED);
                }
                lastCommand = getCommand();
            }
        });

        // IP选择框选择IP时要同步更新command
        ipComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // 值改变的时候更新command
                // lastCommand = getCommand();
                if (isIpNeedSync) {
                    LOG.log(Level.INFO, "ip selected and set!");
                    setUiCommand();
                }
                isIpNeedSync = true;
            }
        });


        // 参数填写框更新事件
        paramCtrl.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                setUiCommand();
                LOG.log(Level.INFO, "params update and set!");

            }
        });

        // 类型选择框事件响应函数，需要同步更新命令选择框和参数区域
        catageComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // 同步更新subcmd 和params
                String catalog = "";
                Object cataItem = catageComboBox.getSelectedItem();
                if (null != cataItem) {
                    catalog = cataItem.toString();
                }
                if (commandNames.keySet().contains(catalog)) {
                    List<String> subCmds = commandNames.get(catalog);
                    comComboBox.removeAllItems();
                    for (String item : subCmds) {
                        comComboBox.addItem(item);
                    }
                    if (!subCmds.isEmpty()) {
                        // 更新下拉框，并且更新参数区域
                        comComboBox.setSelectedItem(subCmds.toArray()[0]);
                        paramCtrl.setText(getParams());
                        setUiCommand();
                        LOG.log(Level.INFO, "catalog selected and set!");
                    }
                } else {
                    LOG.log(Level.WARNING,"None of " + catalog + "has configure in xml file!");
                }
            }
        });

        // 子命令选择框事件
        comComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                paramCtrl.setText(getParams());
                LOG.log(Level.INFO, "subType selected and set!");
                setUiCommand();
            }
        });

        // 搜索框响应文本改变事件，需要设置类别框为all，并更新命令选择下拉框
        searchCtrl.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                // TODO 同步更新catalog和命令框 当为“”时要恢复命令框
                if (searchCtrl.getText() == "") {
                    initcatageComboBox();
                    initcmdComboBox();
                    initParamCtrl();
                } else {
                    catageComboBox.setSelectedItem("all");
                    comComboBox.removeAllItems();
                    Set<String> cmdName = commandUnion.keySet();
                    String seachKey = searchCtrl.getText();
                    String lastKey = null;
                    for (String cmd : cmdName) {
                        if (cmd.contains(seachKey)) {
                            comComboBox.addItem(cmd);
                            lastKey = cmd;
                        }
                    }
                    if (lastKey != null) {
                        comComboBox.setSelectedItem(lastKey);
                        paramCtrl.setText(getParams());
                        lastCommand = getCommand();
                    }

                }
            }
        });

    }

    @Override
    public void setHostIp() {
        // 处理回车，将IP写入到数据库中去
        // 校验IP是否合法,合法将其写入数据库，并更新IP选择框
        String ip = "";
        Object ipItem = ipComboBox.getSelectedItem();
        if(null != ipItem) {
            ip = ipItem.toString();
        }

        if (ipCheck(ip)) {
            sqlObject.insertIpRecode(ip);
            List<String> ipList = sqlObject.getIpRecode();
            isIpNeedSync = false;
            ipComboBox.removeAllItems();
            for (String host : ipList) {
                isIpNeedSync = false;
                ipComboBox.addItem(host);
            }
            isIpNeedSync = false;
            ipComboBox.setSelectedItem(ip);
        }
    }

    private static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }


    private void initcatageComboBox() {
        // 初始化类别下拉框
        catageComboBox.removeAllItems();
        catageComboBox.setToolTipText("选择分类....");
        catageComboBox.setEditable(false);
        Set<String> allCatag = commandNames.keySet();
        for (String key : allCatag) {
            catageComboBox.addItem(key);
        }
        catageComboBox.setSelectedItem("all");
    }

    private void initcmdComboBox() {
        // 初始化类别下拉框
        comComboBox.removeAllItems();
        comComboBox.setToolTipText("选择命令....");
        comComboBox.setEditable(false);
        Object catalogItem = catageComboBox.getSelectedItem();
        if (null != catalogItem) {
            String catalog = catalogItem.toString();
            if (commandNames.keySet().contains(catalog)) {
                List<String> commands = commandNames.get(catalog);
                for (String cmd : commands) {
                    comComboBox.addItem(cmd);
                }
                comComboBox.setSelectedItem(commands.toArray()[0]);
            }
        }
    }

    private void initIpCombox() {
        // 初始化ip下拉框
        ipComboBox.setToolTipText("HOST");
        ipComboBox.setEditable(true);
//        JLabel jl_url = new JLabel("HOST: ");
//        jl_url.setLabelFor(ipComboBox);
//        jl_url.setDisplayedMnemonic('u');
        List<String> ipList = sqlObject.getIpRecode();
        for (String ip : ipList) {
            ipComboBox.addItem(ip);
        }
        if (!ipList.isEmpty()) {
            ipComboBox.setSelectedItem(ipList.toArray()[0]);
        }
    }

    @Override
    public String getLastCmd() {
        return lastCommand;
    }

    @Override
    public String getCmd() {
        String catalog = "";
        String subCmd = "";
        Object cataItem = catageComboBox.getSelectedItem();
        Object comboItem = comComboBox.getSelectedItem();

        if (null != cataItem) {
            catalog = cataItem.toString();
        }
        if (null != comboItem) {
            subCmd = comboItem.toString();
        }
        String cmd = null;
        if (catalog != "" && subCmd != "") {
            if (catalog == "无类别" || catalog == "all") {
                cmd = subCmd;
            } else {
                cmd = catalog + "_" + subCmd;
            }
        }
        return cmd;
    }

    private void setUiCommand() {
        lastCommand = getCommand();
        comComboBox.setToolTipText(getTipsString());
        view.setUrl(lastCommand);
        view.setMethod(getMethod());
        if (view.isNeedEntity()) {
            view.setResBody(getPostData(), getContentType());
        }
    }


    private String getMethod() {
        String catalog = "";
        String subCmd = "";
        Object cataItem = catageComboBox.getSelectedItem();
        Object comboItem = comComboBox.getSelectedItem();

        if (null != cataItem) {
            catalog = cataItem.toString();
        }
        if (null != comboItem) {
            subCmd = comboItem.toString();
        }

        String cmd = null;
        if (catalog != "" && subCmd != "") {
            if (catalog == "无类别" || catalog == "all") {
                cmd = subCmd;
            } else {
                cmd = catalog + "_" + subCmd;
            }
        }
        if (null != cmd) {
            CommandDefined cmdInfo = commandUnion.get(cmd);
            return cmdInfo.method;
        }
        return "NONE";
    }

    private String getCommand() {
        String ip = "";
        String catalog = "";
        String subCmd = "";
        Object ipItem = ipComboBox.getSelectedItem();
        Object cataItem = catageComboBox.getSelectedItem();
        Object comboItem = comComboBox.getSelectedItem();

        if (null != ipItem) {
            ip = ipItem.toString();
        }
        if (null != cataItem) {
            catalog = cataItem.toString();
        }
        if (null != comboItem) {
            subCmd = comboItem.toString();
        }

        String params = paramCtrl.getText();
        String cmd = null;
        if (catalog != "" && subCmd != "") {
            if (catalog == "无类别" || catalog == "all") {
                cmd = subCmd;
            } else {
                cmd = catalog + "_" + subCmd;
            }
        }
        if (null != cmd) {
            CommandDefined cmdInfo = commandUnion.get(cmd);
            return formUtil.FormCmd(ip, cmdInfo.port, cmdInfo.command, params);
        }

        return "debug tools";
    }


    /**
     * 获取所有的命令
     *
     * @return
     */
    private Map<String, CommandDefined> getAllCommand() {
        return xmlParse.getAllCommand();
    }


    private List<String> getXmlPath() {
        Exception e = null;
        List<String> lines = new ArrayList<>();
        try {
            if (!recodeFile.exists()) {
                recodeFile.createNewFile();
            } else if (recodeFile.isDirectory()) {
                e = new Exception("xmlPath.recode is a directory!!!");
            } else {
                FileReader reader = new FileReader(recodeFile.getAbsolutePath());
                BufferedReader br = new BufferedReader(reader);
                String str;
                while ((str = br.readLine()) != null) {
                    if (!str.trim().isEmpty()) {
                        lines.add(str);
                    }
                }
                br.close();
                reader.close();
            }
        } catch (Exception exp) {
            e = exp;
        }

        if (null != e) {
            view.showError(Util.getStackTrace(e));
        }

        return lines;
    }

    private void writeXmlPath(List<String> files) {
        Exception e = null;
        try {
            if (!recodeFile.exists()) {
                recodeFile.createNewFile();
            } else if (recodeFile.isDirectory()) {
                e = new Exception("xmlPath.recode is a directory!!!");
            } else {
                FileWriter writer = new FileWriter(recodeFile.getAbsolutePath());
                BufferedWriter bw = new BufferedWriter(writer);
                StringBuffer sb = new StringBuffer("");
                for (String file : files) {
                    sb.append(file + "\n");
                }
                bw.write(sb.toString().trim());
                bw.flush();
                bw.close();
                writer.close();
            }
        } catch (Exception exp) {
            e = exp;
        }

        if (null != e) {
            view.showError(Util.getStackTrace(e));
        }
        // 数据写入之后，再次解析XML文件获取所有命令, 并更新选项框
        xmlParse = new XmlParse(view, files);
        commandUnion = xmlParse.getAllCommand();
        commandNames = xmlParse.getAllCmdName();
        initcatageComboBox();
        initcmdComboBox();
        initParamCtrl();
        initSearchCtrl();
        lastCommand = getCommand();

    }

}
