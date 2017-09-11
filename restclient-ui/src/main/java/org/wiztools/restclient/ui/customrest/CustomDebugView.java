package org.wiztools.restclient.ui.customrest;

import com.google.inject.ImplementedBy;
import org.wiztools.restclient.ui.ViewPanel;

/**
 * created by 10192065 on 2017/8/31
 * User: 10192065(yzg)
 * Date: 2017/8/31
 */

@ImplementedBy(CustomDebugViewImpl.class)
public interface CustomDebugView extends ViewPanel {

    String getTipsString();

    String getPostData();

    String getCommandCatag();

    String getCommandSubName();

    String getCommandFullName();

    Boolean isPost();

    String getLastCmd();

    String getCmd();

    void setHostIp();


}
