package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public abstract class ReqEntityBasePart implements ReqEntityPart {
    
    protected final String name;
    protected final ContentType contentType;

    public ReqEntityBasePart(String name, ContentType contentType) {
        this.name = name;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReqEntityBasePart other = (ReqEntityBasePart) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.contentType != other.contentType && (this.contentType == null || !this.contentType.equals(other.contentType))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 19 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        return hash;
    }
}
