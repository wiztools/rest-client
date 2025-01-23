package org.wiztools.restclient.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.ImageIcon;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public final class SVGLoad {
    private static final double SCALE_FACTOR = getScreenScaleFactor();

    private static double getScreenScaleFactor() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        return gc.getDefaultTransform().getScaleX();
    }

    /**
     *
     * @param path is the resource path in the classpath.
     * @param baseWidth
     * @param baseHeight
     * @return
     */
    public static ImageIcon loadScaledSVG(String path, int baseWidth, int baseHeight) {
        try {
            // Calculate scaled dimensions
            int scaledWidth = (int) (baseWidth * SCALE_FACTOR);
            int scaledHeight = (int) (baseHeight * SCALE_FACTOR);

            // Set up SVG transcoder
            URL url = SVGLoad.class.getClassLoader().getResource(path);
            TranscoderInput input = new TranscoderInput(url.openStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);

            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) scaledWidth);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) scaledHeight);

            // Perform transcoding
            transcoder.transcode(input, output);

            // Convert to ImageIcon
            byte[] imageData = outputStream.toByteArray();
            return new ImageIcon(imageData);

        } catch (TranscoderException | IOException ex) {
            throw new RuntimeException("[loadSVG]", ex);
        }
    }
}
