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

    /**
     * File extension for PNG format
     */
    private static final String EXT_PNG = "png";

    public static void exportImage(File inputFile, File outputDirectory, float preScale,
                                   Density inputDensity, Density outputDensity) throws FileAlreadyExistsException, IOException {

        String outPath = outputDirectory.getAbsolutePath()
                + "/"	+ outputDensity.getDrawableResourceDirectoryName()
                + "/" + inputFile.getName();

        File outFile = new File(outPath);
        if (outFile.exists()) {
            throw new FileAlreadyExistsException(outFile.getAbsolutePath());
        }

        outFile.getParentFile().mkdirs();
        BufferedImage inputImage = ImageIO.read(inputFile);
        String extension = getFileExtension(inputFile);
        boolean preserveAlpha = EXT_PNG.equalsIgnoreCase(extension);
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        float scale = preScale * outputDensity.getRatioToBaseDensity() / inputDensity.getRatioToBaseDensity();
        int scaledWidth = (int) (inputImage.getWidth() * scale);
        int scaledHeight = (int) (inputImage.getHeight() * scale);
        System.out.println(String.format(Locale.US, "%s: [%d, %d]: %s", extension, scaledWidth, scaledHeight, outPath));
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = outputImage.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
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
}
