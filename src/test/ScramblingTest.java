
import java.util.Arrays;

import encoding.AMIEncoder;
import scrambling.B8ZSScrambler;
import scrambling.B8ZSDescrambler;
import scrambling.HDB3Scrambler;
import scrambling.HDB3Descrambler;

public class ScramblingTest {

    private static void test(
            String name,
            int[] original,
            int[] scrambled,
            int[] restored) {

        System.out.println("\n" + name);
        System.out.println("Original:   " + Arrays.toString(original));
        System.out.println("Scrambled:  " + Arrays.toString(scrambled));
        System.out.println("Restored:   " + Arrays.toString(restored));

        System.out.println("Same length: "
                + (original.length == scrambled.length));

        System.out.println("Round trip: "
                + (Arrays.equals(original, restored)
                        ? "PASS"
                        : "FAIL"));
    }

    public static void main(String[] args) {

        // Long zero runs are deliberately included.
        String bits = "110000000010000000001100000000";

        int[] ami = new AMIEncoder().encode(bits);

        int[] b8zs = new B8ZSScrambler().scramble(ami);
        int[] b8zsRestored = new B8ZSDescrambler().descramble(b8zs);

        test("B8ZS", ami, b8zs, b8zsRestored);

        int[] hdb3 = new HDB3Scrambler().scramble(ami);
        int[] hdb3Restored = new HDB3Descrambler().descramble(hdb3);

        test("HDB3", ami, hdb3, hdb3Restored);
    }
}
