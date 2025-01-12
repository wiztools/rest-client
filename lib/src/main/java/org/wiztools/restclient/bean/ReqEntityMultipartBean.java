package org.wiztools.restclient.bean;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author subwiz
 */
public class ReqEntityMultipartBean implements ReqEntityMultipart {
    
    private final MultipartSubtype subType;
    private final MultipartMode mode;
    private final List<ReqEntityPart> parts;

    public ReqEntityMultipartBean(List<ReqEntityPart> parts) {
        this(parts, null);
    }
    
    public ReqEntityMultipartBean(List<ReqEntityPart> parts,
            MultipartMode mode) {
        this(parts, null, null);
    }
    
    public ReqEntityMultipartBean(List<ReqEntityPart> parts,
            MultipartMode mode,
            MultipartSubtype subType) {
        this.parts = Collections.unmodifiableList(parts);
        this.mode = mode != null? mode: MultipartMode.STRICT;
        this.subType = subType;
    }
    
    @Override
    public MultipartSubtype getSubtype() {
        return subType;
    }
    
    @Override
    public MultipartMode getMode() {
        return mode;
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
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.subType);
        hash = 29 * hash + Objects.hashCode(this.mode);
        hash = 29 * hash + Objects.hashCode(this.parts);
        return hash;
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
        if (this.subType != other.subType) {
            return false;
        }
        if (this.mode != other.mode) {
            return false;
        }
        if (!Objects.equals(this.parts, other.parts)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@ReqEntityMultipart");
        sb.append("{").append(subType).append(", ").append(mode).append("}");
        sb.append("[").append(parts).append("]");
        return sb.toString();
    }
}
