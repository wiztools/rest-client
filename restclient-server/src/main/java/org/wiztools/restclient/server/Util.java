package org.wiztools.restclient.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.StreamUtil;

/**
 *
 * @author subwiz
 */
class Util {
    private Util() {}
    
    static String inputStreamToString(InputStream is) throws IOException {
        CharsetDecoder decoder = Charsets.US_ASCII.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        decoder.replaceWith("?");
        CharBuffer buffer = decoder.decode(
            ByteBuffer.wrap(StreamUtil.inputStream2Bytes(is)));
        return buffer
                .toString()
                .substring(0, 100)
                .replaceAll("\\p{C}", "?") + "...";
    }
}
