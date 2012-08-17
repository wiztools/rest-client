package org.wiztools.restclient.ui.reqauth;

import com.google.inject.ImplementedBy;
import java.util.List;
import org.wiztools.restclient.bean.Auth;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqAuthPanelImpl.class)
public interface ReqAuthPanel extends ViewPanel {
    
    void setAuth(Auth auth);
    Auth getAuth();
    List<String> validateIfFilled();
}
