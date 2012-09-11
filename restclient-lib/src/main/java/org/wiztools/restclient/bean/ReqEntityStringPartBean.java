package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public class ReqEntityStringPartBean implements ReqEntityStringPart {
    
    final String part;
    final ContentType contentType;
    final String name;

    public ReqEntityStringPartBean(String name, ContentType contentType, String part) {
        this.name = name;
        this.contentType = contentType;
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
        if ((this.part == null) ? (other.part != null) : !this.part.equals(other.part)) {
            return false;
        }
        if (this.contentType != other.contentType && (this.contentType == null || !this.contentType.equals(other.contentType))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.part != null ? this.part.hashCode() : 0);
        hash = 53 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
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
