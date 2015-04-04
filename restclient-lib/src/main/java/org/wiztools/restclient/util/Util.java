package org.wiztools.restclient.util;

import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.codec.binary.Base64;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.restclient.Base64Exception;
import org.wiztools.restclient.persistence.XMLException;
import org.wiztools.restclient.bean.ReqResBean;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.persistence.XmlPersistenceRead;
import org.wiztools.restclient.persistence.XmlPersistenceWrite;

/**
 *
 * @author schandran
 */
public final class Util {
    
    // private constructor so that no instance from outside can be created
    private Util(){}
    
    public static String base64encode(String inStr) {
        return base64encode(inStr.getBytes(Charsets.UTF_8));
    }
    
    public static String base64encode(byte[] arr) {
        return Base64.encodeBase64String(arr);
    }
    
    public static byte[] base64decodeByteArray(String base64Str) throws Base64Exception {
        if(!Base64.isBase64(base64Str)) {
            throw new Base64Exception("Provided string is not Base64 encoded");
        }
        byte[] out = Base64.decodeBase64(base64Str);
        return out;
    }
    
    public static String base64decode(String base64Str) throws Base64Exception {
        if(!Base64.isBase64(base64Str)) {
            throw new Base64Exception("Provided string is not Base64 encoded");
        }
        byte[] out = base64decodeByteArray(base64Str);
        CharsetDecoder decoder = Charsets.UTF_8.newDecoder();
        try {
            decoder.onMalformedInput(CodingErrorAction.REPORT);
            decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
            CharBuffer buffer = decoder.decode(
                    ByteBuffer.wrap(Arrays.copyOf(out, out.length)));
            return buffer.toString();
        }
        catch(MalformedInputException ex) {
            throw new Base64Exception("Input is malformed", ex);
        }
        catch(UnmappableCharacterException ex) {
            throw new Base64Exception("Unmappable characters found", ex);
        }
        catch(CharacterCodingException ex) {
            throw new Base64Exception(ex);
        }
    }

    public static String getStackTrace(final Throwable aThrowable) {
        String errorMsg = aThrowable.getMessage();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return errorMsg + "\n" + result.toString();
    }

    public static String getHTMLListFromList(List<String> ll) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><ul>");
        for (String str : ll) {
            sb.append("<li>").append(str).append("</li>");
        }
        sb.append("</ul></html>");
        return sb.toString();
    }
    
    private static final String ENCODE = "UTF-8";

    public static String parameterEncode(MultiValueMap<String, String> params) {
        final StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            try {
                for(final String value: params.get(key)) {
                    String encodedKey = URLEncoder.encode(key, ENCODE);
                    String encodedValue = URLEncoder.encode(value, ENCODE);
                    sb.append(encodedKey).append("=").append(encodedValue).append("&");
                }
            } catch (UnsupportedEncodingException ex) {
                assert true : "Encoder UTF-8 supported in all Java platforms.";
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static void createReqResArchive(Request request, Response response, File zipFile)
            throws IOException, XMLException {
        XmlPersistenceWrite xWriter = new XmlPersistenceWrite();
        
        File requestFile = File.createTempFile("req-", ".xml");
        File responseFile = File.createTempFile("res-", ".xml");
        xWriter.writeRequest(request, requestFile);
        xWriter.writeResponse(response, responseFile);

        Map<String, File> files = new HashMap<>();
        files.put("request.rcq", requestFile);
        files.put("response.rcs", responseFile);
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
            boolean isReqRead = false;
            boolean isResRead = false;
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

                    XmlPersistenceRead xUtl = new XmlPersistenceRead();
                    if (entry.getName().equals("request.rcq")) {
                        Request reqBean = xUtl.getRequestFromFile(tmpFile);
                        encpBean.setRequestBean(reqBean);
                        isReqRead = true;
                    }
                    else if(entry.getName().equals("response.rcs")){
                        Response resBean = xUtl.getResponseFromFile(tmpFile);
                        encpBean.setResponseBean(resBean);
                        isResRead = true;
                    }
                }
                finally{
                    tmpFile.delete();
                }
            }
            if((!isReqRead) || (!isResRead)){
                throw new IOException("Archive does not have request.rcq/response.rcs!");
            }
        }
        finally{
            zis.close();
        }
        return encpBean;
    }
}
