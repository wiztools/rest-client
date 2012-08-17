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
}
