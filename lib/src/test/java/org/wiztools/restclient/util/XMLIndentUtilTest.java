package org.wiztools.restclient.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import org.junit.Test;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;

/**
 * Test of {@link XMLIndentUtil}.
 */
public class XMLIndentUtilTest {
    /**
     * Test of XMLIndentUtil.getIndented(String inXml) with ISO-8859-1 Encoding.
     */
    // @Test
    public void testGetIndentedISO() throws Exception {
        String inXml = FileUtil.getContentAsString(
                new File("src/test/resources/input_ISO.xml"), Charsets.ISO_8859_1);
        String expectedResult = FileUtil.getContentAsString(
                new File("src/test/resources/output_ISO.xml"), Charsets.ISO_8859_1);
        String result = XMLIndentUtil.getIndented(inXml);
        System.out.println("Expected:\n" + expectedResult);
        System.out.println("Is:\n" + result);
        assertEquals(expectedResult, result);
    }
}
