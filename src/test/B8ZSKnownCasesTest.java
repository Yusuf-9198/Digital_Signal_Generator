
import java.util.Arrays;

import encoding.AMIEncoder;
import scrambling.B8ZSScrambler;
import scrambling.B8ZSDescrambler;

public class B8ZSKnownCasesTest {

    public static void main(String[] args) {
        String[] cases = {
                "100000000",
                "11000000001",
                "000000001",
                "10000000000000001",
                "110000000010000000011",
                "101000000001000000001"
        };

        AMIEncoder encoder = new AMIEncoder();
        B8ZSScrambler scrambler = new B8ZSScrambler();
        B8ZSDescrambler descrambler = new B8ZSDescrambler();
        decoding.AMIDecoder decoder = new decoding.AMIDecoder();

        for (String bits : cases) {
            int[] original = encoder.encode(bits);
            int[] scrambled = scrambler.scramble(original);
            int[] restored = descrambler.descramble(scrambled);

            System.out.println("Input:       " + bits);
            System.out.println("AMI:         " + Arrays.toString(original));
            System.out.println("Scrambled:   " + Arrays.toString(scrambled));
            System.out.println("Restored:    " + Arrays.toString(restored));

            // Convert the restored AMI signal back to bits
            String recovered = decoder.decode(restored);

            boolean scrambledDifferent = !Arrays.equals(original, scrambled);
            boolean roundTripPassed = bits.equals(recovered);

            System.out.println("Recovered:   " + recovered);
            System.out.println("Substituted: " + (scrambledDifferent ? "YES (B8ZS triggered)" : "NO"));
            System.out.println("Round trip:  " + (roundTripPassed ? "PASS" : "FAIL"));
            System.out.println();
        }
    }
}
