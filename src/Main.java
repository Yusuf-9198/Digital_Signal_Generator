import data.DataType;
import data.DigitalDataGenerator;
import data.PalindromeFinder;
import decoding.LineDecoder;
import decoding.NRZLDecoder;
import decoding.NRZIDecoder;
import decoding.ManchesterDecoder;
import decoding.DifferentialManchesterDecoder;
import decoding.AMIDecoder;
import encoding.LineEncoder;
import encoding.NRZLEncoder;
import encoding.NRZIEncoder;
import encoding.ManchesterEncoder;
import encoding.DifferentialManchesterEncoder;
import encoding.AMIEncoder;
import visualization.SignalPlotter;

import java.util.Scanner;

public class Main {
    private static final LineEncoder[] ENCODERS = {
            new NRZLEncoder(),
            new NRZIEncoder(),
            new ManchesterEncoder(),
            new DifferentialManchesterEncoder(),
            new AMIEncoder()
    };

    private static final LineDecoder[] DECODERS = {
            new NRZLDecoder(),
            new NRZIDecoder(),
            new ManchesterDecoder(),
            new DifferentialManchesterDecoder(),
            new AMIDecoder()
    };

    private static final String[] NAMES = {
            "NRZ-L",
            "NRZ-I",
            "Manchester",
            "Differential Manchester",
            "AMI"
    };

    public static void main(String[] args) {
        if (args.length > 0) {
            handleCommandArgs(args);
            return;
        }

        runDemo();
    }

    private static void handleCommandArgs(String[] args) {
        String arg = args[0].trim().toLowerCase();

        if ("--test".equals(arg) || "test".equals(arg)) {
            ProjectTest.main(new String[0]);
            return;
        }

        if ("--demo".equals(arg) || "demo".equals(arg)) {
            runDemo();
            return;
        }

        System.out.println("Unknown option: " + args[0]);
        printUsage();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java -cp bin Main --demo");
        System.out.println("  java -cp bin Main --test");
    }

    private static void runDemo() {
        System.out.println("==============================================");
        System.out.println("  DIGITAL SIGNAL GENERATOR & ANALYZER");
        System.out.println("==============================================");

        DigitalDataGenerator dataGenerator = new DigitalDataGenerator();
        String bits = dataGenerator.generate(16, DataType.RANDOM);
        String palindrome = PalindromeFinder.findLongestPalindrome(bits);

        System.out.println("Generated bitstream: " + bits);
        System.out.println("Longest palindrome: " + palindrome);
        System.out.println("Palindrome length: " + palindrome.length());

        int selectedScheme = 0;
        System.out.println("\nSelected scheme: " + NAMES[selectedScheme]);

        int[] signal = ENCODERS[selectedScheme].encode(bits);
        String recovered = DECODERS[selectedScheme].decode(signal);

        System.out.println("Encoded signal length: " + signal.length);
        System.out.println("Recovered bitstream: " + recovered);
        System.out.println("Round-trip successful: " + bits.equals(recovered));

        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            SignalPlotter.plotDigital("Digital Signal Demo", signal);
        }

        System.out.println("\nExecutable demo completed successfully.");
        System.out.println("Use --test to run the project verification suite.");
    }
}
