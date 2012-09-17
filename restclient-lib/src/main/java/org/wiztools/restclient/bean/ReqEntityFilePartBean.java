package org.wiztools.restclient.bean;

import java.io.File;

/**
 *
 * @author subwiz
 */
public class ReqEntityFilePartBean extends ReqEntityBasePart implements ReqEntityFilePart {
    
    private final File file;

    public ReqEntityFilePartBean(String fileName, ContentType type, File file) {
        super(fileName, type);
        this.file = file;
    }
    
    public ReqEntityFilePartBean(File file) {
        super(file.getName(), null);
        this.file = file;
    }

    @Override
    public File getPart() {
        return file;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReqEntityFilePartBean other = (ReqEntityFilePartBean) obj;
        if (!super.equals(obj)) {
            return false;
        }
        if (this.file != other.file && (this.file == null || !this.file.equals(other.file))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (super.hashCode());
        hash = 19 * hash + (this.file != null ? this.file.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@ReqEntityFilePart[")
                .append("fileName=").append(name).append(", ")
                .append("contentType=").append(contentType).append(", ")
                .append("file=").append(file)
                .append("]");
        return sb.toString();
    }
}
