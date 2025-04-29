package org.wiztools.restclient.util;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

public class IDNUtilTest {
    @Test
    public void TestIDNURL() {
        try {
            URL url = new URI("http://wiztools.org/path?qry=subhash").toURL();
            assertEquals(url.getPath(), "/path");
            URL idnUrl = IDNUtil.getIDNizedURL(url);
            assertEquals(idnUrl.getPath(), "/path");
        } catch(MalformedURLException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }
}
