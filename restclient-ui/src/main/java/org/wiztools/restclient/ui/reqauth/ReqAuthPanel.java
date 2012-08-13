package org.wiztools.restclient.ui.reqauth;

import com.google.inject.ImplementedBy;
import org.wiztools.restclient.bean.HTTPAuthMethod;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subwiz
 */
@ImplementedBy(ReqAuthPanelImpl.class)
public interface ReqAuthPanel extends ViewPanel {

    String getAuthMethod();

    String getBearerToken();

    String getDomain();

    String getHost();

    char[] getNtlmPassword();

    String getNtlmUsername();

    char[] getPassword();

    String getRealm();

    String getUsername();

    String getWorkstation();

    boolean isAuthSelected();

    boolean isPreemptive();

    void setAuthMethod(HTTPAuthMethod authMethod);

    void setBearerToken(String bearerToken);

    void setDomain(String authDomain);

    void setHost(String authHost);

    void setNtlmPassword(String ntlmPassword);

    void setNtlmUsername(String ntlmUsername);

    void setPassword(String authPassword);

    void setPreemptive(boolean b);

    void setRealm(String authRealm);

    void setUsername(String authUsername);

    void setWorkstation(String authWorkstation);
    
}
