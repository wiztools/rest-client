package org.wiztools.restclient.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;

/**
 * Test of {@link XMLIndentUtil}.
 */
public class XMLIndentUtilTest {
    private static final int BUFFER_SIZE = 0x10000; // input buffer size in bytes (64 KB)

    /**
     * Test of XMLIndentUtil.getIndented(String inXml) with UTF-8 Encoding.
     */
    @Test
    public void testGetIndentedUTF() throws Exception {
        String inXml = this.readTextFile("input_UTF.xml");
        String expectedResult = this.readTextFile("input_UTF.xml");
        String result = XMLIndentUtil.getIndented(inXml);
        System.out.println("Expected:\n" + expectedResult);
        System.out.println("Is:\n" + result);
        assertEquals(expectedResult, result);
    }

    /**
     * Test of XMLIndentUtil.getIndented(String inXml) with ISO-8859-1 Encoding.
     */
    @Test
    public void testGetIndentedISO() throws Exception {
        String inXml = this.readTextFile("input_ISO.xml");
        String expectedResult = this.readTextFile("input_ISO.xml");
        String result = XMLIndentUtil.getIndented(inXml);
        System.out.println("Expected:\n" + expectedResult);
        System.out.println("Is:\n" + result);
        assertEquals(expectedResult, result);
    }

    /**
     * Test of XMLIndentUtil.getIndented(String inXml) without explicit Encoding.
     */
    @Test
    public void testGetIndentedNONE() throws Exception {
        String inXml = this.readTextFile("input_NONE.xml");
        String expectedResult = this.readTextFile("input_UTF.xml");
        String result = XMLIndentUtil.getIndented(inXml);
        System.out.println("Expected:\n" + expectedResult);
        System.out.println("Is:\n" + result);
        assertEquals(expectedResult, result);
    }

    /**
     * Read a file and return its content as text.
     *
     * @param fileName file to read
     * @return the file's content as text
     */
    private String readTextFile(String fileName) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
            out.write(data, 0, count);
        }
        System.out.println("Read " + out.size() + " bytes from " + fileName);

        return out.toString();
    }

}
