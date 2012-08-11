package org.wiztools.restclient.util;

import org.wiztools.commons.Charsets;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Code is derived from:
 * http://www.devdaily.com/java/jwarehouse/syslogd-2.2/source/com/ice/util/HexDump.java.shtml
 * @author subwiz
 */
public class HexDump {

    private HexDump() {
    }
    private static final int ROW_BYTES = 16;
    private static final int ROW_QTR1 = 3;
    private static final int ROW_HALF = 7;
    private static final int ROW_QTR2 = 11;
    
    public static String getHexDataDumpAsString(byte[] buf) {
        return getHexDataDumpAsString(buf, buf.length);
    }
    
    public static String getHexDataDumpAsString(byte[] buf, int len) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(baos, false, Charsets.UTF_8.name());
            dumpHexData(out, buf, len);
            return new String(baos.toByteArray(), Charsets.UTF_8.name());
        }
        catch(UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void dumpHexData(PrintStream out, byte[] buf) {
        dumpHexData(out, buf, buf.length);
    }

    public static void dumpHexData(PrintStream out, byte[] buf, int len) {
        int rows, residue, i, j;
        byte[] save_buf = new byte[ROW_BYTES + 2];
        byte[] hex_buf = new byte[4];
        byte[] idx_buf = new byte[8];
        byte[] hex_chars = new byte[20];

        hex_chars[0] = '0';
        hex_chars[1] = '1';
        hex_chars[2] = '2';
        hex_chars[3] = '3';
        hex_chars[4] = '4';
        hex_chars[5] = '5';
        hex_chars[6] = '6';
        hex_chars[7] = '7';
        hex_chars[8] = '8';
        hex_chars[9] = '9';
        hex_chars[10] = 'A';
        hex_chars[11] = 'B';
        hex_chars[12] = 'C';
        hex_chars[13] = 'D';
        hex_chars[14] = 'E';
        hex_chars[15] = 'F';

        rows = len >> 4;
        residue = len & 0x0000000F;
        for (i = 0; i < rows; i++) {
            int hexVal = (i * ROW_BYTES);
            idx_buf[0] = hex_chars[((hexVal >> 12) & 15)];
            idx_buf[1] = hex_chars[((hexVal >> 8) & 15)];
            idx_buf[2] = hex_chars[((hexVal >> 4) & 15)];
            idx_buf[3] = hex_chars[(hexVal & 15)];

            String idxStr = new String(idx_buf, 0, 4);
            out.print(idxStr + ": ");

            for (j = 0; j < ROW_BYTES; j++) {
                save_buf[j] = buf[(i * ROW_BYTES) + j];

                hex_buf[0] = hex_chars[(save_buf[j] >> 4) & 0x0F];
                hex_buf[1] = hex_chars[save_buf[j] & 0x0F];

                out.print((char) hex_buf[0]);
                out.print((char) hex_buf[1]);
                out.print(' ');

                if (j == ROW_QTR1 || j == ROW_HALF || j == ROW_QTR2) {
                    out.print(" ");
                }

                if (save_buf[j] < 0x20 || save_buf[j] > 0xD9) {
                    save_buf[j] = '.';
                }
            }

            String saveStr = new String(save_buf, 0, j);
            out.println(" | " + saveStr + " |");
        }

        if (residue > 0) {
            int hexVal = (i * ROW_BYTES);
            idx_buf[0] = hex_chars[((hexVal >> 12) & 15)];
            idx_buf[1] = hex_chars[((hexVal >> 8) & 15)];
            idx_buf[2] = hex_chars[((hexVal >> 4) & 15)];
            idx_buf[3] = hex_chars[(hexVal & 15)];

            String idxStr = new String(idx_buf, 0, 4);
            out.print(idxStr + ": ");

            for (j = 0; j < residue; j++) {
                save_buf[j] = buf[(i * ROW_BYTES) + j];

                hex_buf[0] = hex_chars[(save_buf[j] >> 4) & 0x0F];
                hex_buf[1] = hex_chars[save_buf[j] & 0x0F];

                out.print((char) hex_buf[0]);
                out.print((char) hex_buf[1]);
                out.print(' ');

                if (j == ROW_QTR1 || j == ROW_HALF || j == ROW_QTR2) {
                    out.print(" ");
                }

                if (save_buf[j] < 0x20 || save_buf[j] > 0xD9) {
                    save_buf[j] = '.';
                }
            }

            for ( /*
                     * j INHERITED
                     */; j < ROW_BYTES; j++) {
                save_buf[j] = ' ';
                out.print("   ");
                if (j == ROW_QTR1 || j == ROW_HALF || j == ROW_QTR2) {
                    out.print(" ");
                }
            }

            String saveStr = new String(save_buf, 0, j);
            out.println(" | " + saveStr + " |");
        }
    }
}
