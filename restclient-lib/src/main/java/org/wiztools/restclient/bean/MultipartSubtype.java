package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public enum MultipartSubtype {
    FORM_DATA, MIXED, DIGEST, MESSAGE, ALTERNATIVE, RELATED, REPORT, SIGNED,
    ENCRYPTED, X_MIXED_REPLACE, BYTERANGE;

    @Override
    public String toString() {
        return this.name().toLowerCase().replaceAll("_", "-");
    }
}
