package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public class ReqEntityStringPartBean extends ReqEntityBasePart implements ReqEntityStringPart {
    
    final String part;

    public ReqEntityStringPartBean(String name, ContentType contentType, String part) {
        super(name, contentType);
        this.part = part;    
    }

    @Override
    public String getPart() {
        return part;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReqEntityStringPartBean other = (ReqEntityStringPartBean) obj;
        if (!super.equals(obj)) {
            return false;
        }
        if ((this.part == null) ? (other.part != null) : !this.part.equals(other.part)) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (super.hashCode());
        hash = 53 * hash + (this.part != null ? this.part.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@ReqEntityStringPart[")
                .append("name=").append(name).append(", ")
                .append("contentType=").append(contentType).append(", ")
                .append("part=").append(part)
                .append("]");
        return sb.toString();
    }
}
