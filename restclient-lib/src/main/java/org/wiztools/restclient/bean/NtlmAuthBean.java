package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public class NtlmAuthBean extends UsernamePasswordAuthBaseBean implements NtlmAuth {
    
    private String domain;
    private String workstation;

    @Override
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getWorkstation() {
        return workstation;
    }

    public void setWorkstation(String workstation) {
        this.workstation = workstation;
    }
}
