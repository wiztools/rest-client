package org.wiztools.restclient.bean;

import java.io.File;

/**
 *
 * @author subwiz
 */
public class ReqEntityFilePartBean extends ReqEntityBasePart implements ReqEntityFilePart {
    
    private final File file;
    private final String filename;

    public ReqEntityFilePartBean(String name, String fileName, ContentType type, File file) {
        super(name, type);
        this.file = file;
        this.filename = fileName;
    }
    
    public ReqEntityFilePartBean(String name, File file) {
        super(name, null);
        this.file = file;
        this.filename = file.getName();
    }

    @Override
    public File getPart() {
        return file;
    }

    @Override
    public String getFilename() {
        return filename;
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
        if (this.filename != other.filename && (this.filename == null || !this.filename.equals(other.filename))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (super.hashCode());
        hash = 19 * hash + (this.file != null ? this.file.hashCode() : 0);
        hash = 19 * hash + (this.filename != null ? this.filename.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@ReqEntityFilePart[")
                .append("name=").append(name).append(", ")
                .append("fileName=").append(filename).append(", ")
                .append("contentType=").append(contentType).append(", ")
                .append("file=").append(file)
                .append("]");
        return sb.toString();
    }
}
