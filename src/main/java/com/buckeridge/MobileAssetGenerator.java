package com.buckeridge;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Locale;

/**
 * The entry point for the resizer
 */
public class MobileAssetGenerator {

    /**
     * App usage
     */
    private static final String USAGE =
            "Arguments: \n" +
            "Pre-scale : --pre-scale | -ps {floatValue} \n" +
            "Input file: --file | -f {filename.png} \n";

    /**
     * Prescale commands
     */
    private static final String CMD_PRESCALE[] = {"--pre-scale", "-ps"};

    /**
     * File commands
     */
    private static final String CMD_FILE[] = {"--file", "-f"};

    /**
     * Default output directory
     */
    private static final String OUTPUT_DIR_DEFAULT_NAME = "android-resources";

    public static void main(String[] args) {

        /**
         * Pre-scale value
         */
        float preScale = 1;

        /**
         * The filename to process
         */
        String inputFilename = null;

        /**
         * Output directory
         */
        File outputDirectory = new File(OUTPUT_DIR_DEFAULT_NAME);

        /**
         * The input density.
         * Defaults to the highest.
         */
        Density inputDensity = Density.XXXHDPI;

	    //process args
        if (args == null || args.length == 0) {
            printUsageAndExit();
        }
        for (int i = 0; i < args.length; i++) {
            // pre-scale
            if (CMD_PRESCALE[0].equalsIgnoreCase(args[i]) || CMD_PRESCALE[1].equalsIgnoreCase(args[i])) {
                ++i; // increment to point to the next argument
                if (args.length > i) {
                    try {
                        preScale = Float.parseFloat(args[i]);
                    } catch (NumberFormatException e) {
                        //no args provided
                        System.err.println("Pre-scale should be a float number");
                        System.exit(-1);
                    }
                } else {
                    printUsageAndExit();
                }
            }

            // file
            if (CMD_FILE[0].equalsIgnoreCase(args[i]) || CMD_FILE[1].equalsIgnoreCase(args[i])) {
                ++i; // increment to point to the next argument
                if (args.length > i) {
                    inputFilename = args[i];
                } else {
                    printUsageAndExit();
                }
            }
        }
        //validate input filename
        if (inputFilename == null || "".equalsIgnoreCase(inputFilename)) {
            printUsageAndExit();
        }
        //check prescale correctness
        if (preScale <= 0) {
            preScale = 1;
        }

        System.out.println(String.format(Locale.US, "prescale: %.2f, filename: %s", preScale, inputFilename));

        File inputFile = new File(inputFilename);
        if (!inputFile.exists()) {
            System.err.println(String.format(Locale.US, "File: %s does not exists!", inputFilename));
            System.exit(-1);
        }

        outputDirectory.mkdirs();
        if (inputDensity == null) {
            // default to highest
            inputDensity = Density.XXXHDPI;
        }
        //export all densities
        for (Density outputDensity : Density.values()) {
            try {
                ImageProcessor.exportImage(inputFile, outputDirectory, preScale, inputDensity, outputDensity);
            } catch (FileAlreadyExistsException e) {
                System.err.println(String.format(Locale.US, "File already exists: %s", e.getMessage()));
            } catch (IOException e) {
                System.err.println(e.getCause() + e.getMessage());
            }
        }
    }

    /**
     * Prints app usage and exits
     */
    private static void printUsageAndExit() {
        //no args provided
        System.err.println(USAGE);
        System.exit(-1);
    }
}
