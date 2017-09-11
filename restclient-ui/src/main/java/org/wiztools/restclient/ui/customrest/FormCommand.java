package org.wiztools.restclient.ui.customrest;

/**
 * created by 10192065 on 2017/8/30
 * User: 10192065(yzg)
 * Date: 2017/8/30
 * 根据xml配置以及设置的IP等参数构造出一条完整的命令
 */
public final class FormCommand {

    /**
     * 形成完整的URL
     *
     * @param commandUnion xml建模好的对象
     * @return 组装好的URL
     */
    String FormCmd(String ip, CommandDefined commandUnion) {
        String command = commandUnion.command;
        String[] params = commandUnion.params.split("\\$##\\$");
        for (String param : params) {
            command = command.replaceFirst("\\$##\\$", param);
        }
        // 可能提供的参数不全，将没替换的占位符全部替换掉
        command = command.replaceAll("\\$##\\$", "");
        if (command.startsWith("/")) {
            return ("http://" + ip + ":" + commandUnion.port + command);
        } else {
            return ("http://" + ip + ":" + commandUnion.port + "/" + command);
        }

    }

    String FormCmd(String ip, String port, String command, String sparams) {
        String[] params = sparams.split("\\$##\\$");
        for (String param : params) {
            command = command.replaceFirst("\\$##\\$", param);
        }
        // 可能提供的参数不全，将没替换的占位符全部替换掉
        command = command.replaceAll("\\$##\\$", "");
        if (command.startsWith("/")) {
            return ("http://" + ip + ":" + port + command);
        } else {
            return ("http://" + ip + ":" + port + "/" + command);
        }

    }
}
