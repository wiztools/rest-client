package org.wiztools.restclient.bean;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author subwiz
 */
public class ReqEntityMultipartBean implements ReqEntityMultipart {
    
    private final List<ReqEntityPart> parts;

    public ReqEntityMultipartBean(List<ReqEntityPart> parts) {
        this.parts = Collections.unmodifiableList(parts);
    }

    @Override
    public List<ReqEntityPart> getBody() {
        return parts;
    }

    @Override
    public Object clone() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReqEntityMultipartBean other = (ReqEntityMultipartBean) obj;
        if (this.parts != other.parts && (this.parts == null || !this.parts.equals(other.parts))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.parts != null ? this.parts.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@ReqEntityMultipart[").append(parts).append("]");
        return sb.toString();
    }
}
