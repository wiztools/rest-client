package org.wiztools.restclient.bean;

/**
 *
 * @author subwiz
 */
public enum KeyStoreType {
    JKS, PKCS12, PEM;
    
    public String[] getFileExtns() {
        switch(this) {
            case JKS:
                return new String[]{".jks"};
            case PKCS12:
                return new String[]{".p12", ".pfx", ".pkcs12"};
            case PEM:
                return new String[]{".pem", ".cert", ".cer", ".crt"};
        }
        return new String[]{};
    }
    
    public static KeyStoreType detectByExtn(String fileName) {
        fileName = fileName.toLowerCase();
        
        if(isOfType(fileName, JKS)) return JKS;
        if(isOfType(fileName, PKCS12)) return PKCS12;
        if(isOfType(fileName, PEM)) return PEM;
        
        return null;
    }
    
    private static boolean isOfType(String fileName, KeyStoreType type) {
        for(String extn: type.getFileExtns()) {
            if(fileName.endsWith(extn)) {
                return true;
            }
        }
        return false;
    }
}
