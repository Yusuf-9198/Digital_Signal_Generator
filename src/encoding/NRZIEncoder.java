
package encoding;

public class NRZIEncoder implements LineEncoder {

    @Override
    public int[] encode(String bits) {

        if (bits == null) {
            throw new IllegalArgumentException(
                    "Input bits cannot be null.");
        }

        int[] signal = new int[bits.length()];

        int level = -1; // Initial signal level

        for (int i = 0; i < bits.length(); i++) {

            char bit = bits.charAt(i);

            if (bit == '1') {
                level = -level; // Invert on 1
            } else if (bit != '0') {
                throw new IllegalArgumentException(
                        "Input must contain only 0 and 1.");
            }

            signal[i] = level;
        }

        return signal;
    }
}
