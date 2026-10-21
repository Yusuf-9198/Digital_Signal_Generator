
package encoding;

public class NRZLEncoder implements LineEncoder {

    @Override
    public int[] encode(String bits) {

        if (bits == null) {
            throw new IllegalArgumentException(
                    "Input bits cannot be null.");
        }

        int[] signal = new int[bits.length()];

        for (int i = 0; i < bits.length(); i++) {

            char bit = bits.charAt(i);

            if (bit == '0') {
                signal[i] = 1;
            } else if (bit == '1') {
                signal[i] = -1;
            } else {
                throw new IllegalArgumentException(
                        "Input must contain only 0 and 1.");
            }
        }

        return signal;
    }
}
