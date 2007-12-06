/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiztools.restclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author schandran
 */
public class Util {

    public static boolean isStrEmpty(final String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static String getStackTrace(final Throwable aThrowable) {
        String errorMsg = aThrowable.getMessage();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return errorMsg + "\n" + result.toString();
    }

    public static String inputStream2String(final InputStream in) throws IOException {
        if(in == null){
            return "";
        }
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
    
    private static final String ENCODE = "UTF-8";
    public static String parameterEncode(Map<String, String> params){
        StringBuffer sb = new StringBuffer();
        for(String key: params.keySet()){
            try {
                String value = params.get(key);
                String encodedKey = URLEncoder.encode(key, ENCODE);
                String encodedValue = URLEncoder.encode(value, ENCODE);
                sb.append(encodedKey).append("=").append(encodedValue).append("&");
            } catch (UnsupportedEncodingException ex) {
                assert true: "Encoder UTF-8 supported in all Java platforms.";
            }
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
