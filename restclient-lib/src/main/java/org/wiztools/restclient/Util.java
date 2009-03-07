package org.wiztools.restclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.wiztools.restclient.XMLException;
import org.wiztools.restclient.XMLUtil;

/**
 *
 * @author schandran
 */
public final class Util {

    // private constructor so that no instance from outside can be created
    private Util() {
    }

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
        StringBuilder sb = new StringBuilder();
        sb.append("<html><ul>");
        for (String str : ll) {
            sb.append("<li>").append(str).append("</li>");
        }
        sb.append("</ul></html>");
        return sb.toString();
    }
    private static final String ENCODE = "UTF-8";
    private static final Charset UTF8CHARSET = Charset.forName(ENCODE);

    private static CharBuffer decodeHelper(byte[] byteArray, int numberOfBytes) throws IOException {
        CharsetDecoder decoder = UTF8CHARSET.newDecoder();
        CharBuffer charBuffer = null;
        try {
            charBuffer = decoder.decode(ByteBuffer.wrap(byteArray, 0,
                    numberOfBytes));
        } catch (MalformedInputException ex) {
            charBuffer = null;
        }
        return charBuffer;


    }

    public static String inputStream2String(final InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        byte[] savedBytes = new byte[1];
        boolean hasSavedBytes = false;
        CharsetDecoder decoder = UTF8CHARSET.newDecoder();
        for (int n; (n = in.read(b)) != -1;) {
            if (hasSavedBytes) {
                byte[] bTmp = new byte[savedBytes.length + b.length];
                System.arraycopy(savedBytes, 0, bTmp, 0,
                        savedBytes.length);
                System.arraycopy(b, 0, bTmp, savedBytes.length, b.length);
                b = bTmp;
                hasSavedBytes = false;
                n = n + savedBytes.length;
            }

            CharBuffer charBuffer = decodeHelper(b, n);
            if (charBuffer == null) {
                int nrOfChars = 0;
                while (charBuffer == null) {
                    nrOfChars++;
                    charBuffer = decodeHelper(b, n - nrOfChars);
                    if (nrOfChars > 10 && nrOfChars < n) {
                        try {
                            charBuffer = decoder.decode(ByteBuffer.wrap(b,
                                    0, n));
                        } catch (MalformedInputException ex) {
                            throw new IOException(
                                    "File not in supported encoding (" +
                                    ENCODE + ")", ex);
                        }
                    }
                }
                savedBytes = new byte[nrOfChars];
                hasSavedBytes = true;
                for (int i = 0; i < nrOfChars; i++) {
                    savedBytes[i] = b[n - nrOfChars + i];
                }
            }

            charBuffer.rewind(); // Bring the buffer's pointer to 0
            out.append(charBuffer.toString());
        }
        if (hasSavedBytes) {
            try {
                CharBuffer charBuffer = decoder.decode(ByteBuffer.wrap(savedBytes, 0, savedBytes.length));
            } catch (MalformedInputException ex) {
                throw new IOException(
                        "File not in supported encoding (" + ENCODE + ")",
                        ex);
            }
        }
        return out.toString();
    }

    public static String parameterEncode(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
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
        InputStream is = null;
        try {
            is = new FileInputStream(f);
            return inputStream2String(is);
        } finally {
            if (is != null) {
                is.close();
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
        } finally {
            if (uc != null) {
                // No method like uc.close() !!
            }
        }
        return type;
    }

    public static void createReqResArchive(Request request, Response response, File zipFile)
            throws IOException, XMLException {
        File requestFile = File.createTempFile("req-", ".xml");
        File responseFile = File.createTempFile("res-", ".xml");
        XMLUtil.writeRequestXML(request, requestFile);
        XMLUtil.writeResponseXML(response, responseFile);

        Map<String, File> files = new HashMap<String, File>();
        files.put("request.rcq", requestFile);
        files.put("response.rcs", responseFile);
        byte[] buf = new byte[BUFF_SIZE];
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        boolean isSuccess = false;
        try {
            for (String entryName : files.keySet()) {
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
        } finally {
            IOException ioe = null;
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException ex) {
                    isSuccess = false;
                    ioe = ex;
                }
            }
            if (!isSuccess) { // Failed: delete half-written zip file
                zipFile.delete();
            }
            requestFile.delete();
            responseFile.delete();
            if (ioe != null) {
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
        try {
            boolean isReqRead = false;
            boolean isResRead = false;
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                byte data[] = new byte[BUFF_SIZE];
                File tmpFile = File.createTempFile(entry.getName(), "");
                try {
                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFF_SIZE);
                    while ((count = zis.read(data, 0, BUFF_SIZE)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();

                    if (entry.getName().equals("request.rcq")) {
                        Request reqBean = XMLUtil.getRequestFromXMLFile(tmpFile);
                        encpBean.setRequestBean(reqBean);
                        isReqRead = true;
                    } else if (entry.getName().equals("response.rcs")) {
                        Response resBean = XMLUtil.getResponseFromXMLFile(tmpFile);
                        encpBean.setResponseBean(resBean);
                        isResRead = true;
                    }
                } finally {
                    tmpFile.delete();
                }
            }
            if ((!isReqRead) || (!isResRead)) {
                throw new IOException("Archive does not have request.rcq/response.rcs!");
            }
        } finally {
            zis.close();
        }
        return encpBean;
    }

    /**
     * Parses the HTTP response status line, and returns the status code.
     * @param statusLine
     * @return The status code from HTTP response status line.
     */
    public static final int getStatusCodeFromStatusLine(final String statusLine) {
        int retVal = -1;
        final String STATUS_PATTERN = "[^\\s]+\\s([0-9]{3})\\s.*";
        Pattern p = Pattern.compile(STATUS_PATTERN);
        Matcher m = p.matcher(statusLine);
        if (m.matches()) {
            retVal = Integer.parseInt(m.group(1));
        }
        return retVal;
    }

    /**
     * Method formats content-type and charset for use as HTTP header value
     * @param contentType
     * @param charset
     * @return The formatted content-type and charset.
     */
    public static final String getFormattedContentType(final String contentType, final String charset) {
        String charsetFormatted = Util.isStrEmpty(charset) ? "" : "; charset=" + charset;
        return contentType + charsetFormatted;
    }
}
