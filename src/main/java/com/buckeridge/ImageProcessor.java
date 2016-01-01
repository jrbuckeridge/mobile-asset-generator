package com.buckeridge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Locale;

/**
 * Processes the input image file and generates the output
 *
 * Created by buckeridge85 on 12/23/15.
 */
public class ImageProcessor {

    public static void exportImage(File inputFile, File outputDirectory, float preScale,
                                   Density inputDensity, Density outputDensity, boolean overwrite) throws IOException {

        String outPath = outputDirectory.getAbsolutePath()
                + "/"	+ outputDensity.getDrawableResourceDirectoryName()
                + "/" + inputFile.getName();

        File outFile = new File(outPath);
        if (!overwrite && outFile.exists()) {
            throw new FileAlreadyExistsException(outFile.getAbsolutePath());
        }

        outFile.getParentFile().mkdirs();
        BufferedImage inputImage = ImageIO.read(inputFile);
        String extension = getFileExtension(inputFile);
        float scale = preScale * outputDensity.getRatioToBaseDensity() / inputDensity.getRatioToBaseDensity();
        int scaledWidth = (int) (inputImage.getWidth() * scale);
        int scaledHeight = (int) (inputImage.getHeight() * scale);
        System.out.println(String.format(Locale.US, "%s: [%d, %d]: %s", extension, scaledWidth, scaledHeight, outPath));
        BufferedImage outputImage = getScaledInstance(inputImage, scaledWidth, scaledHeight, true);
        //write output image
        ImageIO.write(outputImage, extension, outFile);
    }

    /**
     * Gets the file extension in lowercase
     *
     * @param file The file
     * @return The extension of the file
     */
    private static String getFileExtension(File file) {
        if (file.isFile()) {
            String filename = file.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0) {
                return filename.substring(i + 1).toLowerCase(Locale.US);
            }
        }
        return "";
    }

    /**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance, in pixels
     * @param targetHeight the desired height of the scaled instance, in pixels
     * @param higherQuality if true, this method will use a multi-step
     *                      scaling technique that provides higher quality than the usual
     *                      one-step technique (only useful in downscaling cases, where
     *                      {@code targetWidth} or {@code targetHeight} is
     *                      smaller than the original dimensions, and generally only when
     *                      the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    private static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }
            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }
            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();
            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
}
