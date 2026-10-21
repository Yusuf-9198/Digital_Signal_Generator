import java.util.Arrays;
import data.DataType;
import data.DigitalDataGenerator;
import data.PalindromeFinder;
import encoding.*;
import decoding.*;
import scrambling.*;
import modulation.*;

public class ProjectTest {

    private static int totalTests = 0;
    private static int passedTests = 0;

    private static void check(String testName, boolean condition) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.printf("  [PASS] %s%n", testName);
        } else {
            System.err.printf("  [FAIL] %s%n", testName);
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  RUNNING ALL PROJECT VERIFICATION SUITES");
        System.out.println("==================================================");

        // 1. Data Generator & Palindrome Tests
        System.out.println("\n--- 1. Data Generator & Palindrome Tests ---");
        DigitalDataGenerator gen = new DigitalDataGenerator();

        String randData = gen.generate(16, DataType.RANDOM);
        check("Random data generation of length 16", randData.length() == 16 && randData.matches("[01]+"));

        String fourZ = gen.generate(16, DataType.FOUR_ZEROS);
        check("Generation with 4 zeros contains at least '0000'", fourZ.contains("0000"));

        String eightZ = gen.generate(20, DataType.EIGHT_ZEROS);
        check("Generation with 8 zeros contains at least '00000000'", eightZ.contains("00000000"));

        boolean threwForInvalidLength = false;
        try {
            gen.generate(0, DataType.RANDOM);
        } catch (IllegalArgumentException e) {
            threwForInvalidLength = true;
        }
        check("Rejection of non-positive length", threwForInvalidLength);

        // Palindrome tests
        check("Palindrome of empty string", PalindromeFinder.findLongestPalindrome("").equals(""));
        check("Palindrome of single bit '1'", PalindromeFinder.findLongestPalindrome("1").equals("1"));
        check("Palindrome of '10101'", PalindromeFinder.findLongestPalindrome("10101").equals("10101"));
        check("Palindrome of '1001'", PalindromeFinder.findLongestPalindrome("1001").equals("1001"));
        check("Palindrome substring inside '110010011'", PalindromeFinder.findLongestPalindrome("110010011").equals("110010011"));

        // 2. Line Coding Encoders & Decoders Round-Trip
        System.out.println("\n--- 2. Line Encoders & Decoders Round-Trip ---");
        LineEncoder[] encoders = {
            new NRZLEncoder(), new NRZIEncoder(), new ManchesterEncoder(), new DifferentialManchesterEncoder(), new AMIEncoder()
        };
        LineDecoder[] decoders = {
                new NRZLDecoder(), new NRZIDecoder(), new ManchesterDecoder(), new DifferentialManchesterDecoder(), new AMIDecoder()
        };
        String[] names = { "NRZ-L", "NRZ-I", "Manchester", "Differential Manchester", "AMI" };

        String[] testBitstreams = {
                "0", "1", "10", "01", "1100101011", "00001111", "1011001010010110"
        };

        for (int i = 0; i < encoders.length; i++) {
            boolean allPassed = true;
            for (String bits : testBitstreams) {
                int[] signal = encoders[i].encode(bits);
                String recovered = decoders[i].decode(signal);
                if (!bits.equals(recovered)) {
                    allPassed = false;
                    break;
                }
            }
            check(names[i] + " round-trip on all test bitstreams", allPassed);
        }

        // 3. Scrambling Tests (B8ZS & HDB3)
        System.out.println("\n--- 3. Scrambling & Descrambling Tests ---");
        AMIEncoder ami = new AMIEncoder();
        AMIDecoder amiDecoder = new AMIDecoder();
        B8ZSScrambler b8zs = new B8ZSScrambler();
        B8ZSDescrambler b8zsDesc = new B8ZSDescrambler();
        HDB3Scrambler hdb3 = new HDB3Scrambler();
        HDB3Descrambler hdb3Desc = new HDB3Descrambler();

        // B8ZS tests
        String[] b8zsCases = {
                "100000000", "11000000001", "000000001", "10000000000000001", "110000000010000000011"
        };
        boolean allB8ZSPassed = true;
        for (String bits : b8zsCases) {
            int[] original = ami.encode(bits);
            int[] scrambled = b8zs.scramble(original);
            int[] restored = b8zsDesc.descramble(scrambled);
            String recovered = amiDecoder.decode(restored);
            if (!bits.equals(recovered) || Arrays.equals(original, scrambled)) {
                allB8ZSPassed = false;
                break;
            }
        }
        check("B8ZS substitution and descrambling round-trip", allB8ZSPassed);

        // HDB3 tests
        String[] hdb3Cases = {
                "10000", "110000", "1100000001", "1010000100001", "11000001000011000001"
        };
        boolean allHDB3Passed = true;
        for (String bits : hdb3Cases) {
            int[] original = ami.encode(bits);
            int[] scrambled = hdb3.scramble(original);
            int[] restored = hdb3Desc.descramble(scrambled);
            String recovered = amiDecoder.decode(restored);
            if (!bits.equals(recovered) || Arrays.equals(original, scrambled)) {
                allHDB3Passed = false;
                break;
            }
        }
        check("HDB3 substitution and descrambling round-trip", allHDB3Passed);

        // 4. Modulation Tests (PCM & Delta Modulation)
        System.out.println("\n--- 4. Modulation & Demodulation Tests ---");
        double[] analogSamples = { -1.0, -0.75, -0.2, 0.0, 0.35, 0.8, 1.0 };

        // PCM
        PCMEncoder pcmEnc = new PCMEncoder(4);
        PCMDecoder pcmDec = new PCMDecoder(4);
        String pcmBits = pcmEnc.encode(analogSamples);
        double[] pcmReconstructed = pcmDec.decode(pcmBits);
        check("PCM bitstream length matches samples * bitsPerSample",
                pcmBits.length() == analogSamples.length * 4);
        check("PCM reconstructed count equals input sample count",
                pcmReconstructed.length == analogSamples.length);

        // Delta Modulation
        DeltaModulator dmEnc = new DeltaModulator(0.2);
        DeltaDemodulator dmDec = new DeltaDemodulator(0.2);
        String dmBits = dmEnc.encode(analogSamples);
        double[] dmReconstructed = dmDec.decode(dmBits);
        check("Delta modulation bitstream length matches sample count",
                dmBits.length() == analogSamples.length);
        check("Delta modulation reconstructed count matches sample count",
                dmReconstructed.length == analogSamples.length);

        // Summary
        System.out.println("\n==================================================");
        System.out.printf("  TEST RESULTS: %d / %d PASSED (%.1f%%)%n",
                passedTests, totalTests, (100.0 * passedTests / totalTests));
        System.out.println("==================================================");

        if (passedTests != totalTests) {
            System.exit(1);
        }
    }
}
