package org.wiztools.restclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.wiztools.restclient.xml.XMLException;
import org.wiztools.restclient.xml.XMLUtil;

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

    public static String getNullStrIfNull(final String str) {
        return str == null ? "" : str;
    }

    public static String getStackTrace(final Throwable aThrowable) {
        String errorMsg = aThrowable.getMessage();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return errorMsg + "\n" + result.toString();
    }

    public static String getHTMLListFromList(List<String> ll) {
        StringBuffer sb = new StringBuffer();
        sb.append("<html><ul>");
        for (String str : ll) {
            sb.append("<li>").append(str).append("</li>");
        }
        sb.append("</ul></html>");
        return sb.toString();
    }

    public static String inputStream2String(final InputStream in) throws IOException {
        if (in == null) {
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

    public static String parameterEncode(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        for (String key : params.keySet()) {
            try {
                String value = params.get(key);
                String encodedKey = URLEncoder.encode(key, ENCODE);
                String encodedValue = URLEncoder.encode(value, ENCODE);
                sb.append(encodedKey).append("=").append(encodedValue).append("&");
            } catch (UnsupportedEncodingException ex) {
                assert true : "Encoder UTF-8 supported in all Java platforms.";
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String getStringFromFile(File f) throws FileNotFoundException, IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static String getMimeType(File f) {
        String type = null;
        URLConnection uc = null;
        try {
            URL u = f.toURI().toURL();
            uc = u.openConnection();
            type = uc.getContentType();
        } catch (Exception e) {
            // Do nothing!
            e.printStackTrace();
        }
        finally{
            if(uc != null){
                // No method like uc.close() !!
            }
        }
        return type;
    }

    public static void createReqResArchive(RequestBean request, ResponseBean response, File zipFile)
            throws IOException, XMLException {
        File requestFile = File.createTempFile("req-", ".xml");
        File responseFile = File.createTempFile("res-", ".xml");
        XMLUtil.writeRequestXML(request, requestFile);
        XMLUtil.writeResponseXML(response, responseFile);

        Map<String, File> files = new HashMap<String, File>();
        files.put("request.xml", requestFile);
        files.put("response.xml", responseFile);
        byte[] buf = new byte[BUFF_SIZE];
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        boolean isSuccess = false;
        try{
            for (String entryName: files.keySet()) {
                File entryFile = files.get(entryName);
                FileInputStream fis = new FileInputStream(entryFile);
                zos.putNextEntry(new ZipEntry(entryName));
                int len;
                while ((len = fis.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            isSuccess = true;
        }
        finally{
            IOException ioe = null;
            if(zos != null){
                try{
                    zos.close();
                }
                catch(IOException ex){
                    isSuccess = false;
                    ioe = ex;
                }
            }
            if(!isSuccess){ // Failed: delete half-written zip file
                zipFile.delete();
            }
            requestFile.delete();
            responseFile.delete();
            if(ioe != null){
                throw ioe;
            }
        }
    }

    private static final int BUFF_SIZE = 1024 * 4;
    
    public static ReqResBean getReqResArchive(File zipFile)
            throws FileNotFoundException, IOException, XMLException {
        ReqResBean encpBean = new ReqResBean();
        // BufferedOutputStream dest = null;
        FileInputStream fis = new FileInputStream(zipFile);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        try{
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                byte data[] = new byte[BUFF_SIZE];
                File tmpFile = File.createTempFile(entry.getName(), "");
                try{
                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFF_SIZE);
                    while ((count = zis.read(data, 0, BUFF_SIZE)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();

                    if (entry.getName().equals("request.xml")) {
                        RequestBean reqBean = XMLUtil.getRequestFromXMLFile(tmpFile);
                        encpBean.setRequestBean(reqBean);
                    }
                    else {
                        ResponseBean resBean = XMLUtil.getResponseFromXMLFile(tmpFile);
                        encpBean.setResponseBean(resBean);
                    }
                }
                finally{
                    tmpFile.delete();
                }
            }
        }
        finally{
            zis.close();
        }
        return encpBean;
    }
}











