
package encoding;

public class DifferentialManchesterEncoder implements LineEncoder {

    @Override
    public int[] encode(String bits) {

        if (bits == null) {
            throw new IllegalArgumentException(
                    "Input bits cannot be null.");
        }

        int[] signal = new int[bits.length() * 2];

        int level = 1; // Initial signal level

        for (int i = 0; i < bits.length(); i++) {

            char bit = bits.charAt(i);

            if (bit != '0' && bit != '1') {
                throw new IllegalArgumentException(
                        "Input must contain only 0 and 1.");
            }

            // A 0 causes a transition at the start.
            if (bit == '0') {
                level = -level;
            }

            // First half of the bit
            signal[2 * i] = level;

            // Mandatory transition in the middle
            level = -level;

            // Second half of the bit
            signal[2 * i + 1] = level;
        }

        return signal;
    }
}
