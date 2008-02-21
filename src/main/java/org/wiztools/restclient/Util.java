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
        try {
            URL u = f.toURI().toURL();
            URLConnection uc = u.openConnection();
            type = uc.getContentType();
        } catch (Exception e) {
            // Do nothing!
            e.printStackTrace();
        }
        return type;
    }

    public static void createReqResArchive(RequestBean request, ResponseBean response, File zipFile)
            throws IOException, XMLException {
        File requestFile = new File("request.xml");
        File responseFile = new File("response.xml");
        XMLUtil.writeRequestXML(request, requestFile);
        XMLUtil.writeResponseXML(response, responseFile);

        String[] filesToZip = new String[]{"request.xml", "response.xml"};
        byte[] buf = new byte[1024];
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        for (int i = 0; i < filesToZip.length; i++) {
            FileInputStream fis = new FileInputStream(filesToZip[i]);
            zos.putNextEntry(new ZipEntry(filesToZip[i]));
            int len;
            while((len = fis.read(buf)) > 0){
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            fis.close();
        }
        zos.close();
        requestFile.delete();
        responseFile.delete();
    }
    public static EncapsulateBean getReqResArchive(File zipFile)
            throws FileNotFoundException, IOException, XMLException{
        EncapsulateBean encpBean = new EncapsulateBean();
        BufferedOutputStream dest = null;
         FileInputStream fis = new 
	   FileInputStream(zipFile);
         ZipInputStream zis = new 
	   ZipInputStream(new BufferedInputStream(fis));
         ZipEntry entry;
         while((entry = zis.getNextEntry()) != null) {
            int count;
            byte data[] = new byte[1024];
            File tmpFile = new File(entry.getName());
            FileOutputStream fos = new 
	      FileOutputStream(tmpFile);
            dest = new 
              BufferedOutputStream(fos, 1024);
            while ((count = zis.read(data, 0, 1024)) 
              != -1) {
               dest.write(data, 0, count);
            }
            if(entry.getName().equals("request.xml")){
                RequestBean reqBean = XMLUtil.getRequestFromXMLFile(tmpFile);
                encpBean.setRequestBean(reqBean);
            }
            else{
                ResponseBean resBean = XMLUtil.getResponseFromXMLFile(tmpFile);
                encpBean.setResponseBean(resBean);
            }
            dest.flush();
            dest.close();
            tmpFile.delete();
         }
         zis.close();
        return encpBean;
    }
}











