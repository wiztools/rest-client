package org.wiztools.restclient.ui.customrest;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.wiztools.restclient.ui.RESTView;
import org.wiztools.restclient.util.Util;

/**
 * created by 10192065 on 2017/8/30
 * User: 10192065(yzg)
 * Date: 2017/8/30
 */
public class XmlParse {
    private static final Logger LOG = Logger.getLogger(XmlParse.class.getName());

    private Map<String, CommandDefined> commandUnion = new HashMap();

    private Map<String, List<String>> commandNames = new HashMap();

    public Map<String, CommandDefined> getAllCommand() {
        return commandUnion;
    }

    public Map<String, List<String>> getAllCmdName() {
        return commandNames;
    }

    private void parseComandNames() {
        Set<String> keys = commandUnion.keySet();
        commandNames.clear();
        for (String key : keys) {
            String catag = null;

            // 无论是什么，都添加到all组别里面
            if (commandNames.keySet().contains("all")) {
                commandNames.get("all").add(key);
            } else {
                List<String> subCmd = new ArrayList();
                subCmd.add(key);
                commandNames.put("all", subCmd);
            }

            // 再根据具体情况进行细分
            if (-1 != key.indexOf("_")) {
                catag = key.split("_")[0];

                // 如果已经包含这个类别，将命令添加到后面的list即可
                if (commandNames.keySet().contains(catag)) {
                    commandNames.get(catag).add(key.split("_")[1]);
                } else {
                    List<String> subCmd = new ArrayList();
                    subCmd.add(key.split("_")[1]);
                    commandNames.put(catag, subCmd);
                }
            } else {
                // 如果不存在‘_’符号，默认没有进行类别分组，默认放到“无类别”组
                if (commandNames.keySet().contains("无类别")) {
                    commandNames.get("无类别").add(key);
                } else {
                    List<String> subCmd = new ArrayList();
                    subCmd.add(key);
                    commandNames.put("无类别", subCmd);
                }
            }
        }
    }


    /**
     * 传入xml的文件路径
     *
     * @param view     视图
     * @param filePath list形式的xml文件路径
     */
    XmlParse(final RESTView view, List<String> filePath) {
        for (String oneFile : filePath) {
            initXml(view, oneFile);
        }
        parseComandNames();
    }


    private void initXml(final RESTView view, String filePath) {
        Exception e = null;
        File file = new File(filePath);
        SAXReader reader = new SAXReader();
        try {
            if (file.exists()) {
                Document document = reader.read(file);
                Element root = document.getRootElement();
                String rootName = root.getName();
                // 当且仅当标签为customcomand时才进行解析到内存
                if (rootName == "customcomand") {
                    List<Element> eleList = root.elements();
                    // 解析所有的元素
                    for (Element ele : eleList) {
                        String port = "8889";
                        String method = "get";
                        String params = null;
                        if (null != ele.attributeValue("method")) {
                            method = ele.attributeValue("method");
                        }
                        if (null != ele.attributeValue("serviceport")) {
                            port = ele.attributeValue("serviceport");
                        }
                        String commandName = ele.elementText("commandname");
                        String command = ele.elementText("command");
                        if(null != ele.elementText("params")) {
                            params = ele.elementText("params");
                        }
                        String tips = ele.elementText("tips");
                        String postData = ele.elementText("postdata");
                        String contentType = ele.elementText("contenttype");
                        String contentBody = ele.elementText("bodyType");
                        CommandDefined commandInstance = new CommandDefined(method, port, commandName, command,
                                params, tips, postData, contentType, contentBody);
                        commandUnion.put(commandName, commandInstance);
                    }
                } else {
                    e = new Exception("only support for customcomand!!!");
                }
            } else {
                e = new Exception("file " + filePath + "is not exist!!!");
            }
        } catch (Exception execption) {
            e = execption;

        }

        if (null != e) {
            LOG.log(Level.WARNING, "error: "+ e);
            view.showError(Util.getStackTrace(e));
        }
    }
}