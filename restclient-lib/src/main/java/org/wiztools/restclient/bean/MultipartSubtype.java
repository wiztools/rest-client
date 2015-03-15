package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public enum MultipartSubtype {
    FORM_DATA, MIXED;

    @Override
    public String toString() {
        if(this.equals(FORM_DATA)) {
            return "form-data";
        }
        else {
            return "mixed";
        }
    }
}
