
import java.util.Arrays;
import encoding.AMIEncoder;
import scrambling.HDB3Scrambler;
import scrambling.HDB3Descrambler;

public class HDB3KnownCasesTest {
    public static void main(String[] args) {
        String[] cases = {
                "10000",
                "110000",
                "1100000001",
                "1010000100001",
                "11000001000011000001"
        };

        HDB3Scrambler encoder = new HDB3Scrambler();
        HDB3Descrambler decoder = new HDB3Descrambler();
        AMIEncoder amiEncoder = new AMIEncoder();

        for (String bits : cases) {
            int[] original = amiEncoder.encode(bits);
            int[] scrambled = encoder.scramble(original);
            int[] restored = decoder.descramble(scrambled);

            System.out.println("\nInput: " + bits);
            System.out.println("Scrambled: "
                    + Arrays.toString(scrambled));
            System.out.println("Round trip: "
                    + (Arrays.equals(original, restored)
                            ? "PASS"
                            : "FAIL"));
        }
    }
}
