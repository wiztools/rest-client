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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if(!super.equals(obj)) {
            return false;
        }
        final NtlmAuthBean other = (NtlmAuthBean) obj;
        if ((this.domain == null) ? (other.domain != null) : !this.domain.equals(other.domain)) {
            return false;
        }
        if ((this.workstation == null) ? (other.workstation != null) : !this.workstation.equals(other.workstation)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 83 * hash + (this.domain != null ? this.domain.hashCode() : 0);
        hash = 83 * hash + (this.workstation != null ? this.workstation.hashCode() : 0);
        return hash;
    }
}
