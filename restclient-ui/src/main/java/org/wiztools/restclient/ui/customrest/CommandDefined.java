package org.wiztools.restclient.ui.customrest;

/**
 * created by 10192065 on 2017/8/30
 * User: 10192065(yzg)
 * Date: 2017/8/30
 */
public class CommandDefined {
    String method;
    String port;
    String commandName;
    String command;
    String params;
    String tips;
    String postData;
    String bodyType;
    String contentType;

    public CommandDefined(String sMethod, String sPort, String sCommandName,
                          String sCommand, String sParams, String sTips,
                          String sPostData, String sContentType, String sBodyType) {
        method = sMethod;
        port = sPort;
        commandName = sCommandName;
        command = sCommand;
        if (null == sParams) {
            params = "";
        } else {
            params = sParams;
        }
        if (null == sTips) {
            tips = "";
        } else {
            tips = sTips;
        }
        if (null == sPostData) {
            postData = "";
        } else {
            postData = sPostData;
        }
        if (null == sContentType) {
            contentType = "application/json;utf-8";
        } else {
            contentType = sContentType;
        }

        if (null == sBodyType) {
            bodyType = "string";
        } else {
            bodyType = sBodyType;
        }

    }
}
