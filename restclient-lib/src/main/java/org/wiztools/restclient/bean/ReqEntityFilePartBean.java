package org.wiztools.restclient.bean;

import java.io.File;

/**
 *
 * @author subwiz
 */
public class ReqEntityFilePartBean implements ReqEntityFilePart {
    
    private final String fileName;
    private final File file;

    public ReqEntityFilePartBean(String fileName, File file) {
        this.fileName = fileName;
        this.file = file;
    }
    
    public ReqEntityFilePartBean(File file) {
        this.fileName = file.getName();
        this.file = file;
    }

    @Override
    public File getPart() {
        return file;
    }

    @Override
    public String getFileName() {
        return fileName;
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
        if ((this.fileName == null) ? (other.fileName != null) : !this.fileName.equals(other.fileName)) {
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
        hash = 19 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        hash = 19 * hash + (this.file != null ? this.file.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@ReqEntityFilePart[")
                .append("fileName=").append(fileName).append(", ")
                .append("file=").append(file)
                .append("]");
        return super.toString();
    }
}
