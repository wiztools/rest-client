package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public class BasicDigestAuthBaseBean extends UsernamePasswordAuthBaseBean implements BasicDigestAuth {
    
    private String host;
    private String realm;
    private boolean preemptive;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPreemptive(boolean preemptive) {
        this.preemptive = preemptive;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getRealm() {
        return realm;
    }

    @Override
    public boolean isPreemptive() {
        return preemptive;
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
        final BasicDigestAuthBaseBean other = (BasicDigestAuthBaseBean) obj;
        if ((this.host == null) ? (other.host != null) : !this.host.equals(other.host)) {
            return false;
        }
        if ((this.realm == null) ? (other.realm != null) : !this.realm.equals(other.realm)) {
            return false;
        }
        if (this.preemptive != other.preemptive) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 13 * hash + (this.host != null ? this.host.hashCode() : 0);
        hash = 13 * hash + (this.realm != null ? this.realm.hashCode() : 0);
        hash = 13 * hash + (this.preemptive ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@BasicDigestAuth[");
        sb.append("username=").append(username).append(", ");
        sb.append("password-length=").append(password.length).append(", ");
        sb.append("host=").append(host).append(", ");
        sb.append("realm=").append(realm).append(", ");
        sb.append("preemptive=").append(preemptive);
        sb.append("]");
        return sb.toString();
    }
}
